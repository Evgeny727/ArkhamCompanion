package com.arkhamcompanion.ui.navigation.cards

import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableSet

sealed interface CardsGraphState {
    data object Main : CardsGraphState

    data class Investigator(val investigatorId: String, val parallelCode: String?) : CardsGraphState

    data class Card(val cardId: String) : CardsGraphState
}

//Only filters that differs between graphs
enum class CardsGraphFilter {
    LevelFilter,
    TypeFilter,
    SubTypeFilter,
    InvestigatorAccessFilter,
    CardsAccessFilter,
    CostFiler,
    SkillsFilter,
    AssetFilter,
    EnemyFilter,
    LocationFilter,
    EncounterSetFilter,
}

val MAIN_CARDS_GRAPH_FILTERS = CardsGraphFilter.entries.toImmutableSet()

val AVAILABLE_CARDS_GRAPH_FILTERS = MAIN_CARDS_GRAPH_FILTERS
    .minus(CardsGraphFilter.CardsAccessFilter)
    .toImmutableSet()

val ELIGIBLE_INVESTIGATORS_GRAPH_FILTERS = persistentSetOf(CardsGraphFilter.CardsAccessFilter)