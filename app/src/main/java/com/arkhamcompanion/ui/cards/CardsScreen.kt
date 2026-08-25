package com.arkhamcompanion.ui.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.model.cards.CardFilters
import com.arkhamcompanion.ui.components.ArkhamButton
import com.arkhamcompanion.ui.components.ArkhamButtonSearchIcon
import com.arkhamcompanion.ui.components.ArkhamSearchBox
import com.arkhamcompanion.ui.components.CardsSearchOptions
import com.arkhamcompanion.ui.components.LazyCardListWithStickyHeaders
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.applyScaffoldPaddings
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun CardsScreen(
    viewModel: CardsViewModel,
    emitError: (Throwable) -> Unit,
    onCardClick: (String) -> Unit,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val spoilerState by viewModel.spoilerState.collectAsState()
    val searchOptions by viewModel.searchOptions.collectAsState()
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()
    val searchResultCodes by viewModel.searchResultCodes.collectAsState()
    val searchFilters by viewModel.cardFilters.collectAsState()
    val defaultFilters = remember { CardFilters() }
    val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }

    val density = LocalDensity.current
    val rowHeight = with(density) {
        maxOf(
            a = 36.dp,
            b = (47 * CustomTheme.typography.scaleFactor * fontScale).dp
        )
    }

    LaunchedEffect(Unit) {
        viewModel.errors.collect {
            emitError(it.exception)
        }
    }
    // Whenever the search query changes, scroll the list back to the top.
    LaunchedEffect(searchResults) {
        snapshotFlow {
            searchResults.loadState.refresh
        }
            .distinctUntilChanged()
            .collectLatest { refresh ->
                if (refresh is LoadState.Loading) {
                    listState.scrollToItem(0)
                }
            }
    }

    Column(
        modifier = modifier
            .applyScaffoldPaddings(innerPadding)
            .fillMaxSize(),
    ) {
        ArkhamSearchBox(
            searchQuery = searchOptions.searchQuery,
            onQueryChange = viewModel::updateSearchQuery,
            onClearQuery = viewModel::clearSearchQuery,
            searchPlaceholder = stringResource(R.string.search_for_a_card)
        ) {
            CardsSearchOptions(
                searchGame = searchOptions.searchGame,
                onSearchGameChange = viewModel::onSearchGameTextChange,
                searchFlavor = searchOptions.searchFlavor,
                onSearchFlavorChange = viewModel::onSearchFlavorTextChange,
                searchBack = searchOptions.searchBack,
                onSearchBackChange = viewModel::onSearchBackTextChange
            )
        }

        LazyCardListWithStickyHeaders(
            searchQuery = searchOptions.searchQuery,
            searchResults = searchResults,
            searchResultCodes = searchResultCodes,
            listState = listState,
            rowHeight = rowHeight,
            onCardClick = onCardClick,
            showClearFilters = searchFilters != defaultFilters,
            onClearFilters = viewModel::clearCardFilters,
        ) {
            item("clear_search_button", contentType = "button") {
                ArkhamButton(
                    title = stringResource(R.string.clear_query_search, searchOptions.searchQuery),
                    onClick = viewModel::clearSearchQuery,
                    modifier = Modifier
                        .padding(8.dp)
                        .animateItem(),
                ) { color ->
                    ArkhamButtonSearchIcon(color)
                }
            }

            if (!searchOptions.searchGame) {
                item("search_game_button", contentType = "button") {
                    ArkhamButton(
                        title = stringResource(R.string.search_game_text),
                        onClick = { viewModel.onSearchGameTextChange(true) },
                        modifier = Modifier
                            .padding(8.dp)
                            .animateItem(),
                    ) { color ->
                        ArkhamButtonSearchIcon(color)
                    }
                }
            }

            if (!searchOptions.searchFlavor) {
                item("search_flavor_button", contentType = "button") {
                    ArkhamButton(
                        title = stringResource(R.string.search_flavor_text),
                        onClick = { viewModel.onSearchFlavorTextChange(true) },
                        modifier = Modifier
                            .padding(8.dp)
                            .animateItem(),
                    ) { color ->
                        ArkhamButtonSearchIcon(color)
                    }
                }
            }

            if (!searchOptions.searchBack) {
                item("search_back_button", contentType = "button") {
                    ArkhamButton(
                        title = stringResource(R.string.search_card_backs),
                        onClick = { viewModel.onSearchBackTextChange(true) },
                        modifier = Modifier
                            .padding(8.dp)
                            .animateItem(),
                    ) { color ->
                        ArkhamButtonSearchIcon(color)
                    }
                }
            }

            item("search_player_encounter_button", contentType = "button") {
                ArkhamButton(
                    title = stringResource(if (spoilerState) R.string.search_player_cards
                    else R.string.search_encounter_cards),
                    onClick = { viewModel.toggleSpoiler(!spoilerState) },
                    modifier = Modifier
                        .padding(8.dp)
                        .animateItem(),
                ) { color ->
                    ArkhamButtonSearchIcon(color)
                }
            }
        }
    }
}