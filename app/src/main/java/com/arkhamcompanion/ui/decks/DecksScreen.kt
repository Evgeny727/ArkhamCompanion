package com.arkhamcompanion.ui.decks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.domain.model.decks.DeckListItem
import com.arkhamcompanion.ui.components.ArkhamRoundedCardFooter
import com.arkhamcompanion.ui.components.ArkhamRoundedCardFooterItem
import com.arkhamcompanion.ui.components.ArkhamRoundedCardHeader
import com.arkhamcompanion.ui.components.ArkhamRoundedFactionCard
import com.arkhamcompanion.ui.components.ArkhamSearchBox
import com.arkhamcompanion.ui.decks.components.DeckListItem
import com.arkhamcompanion.ui.decks.components.PlaceholderDeckListItem
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.appSp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun DecksScreen(
    userId: String?,
    userLoading: Boolean,
    onUserConnect: () -> Unit,
    navigateToDeck: (String) -> Unit,
    decksViewModel: DecksViewModel,
    modifier: Modifier = Modifier
) {
    val decksUiState by decksViewModel.decksUiState.collectAsState()
    val refreshState = rememberPullToRefreshState()
    var savedUserId by rememberSaveable { mutableStateOf<String?>(null) }
    val searchQuery by decksViewModel.searchQuery.collectAsState()
    val searchResults = decksViewModel.searchResults.collectAsLazyPagingItems()
    val localizedFactions by decksViewModel.localizedFactions.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(userId) {
        if (savedUserId != userId) {
            decksViewModel.syncAllRemoteDecks(userId)
            savedUserId = userId
        }
    }

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
        modifier = modifier.fillMaxSize(),
    ) {
        ArkhamSearchBox(
            searchQuery = searchQuery,
            onQueryChange = decksViewModel::onSearchQueryChanged,
            onClearQuery = decksViewModel::clearSearchQuery,
            searchPlaceholder = stringResource(R.string.search_decks),
            searchOptions = null //TODO: add options
        )

        PullToRefreshBox(
            isRefreshing = decksUiState is DecksUiState.Loading,
            state = refreshState,
            onRefresh = { decksViewModel.syncAllRemoteDecks(userId) },
            indicator = {
                PullToRefreshDefaults.Indicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    isRefreshing = decksUiState is DecksUiState.Loading,
                    containerColor = CustomTheme.colors.d10,
                    color = CustomTheme.colors.l30,
                    state = refreshState
                )
            }
        ) {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                state = listState
            ) {
                if (searchResults.itemCount == 0 && searchResults.loadState.isIdle) {
                    item("no_results", contentType = "text") {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .animateItem()
                        ) {
                            Text(
                                text = if (searchQuery.isBlank()) {
                                    stringResource(R.string.no_decks_yet)
                                } else {
                                    stringResource(
                                        id = R.string.no_matching_decks_for_query,
                                        searchQuery
                                    )
                                },
                                style = CustomTheme.typography.text,
                            )
                        }
                    }
                }

                searchResults.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            item("loading", contentType = "text") {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(32.dp),
                                        color = CustomTheme.colors.m
                                    )
                                }
                            }
                        }
                    }
                }

                items(
                    count = searchResults.itemCount,
                    key = searchResults.itemKey { it.id },
                    contentType = { "deck" }
                ) {
                    when (val item = searchResults[it]) {
                        is DeckListItem -> {
                            DeckListItem(
                                deckListItem = item,
                                resolveFactionTag = { tag -> localizedFactions[tag.lowercase()] ?: tag },
                            ) {
                                navigateToDeck(item.id)
                            }
                        }

                        null -> {
                            PlaceholderDeckListItem(
                                modifier = Modifier.height(124.dp)
                            )
                        }
                    }
                }

                //TODO: uncomment when deck creation is added
//                item("new_deck_button", contentType = "button") {
//                    ArkhamButton(
//                        title = stringResource(R.string.new_deck),
//                        onClick = { /*TODO: create new deck*/ },
//                        maxLines = 1,
//                    ) { color ->
//                        ArkhamIconText(
//                            iconGlyph = AppIcon.Deck,
//                            color = color,
//                            size = 24.dp
//                        )
//                    }
//                }

                if (userId == null && !userLoading) {
                    item(key = "sign_in_block", contentType = "sign_in_block") {
                        ArkhamBuildSignInSuggestion(onUserConnect)
                    }
                }
            }
        }
    }
}

@Composable
private fun ArkhamBuildSignInSuggestion(onUserConnect: () -> Unit) {
    ArkhamRoundedFactionCard(
        faction = Faction.Neutral,
        header = {
            ArkhamRoundedCardHeader(
                title = stringResource(R.string.arkham_build_account),
                faction = Faction.Neutral,
            )
        },
        footer = {
            ArkhamRoundedCardFooter(modifier = Modifier) {
                ArkhamRoundedCardFooterItem(
                    iconGlyph = AppIcon.BuildLogo,
                    iconSize = 24.appSp(CustomTheme.typography.scaleFactor),
                    text = stringResource(R.string.log_in),
                    onClick = onUserConnect
                )
            }
        }
    ) {
        Text(
            text = stringResource(R.string.log_in_suggestion),
            style = CustomTheme.typography.text,
        )
    }
}