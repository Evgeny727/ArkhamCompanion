package com.arkhamcompanion.data.local.cards

import androidx.room3.ColumnInfo
import androidx.room3.Embedded
import kotlinx.serialization.json.JsonElement

data class CardSearchResultEntity(
    //QL fields
    @Embedded
    val front: CardSearchQLFields,
    @Embedded(prefix = "back_")
    val back: CardSearchQLFields?,

    //Search fields
    @Embedded
    val searchFields: CardSearchFields,
    @Embedded(prefix = "back_")
    val searchFieldsBack: CardSearchFields?,
)

data class CardSearchQLFields(
    val backIllustrator: String?,
    @ColumnInfo("back_type")
    val backType: String,
    val chapter: Int?,
    val clues: Int?,
    val code: String,
    val cost: Int?,
    @ColumnInfo("cycle_code")
    val cycleCode: String,
    val cycleName: String,
    val cycleRealName: String,
    @ColumnInfo("deck_limit")
    val deckLimit: Int?,
    val doom: Int?,
    @ColumnInfo("encounter_code")
    val encounterCode: String?,
    val encounterName: String?,
    val encounterRealName: String?,
    @ColumnInfo("enemy_damage")
    val enemyDamage: Int?,
    @ColumnInfo("enemy_horror")
    val enemyHorror: Int?,
    @ColumnInfo("enemy_fight")
    val enemyFight: Int?,
    @ColumnInfo("enemy_evade")
    val enemyEvade: Int?,
    val exceptional: Boolean,
    val exile: Boolean,
    @ColumnInfo("faction_code")
    val factionCode: String,
    val factionName: String,
    @ColumnInfo("faction2_code")
    val faction2Code: String?,
    val faction2Name: String?,
    @ColumnInfo("faction3_code")
    val faction3Code: String?,
    val faction3Name: String?,
    val health: Int?,
    val id: String,
    val illustrator: String?,
    @ColumnInfo("is_unique")
    val isUnique: Boolean,
    val myriad: Boolean,
    val official: Boolean,
    @ColumnInfo("pack_code")
    val packCode: String,
    val packName: String,
    val packRealName: String,
    val parallel: Boolean,
    val permanent: Boolean,
    @ColumnInfo("real_back_flavor")
    val realBackFlavor: String?,
    @ColumnInfo("real_back_name")
    val realBackName: String?,
    @ColumnInfo("real_back_subname")
    val realBackSubname: String?,
    @ColumnInfo("real_back_text")
    val realBackText: String?,
    @ColumnInfo("real_back_traits")
    val realBackTraits: String?,
    @ColumnInfo("real_customization_text")
    val realCustomizationText: String?,
    @ColumnInfo("real_flavor")
    val realFlavor: String?,
    @ColumnInfo("real_name")
    val realName: String,
    @ColumnInfo("real_slot")
    val realSlot: String?,
    @ColumnInfo("real_subname")
    val realSubname: String?,
    @ColumnInfo("real_text")
    val realText: String?,
    @ColumnInfo("real_traits")
    val realTraits: String?,
    val restrictions: JsonElement?,
    val sanity: Int?,
    val shroud: Int?,
    @Embedded
    val skills: Skills,
    val stage: Int?,
    @ColumnInfo("subtype_code")
    val subTypeCode: String?,
    val subTypeName: String?,
    val xp: Int?,
    val vengeance: Int?,
    val victory: Int?,
    val quantity: Int,
    @ColumnInfo("type_code")
    val typeCode: String,
    val typeName: String,

    //Taboo fields
    @ColumnInfo("taboo_xp")
    val tabooXp: Int?,
    @ColumnInfo("taboo_set_id")
    val tabooSetId: Int?,
    val tabooSetName: String?,
    @ColumnInfo("taboo_placeholder")
    val tabooPlaceholder: Boolean,


    //Translation
    @Embedded(prefix = "translation_")
    val translation: Translation,
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
