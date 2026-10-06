package com.arkhamcompanion.ui.navigation

import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.arkhamcompanion.ui.campaigns.CampaignsScreen

fun EntryProviderScope<NavKey>.campaignsGraph(navigator: Navigator) {
    entry<TopLevelRoute.Campaigns> {
        val topAppBarState = LocalTopAppBarState.current

        CampaignsScreen()

        topAppBarState.update(
            title = stringResource(TopLevelRoute.Campaigns.label),
            subtitle = null,
            color = null,
            contentColor = null,
            rightActions = null,
            leftAction = null,
        )
    }
}
