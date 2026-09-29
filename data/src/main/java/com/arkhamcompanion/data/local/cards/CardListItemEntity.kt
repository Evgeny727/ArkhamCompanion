package com.arkhamcompanion.data.local.cards

import androidx.room3.ColumnInfo
import androidx.room3.Embedded
import kotlinx.serialization.json.JsonElement

data class CardListItemEntity(
    val id: String,
    val code: String,
    val thumbnailurl: String?,

    //Cost
    val cost: Int?,
    val xp: Int?,
    val permanent: Boolean,

    //Taboo
    @ColumnInfo(name = "taboo_xp")
    val tabooXp: Int?,
    @ColumnInfo(name = "taboo_set_id")
    val tabooSetId: Int?,
    @ColumnInfo(name = "taboo_placeholder")
    val tabooPlaceholder: Boolean,

    //Type
    @ColumnInfo(name = "type_code")
    val typeCode: String,
    val typeName: String,
    @ColumnInfo(name = "sort_by_type")
    val typeNumber: Int,
    @ColumnInfo(name = "subtype_code")
    val subTypeCode: String?,
    val subTypeName: String?,

    //Faction
    @ColumnInfo(name = "faction_code")
    val factionCode: String,
    val factionName: String,
    @ColumnInfo(name = "faction2_code")
    val faction2Code: String?,
    @ColumnInfo(name = "faction3_code")
    val faction3Code: String?,
    @ColumnInfo(name = "sort_by_faction")
    val factionNumber: Int,

    //Pack + Encounter + Cycle
    @ColumnInfo(name = "pack_code")
    val packCode: String,
    val packName: String,
    @ColumnInfo(name = "pack_position")
    val packPosition: Int,
    @ColumnInfo(name = "encounter_code")
    val encounterCode: String?,
    val encounterName: String?,
    @ColumnInfo(name = "cycle_code")
    val cycleCode: String,
    val cycleName: String,
    val cyclePosition: Int,
    @ColumnInfo(name = "reprint_pack_code")
    val reprintPackCode: String?,

    //Name
    val name: String,
    val subname: String?,

    //Skill
    @Embedded
    val skills: Skills,

    val parallel: Boolean,
    @ColumnInfo(name = "is_unique")
    val isUnique: Boolean,
    val slot: String?,
    @ColumnInfo(name = "sort_by_slot")
    val slotNumber: Int,
    val stage: Int?,

    //Fields for investigator access
    @ColumnInfo(name = "alternate_of_code")
    val alternateOfCode: String?,
    @ColumnInfo(name = "duplicate_of_code")
    val duplicateOfCode: String?,
    @ColumnInfo(name = "real_traits")
    val realTraits: String?,
    @ColumnInfo(name = "customization_options")
    val customizationOptions: JsonElement?,
    @ColumnInfo(name = "deck_options")
    val deckOptions: JsonElement?,
    @ColumnInfo(name = "deck_requirements")
    val deckRequirements: JsonElement?,
    @ColumnInfo(name = "side_deck_options")
    val sideDeckOptions: JsonElement?,
    @ColumnInfo(name = "side_deck_requirements")
    val sideDeckRequirements: JsonElement?,
    @ColumnInfo(name = "restrictions")
    val restrictions: JsonElement?,
    @ColumnInfo(name = "real_text")
    val realText: String?,
    @ColumnInfo(name = "real_back_text")
    val realBackText: String?,
    @ColumnInfo(name = "real_customization_text")
    val realCustomizationText: String?,
)