package com.arkhamcompanion.domain.model.cards

import com.arkhamcompanion.domain.enums.CardSubType
import com.arkhamcompanion.domain.enums.CardType
import com.arkhamcompanion.domain.enums.Faction
import kotlinx.collections.immutable.ImmutableList

data class CardListItem(
    val id: String,
    override val code: String,
    val thumbnailUrl: String?,

    //Cost
    val realCost: String?,
    val cost:  Int?,
    override val xp: Int?,
    override val permanent: Boolean,

    //Taboo
    val tabooXp: Int?,
    val tabooSetId: Int?,
    val tabooPlaceholder: Boolean,

    //Type
    override val type: CardType,
    val typeName: String,
    val typeNumber: Int,
    override val subType: CardSubType?,
    val subTypeName: String?,

    //Faction
    override val faction: Faction,
    override val faction2: Faction?,
    override val faction3: Faction?,
    val factionNumber: Int,

    //Slot
    val slot: String?,
    val slotNumber: Int,

    //Pack
    val packCode: String,
    val packPosition: Int,
    val packName: String,
    val cycleCode: String,
    val cycleName: String,
    val cyclePosition: Int,
    val encounterCode: String?,
    val encounterName: String?,
    val reprintPackCode: String?,

    //Name
    override val name: String,
    val subname: String?,

    //Skill
    val skillWillpower: Int?,
    val skillIntellect: Int?,
    val skillCombat: Int?,
    val skillAgility: Int?,
    val skillWild: Int?,

    val parallel: Boolean,
    val isUnique: Boolean,
    val stage: Int?,

    //Fields for investigator access
    override val alternateOfCode: String?,
    override val duplicateOfCode: String?,
    override val realTraits: Set<String>,
    override val customizationOptions: List<CustomizationOption>?,
    override val deckOptions: ImmutableList<DeckOption>?,
    override val deckRequirements: DeckRequirements?,
    override val sideDeckOptions: ImmutableList<DeckOption>?,
    override val sideDeckRequirements: DeckRequirements?,
    override val restrictions: Restrictions?,
    override val realText: String?,
    override val realBackText: String?,
    override val realCustomizationText: String?,
) : CardInvestigatorAccessFields
