package com.arkhamcompanion.data.local.decks

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import kotlinx.serialization.json.JsonElement

@Entity(tableName = "deck")
data class DeckEntity(
    @ColumnInfo("date_creation")
    val dateCreation: String,

    @ColumnInfo("date_update")
    val dateUpdate: String,

    @ColumnInfo("description_md")
    val descriptionMd: String,

    @ColumnInfo("exile_string")
    val exileString: String? = null,

    @ColumnInfo("ignore_deck_limit_slots")
    val ignoreDeckLimitSlots: JsonElement? = null,

    @PrimaryKey val id: String,

    @ColumnInfo("investigator_code")
    val investigatorCode: String,

    @ColumnInfo("alternate_front_code")
    val alternateFrontCode: String? = null,

    @ColumnInfo("alternate_back_code")
    val alternateBackCode: String? = null,

    val meta: JsonElement,

    val name: String,

    @ColumnInfo("next_deck")
    val nextDeck: String? = null,

    @ColumnInfo("previous_deck")
    val previousDeck: String? = null,

    val problem: String? = null,

    @ColumnInfo("side_slots")
    val sideSlots: JsonElement? = null,

    val slots: JsonElement,

    @ColumnInfo("extra_deck")
    val extraDeck: String?,

    @ColumnInfo("card_pool")
    val cardPool: String?,

    val source: String? = null,

    @ColumnInfo("taboo_id")
    val tabooId: Int? = null,

    val tags: String,

    @ColumnInfo("user_id")
    val userId: Long? = null,

    val version: String,

    @ColumnInfo("xp_adjustment")
    val xpAdjustment: Int? = null,

    @ColumnInfo("xp_spent")
    val xpSpent: Int? = null,

    val xp: Int? = null,
)