package com.arkhamcompanion.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.arkhamcompanion.ui.decks.Decks
import com.arkhamcompanion.ui.decks.DecksScreen

fun NavGraphBuilder.decksGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
) {
    navigation<BottomBarItem.Decks>(
        startDestination = BottomBarItem.Decks.startDestination
    ) {
        composable<Decks> {
            val topAppBarState = LocalTopAppBarState.current

            DecksScreen(innerPadding)

            topAppBarState.update(
                title = stringResource(BottomBarItem.Decks.label),
                subtitle = null,
                color = null,
                contentColor = null,
                rightActions = null,
                leftAction = null,
            )
        }
    }
}