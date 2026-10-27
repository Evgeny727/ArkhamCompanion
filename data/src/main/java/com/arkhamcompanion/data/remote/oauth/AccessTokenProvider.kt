package com.arkhamcompanion.data.remote.oauth

import com.arkhamcompanion.data.local.auth.OAuthTokenStore
import com.arkhamcompanion.data.local.auth.TokenStoreException
import com.arkhamcompanion.domain.exceptions.OAuthException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

interface AccessTokenProvider {
    suspend fun getValidAccessToken(): String?
}

@Singleton
internal class OAuthAccessTokenProvider @Inject constructor(
    private val api: ArkhamBuildOAuthApi,
    private val tokenStore: OAuthTokenStore,
) : AccessTokenProvider {
    private val refreshMutex = Mutex()

    override suspend fun getValidAccessToken(): String? =
        refreshMutex.withLock {
            val tokens = tokenStore.read()
                ?: return@withLock null

            val now = System.currentTimeMillis().seconds

            if (tokens.expiresAtEpochSeconds.seconds > now + 2.minutes) {
                tokens.accessToken
            } else {
                val refreshed = try {
                    api.refreshToken(tokens.refreshToken)
                } catch (e: OAuthException) {
                    if (e is OAuthException.InvalidGrant) {
                        tokenStore.clear()
                    }
                    throw e
                }

                try {
                    tokenStore.save(refreshed)
                } catch (e: TokenStoreException) {
                    throw OAuthException.InvalidTokensStoreData(
                        message = e.message.toString(),
                        cause = e.cause
                    )
                }

                refreshed.accessToken
            }
        }
}