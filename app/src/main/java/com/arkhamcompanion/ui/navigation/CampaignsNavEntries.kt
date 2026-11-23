package com.arkhamcompanion.ui.navigation

import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.arkhamcompanion.ui.campaigns.CampaignsScreen

fun EntryProviderScope<NavKey>.campaignsEntries(navigator: Navigator) {
    entry<TopLevelRoute.Campaigns>(
        clazzContentKey = { key -> key.toContentKey() }
    ) { backStackEntry ->
        val topAppBarState = LocalTopAppBarState.current

        CampaignsScreen()

        topAppBarState.update(
            entryKey = backStackEntry,
            activeEntryKey = LocalTopAppBarActiveEntry.current,
            title = stringResource(TopLevelRoute.Campaigns.label),
            subtitle = null,
            color = null,
            contentColor = null,
            rightActions = null,
            leftAction = null,
        )
    }
}
