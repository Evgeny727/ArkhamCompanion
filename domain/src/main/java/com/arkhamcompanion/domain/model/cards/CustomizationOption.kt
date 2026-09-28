package com.arkhamcompanion.domain.model.cards

data class CustomizationOption(
    val card: CustomizationOptionCard?,
    val choice: CustomizationChoice?,
    val cost: Int?,
    val deckLimit: Int?,
    val health: Int?,
    val position: Int?,
    val quantity: Int?,
    val realSlot: String?,
    val realText: String?,
    val realTraits: String?,
    val sanity: Int?,
    val tags: List<String>,
    val textChange: CustomizationTextChange?,
    val xp: Int
)

data class CustomizationOptionCard(
    val type: List<String>,
    val trait: List<String>,
)

enum class CustomizationChoice(val code: String) {
    ChooseCard("choose_card"),
    ChooseTrait("choose_trait"),
    RemoveSlot("remove_slot"),
    ChooseSkill("choose_skill");

    companion object {
        fun byCode(code: String?): CustomizationChoice? {
            return entries.firstOrNull { it.code == code }
        }
    }
}

enum class CustomizationTextChange(val code: String) {
    Append("append"),
    Insert("insert"),
    Replace("replace"),
    Trait("trait");

    companion object {
        fun byCode(code: String?): CustomizationTextChange? {
            return entries.firstOrNull { it.code == code }
        }
    }
}