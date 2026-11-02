package com.arkhamcompanion.data.remote.dto

import com.arkhamcompanion.data.objects.FlexibleIdSerializer
import com.arkhamcompanion.data.objects.JsonStringSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class DecksDto(
    val decks: List<DeckDto>,
)

@Serializable
data class DeckDto(
    @SerialName("date_creation")
    val dateCreation: String,

    @SerialName("date_update")
    val dateUpdate: String,

    @SerialName("description_md")
    val descriptionMd: String,

    @SerialName("exile_string")
    val exileString: String? = null,

    val ignoreDeckLimitSlots: JsonObject? = null,

    @Serializable(with = FlexibleIdSerializer::class)
    val id: String,

    @SerialName("investigator_code")
    val investigatorCode: String,

    @Serializable(with = JsonStringSerializer::class)
    val meta: JsonElement,

    val name: String,

    @SerialName("next_deck")
    @Serializable(with = FlexibleIdSerializer::class)
    val nextDeck: String? = null,

    @SerialName("previous_deck")
    @Serializable(with = FlexibleIdSerializer::class)
    val previousDeck: String? = null,

    val problem: String? = null,

    val sideSlots: JsonObject? = null,

    val slots: JsonObject,

    val source: String? = null,

    @SerialName("taboo_id")
    val tabooId: Int? = null,

    val tags: String,

    @SerialName("user_id")
    val userId: Long? = null,

    val version: String,

    @SerialName("xp_adjustment")
    val xpAdjustment: Int? = null,

    @SerialName("xp_spent")
    val xpSpent: Int? = null,

    val xp: Int? = null,
)