package com.arkhamcompanion.ui.cards.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.model.cards.CardListItem
import com.arkhamcompanion.domain.model.cards.DeckOption
import com.arkhamcompanion.domain.model.cards.InvestigatorAccessConfig
import com.arkhamcompanion.ui.cards.CardsFiltersViewModel
import com.arkhamcompanion.ui.cards.components.CardListItem
import com.arkhamcompanion.ui.cards.components.PlaceholderCardListItem
import com.arkhamcompanion.ui.components.ArkhamSearchBox
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.applyScaffoldPaddings

@Composable
fun CardsFiltersInvestigatorAccessScreen(
    cardsFiltersViewModel: CardsFiltersViewModel,
    onInvestigatorSelect: (
        config: InvestigatorAccessConfig,
        deckOptions: List<DeckOption>,
        deckRequirements: List<String>,
        sideDeckOptions: List<DeckOption>,
        sideDeckRequirements: List<String>
    ) -> Unit,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val investigators = cardsFiltersViewModel.investigators.collectAsLazyPagingItems()
    val query by cardsFiltersViewModel.dialogQuery.collectAsState()

    val density = LocalDensity.current
    val rowHeight = with(density) {
        maxOf(
            a = 36.dp,
            b = (47 * CustomTheme.typography.scaleFactor * fontScale).dp
        )
    }

    Column(
        modifier = modifier
            .applyScaffoldPaddings(innerPadding)
            .fillMaxSize(),
    ) {
        ArkhamSearchBox(
            searchQuery = query,
            onQueryChange = cardsFiltersViewModel::updateSearchQuery,
            onClearQuery = cardsFiltersViewModel::clearSearchQuery,
            searchPlaceholder = stringResource(R.string.search_for_a_card),
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            if (investigators.itemCount == 0 && investigators.loadState.isIdle) {
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
                            text = if (query.isBlank()) {
                                stringResource(R.string.no_matching_cards)
                            } else {
                                stringResource(
                                    id = R.string.no_matching_cards_for_query,
                                    query
                                )
                            },
                            style = CustomTheme.typography.text,
                        )
                    }
                }
            }

            // Handle load states: initial load and pagination load errors/loading.
            investigators.apply {
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
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.searching_cards),
                                    style = CustomTheme.typography.text,
                                )
                            }
                        }
                    }

                    loadState.append is LoadState.Loading -> {
                        item("appending", contentType = "text") {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
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
                count = investigators.itemCount,
                key = investigators.itemKey { it.id },
                contentType = investigators.itemContentType { "card" }
            ) { index ->
                when (val item = investigators[index]) {
                    null -> {
                        PlaceholderCardListItem(rowHeight = rowHeight)
                    }

                    is CardListItem -> {
                        CardListItem(
                            cardListItem = item,
                            rowHeight = rowHeight,
                            isFavorite = false,
                            onClick = {
                                item.run {
                                    onInvestigatorSelect(
                                        InvestigatorAccessConfig(
                                            investigatorId = alternateOfCode ?: duplicateOfCode ?: code,
                                            investigatorName = name,
                                            investigatorFaction = faction,
                                            investigatorTraits = realTraits,
                                        ),
                                        deckOptions.orEmpty(),
                                        deckRequirements?.card.orEmpty().flatten(),
                                        sideDeckOptions.orEmpty(),
                                        sideDeckRequirements?.card.orEmpty().flatten(),
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}