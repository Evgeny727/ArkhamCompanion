package com.arkhamcompanion.domain.model.cards

import com.arkhamcompanion.domain.enums.CardType
import com.arkhamcompanion.domain.enums.Faction
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class DeckOption(
    val atLeast: AtLeastOption? = null,
    val deckSizeSelect: ImmutableList<String> = persistentListOf(),
    val error: String? = null,
    val factionSelect: ImmutableList<Faction> = persistentListOf(),
    val faction: ImmutableList<Faction> = persistentListOf(),
    val id: String? = null,
    val level: IntRange = IntRange(0, 5),
    val limit: Int? = null,
    val name: String? = null,
    val not: Boolean = false,
    val optionSelect: ImmutableList<OptionSelect> = persistentListOf(),
    val permanent: Boolean? = null,
    val slot: ImmutableList<String> = persistentListOf(),
    val tag: ImmutableList<String> = persistentListOf(),
    val text: ImmutableList<Regex> = persistentListOf(),
    val trait: ImmutableList<String> = persistentListOf(),
    val type: ImmutableList<CardType> = persistentListOf(),
    val uses: ImmutableList<String> = persistentListOf(),
    val virtual: Boolean = false,
)

data class AtLeastOption(
    val factions: Int? = null,
    val min: Int,
    val types: Int? = null,
    val traits: Int? = null,
)

data class OptionSelect(
    val id: String,
    val level: IntRange = IntRange(0, 5),
    val name: String,
    val size: Int? = null,
    val trait: ImmutableList<String> = persistentListOf(),
    val type: ImmutableList<CardType> = persistentListOf(),
)
