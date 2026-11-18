package com.arkhamcompanion.data.remote.dto

import com.arkhamcompanion.data.objects.JsonStringSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class DeckWriteRequestDto(
    @SerialName("description_md")
    val descriptionMd: String = "",

    @SerialName("exile_string")
    val exileString: String? = null,

    val ignoreDeckLimitSlots: JsonObject? = null,

    @SerialName("investigator_code")
    val investigatorCode: String,

    @SerialName("investigator_name")
    val investigatorName: String? = null,

    @Serializable(with = JsonStringSerializer::class)
    val meta: JsonElement,

    val name: String,

    val problem: String? = null,

    val sideSlots: JsonObject? = null,

    val slots: JsonObject,

    @SerialName("taboo_id")
    val tabooId: Int?,

    val tags: String,

    @SerialName("xp_adjustment")
    val xpAdjustment: Int? = null,

    @SerialName("xp_spent")
    val xpSpent: Int? = null,

    val xp: Int? = null,
)