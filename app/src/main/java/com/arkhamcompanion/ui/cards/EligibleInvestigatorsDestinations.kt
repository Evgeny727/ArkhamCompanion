package com.arkhamcompanion.ui.cards

import kotlinx.serialization.Serializable

@Serializable
object EligibleInvestigators

@Serializable
object EligibleInvestigatorsSortScreen

@Serializable
object EligibleInvestigatorsFiltersScreen

@Serializable
object EligibleInvestigatorsFiltersCardsAccessScreen

@Serializable
object EligibleInvestigatorsFiltersActionsScreen

@Serializable
object EligibleInvestigatorsFiltersTraitsScreen

@Serializable
object EligibleInvestigatorsFiltersPacksScreen

@Serializable
object EligibleInvestigatorsFiltersIllustratorsScreen

@Serializable
data class EligibleInvestigatorDetailsScreen(
    val cardCode: String
)

@Serializable
data class EligibleInvestigatorsTabooHistoryScreen(
    val cardCode: String,
    val cardName: String
)