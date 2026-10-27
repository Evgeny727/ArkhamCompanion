package com.arkhamcompanion.data.objects

import com.arkhamcompanion.data.BuildConfig

object ArkhamBuildOAuthConfig {
    const val API_BASE_URL = "https://api.arkham.build/"

    const val AUTHORIZATION_ENDPOINT = "v2/oauth/authorize"

    const val TOKEN_ENDPOINT = "v2/oauth/token"

    const val REVOKE_ENDPOINT = "v2/oauth/revoke"

    const val USER_ENDPOINT = "v2/user/me"

    // Full requested permissions.
    const val SCOPES = "profile:read decks:delete"

    val clientId: String
        get() = BuildConfig.ARKHAM_CLIENT_ID

    val clientSecret: String
        get() = BuildConfig.ARKHAM_CLIENT_SECRET

    val redirectUri: String
        get() = BuildConfig.ARKHAM_REDIRECT_URI
}