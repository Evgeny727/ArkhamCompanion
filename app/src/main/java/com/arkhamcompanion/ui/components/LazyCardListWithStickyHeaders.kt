package com.arkhamcompanion.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.model.cards.CardListItemUiModel
import com.arkhamcompanion.domain.model.cards.CardSearchResult
import com.arkhamcompanion.ui.cards.components.CardListItem
import com.arkhamcompanion.ui.cards.components.CardSectionHeader
import com.arkhamcompanion.ui.cards.components.PlaceholderCardListItem
import com.arkhamcompanion.ui.cards.components.buildHeaderTitle
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.theme.CustomTheme
import kotlinx.collections.immutable.ImmutableList

@Composable
fun LazyCardListWithStickyHeaders(
    searchQuery: String,
    searchResults: LazyPagingItems<CardListItemUiModel>,
    searchResultCodes: ImmutableList<CardSearchResult>,
    listState: LazyListState,
    rowHeight: Dp,
    onCardClick: (String) -> Unit,
    showClearFilters: Boolean,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier,
    bottomButtons: LazyListScope.() -> Unit,
) {
    val derivedSectionBoundaries by rememberSectionBoundaries(searchResults)
    val codeToIndex by remember(searchResultCodes) {
        derivedStateOf {
            searchResultCodes
                .mapIndexed { index, code ->
                    code.code to index
                }
                .toMap()
        }
    }
    val sectionBoundaries by remember(derivedSectionBoundaries, codeToIndex) {
        derivedStateOf {
            derivedSectionBoundaries
                .mapNotNull { header ->
                    codeToIndex[header.firstCardCode]?.let { index ->
                        SectionBoundary(
                            firstCardIndex = index,
                            firstCardCode = header.firstCardCode,
                            header = header
                        )
                    }
                }
                .sortedBy { it.firstCardIndex }
        }
    }
    val currentCardCode by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.firstNotNullOfOrNull { itemInfo ->
                searchResults.peekOrNull(itemInfo.index)?.let { item ->
                    item as? CardListItemUiModel.CardItem
                }?.card?.code
            }
        }
    }
    val currentCardIndex = currentCardCode?.let(codeToIndex::get)
    val currentHeader by remember(currentCardIndex, sectionBoundaries) {
        derivedStateOf {
            currentCardIndex?.let {
                findSection(
                    cardIndex = it,
                    boundaries = sectionBoundaries,
                    codeToIndex = codeToIndex
                )
            }
        }
    }
    val visibleHeader by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.firstNotNullOfOrNull { itemInfo ->
                searchResults.peekOrNull(itemInfo.index) as? CardListItemUiModel.CategoryHeader
            }
        }
    }
    val stickyHeader = currentHeader ?: visibleHeader
    val nextHeaderInfo by remember(stickyHeader) {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.firstNotNullOfOrNull { itemInfo ->
                searchResults.peekOrNull(itemInfo.index)?.let { item ->
                    if (
                        item is CardListItemUiModel.CategoryHeader &&
                        item.key != stickyHeader?.key
                    ) {
                        itemInfo
                    } else {
                        null
                    }
                }
            }
        }
    }
    var stickyHeaderHeightPx by remember { mutableIntStateOf(0) }
    val stickyOffsetPx by remember(nextHeaderInfo, stickyHeaderHeightPx) {
        derivedStateOf {
            if (nextHeaderInfo == null || stickyHeaderHeightPx == 0) {
                0
            } else {
                minOf(0, nextHeaderInfo!!.offset - stickyHeaderHeightPx)
            }
        }
    }

    Box(
        modifier = Modifier.clipToBounds()
    ) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 8.dp),
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
                                stringResource(R.string.no_matching_cards)
                            } else {
                                stringResource(
                                    id = R.string.no_matching_cards_for_query,
                                    searchQuery
                                )
                            },
                            style = CustomTheme.typography.text,
                        )
                        if (searchQuery.isBlank() && !showClearFilters) {
                            Text(
                                text = stringResource(R.string.edit_collection_in_settings),
                                style = CustomTheme.typography.text,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            // Handle load states: initial load and pagination load errors/loading.
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
                count = searchResults.itemCount,
                key = searchResults.itemKey { when (it) {
                    is CardListItemUiModel.CategoryHeader -> it.key
                    is CardListItemUiModel.CardItem -> it.card.id
                } },
                contentType = searchResults.itemContentType { when (it) {
                    is CardListItemUiModel.CategoryHeader -> "header"
                    is CardListItemUiModel.CardItem -> "card"
                } }
            ) { index ->
                when (val item = searchResults[index]) {
                    null -> {
                        PlaceholderCardListItem(rowHeight = rowHeight)
                    }

                    is CardListItemUiModel.CategoryHeader -> {
                        val title = buildHeaderTitle(item.category, item.value)
                        CardSectionHeader(title)
                    }

                    is CardListItemUiModel.CardItem -> {
                        CardListItem(
                            cardListItem = item.card,
                            rowHeight = rowHeight,
                            onClick = {
                                onCardClick(item.card.code)
                            }
                        )
                    }
                }
            }

            if (showClearFilters) {
                item("clear_filters_button", contentType = "button") {
                    ArkhamButton(
                        title = stringResource(R.string.clear_search_filters),
                        onClick = onClearFilters,
                        modifier = Modifier
                            .padding(8.dp)
                            .animateItem(),
                    ) { color ->
                        ArkhamIconText(
                            iconGlyph = AppIcon.FilterClear,
                            color = color,
                            size = 24.dp
                        )
                    }
                }
            }
            if (searchQuery.isNotBlank()) bottomButtons()
        }

        stickyHeader?.let { header ->
            CardSectionHeader(
                title = buildHeaderTitle(header.category, header.value),
                modifier = Modifier
                    .fillMaxWidth()
                    .offset {
                        IntOffset(
                            x = 0,
                            y = stickyOffsetPx
                        )
                    }
                    .onSizeChanged {
                        stickyHeaderHeightPx = it.height
                    }
            )
        }
    }
}

@Immutable
private data class SectionBoundary(
    val firstCardIndex: Int,
    val firstCardCode: String,
    val header: CardListItemUiModel.CategoryHeader,
)

@Composable
private fun rememberSectionBoundaries(
    searchResults: LazyPagingItems<CardListItemUiModel>
): State<List<CardListItemUiModel.CategoryHeader>> {
    return remember {
        derivedStateOf {
            searchResults.itemSnapshotList.items
                .filterIsInstance<CardListItemUiModel.CategoryHeader>()
        }
    }
}

private fun findSection(
    cardIndex: Int,
    boundaries: List<SectionBoundary>,
    codeToIndex: Map<String, Int>,
): CardListItemUiModel.CategoryHeader? {
    var low = 0
    var high = boundaries.lastIndex
    var result: CardListItemUiModel.CategoryHeader? = null

    while (low <= high) {
        val mid = (low + high) ushr 1

        val boundaryIndex =
            codeToIndex[boundaries[mid].firstCardCode]
                ?: run {
                    low = mid + 1
                    continue
                }

        if (boundaryIndex <= cardIndex) {
            result = boundaries[mid].header
            low = mid + 1
        } else {
            high = mid - 1
        }
    }

    return result
}

private fun <T : Any> LazyPagingItems<T>.peekOrNull(index: Int): T? {
    if (index !in 0 until itemCount) {
        return null
    }

    return peek(index)
}