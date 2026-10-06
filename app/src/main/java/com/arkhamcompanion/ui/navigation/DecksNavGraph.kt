package com.arkhamcompanion.ui.navigation

import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.arkhamcompanion.ui.decks.DecksScreen

fun EntryProviderScope<NavKey>.decksGraph(navigator: Navigator) {
    entry<TopLevelRoute.Decks> {
        val topAppBarState = LocalTopAppBarState.current

        DecksScreen()

        topAppBarState.update(
            title = stringResource(TopLevelRoute.Decks.label),
            subtitle = null,
            color = null,
            contentColor = null,
            rightActions = null,
            leftAction = null,
        )
    }
}