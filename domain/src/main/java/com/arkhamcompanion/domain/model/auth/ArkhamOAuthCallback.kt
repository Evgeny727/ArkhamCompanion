package com.arkhamcompanion.domain.model.auth

sealed interface ArkhamOAuthCallback {
    data class Authorized(val grant: AuthorizationCodeGrant) : ArkhamOAuthCallback

    data class Failed(val errorCode: String?) : ArkhamOAuthCallback

    data object Cancelled : ArkhamOAuthCallback
}

object ArkhamOAuthCallbackActions {
    const val COMPLETED =
        "com.arkhamcompanion.oauth.ACTION_COMPLETED"

    const val CANCELLED =
        "com.arkhamcompanion.oauth.ACTION_CANCELLED"
}

/**
 * App-layer adapter supplies its Activity's class name.
 * No Android SDK types are exposed through this contract.
 */
interface OAuthCallbackTargetProvider {
    fun activityClassName(): String
}