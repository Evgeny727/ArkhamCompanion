package com.arkhamcompanion.data.local.cards

import androidx.room3.ColumnInfo

data class CardDetailsEntity(
    val id: String,
    val code: String,

    //translated fields
    @ColumnInfo("back_flavor")
    val backFlavor: String?,
    @ColumnInfo("back_name")
    val backName: String?,
    @ColumnInfo("back_subname")
    val backSubname: String?,
    @ColumnInfo("back_text")
    val backText: String?,
    @ColumnInfo("back_traits")
    val backTraits: String?,
    @ColumnInfo("customization_text")
    val customizationText: String?,
    val flavor: String?,
    val name: String,
    val slot: String?,
    val subname: String?,
    @ColumnInfo("taboo_original_back_text")
    val tabooOriginalBackText: String?,
    @ColumnInfo("taboo_original_text")
    val tabooOriginalText: String?,
    val text: String?,
    val traits: String?,

    @ColumnInfo("back_illustrator")
    val backIllustrator: String?,
    @ColumnInfo("back_type")
    val backType: String,
    val clues: Int?,
    @ColumnInfo("clues_fixed")
    val cluesFixed: Boolean,
    val cost: Int?,
    val doom: Int?,
    @ColumnInfo("doom_per_investigator")
    val doomPerInvestigator: Boolean,
    @ColumnInfo("double_sided")
    val doubleSided: Boolean,
    @ColumnInfo("duplicate_of_code")
    val duplicateOfCode: String?,
    @ColumnInfo("deck_limit")
    val deckLimit: Int?,
    @ColumnInfo("encounter_code")
    val encounterCode: String?,
    @ColumnInfo("encounter_position")
    val encounterPosition: Int?,
    val encounterName: String?,
    @ColumnInfo("enemy_damage")
    val enemyDamage: Int?,
    @ColumnInfo("enemy_horror")
    val enemyHorror: Int?,
    @ColumnInfo("enemy_fight")
    val enemyFight: Int?,
    @ColumnInfo("enemy_fight_per_investigator")
    val enemyFightPerInvestigator: Boolean,
    @ColumnInfo("enemy_evade")
    val enemyEvade: Int?,
    @ColumnInfo("enemy_evade_per_investigator")
    val enemyEvadePerInvestigator: Boolean,
    @ColumnInfo("faction_code")
    val factionCode: String,
    @ColumnInfo("faction2_code")
    val faction2Code: String?,
    @ColumnInfo("faction3_code")
    val faction3Code: String?,
    val health: Int?,
    @ColumnInfo("health_per_investigator")
    val healthPerInvestigator: Boolean,
    val illustrator: String?,
    val isFavorite: Boolean,
    @ColumnInfo("is_unique")
    val isUnique: Boolean,
    val official: Boolean,
    @ColumnInfo("pack_code")
    val packCode: String,
    val packName: String,
    @ColumnInfo("pack_position")
    val packPosition: Int,
    val parallel: Boolean,
    val permanent: Boolean,
    @ColumnInfo("reprint_pack_code")
    val reprintPackCode: String?,
    val reprintPackName: String?,
    @ColumnInfo("real_slot")
    val realSlot: String?,
    val sanity: Int?,
    val shroud: Int?,
    @ColumnInfo("shroud_per_investigator")
    val shroudPerInvestigator: Boolean,
    @ColumnInfo("skill_willpower")
    val skillWillpower: Int?,
    @ColumnInfo("skill_intellect")
    val skillIntellect: Int?,
    @ColumnInfo("skill_combat")
    val skillCombat: Int?,
    @ColumnInfo("skill_agility")
    val skillAgility: Int?,
    @ColumnInfo("skill_wild")
    val skillWild: Int?,
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

    //image fields
    val thumbnailurl: String?,
    val backthumbnailurl: String?,
    val imageurl: String?,
    val backimageurl: String?,

    //Taboo fields
    @ColumnInfo("taboo_set_id")
    val tabooSetId: String?,
    @ColumnInfo("taboo_xp")
    val tabooXp: Int?,
    @ColumnInfo("taboo_placeholder")
    val tabooPlaceholder: Boolean,
)
