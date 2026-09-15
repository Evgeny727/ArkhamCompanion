package com.arkhamcompanion.data.local.cards

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "favorite_card")
data class FavoriteCardEntity(
    @PrimaryKey
    val code: String
)
