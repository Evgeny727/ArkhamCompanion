package com.arkhamcompanion.data.local.cards.patches

import kotlinx.serialization.json.JsonElement

data class CardPatch(
    val code: String,
    val values: EntityPatch = EntityPatch(),
) {
    fun merge(other: CardPatch): CardPatch {
        require(code == other.code) { "Cannot merge patches for different cards: $code vs ${other.code}" }
        return copy(
            values = values.merge(other.values)
        )
    }
}

data class EntityPatch(
    val duplicateOf: PatchValue<String?> = PatchValue.Unset,
    val reprintOf: PatchValue<String?> = PatchValue.Unset,
    val preview: PatchValue<Boolean?> = PatchValue.Unset,
    val gameBeginAttribute: PatchValue<String?> = PatchValue.Unset,
    val hidden: PatchValue<Boolean?> = PatchValue.Unset,
    val backLinkId: PatchValue<String?> = PatchValue.Unset,
    val tags: PatchValue<JsonElement?> = PatchValue.Unset,
    val doomPerInvestigator: PatchValue<Boolean?> = PatchValue.Unset,
    val enemyEvadePerInvestigator: PatchValue<Boolean?> = PatchValue.Unset,
    val enemyFightPerInvestigator: PatchValue<Boolean?> = PatchValue.Unset,
    val shroudPerInvestigator: PatchValue<Boolean?> = PatchValue.Unset,
    val shroud: PatchValue<Int?> = PatchValue.Unset,
    val skillIntellect: PatchValue<Int?> = PatchValue.Unset,
    val deckLimit: PatchValue<Int?> = PatchValue.Unset,
    val backType: PatchValue<String?> = PatchValue.Unset,
    val deckOptions: PatchValue<JsonElement?> = PatchValue.Unset,
    val deckRequirements: PatchValue<JsonElement?> = PatchValue.Unset,
) {
    fun merge(other: EntityPatch): EntityPatch = EntityPatch(
        duplicateOf = duplicateOf.merge(other.duplicateOf),
        reprintOf = reprintOf.merge(other.reprintOf),
        gameBeginAttribute = gameBeginAttribute.merge(other.gameBeginAttribute),
        hidden = hidden.merge(other.hidden),
        preview = preview.merge(other.preview),
        backLinkId = backLinkId.merge(other.backLinkId),
        tags = tags.merge(other.tags),
    )
}