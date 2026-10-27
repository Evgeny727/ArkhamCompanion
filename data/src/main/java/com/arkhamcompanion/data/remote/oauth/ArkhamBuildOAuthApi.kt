package com.arkhamcompanion.data.remote.oauth

import com.arkhamcompanion.data.remote.api.OAuthApi
import com.arkhamcompanion.data.remote.dto.OAuthProfileResponseDto
import com.arkhamcompanion.data.remote.dto.OAuthTokenResponseDto
import com.arkhamcompanion.domain.exceptions.ArkhamBuildException
import com.arkhamcompanion.domain.exceptions.getArkhamBuildErrorByType
import com.arkhamcompanion.domain.exceptions.getRemoteOAuthErrorByType
import com.arkhamcompanion.domain.model.auth.ArkhamBuildUser
import com.arkhamcompanion.domain.model.auth.AuthorizationCodeGrant
import okio.IOException
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ArkhamBuildOAuthApi @Inject constructor(
    private val api: OAuthApi,
) {

    suspend fun exchangeCode(grant: AuthorizationCodeGrant): StoredOAuthTokens {
        // Capture the time before the request so the local expiry
        // estimate errs slightly on the safe side.
        val requestStartedAt = currentEpochSeconds()

        return api.exchangeAuthorizationCode(
            code = grant.code,
            codeVerifier = grant.codeVerifier,
        )
            .requireBody()
            .toStoredTokens(requestStartedAt)
    }

    suspend fun refreshToken(currentRefreshToken: String): StoredOAuthTokens {
        val requestStartedAt = currentEpochSeconds()

        return api.refreshAccessToken(refreshToken = currentRefreshToken)
            .requireBody()
            .toStoredTokens(requestStartedAt)
    }

    suspend fun getCurrentUser(accessToken: String): ArkhamBuildUser {
        val dto = api.getCurrentUser(
            authorization = "Bearer $accessToken",
        ).requireBody()

        return dto.toDomain()
    }

    suspend fun revokeRefreshToken(refreshToken: String) {
        api.revokeRefreshToken(refreshToken = refreshToken).ensureSuccessful()
    }

    private fun OAuthTokenResponseDto.toStoredTokens(requestStartedAt: Long): StoredOAuthTokens =
        StoredOAuthTokens(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresAtEpochSeconds = requestStartedAt + expiresIn,
            scope = scope,
        )

    private fun OAuthProfileResponseDto.toDomain(): ArkhamBuildUser =
        ArkhamBuildUser(
            id = id,
            username = username,
        )

    private fun currentEpochSeconds(): Long =
        System.currentTimeMillis() / 1_000

    private fun <T : Any> Response<T>.requireBody(): T {
        ensureSuccessful()

        return body() ?: throw ArkhamBuildException.EmptyBody(
            message = "Arkham Build returned an empty response"
        )
    }

    private fun Response<*>.ensureSuccessful() {
        if (isSuccessful) return

        val errorPayload = try {
            errorBody()?.string()?.let(::JSONObject)
        } catch (_: JSONException) {
            null
        } catch (_: IOException) {
            null
        }

        val oauthError = errorPayload.nonBlankField("error")

        val description = errorPayload
            .nonBlankField("error_description")
            ?: errorPayload.nonBlankField("message")
            ?: "Arkham.Build request failed (HTTP ${code()})"

        val exception = getRemoteOAuthErrorByType(oauthError, description) ?:
            getArkhamBuildErrorByType(oauthError, description)

        exception?.let { throw it }

        throw IllegalStateException("Arkham.Build request failed (HTTP ${code()})")
    }

    private fun JSONObject?.nonBlankField(name: String): String? {
        val value = this?.optString(name) ?: return null

        return value.takeIf {
            it.isNotBlank() && it != "null"
        }
    }
}