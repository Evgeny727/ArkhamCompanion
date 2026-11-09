package com.arkhamcompanion.ui.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.arkhamcompanion.AppViewModel
import com.arkhamcompanion.R
import com.arkhamcompanion.ui.cards.StandaloneCardDetailsScreen
import com.arkhamcompanion.ui.decks.DecksFilersTagsScreen
import com.arkhamcompanion.ui.decks.DecksFiltersCardsScreen
import com.arkhamcompanion.ui.decks.DecksFiltersCardsSelectionScreen
import com.arkhamcompanion.ui.decks.DecksFiltersScreen
import com.arkhamcompanion.ui.decks.DecksFiltersTagsSelectionScreen
import com.arkhamcompanion.ui.decks.DecksScreen
import com.arkhamcompanion.ui.decks.DecksSortDialog
import com.arkhamcompanion.ui.decks.DecksViewModel
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.navigation.cards.CardsGraphState
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
        val isFilterActive by decksViewModel.isFiltersActive.collectAsState()
        val selectedSort by decksViewModel.selectedSort.collectAsState()
        var showSortDialog by rememberSaveable { mutableStateOf(false) }
        val createNewDeckNavigation = {
            navigator.navigateSingleTop(
                route = TopLevelRoute.Cards(CardsGraphState.DeckInvestigator),
                withinStack = true
            )
        }

        DecksScreen(
            userId = userState.arkhamBuildUser?.id,
            userLoading = appState.isUserLoading(),
            onUserConnect = viewModel::oAuthLogIn,
            navigateToDeck = { deckId -> },
            onCreateNewDeck = createNewDeckNavigation,
            decksViewModel = decksViewModel,
        )

        if (showSortDialog) {
            DecksSortDialog(
                selectedSortOption = selectedSort,
                onApply = { newSort -> decksViewModel.applyNewSort(newSort) },
                onDismiss = { showSortDialog = false }
            )
        }

        topAppBarState.update(
            title = stringResource(TopLevelRoute.Decks.label),
            subtitle = null,
            color = null,
            contentColor = null,
            rightActions = {
                ArkhamAppBarAction(
                    contentColor = CustomTheme.colors.m,
                    onClick = createNewDeckNavigation,
                    iconGlyph = AppIcon.PlusButton,
                )

                ArkhamAppBarAction(
                    contentColor = CustomTheme.colors.m,
                    onClick = { navigator.navigateSingleTop(DecksFiltersScreen) },
                    iconGlyph = AppIcon.Filter,
                    isActive = isFilterActive
                )

                ArkhamAppBarAction(
                    contentColor = CustomTheme.colors.m,
                    onClick = { showSortDialog = true },
                    iconGlyph = AppIcon.Sort,
                )
            },
            leftAction = null,
        )
    }

    entry<DecksFiltersScreen>(
        metadata = { SharedViewModelStoreNavEntryDecorator.parent(
            TopLevelRoute.Decks.toContentKey()
        )}
    ) {
        val topAppBarState = LocalTopAppBarState.current
        val decksViewModel = hiltViewModel<DecksViewModel>(
            LocalSharedViewModelStoreOwner.current
        )

        DecksFiltersScreen(
            decksViewModel = decksViewModel,
            navigateTo = { navKey -> navigator.navigateSingleTop(navKey) }
        )

        topAppBarState.update(
            title = stringResource(R.string.filters),
            subtitle = null,
            color = null,
            contentColor = null,
            rightActions = {
                ArkhamAppBarAction(
                    contentColor = CustomTheme.colors.m,
                    onClick = decksViewModel::clearFilters,
                    iconGlyph = AppIcon.Trash,
                )
            },
            leftAction = { color ->
                ArkhamAppBarAction(
                    contentColor = color,
                    onClick = {
                        decksViewModel.clearCardSearchQuery()
                        navigator.goBack()
                    },
                    iconGlyph = AppIcon.ArrowBack,
                )
            },
            ignoreErrorState = true,
        )
    }

    entry<DecksFiltersTagsSelectionScreen>(
        metadata = { SharedViewModelStoreNavEntryDecorator.parent(
            TopLevelRoute.Decks.toContentKey()
        )}
    ) {
        val topAppBarState = LocalTopAppBarState.current
        val decksViewModel = hiltViewModel<DecksViewModel>(
            LocalSharedViewModelStoreOwner.current
        )
        val filters by decksViewModel.deckFilters.collectAsState()
        val allTags by decksViewModel.allTags.collectAsState()
        val localizedFactions by decksViewModel.localizedFactions.collectAsState()

        DecksFilersTagsScreen(
            selectedTags = filters.tags,
            allTags = allTags,
            onTagToggle = decksViewModel::toggleDeckTags,
            resolveFactionTag = { tag ->
                localizedFactions[tag.lowercase()] ?: tag
            },
        )

        topAppBarState.update(
            title = stringResource(R.string.tags),
            subtitle = null,
            color = null,
            contentColor = null,
            rightActions = {
                ArkhamAppBarAction(
                    contentColor = CustomTheme.colors.m,
                    onClick = decksViewModel::clearTagsFilter,
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

    entry<DecksFiltersCardsSelectionScreen>(
        metadata = { SharedViewModelStoreNavEntryDecorator.parent(
            TopLevelRoute.Decks.toContentKey()
        )}
    ) {
        val topAppBarState = LocalTopAppBarState.current
        val decksViewModel = hiltViewModel<DecksViewModel>(
            LocalSharedViewModelStoreOwner.current
        )
        val filters by decksViewModel.deckFilters.collectAsState()

        DecksFiltersCardsScreen(
            decksViewModel = decksViewModel,
            selectedCards = filters.cardsInDeck,
            onCardToggle = decksViewModel::toggleDeckCardsFilter,
            onCardClick = { code, tabooSetId ->
                navigator.navigateSingleTop(
                    StandaloneCardDetailsScreen(code, tabooSetId)
                )
            }
        )

        topAppBarState.update(
            title = stringResource(R.string.deck_cards),
            subtitle = null,
            color = null,
            contentColor = null,
            rightActions = {
                ArkhamAppBarAction(
                    contentColor = CustomTheme.colors.m,
                    onClick = decksViewModel::clearDeckCardsFilter,
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