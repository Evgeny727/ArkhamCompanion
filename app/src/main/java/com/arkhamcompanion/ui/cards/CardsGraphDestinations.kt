package com.arkhamcompanion.ui.cards

import androidx.navigation3.runtime.NavKey
import com.arkhamcompanion.ui.navigation.cards.CardsGraphState
import kotlinx.serialization.Serializable

interface CardsGraph : NavKey {
    val parentConfig: CardsGraphState
}

@Serializable
data class CardsSortScreen(
    val spoilerState: Boolean
) : NavKey

@Serializable
data class CardsFiltersScreen(
    override val parentConfig: CardsGraphState
) : CardsGraph

@Serializable
data class CardsFiltersTypesScreen(
    override val parentConfig: CardsGraphState
) : CardsGraph

@Serializable
data class CardsFiltersSubTypesScreen(
    override val parentConfig: CardsGraphState
) : CardsGraph

@Serializable
data class CardsFiltersInvestigatorAccessScreen(
    override val parentConfig: CardsGraphState
) : CardsGraph

@Serializable
data class CardsFiltersCardsAccessScreen(
    override val parentConfig: CardsGraphState
) : CardsGraph

@Serializable
data class CardsFiltersActionsScreen(
    override val parentConfig: CardsGraphState
) : CardsGraph

@Serializable
data class CardsFiltersTraitsScreen(
    override val parentConfig: CardsGraphState
) : CardsGraph

@Serializable
data class CardsFiltersSlotsScreen(
    override val parentConfig: CardsGraphState
) : CardsGraph

@Serializable
data class CardsFiltersUsesScreen(
    override val parentConfig: CardsGraphState
) : CardsGraph

@Serializable
data class CardsFiltersAssetsScreen(
    override val parentConfig: CardsGraphState
) : CardsGraph

@Serializable
data class CardsFiltersEnemiesScreen(
    override val parentConfig: CardsGraphState
) : CardsGraph

@Serializable
data class CardsFiltersLocationsScreen(
    override val parentConfig: CardsGraphState
) : CardsGraph

@Serializable
data class CardsFiltersEncountersScreen(
    override val parentConfig: CardsGraphState
) : CardsGraph

@Serializable
data class CardsFiltersPacksScreen(
    override val parentConfig: CardsGraphState
) : CardsGraph

@Serializable
data class CardsFiltersIllustratorsScreen(
    override val parentConfig: CardsGraphState
) : CardsGraph

@Serializable
data class CardDetailsScreen(
    override val parentConfig: CardsGraphState,
    val cardCode: String
) : CardsGraph

@Serializable
data class CardTabooHistoryScreen(
    val cardCode: String,
    val cardName: String
) : NavKey

@Serializable
data class CardFaqScreen(
    val cardCode: String,
    val cardName: String,
    val tabooSetId: Int?
) : NavKey

@Serializable
data class StandaloneCardDetailsScreen(
    val cardCode: String,
    val tabooSetId: Int?
) : NavKey