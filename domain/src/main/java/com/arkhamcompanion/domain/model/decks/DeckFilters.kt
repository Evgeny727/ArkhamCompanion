package com.arkhamcompanion.domain.model.decks

import com.arkhamcompanion.domain.enums.DeckSource
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.domain.model.cards.CardInvestigatorAccessFields
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf

data class DeckFilters(
    val factions: ImmutableSet<Faction> = persistentSetOf(),
    val cardsInDeck: ImmutableSet<CardInvestigatorAccessFields> = persistentSetOf(),
    val tags: ImmutableSet<String> = persistentSetOf(),
    val source: ImmutableSet<DeckSource> = persistentSetOf(),
    val alternateInvestigatorsOnly: Boolean = false,
)