package com.arkhamcompanion.domain.model.cards

import com.arkhamcompanion.domain.enums.CardBackType
import com.arkhamcompanion.domain.enums.CardSubType
import com.arkhamcompanion.domain.enums.CardType
import com.arkhamcompanion.domain.enums.Faction
import kotlinx.collections.immutable.ImmutableList

data class CardDetails(
    val id: String,
    override val code: String,

    //translated fields
    val backName: String?,
    val backSubname: String?,
    val backTraits: String?,
    override val name: String,
    val slot: String?,
    val subname: String?,
    val traits: String?,

    val backIllustrator: String?,
    val backType: CardBackType,
    val clues: Int?,
    val cluesFixed: Boolean,
    val cost: String?,
    val doom: Int?,
    val doomPerInvestigator: Boolean,
    val doubleSided: Boolean,
    override val duplicateOfCode: String?,
    val deckLimit: Int?,
    val encounterCode: String?,
    val encounterPosition: Int?,
    val encounterName: String?,
    val enemyDamage: Int?,
    val enemyHorror: Int?,
    val enemyFight: Int?,
    val enemyFightPerInvestigator: Boolean,
    val enemyEvade: Int?,
    val enemyEvadePerInvestigator: Boolean,
    override val faction: Faction,
    override val faction2: Faction?,
    override val faction3: Faction?,
    val health: Int?,
    val healthPerInvestigator: Boolean,
    val illustrator: String?,
    val isFavorite: Boolean,
    val isUnique: Boolean,
    val official: Boolean,
    val packCode: String,
    val packName: String,
    val packPosition: Int,
    val parallel: Boolean,
    override val permanent: Boolean,
    val reprintPackCode: String?,
    val reprintPackName: String?,
    val realSlot: String?,
    override val realTraits: Set<String>,
    val sanity: Int?,
    val shroud: Int?,
    val shroudPerInvestigator: Boolean,
    val skillWillpower: Int?,
    val skillIntellect: Int?,
    val skillCombat: Int?,
    val skillAgility: Int?,
    val skillWild: Int?,
    val stage: Int?,
    override val subType: CardSubType?,
    val subTypeName: String?,
    override val xp: Int?,
    val vengeance: Int?,
    val victory: Int?,
    val quantity: Int,
    override val type: CardType,
    val typeName: String,

    //image fields
    val thumbnailUrl: String?,
    val backThumbnailUrl: String?,
    val imageUrl: String?,
    val backImageUrl: String?,

    //Taboo fields
    val tabooSetId: String?,
    val tabooXp: Int?,
    val tabooPlaceholder: Boolean,

    //Parsed fields
    val parsedBackFlavor: CardText?,
    val parsedBackText: CardText?,
    val parsedFlavor: CardText?,
    val parsedText: CardText?,
    val parsedCustomizationText: CardText?,
    val parsedTabooOriginalBackText: CardText?,
    val parsedTabooOriginalText: CardText?,

    //Linked Back info
    val backInfo: CardBackInfo?,

    //Investigator access fields
    override val alternateOfCode: String?,
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

data class CardPackInfo(
    val code: String,
    val reprintCode: String?,
    val name: String,
    val reprintName: String?,
    val quantity: Int,
    val position: Int,
)

data class CardBackInfo(
    val code: String,
    val type: CardType,
    val tabooSetId: String?,
    val tabooPlaceholder: Boolean,
    val imageUrl: String?
)

const val ARKHAM_BUILD_BASE_IMAGE_URL = "https://cdn.arkham.build/"