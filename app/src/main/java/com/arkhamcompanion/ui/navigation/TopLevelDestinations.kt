package com.arkhamcompanion.ui.navigation

import androidx.annotation.StringRes
import androidx.navigation3.runtime.NavKey
import com.arkhamcompanion.R
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.icons.IconGlyph
import com.arkhamcompanion.ui.navigation.cards.CardsGraphState
import kotlinx.serialization.Serializable

sealed interface TopLevelRoute : NavKey {

    val icon: IconGlyph
    @get:StringRes
    val label: Int

    @Serializable
    data class Cards(
        val graphState: CardsGraphState = CardsGraphState.Main
    ) : TopLevelRoute {
        override val icon = AppIcon.Cards
        override val label = R.string.cards
    }

    @Serializable
    data object Decks : TopLevelRoute {
        override val icon = AppIcon.Deck
        override val label = R.string.decks
    }

    @Serializable
    data object Campaigns : TopLevelRoute {
        override val icon = AppIcon.Book
        override val label = R.string.campaigns
    }

    @Serializable
    data object Settings : TopLevelRoute {
        override val icon = AppIcon.Settings
        override val label = R.string.settings
    }
}