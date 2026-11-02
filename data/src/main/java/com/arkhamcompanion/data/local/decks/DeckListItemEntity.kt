package com.arkhamcompanion.data.local.decks

import androidx.room3.ColumnInfo
import kotlinx.serialization.json.JsonElement

data class DeckListItemEntity(
    val id: String,
    val name: String,
    val problem: String?,
    val source: String?,
    val tags: String,
    @ColumnInfo(name = "xp_spent")
    val xpSpent: Int?,
    @ColumnInfo(name = "xp_adjustment")
    val xpAdjustment: Int?,
    val xp: Int?,
    val totalXp: Int,
    @ColumnInfo(name = "extra_deck")
    val extraDeck: String?,
    val slots: JsonElement,
    @ColumnInfo(name = "card_pool")
    val cardPool: String?,

    //Investigator fields
    val investigatorName: String?,
    val investigatorParallel: Boolean?,
    val investigatorFaction: String?,
    val investigatorFaction2: String?,
    val investigatorFaction3: String?,
    val investigatorThumbnailUrl: String?,
    val investigatorImageUrl: String?
)
