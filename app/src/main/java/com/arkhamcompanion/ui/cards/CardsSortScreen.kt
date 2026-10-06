package com.arkhamcompanion.ui.cards

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.ui.components.ArkhamButtonColor
import com.arkhamcompanion.ui.components.ArkhamCheckboxButton
import com.arkhamcompanion.ui.components.ArkhamIconText
import com.arkhamcompanion.ui.components.ArkhamSquareButton
import com.arkhamcompanion.ui.components.iconSize
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.objects.CardsSortOptions
import com.arkhamcompanion.ui.objects.SortOption
import com.arkhamcompanion.ui.theme.CustomTheme
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun CardsSortScreen(
    spoilerState: Boolean,
    navigateUp: () -> Unit,
    cardsSortViewModel: CardsSortViewModel,
    onApply: (List<String>) -> Unit,
) {
    val playerOrder by cardsSortViewModel.playerSortOptions.collectAsState()
    val mythosOrder by cardsSortViewModel.mythosSortOptions.collectAsState()
    val sortOptions = if (spoilerState) mythosOrder else playerOrder
    val sortOptionsMap = remember(spoilerState) { CardsSortOptions.getSortOptions(spoilerState) }
    val localSortOptionsList = remember { mutableStateListOf<SortOption>() }
    val lazyState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(lazyState) { from, to ->
        val moved = localSortOptionsList.removeAt(from.index)
        localSortOptionsList.add(to.index.coerceIn(0, localSortOptionsList.size), moved)
    }
    val activeSortOptions by remember {
        derivedStateOf {
            localSortOptionsList
                .filter { it.isActive }
                .map { it.id }
        }
    }

    LaunchedEffect(sortOptions, sortOptionsMap) {
        localSortOptionsList.clear()
        localSortOptionsList += createSortOptions(sortOptions, sortOptionsMap)
    }

    LaunchedEffect(Unit) {
        cardsSortViewModel.clearClicked.collect {
            localSortOptionsList.clear()
            localSortOptionsList += createSortOptions(sortOptions, sortOptionsMap)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = lazyState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(localSortOptionsList, { _, it -> it.id }) { index, sortOption ->
                ReorderableItem(reorderableState, sortOption.id) { isDragging ->
                    val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Transparent,
                        tonalElevation = elevation,
                        shape = CustomTheme.shapes.medium
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ArkhamCheckboxButton(
                                title = stringResource(sortOption.resId),
                                isSelected = sortOption.isActive,
                                iconGlyph = AppIcon.Menu,
                                modifier = Modifier.draggableHandle()
                            ) { value ->
                                localSortOptionsList[index] = sortOption.copy(isActive = value)
                            }

                            if (index != localSortOptionsList.lastIndex) HorizontalDivider(color = CustomTheme.colors.divider)
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ArkhamSquareButton(
                title = stringResource(R.string.cancel),
                onClick = navigateUp,
                modifier = Modifier.weight(1f),
                colors = ArkhamButtonColor.RedOutline,
            ) { color ->
                ArkhamIconText(
                    iconGlyph = AppIcon.Dismiss,
                    color = color,
                    size = iconSize(AppIcon.Dismiss)
                )
            }
            ArkhamSquareButton(
                title = stringResource(R.string.apply),
                onClick = { onApply(activeSortOptions) },
                modifier = Modifier.weight(1f),
                enabled = activeSortOptions != sortOptions
            ) { color ->
                ArkhamIconText(
                    iconGlyph = AppIcon.CheckThin,
                    color = color,
                    size = iconSize(AppIcon.CheckThin)
                )
            }
        }
    }
}

internal fun createSortOptions(
    sortOptions: List<String>,
    sortOptionsMap: Map<String, Int>,
): List<SortOption> {
    return sortOptions.map {
        SortOption(it, sortOptionsMap.getValue(it), true)
    } + sortOptionsMap
        .filter { !sortOptions.contains(it.key) }
        .map { SortOption(it.key, it.value) }
}