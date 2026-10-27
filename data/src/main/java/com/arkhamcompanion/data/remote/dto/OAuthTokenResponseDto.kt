package com.arkhamcompanion.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OAuthTokenResponseDto(
    @SerialName("access_token")
    val accessToken: String,

    @SerialName("expires_in")
    val expiresIn: Long,

    @SerialName("refresh_token")
    val refreshToken: String,

    val scope: String,
)