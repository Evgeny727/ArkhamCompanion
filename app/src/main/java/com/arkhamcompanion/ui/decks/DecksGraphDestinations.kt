package com.arkhamcompanion.ui.decks

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
object DecksFiltersScreen : NavKey

@Serializable
object DecksFiltersTagsSelectionScreen : NavKey

@Serializable
object DecksFiltersCardsSelectionScreen : NavKey

@Serializable
data class DecksDeckCreationScreen(
    val code: String,
    val parallel: Boolean
) : NavKey

@Serializable
data class DecksDeckCreationCardPoolSelection(
    val creationState: DecksDeckCreationScreen,
) : NavKey

@Serializable
data class DeckScreen(
    val id: String
) : NavKey