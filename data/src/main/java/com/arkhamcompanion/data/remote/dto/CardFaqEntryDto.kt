package com.arkhamcompanion.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CardFaqEntryDto(
    val id: Int,
    val question: String,
    val ruling: String,
    val citation: String,
    val position: Int,
)
