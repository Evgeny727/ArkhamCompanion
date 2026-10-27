package com.arkhamcompanion.data.local.auth

import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.datastore.core.IOException
import com.arkhamcompanion.data.remote.oauth.StoredOAuthTokens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.UnrecoverableKeyException
import javax.crypto.AEADBadTagException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import androidx.core.content.edit
import javax.inject.Singleton

internal interface OAuthTokenStore {
    suspend fun read(): StoredOAuthTokens?

    suspend fun save(tokens: StoredOAuthTokens)

    suspend fun clear()
}

@Singleton
internal class OAuthTokenStoreImpl @Inject constructor(
    private val preferences: SharedPreferences,
) : OAuthTokenStore, PendingAuthorizationStore {

    /**
     * Serializes access to the preference record and keystore operations
     * within this process.
     */
    private val mutex = Mutex()

    override suspend fun read(): StoredOAuthTokens? =
        withContext(Dispatchers.IO) {
            mutex.withLock {
                val encryptedRecord = preferences.getString(
                    TOKEN_RECORD_KEY,
                    null,
                ) ?: return@withLock null

                try {
                    val plaintext = decryptRecord(encryptedRecord)

                    try {
                        deserializeTokens(plaintext)
                    } finally {
                        // Best-effort removal of the decrypted byte buffer.
                        plaintext.fill(0)
                    }
                } catch (_: KeyPermanentlyInvalidatedException) {
                    resetUnreadableRecord()
                    null
                } catch (_: UnrecoverableKeyException) {
                    resetUnreadableRecord()
                    null
                } catch (_: AEADBadTagException) {
                    // The ciphertext was changed or no longer matches its key.
                    resetUnreadableRecord()
                    null
                } catch (_: InvalidTokenRecordException) {
                    resetUnreadableRecord()
                    null
                } catch (_: IllegalArgumentException) {
                    // For example, invalid Base64 in the stored record.
                    resetUnreadableRecord()
                    null
                } catch (_: JSONException) {
                    // The decrypted record is malformed or from an incompatible format.
                    resetUnreadableRecord()
                    null
                } catch (e: GeneralSecurityException) {
                    throw TokenStoreException(
                        "Unable to decrypt stored OAuth tokens",
                        e,
                    )
                } catch (e: IOException) {
                    throw TokenStoreException(
                        "Unable to access the OAuth encryption key",
                        e,
                    )
                } catch (e: Exception) {
                    if (e is TokenStoreException) throw e

                    throw TokenStoreException(
                        "Unable to read stored OAuth tokens",
                        e,
                    )
                }
            }
        }

    override suspend fun save(tokens: StoredOAuthTokens) {
        validate(tokens)

        withContext(Dispatchers.IO) {
            mutex.withLock {
                val plaintext = serializeTokens(tokens)

                val encryptedRecord = try {
                    encryptRecord(plaintext)
                } catch (e: Exception) {
                    if (e is TokenStoreException) throw e

                    throw TokenStoreException(
                        "Unable to encrypt OAuth tokens",
                        e,
                    )
                } finally {
                    plaintext.fill(0)
                }

                // A single encrypted record means all fields are replaced
                // together. commit() is deliberate: return only after the
                // storage operation reports success.
                val saved = preferences.edit()
                    .putString(TOKEN_RECORD_KEY, encryptedRecord)
                    .commit()

                if (!saved) {
                    throw TokenStoreException(
                        "Unable to persist OAuth tokens",
                    )
                }
            }
        }
    }

    override suspend fun clear() {
        withContext(Dispatchers.IO) {
            mutex.withLock {
                val cleared = preferences.edit()
                    .remove(TOKEN_RECORD_KEY)
                    .commit()

                if (!cleared) {
                    throw TokenStoreException(
                        "Unable to clear stored OAuth tokens",
                    )
                }

                // The AES key itself contains no user token data.
                // We can retain it and reuse it for the next sign-in.
            }
        }
    }

    override suspend fun saveRequest(serializedRequest: String) {
        withContext(Dispatchers.IO) {
            mutex.withLock {
                val bytes = serializedRequest.toByteArray(StandardCharsets.UTF_8)

                val encrypted = try {
                    encryptRecord(bytes)
                } finally {
                    bytes.fill(0)
                }

                val saved = preferences.edit()
                    .putString(PENDING_AUTH_RECORD_KEY, encrypted)
                    .commit()

                if (!saved) {
                    throw TokenStoreException(
                        "Unable to save pending OAuth authorization"
                    )
                }
            }
        }
    }

    override suspend fun readRequest(): String? =
        withContext(Dispatchers.IO) {
            mutex.withLock {
                val encrypted = preferences.getString(
                    PENDING_AUTH_RECORD_KEY,
                    null,
                ) ?: return@withLock null

                val plaintext = try {
                    decryptRecord(encrypted)
                } catch (error: Exception) {
                    preferences.edit(commit = true) {
                        remove(PENDING_AUTH_RECORD_KEY)
                    }
                    throw TokenStoreException(
                        "Unable to read pending OAuth authorization",
                        error,
                    )
                }

                try {
                    String(plaintext, StandardCharsets.UTF_8)
                } finally {
                    plaintext.fill(0)
                }
            }
        }

    override suspend fun clearRequest() {
        withContext(Dispatchers.IO) {
            mutex.withLock {
                val cleared = preferences.edit()
                    .remove(PENDING_AUTH_RECORD_KEY)
                    .commit()

                if (!cleared) {
                    throw TokenStoreException(
                        "Unable to clear pending OAuth authorization"
                    )
                }
            }
        }
    }

    private fun encryptRecord(plaintext: ByteArray): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)

        // Android Keystore generates and protects the AES key.
        // GCM encryption generates a fresh IV for each operation.
        cipher.init(
            Cipher.ENCRYPT_MODE,
            getOrCreateSecretKey(),
        )
        cipher.updateAAD(ASSOCIATED_DATA)

        val iv = cipher.iv
            ?: throw TokenStoreException(
                "The encryption provider did not return an IV",
            )

        if (iv.size != GCM_IV_LENGTH_BYTES) {
            throw TokenStoreException(
                "Unexpected AES-GCM IV length",
            )
        }

        val ciphertext = cipher.doFinal(plaintext)

        return listOf(
            RECORD_FORMAT_VERSION,
            Base64.encodeToString(iv, Base64.NO_WRAP),
            Base64.encodeToString(ciphertext, Base64.NO_WRAP),
        ).joinToString(".")
    }

    @Throws(
        GeneralSecurityException::class,
        InvalidTokenRecordException::class,
    )
    private fun decryptRecord(record: String): ByteArray {
        val parts = record.split('.', limit = 3)

        if (
            parts.size != 3 ||
            parts[0] != RECORD_FORMAT_VERSION
        ) {
            throw InvalidTokenRecordException()
        }

        val iv = Base64.decode(parts[1], Base64.NO_WRAP)
        val ciphertext = Base64.decode(parts[2], Base64.NO_WRAP)

        if (
            iv.size != GCM_IV_LENGTH_BYTES ||
            ciphertext.size < GCM_TAG_LENGTH_BITS / 8
        ) {
            throw InvalidTokenRecordException()
        }

        val cipher = Cipher.getInstance(TRANSFORMATION)

        cipher.init(
            Cipher.DECRYPT_MODE,
            getOrCreateSecretKey(),
            GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv),
        )
        cipher.updateAAD(ASSOCIATED_DATA)

        // GCM verifies the authentication tag here. Tampering causes
        // AEADBadTagException instead of returning unauthenticated data.
        return cipher.doFinal(ciphertext)
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)

        val existingKey = keyStore.getKey(KEY_ALIAS, null)

        if (existingKey != null) {
            return existingKey as? SecretKey
                ?: throw TokenStoreException(
                    "Unexpected key type in Android Keystore",
                )
        }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE,
        )

        val keySpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or
                    KeyProperties.PURPOSE_DECRYPT,
        )
            .setKeySize(AES_KEY_SIZE_BITS)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(
                KeyProperties.ENCRYPTION_PADDING_NONE,
            )
            .setRandomizedEncryptionRequired(true)
            .build()

        keyGenerator.init(keySpec)

        return keyGenerator.generateKey()
    }

    private fun serializeTokens(tokens: StoredOAuthTokens): ByteArray {
        val json = JSONObject()
            .put("schema_version", DATA_SCHEMA_VERSION)
            .put("access_token", tokens.accessToken)
            .put("refresh_token", tokens.refreshToken)
            .put(
                "expires_at_epoch_seconds",
                tokens.expiresAtEpochSeconds,
            )
            .put("scope", tokens.scope)

        return json.toString().toByteArray(StandardCharsets.UTF_8)
    }

    private fun deserializeTokens(plaintext: ByteArray): StoredOAuthTokens {
        val json = JSONObject(
            String(plaintext, StandardCharsets.UTF_8),
        )

        if (json.getInt("schema_version") != DATA_SCHEMA_VERSION) {
            throw InvalidTokenRecordException()
        }

        val tokens = StoredOAuthTokens(
            accessToken = json.getString("access_token"),
            refreshToken = json.getString("refresh_token"),
            expiresAtEpochSeconds = json.getLong("expires_at_epoch_seconds"),
            scope = json.getString("scope"),
        )

        if (
            tokens.accessToken.isBlank() ||
            tokens.refreshToken.isBlank() ||
            tokens.expiresAtEpochSeconds <= 0 ||
            tokens.scope.isBlank()
        ) {
            throw InvalidTokenRecordException()
        }

        return tokens
    }

    private fun validate(tokens: StoredOAuthTokens) {
        if (
            tokens.accessToken.isBlank() ||
            tokens.refreshToken.isBlank() ||
            tokens.expiresAtEpochSeconds <= 0 ||
            tokens.scope.isBlank()
        ) {
            throw TokenStoreException(
                "Invalid OAuth token data",
            )
        }
    }

    /**
     * If the record cannot be trusted or decrypted, fail closed:
     * remove the record and reset its encryption key. The next read will
     * report no saved session, so the user must authenticate again.
     */
    private fun resetUnreadableRecord() {
        val removed = preferences.edit()
            .remove(TOKEN_RECORD_KEY)
            .commit()

        if (!removed) {
            throw TokenStoreException(
                "Unable to clear unreadable OAuth token data",
            )
        }

        try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)

            if (keyStore.containsAlias(KEY_ALIAS)) {
                keyStore.deleteEntry(KEY_ALIAS)
            }
        } catch (e: Exception) {
            throw TokenStoreException(
                "Unable to reset the OAuth encryption key",
                e,
            )
        }
    }

    private class InvalidTokenRecordException : Exception("Invalid encrypted token record")

    private companion object {
        private const val PENDING_AUTH_RECORD_KEY =
            "encrypted_pending_authorization_request"

        private const val TOKEN_RECORD_KEY =
            "encrypted_token_record"

        private const val ANDROID_KEYSTORE =
            "AndroidKeyStore"

        private const val KEY_ALIAS =
            "com.arkhamcompanion.arkham_oauth_tokens.v1"

        private const val TRANSFORMATION =
            "AES/GCM/NoPadding"

        private const val AES_KEY_SIZE_BITS = 256
        private const val GCM_IV_LENGTH_BYTES = 12
        private const val GCM_TAG_LENGTH_BITS = 128

        private const val RECORD_FORMAT_VERSION = "v1"
        private const val DATA_SCHEMA_VERSION = 1

        /**
         * Authenticates the record's purpose and format version.
         * This value is not secret.
         */
        private val ASSOCIATED_DATA =
            "com.arkhamcompanion.arkham_oauth_tokens.v1"
                .toByteArray(StandardCharsets.UTF_8)
    }
}

internal class TokenStoreException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)