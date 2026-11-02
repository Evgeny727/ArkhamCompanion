package com.arkhamcompanion.data.remote.dto

import com.arkhamcompanion.data.objects.FlexibleIdSerializer
import kotlinx.serialization.Serializable

@Serializable
data class DeckBatchRequestDto(
    val arkhamdbSyncToken: String?,
    val decks: List<DeckReferenceDto>
)

@Serializable
data class DeckReferenceDto(
    val source: String,
    @Serializable(with = FlexibleIdSerializer::class)
    val id: String
)