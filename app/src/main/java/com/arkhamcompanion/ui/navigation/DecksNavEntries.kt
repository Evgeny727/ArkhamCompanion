package com.arkhamcompanion.ui.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.arkhamcompanion.AppViewModel
import com.arkhamcompanion.ui.decks.DecksScreen
import com.arkhamcompanion.ui.decks.DecksViewModel

fun EntryProviderScope<NavKey>.decksEntries(
    navigator: Navigator,
    viewModel: AppViewModel
) {
    entry<TopLevelRoute.Decks>(
        clazzContentKey = { key -> key.toContentKey() }
    ) {
        val topAppBarState = LocalTopAppBarState.current

        val decksViewModel = hiltViewModel<DecksViewModel>()

        LaunchedEffect(Unit) {
            decksViewModel.errors.collect {
                viewModel.emitError(it.exception)
            }
        }

        val userState by viewModel.userState.collectAsState()
        val appState by viewModel.appState.collectAsState()

        DecksScreen(
            userId = userState.arkhamBuildUser?.id,
            userLoading = appState.isUserLoading(),
            onUserConnect = viewModel::oAuthLogIn,
            navigateToDeck = { deckId -> },
            decksViewModel = decksViewModel,
        )

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