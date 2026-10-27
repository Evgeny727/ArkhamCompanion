package com.arkhamcompanion.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class OAuthProfileResponseDto(
    val id: String,
    val username: String,
)