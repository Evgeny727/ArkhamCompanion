package com.arkhamcompanion.data.remote.oauth

internal data class StoredOAuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val expiresAtEpochSeconds: Long,
    val scope: String,
)
