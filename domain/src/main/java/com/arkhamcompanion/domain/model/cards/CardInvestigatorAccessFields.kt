package com.arkhamcompanion.domain.model.cards

import com.arkhamcompanion.domain.enums.CardSubType
import com.arkhamcompanion.domain.enums.CardType
import com.arkhamcompanion.domain.enums.Faction
import kotlinx.collections.immutable.ImmutableList

interface CardInvestigatorAccessFields {
    val code: String
    val alternateOfCode: String?
    val duplicateOfCode: String?
    val name: String
    val realTraits: Set<String>
    val customizationOptions: List<CustomizationOption>?
    val deckOptions: ImmutableList<DeckOption>?
    val deckRequirements: DeckRequirements?
    val sideDeckOptions: ImmutableList<DeckOption>?
    val sideDeckRequirements: DeckRequirements?
    val restrictions: Restrictions?
    val faction: Faction
    val faction2: Faction?
    val faction3: Faction?
    val type: CardType
    val subType: CardSubType?
    val xp: Int?
    val permanent: Boolean
    val realText: String?
    val realBackText: String?
    val realCustomizationText: String?
}
