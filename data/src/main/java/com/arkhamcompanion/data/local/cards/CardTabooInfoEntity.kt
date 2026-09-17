package com.arkhamcompanion.data.local.cards

import androidx.room3.ColumnInfo

data class CardTabooInfoEntity(
    @ColumnInfo("taboo_xp")
    val tabooXp: Int?,
    @ColumnInfo("taboo_text_change")
    val tabooTextChange: String?,
    val tabooName: String?,
    val tabooDate: String?,
    @ColumnInfo("taboo_placeholder")
    val tabooPlaceholder: Boolean,
)
