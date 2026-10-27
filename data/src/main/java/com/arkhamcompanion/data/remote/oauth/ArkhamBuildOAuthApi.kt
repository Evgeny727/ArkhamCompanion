package com.arkhamcompanion.data.remote.oauth

import com.arkhamcompanion.data.remote.api.OAuthApi
import com.arkhamcompanion.data.remote.dto.OAuthProfileResponseDto
import com.arkhamcompanion.data.remote.dto.OAuthTokenResponseDto
import com.arkhamcompanion.data.utils.bearerToken
import com.arkhamcompanion.data.utils.ensureSuccessful
import com.arkhamcompanion.data.utils.requireBody
import com.arkhamcompanion.domain.model.auth.ArkhamBuildUser
import com.arkhamcompanion.domain.model.auth.AuthorizationCodeGrant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ArkhamBuildOAuthApi @Inject constructor(private val api: OAuthApi) {

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
        val dto = api.getCurrentUser(authorization = accessToken.bearerToken).requireBody()

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
}