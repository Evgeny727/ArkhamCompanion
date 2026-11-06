package com.arkhamcompanion.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.model.meta.Pack
import com.arkhamcompanion.ui.cards.components.CardSectionHeader
import com.arkhamcompanion.ui.icons.PackIcon
import com.arkhamcompanion.ui.settings.components.groupPacks
import com.arkhamcompanion.ui.theme.CustomTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun CardPoolSelectionScreen(
    selectedPacks: ImmutableSet<String>,
    deckbuildingPacks: ImmutableList<Pack>,
    onPackAdd: (Pack) -> Unit,
    onPackRemove: (Pack) -> Unit,
    modifier: Modifier = Modifier,
) {
    val groupedPacks = remember(deckbuildingPacks) {
        groupPacks(deckbuildingPacks)
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        groupedPacks.forEach { (chapter, cycles) ->
            stickyHeader(key = "chapter_$chapter", contentType = "chapter_header") {
                CardSectionHeader(
                    title = stringResource(when (chapter) {
                        2 -> R.string.chapter_2
                        1 -> R.string.chapter_1
                        else -> R.string.fanmade_cycles
                    }),
                    isSubTitle = false
                )
            }

            cycles.forEach { (cycleName, reprintPacks, _, packs) ->
                item(key = "cycle_${cycleName}", contentType = "cycle_header") {
                    CardSectionHeader(title = cycleName)
                }

                items(reprintPacks + packs, key = { it.code }) { pack ->
                    val packIcon = PackIcon.fromPackCode(pack.code)
                    val selected = selectedPacks.contains(pack.code)
                            || selectedPacks.contains("cycle:${pack.cycleCode}")

                    ArkhamCheckboxButton(
                        title = pack.name,
                        iconGlyph = packIcon,
                        isSelected = selected,
                        isPackRow = true,
                        modifier = Modifier.padding(8.dp)
                    ) { value ->
                        if (value) {
                            onPackAdd(pack)
                        } else {
                            onPackRemove(pack)
                        }
                    }
                    HorizontalDivider(color = CustomTheme.colors.divider)
                }
            }
        }
    }
}