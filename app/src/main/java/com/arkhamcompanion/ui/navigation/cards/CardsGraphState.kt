package com.arkhamcompanion.ui.navigation.cards

import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.serialization.Serializable

@Serializable
sealed interface CardsGraphState {
    @Serializable
    data object Main : CardsGraphState

    @Serializable
    data class Investigator(val investigatorId: String, val parallelCode: String?) : CardsGraphState

    @Serializable
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