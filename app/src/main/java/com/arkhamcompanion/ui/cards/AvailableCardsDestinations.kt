package com.arkhamcompanion.ui.cards

import kotlinx.serialization.Serializable

@Serializable
object AvailableCards

@Serializable
object AvailableCardsSortScreen

@Serializable
object AvailableCardsFiltersScreen

@Serializable
object AvailableCardsFiltersTypesScreen

@Serializable
object AvailableCardsFiltersSubTypesScreen

@Serializable
object AvailableCardsFiltersInvestigatorAccessScreen

@Serializable
object AvailableCardsFiltersActionsScreen

@Serializable
object AvailableCardsFiltersTraitsScreen

@Serializable
object AvailableCardsFiltersSlotsScreen

@Serializable
object AvailableCardsFiltersUsesScreen

@Serializable
object AvailableCardsFiltersAssetsScreen

@Serializable
object AvailableCardsFiltersEnemiesScreen

@Serializable
object AvailableCardsFiltersLocationsScreen

@Serializable
object AvailableCardsFiltersEncountersScreen

@Serializable
object AvailableCardsFiltersPacksScreen

@Serializable
object AvailableCardsFiltersIllustratorsScreen

@Serializable
data class AvailableCardDetailsScreen(
    val cardCode: String
)

@Serializable
data class AvailableCardTabooHistoryScreen(
    val cardCode: String,
    val cardName: String
)