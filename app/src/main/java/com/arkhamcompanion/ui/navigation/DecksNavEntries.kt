package com.arkhamcompanion.ui.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.arkhamcompanion.AppViewModel
import com.arkhamcompanion.R
import com.arkhamcompanion.ui.decks.DecksScreen
import com.arkhamcompanion.ui.decks.DecksSortScreen
import com.arkhamcompanion.ui.decks.DecksViewModel
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.theme.CustomTheme

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
            rightActions = {
                ArkhamAppBarAction(
                    contentColor = CustomTheme.colors.m,
                    onClick = { navigator.navigateSingleTop(DecksSortScreen) },
                    iconGlyph = AppIcon.Sort,
                )
            },
            leftAction = null,
        )
    }

    entry<DecksSortScreen>(
        metadata = { key -> SharedViewModelStoreNavEntryDecorator.parent(
            TopLevelRoute.Decks.toContentKey()
        )}
    ) {
        val topAppBarState = LocalTopAppBarState.current
        val decksViewModel = hiltViewModel<DecksViewModel>(
            LocalSharedViewModelStoreOwner.current
        )
        val selectedSort by decksViewModel.selectedSort.collectAsState()

        DecksSortScreen(
            selectedSortOption = selectedSort,
            onApply = { newSort -> decksViewModel.applyNewSort(newSort) }
        )

        topAppBarState.update(
            title = stringResource(R.string.sort),
            subtitle = null,
            color = null,
            contentColor = null,
            rightActions = {
                ArkhamAppBarAction(
                    contentColor = CustomTheme.colors.m,
                    onClick = decksViewModel::clearSort,
                    iconGlyph = AppIcon.Trash,
                )
            },
            leftAction = { color ->
                ArkhamAppBarAction(
                    contentColor = color,
                    onClick = navigator::goBack,
                    iconGlyph = AppIcon.ArrowBack,
                )
            },
            ignoreErrorState = true,
        )
    }
}