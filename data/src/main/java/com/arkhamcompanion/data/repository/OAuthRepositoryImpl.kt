package com.arkhamcompanion.data.repository

import com.arkhamcompanion.data.local.auth.ArkhamAuthorizationFlow
import com.arkhamcompanion.data.local.auth.OAuthTokenStore
import com.arkhamcompanion.data.local.auth.PendingAuthorizationStore
import com.arkhamcompanion.data.local.auth.TokenStoreException
import com.arkhamcompanion.data.remote.oauth.AccessTokenProvider
import com.arkhamcompanion.data.remote.oauth.ArkhamBuildOAuthApi
import com.arkhamcompanion.domain.exceptions.OAuthException
import com.arkhamcompanion.domain.model.auth.ArkhamBuildUser
import com.arkhamcompanion.domain.model.auth.AuthorizationCodeGrant
import com.arkhamcompanion.domain.repository.OAuthRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.openid.appauth.AuthorizationRequest
import javax.inject.Inject

class OAuthRepositoryImpl @Inject internal constructor(
    private val authorizationFlow: ArkhamAuthorizationFlow,
    private val api: ArkhamBuildOAuthApi,
    private val tokenStore: OAuthTokenStore,
    private val accessTokenProvider: AccessTokenProvider,
    private val pendingAuthorizationStore: PendingAuthorizationStore,
) : OAuthRepository {
    private val authorizationMutex = Mutex()

    override suspend fun startAuthorization() {
        authorizationFlow.startAuthorization()
    }

    override suspend fun completeAuthorization(grant: AuthorizationCodeGrant) = runCatching {
        authorizationMutex.withLock {
            val serializedRequest = pendingAuthorizationStore.readRequest()
                ?: throw OAuthException.InvalidOAuthTransactionException()

            val request = try {
                AuthorizationRequest.jsonDeserialize(serializedRequest)
            } catch (_: Exception) {
                pendingAuthorizationStore.clearRequest()

                throw OAuthException.InvalidOAuthTransactionException()
            }

            val expectedState = request.state
            val expectedVerifier = request.codeVerifier

            if (
                expectedState.isNullOrBlank() ||
                expectedVerifier.isNullOrBlank() ||
                (grant.state != expectedState) ||
                (grant.codeVerifier != expectedVerifier)
            ) {
                throw OAuthException.InvalidOAuthTransactionException()
            }

            /*
             * The authorization code is single-use. Consume this pending
             * transaction before the network exchange to prevent duplicate
             * callbacks from racing and exchanging the same code twice.
             */
            pendingAuthorizationStore.clearRequest()

            val tokens = api.exchangeCode(grant)

            // Persist both the access token and the rotated refresh token.
            try {
                tokenStore.save(tokens)
            } catch (e: TokenStoreException) {
                throw OAuthException.InvalidTokensStoreData(
                    message = e.message.toString(),
                    cause = e.cause
                )
            }
        }
    }

    override suspend fun cancelAuthorization() {
        pendingAuthorizationStore.clearRequest()
    }

    override suspend fun getCurrentUser(): Result<ArkhamBuildUser?> = runCatching {
        val accessToken = try {
            accessTokenProvider.getValidAccessToken() ?: return@runCatching null
        } catch (e: TokenStoreException) {
            throw OAuthException.InvalidTokensStoreData(
                message = e.message.toString(),
                cause = e.cause
            )
        }

        api.getCurrentUser(accessToken)
    }

    override suspend fun signOut() {
        try {
            val tokens = tokenStore.read() ?: return
            api.revokeRefreshToken(refreshToken = tokens.refreshToken)
        } finally {
            tokenStore.clear()
        }
    }
}