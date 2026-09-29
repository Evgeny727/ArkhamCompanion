package com.arkhamcompanion.data.local.cards

import com.arkhamcompanion.domain.enums.CardSubType
import com.arkhamcompanion.domain.enums.CardType
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.domain.model.cards.CardInvestigatorAccessFields
import com.arkhamcompanion.domain.model.cards.CustomizationOption
import com.arkhamcompanion.domain.model.cards.DeckOption
import com.arkhamcompanion.domain.model.cards.DeckRequirements
import com.arkhamcompanion.domain.model.cards.Restrictions
import kotlinx.collections.immutable.ImmutableList

data class InvestigatorAccessFields(
    override val code: String,
    override val alternateOfCode: String?,
    override val duplicateOfCode: String?,
    override val name: String,
    override val realTraits: Set<String>,
    override val customizationOptions: List<CustomizationOption>?,
    override val deckOptions: ImmutableList<DeckOption>?,
    override val deckRequirements: DeckRequirements?,
    override val sideDeckOptions: ImmutableList<DeckOption>?,
    override val sideDeckRequirements: DeckRequirements?,
    override val restrictions: Restrictions?,
    override val faction: Faction,
    override val faction2: Faction?,
    override val faction3: Faction?,
    override val type: CardType,
    override val subType: CardSubType?,
    override val xp: Int?,
    override val permanent: Boolean,
    override val realText: String?,
    override val realBackText: String?,
    override val realCustomizationText: String?
) : CardInvestigatorAccessFields
