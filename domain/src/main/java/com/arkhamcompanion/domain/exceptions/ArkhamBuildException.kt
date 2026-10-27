package com.arkhamcompanion.domain.exceptions

import com.arkhamcompanion.domain.exceptions.ArkhamBuildException.AccountBanned
import com.arkhamcompanion.domain.exceptions.ArkhamBuildException.Conflict
import com.arkhamcompanion.domain.exceptions.ArkhamBuildException.InsufficientScope
import com.arkhamcompanion.domain.exceptions.ArkhamBuildException.InvalidRequest
import com.arkhamcompanion.domain.exceptions.ArkhamBuildException.InvalidToken
import com.arkhamcompanion.domain.exceptions.ArkhamBuildException.NotFound
import com.arkhamcompanion.domain.exceptions.ArkhamBuildException.UpstreamUnavailable

sealed class ArkhamBuildException(override val message: String) : RuntimeException() {

    data class InvalidToken(
        override val message: String
    ) : ArkhamBuildException(message)

    data class InsufficientScope(
        override val message: String
    ) : ArkhamBuildException(message)

    data class AccountBanned(
        override val message: String
    ) : ArkhamBuildException(message)

    data class InvalidRequest(
        override val message: String
    ) : ArkhamBuildException(message)

    data class NotFound(
        override val message: String
    ) : ArkhamBuildException(message)

    data class Conflict(
        override val message: String
    ) : ArkhamBuildException(message)

    data class UpstreamUnavailable(
        override val message: String
    ) : ArkhamBuildException(message)

    data class EmptyBody(
        override val message: String
    ) : ArkhamBuildException(message)
}

fun getArkhamBuildErrorByType(type: String?, message: String): ArkhamBuildException? = when (type) {
    "invalid_token" -> InvalidToken(message)
    "insufficient_scope" -> InsufficientScope(message)
    "account_banned" -> AccountBanned(message)
    "invalid_request" -> InvalidRequest(message)
    "not_found" -> NotFound(message)
    "conflict" -> Conflict(message)
    "upstream_unavailable" -> UpstreamUnavailable(message)
    else -> null
}