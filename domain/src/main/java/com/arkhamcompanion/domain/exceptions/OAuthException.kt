package com.arkhamcompanion.domain.exceptions

import com.arkhamcompanion.domain.exceptions.OAuthException.InvalidClient
import com.arkhamcompanion.domain.exceptions.OAuthException.InvalidGrant
import com.arkhamcompanion.domain.exceptions.OAuthException.InvalidRequest
import com.arkhamcompanion.domain.exceptions.OAuthException.InvalidScope
import com.arkhamcompanion.domain.exceptions.OAuthException.UnauthorizedClient
import com.arkhamcompanion.domain.exceptions.OAuthException.UnsupportedGrantType
import com.arkhamcompanion.domain.exceptions.OAuthException.UnsupportedResponseType

sealed class OAuthException(override val message: String) : RuntimeException() {

    data class InvalidRequest(
        override val message: String
    ) : OAuthException(message)

    data class InvalidClient(
        override val message: String
    ) : OAuthException(message)

    data class InvalidGrant(
        override val message: String
    ) : OAuthException(message)

    data class InvalidScope(
        override val message: String
    ) : OAuthException(message)

    data class UnauthorizedClient(
        override val message: String
    ) : OAuthException(message)

    data class UnsupportedGrantType(
        override val message: String
    ) : OAuthException(message)

    data class UnsupportedResponseType(
        override val message: String
    ) : OAuthException(message)

    data class InvalidTokensStoreData(
        override val message: String,
        override val cause: Throwable?
    ) : OAuthException(message)

    data class InvalidOAuthTransactionException(
        override val message: String = "Invalid or expired OAuth transaction"
    ) : OAuthException(message)
}

fun getRemoteOAuthErrorByType(type: String?, message: String): OAuthException? = when (type) {
    "invalid_request" -> InvalidRequest(message)
    "invalid_client" -> InvalidClient(message)
    "invalid_grant" -> InvalidGrant(message)
    "invalid_scope" -> InvalidScope(message)
    "unauthorized_client" -> UnauthorizedClient(message)
    "unsupported_grant_type" -> UnsupportedGrantType(message)
    "unsupported_response_type" -> UnsupportedResponseType(message)
    else -> null
}