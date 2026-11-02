package com.arkhamcompanion.data.local.decks

import androidx.room3.ColumnInfo

data class DeckIdWithTimestamp(
    val id: String,
    @ColumnInfo("date_update")
    val dateUpdate: String,
    val source: String,
)