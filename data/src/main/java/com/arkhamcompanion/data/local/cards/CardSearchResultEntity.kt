package com.arkhamcompanion.data.local.cards

import androidx.room3.ColumnInfo
import androidx.room3.Embedded

data class CardSearchResultEntity(
    val id: String,
    val code: String,
    @ColumnInfo(name = "taboo_set_id")
    val tabooSetId: Int?,

    //Search fields
    @Embedded
    val searchFields: CardSearchFields,
    @Embedded(prefix = "back_")
    val searchFieldsBack: CardSearchFields?,
)

data class CardSearchFields(
    @ColumnInfo("search_name")
    val searchName: String,
    @ColumnInfo("search_name_back")
    val searchNameBack: String,
    @ColumnInfo("search_game")
    val searchGame: String,
    @ColumnInfo("search_game_back")
    val searchGameBack: String,
    @ColumnInfo("search_flavor")
    val searchFlavor: String,
    @ColumnInfo("search_flavor_back")
    val searchFlavorBack: String,
    @ColumnInfo("search_real_name")
    val searchRealName: String?,
    @ColumnInfo("search_real_name_back")
    val searchRealNameBack: String?,
    @ColumnInfo("search_real_game")
    val searchRealGame: String?,
    @ColumnInfo("search_real_game_back")
    val searchRealGameBack: String?,
    @ColumnInfo("search_real_flavor")
    val searchRealFlavor: String?,
    @ColumnInfo("search_real_flavor_back")
    val searchRealFlavorBack: String?,
)
