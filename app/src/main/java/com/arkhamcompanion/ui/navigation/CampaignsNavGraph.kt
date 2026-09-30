package com.arkhamcompanion.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.arkhamcompanion.ui.campaigns.Campaigns
import com.arkhamcompanion.ui.campaigns.CampaignsScreen

fun NavGraphBuilder.campaignsGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
) {
    navigation<BottomBarItem.Campaigns>(
        startDestination = BottomBarItem.Campaigns.startDestination
    ) {
        composable<Campaigns> {
            val topAppBarState = LocalTopAppBarState.current

            CampaignsScreen(innerPadding)

            topAppBarState.update(
                title = stringResource(BottomBarItem.Campaigns.label),
                subtitle = null,
                color = null,
                contentColor = null,
                rightActions = null,
                leftAction = null,
            )
        }
    }
}
