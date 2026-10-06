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
import com.arkhamcompanion.domain.model.cards.CardInvestigatorAccessFields
import com.arkhamcompanion.domain.model.cards.CardListItem
import com.arkhamcompanion.ui.cards.CardsFiltersViewModel
import com.arkhamcompanion.ui.cards.components.CardListItem
import com.arkhamcompanion.ui.cards.components.PlaceholderCardListItem
import com.arkhamcompanion.ui.components.ArkhamCheckCircle
import com.arkhamcompanion.ui.components.ArkhamSearchBox
import com.arkhamcompanion.ui.theme.CustomTheme
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun CardsFiltersCardsAccessScreen(
    cardsFiltersViewModel: CardsFiltersViewModel,
    selectedCards: ImmutableSet<CardInvestigatorAccessFields>,
    onCardToggle: (CardInvestigatorAccessFields) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cards = cardsFiltersViewModel.cards.collectAsLazyPagingItems()
    val query by cardsFiltersViewModel.dialogQuery.collectAsState()

    val density = LocalDensity.current
    val rowHeight = with(density) {
        maxOf(
            a = 36.dp,
            b = (47 * CustomTheme.typography.scaleFactor * fontScale).dp
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        ArkhamSearchBox(
            searchQuery = query,
            onQueryChange = cardsFiltersViewModel::updateSearchQuery,
            onClearQuery = cardsFiltersViewModel::clearSearchQuery,
            searchPlaceholder = stringResource(R.string.search_for_a_card),
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(end = 8.dp)
        ) {
            if (cards.itemCount == 0 && cards.loadState.isIdle) {
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
            cards.apply {
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
                count = cards.itemCount,
                key = cards.itemKey { it.id },
                contentType = cards.itemContentType { "card" }
            ) { index ->
                when (val item = cards[index]) {
                    null -> {
                        PlaceholderCardListItem(rowHeight = rowHeight)
                    }

                    is CardListItem -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CardListItem(
                                cardListItem = item,
                                rowHeight = rowHeight,
                                isFavorite = false,
                                onClick = { onCardToggle(item) },
                                modifier = Modifier.weight(1f)
                            )

                            ArkhamCheckCircle(
                                value = item in selectedCards,
                                isRadio = false,
                                onValueChange = { onCardToggle(item) }
                            )
                        }
                    }
                }
            }
        }
    }
}