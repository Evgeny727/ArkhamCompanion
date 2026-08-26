package com.arkhamcompanion.ui.cards

import kotlinx.serialization.Serializable

@Serializable
object Cards

@Serializable
object CardsSortScreen

@Serializable
object CardsFiltersScreen

@Serializable
object CardsFiltersTypesScreen

@Serializable
object CardsFiltersSubTypesScreen

@Serializable
object CardsFiltersActionsScreen

@Serializable
object CardsFiltersTraitsScreen

@Serializable
object CardsFiltersSlotsScreen

@Serializable
object CardsFiltersUsesScreen

@Serializable
object CardsFiltersAssetsScreen

@Serializable
object CardsFiltersEnemiesScreen

@Serializable
object CardsFiltersLocationsScreen

@Serializable
object CardsFiltersEncountersScreen

@Serializable
object CardsFiltersPacksScreen

@Serializable
object CardsFiltersIllustratorsScreen

@Serializable
data class CardDetailsScreen(
    val cardCode: String
)