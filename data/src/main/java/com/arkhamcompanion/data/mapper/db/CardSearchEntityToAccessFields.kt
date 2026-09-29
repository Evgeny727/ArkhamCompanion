package com.arkhamcompanion.data.mapper.db

import com.arkhamcompanion.data.local.cards.CardSearchQLFields
import com.arkhamcompanion.data.local.cards.InvestigatorAccessFields
import com.arkhamcompanion.data.mapper.domain.cards.toCustomizationOptions
import com.arkhamcompanion.data.mapper.domain.cards.toDeckOptions
import com.arkhamcompanion.data.mapper.domain.cards.toDeckRequirements
import com.arkhamcompanion.data.mapper.domain.cards.toRestrictions
import com.arkhamcompanion.domain.enums.CardSubType
import com.arkhamcompanion.domain.enums.CardType
import com.arkhamcompanion.domain.enums.Faction

internal fun CardSearchQLFields.toAccessFields(): InvestigatorAccessFields = InvestigatorAccessFields(
    code = code,
    alternateOfCode = alternateOfCode,
    duplicateOfCode = duplicateOfCode,
    name = translation.name,
    realTraits = realTraits?.split(".")?.map { it.trim().lowercase() }.orEmpty().toSet(),
    customizationOptions = customizationOptions?.toCustomizationOptions(),
    deckOptions = deckOptions?.toDeckOptions(),
    deckRequirements = deckRequirements?.toDeckRequirements(),
    sideDeckOptions = sideDeckOptions?.toDeckOptions(),
    sideDeckRequirements = sideDeckRequirements?.toDeckRequirements(),
    restrictions = restrictions?.toRestrictions(),
    faction = Faction.byFaction(factionCode),
    faction2 = faction2Code?.let { Faction.byFaction(it) },
    faction3 = faction3Code?.let { Faction.byFaction(it) },
    type = CardType.byType(typeCode),
    subType = subTypeCode?.let { CardSubType.bySubType(it) },
    xp = xp,
    permanent = permanent,
    realText = realText,
    realBackText = realBackText,
    realCustomizationText = realCustomizationText
)