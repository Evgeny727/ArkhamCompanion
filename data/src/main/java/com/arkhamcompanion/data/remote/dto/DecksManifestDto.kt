package com.arkhamcompanion.data.remote.dto

import com.arkhamcompanion.domain.enums.DeckSource
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
data class DecksManifestDto(
    val version: String,
    val arkhamdbSyncToken: String? = null,
    val decks: List<DeckIdsDto>
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("source")
sealed interface DeckIdsDto {
    val id: Any
    val updatedAt: String
    val version: String

    val source: DeckSource
        get() = when (this) {
            is AccountDeckDto -> DeckSource.Account
            is ArkhamDbDeckDto -> DeckSource.Arkhamdb
        }

    @Serializable
    @SerialName("account")
    data class AccountDeckDto(
        override val id: String,
        override val updatedAt: String,
        override val version: String
    ) : DeckIdsDto

    @Serializable
    @SerialName("arkhamdb")
    data class ArkhamDbDeckDto(
        override val id: Long,
        override val updatedAt: String,
        override val version: String
    ) : DeckIdsDto
}

internal fun DeckIdsDto.toRequestDto(): DeckReferenceDto =
    DeckReferenceDto(
        source = source.name.lowercase(),
        id = id.toString()
    )
