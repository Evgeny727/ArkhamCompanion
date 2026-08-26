package com.arkhamcompanion.ui.cards.filters

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.model.meta.Pack
import com.arkhamcompanion.domain.model.settings.Collection
import com.arkhamcompanion.ui.cards.components.CardSectionHeader
import com.arkhamcompanion.ui.cards.components.CardSectionHeaderIconButton
import com.arkhamcompanion.ui.components.ArkhamButton
import com.arkhamcompanion.ui.components.ArkhamCheckboxButton
import com.arkhamcompanion.ui.components.ArkhamIconText
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.icons.PackIcon
import com.arkhamcompanion.ui.settings.components.ChapterBuilder
import com.arkhamcompanion.ui.settings.components.CycleBuilder
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.applyScaffoldPaddings
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.FlowPreview

@OptIn(FlowPreview::class)
@Composable
fun CardsFiltersPacksScreen(
    selectedPacks: Collection,
    allPacks: ImmutableList<Pack>,
    onPacksChange: (Collection) -> Unit,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val groupedPacks = remember(allPacks) {
        buildList {
            var currentChapter: ChapterBuilder? = null
            var currentCycle: CycleBuilder? = null

            allPacks.forEach { pack ->
                if (currentChapter?.chapter != pack.chapter) {
                    currentChapter = ChapterBuilder(pack.chapter)
                    add(currentChapter)
                    currentCycle = null
                }

                if (currentCycle?.cycleName != pack.cycleName) {
                    currentCycle = CycleBuilder(pack.cycleName)
                    currentChapter!!.cycles += currentCycle
                }

                if (pack.reprint) {
                    currentChapter!!.reprintPackCodes += pack.code
                    currentCycle.reprintPacks += pack
                    currentCycle.reprintPackCodes += pack.code
                }
                else {
                    currentChapter!!.packCodes += pack.code
                    currentCycle.packs += pack
                    currentCycle.packCodes += pack.code
                }
            }
        }.map { it.build() }.toImmutableList()
    }
    var expandedReprintCycles by rememberSaveable {
        mutableStateOf<Set<String>>(emptySet())
    }

    LazyColumn(
        modifier = modifier
            .applyScaffoldPaddings(innerPadding)
            .fillMaxSize(),
    ) {
        groupedPacks.forEach { (chapter, cycles, reprintCodes, packCodes) ->
            stickyHeader(key = "chapter_$chapter", contentType = "chapter_header") {
                CardSectionHeader(
                    title = stringResource(when (chapter) {
                        2 -> R.string.chapter_2
                        1 -> R.string.chapter_1
                        else -> R.string.fanmade_cycles
                    }),
                    isSubTitle = false
                ) {
                    CardSectionHeaderIconButton(
                        iconGlyph = AppIcon.PlusButton,
                    ) {
                        val newCollection = selectedPacks.copy(
                            reprintPacks = (selectedPacks.reprintPacks + reprintCodes).toImmutableSet(),
                            packs = (selectedPacks.packs + packCodes).toImmutableSet()
                        )
                        onPacksChange(newCollection)
                    }
                    CardSectionHeaderIconButton(
                        iconGlyph = AppIcon.MinusButton,
                    ) {
                        val newCollection = selectedPacks.copy(
                            reprintPacks = (selectedPacks.reprintPacks - reprintCodes).toImmutableSet(),
                            packs = (selectedPacks.packs - packCodes).toImmutableSet()
                        )
                        onPacksChange(newCollection)
                    }
                }
            }
            cycles.forEach { (cycleName, reprintPacks, reprintCodes, packs, packCodes) ->
                val isReprint = reprintPacks.isNotEmpty()

                val isCommonPacksExpanded =
                    !isReprint || cycleName in expandedReprintCycles
                            || packs.any { it.code in selectedPacks.packs }

                if (isReprint) {
                    item(key = "cycle_${cycleName}_new", contentType = "cycle_header") {
                        CardSectionHeader(
                            title = "$cycleName (1 + 1)",
                        ) {
                            CardSectionHeaderIconButton(
                                iconGlyph = AppIcon.PlusButton,
                            ) {
                                val newCollection = selectedPacks.copy(
                                    reprintPacks = (selectedPacks.reprintPacks + reprintCodes).toImmutableSet()
                                )
                                onPacksChange(newCollection)
                            }
                            CardSectionHeaderIconButton(
                                iconGlyph = AppIcon.MinusButton,
                            ) {
                                val newCollection = selectedPacks.copy(
                                    reprintPacks = (selectedPacks.reprintPacks - reprintCodes).toImmutableSet()
                                )
                                onPacksChange(newCollection)
                            }
                        }
                    }
                }

                items(reprintPacks, key = { it.code }) { pack ->
                    val packIcon = PackIcon.fromPackCode(pack.code)
                    val selected = selectedPacks.reprintPacks.contains(pack.code)

                    ArkhamCheckboxButton(
                        title = pack.name,
                        iconGlyph = packIcon,
                        isSelected = selected,
                        isPackRow = true,
                        modifier = Modifier.padding(8.dp)
                    ) { value ->
                        val newCollection = if (value) {
                            selectedPacks.copy(
                                reprintPacks = (selectedPacks.reprintPacks + pack.code).toImmutableSet()
                            )
                        } else {
                            selectedPacks.copy(
                                reprintPacks = (selectedPacks.reprintPacks - pack.code).toImmutableSet()
                            )
                        }

                        onPacksChange(newCollection)
                    }
                    HorizontalDivider(color = CustomTheme.colors.divider)
                }

                if (!isCommonPacksExpanded) {
                    item(key = "expand_$cycleName", contentType = "expand_button") {
                        ArkhamButton(
                            title = stringResource(R.string.show_original_release_packs),
                            onClick = {
                                expandedReprintCycles += cycleName
                            },
                            modifier = Modifier.padding(8.dp)
                        ) { color ->
                            ArkhamIconText(
                                iconGlyph = AppIcon.Show,
                                color = color,
                                size = 28.dp
                            )
                        }
                    }
                }

                if (isCommonPacksExpanded) {
                    item(key = "cycle_${cycleName}_old", contentType = "cycle_header") {
                        CardSectionHeader(
                            title = cycleName + if (isReprint) " (1 + 6)" else "",
                        ) {
                            CardSectionHeaderIconButton(
                                iconGlyph = AppIcon.PlusButton,
                            ) {
                                val newCollection = selectedPacks.copy(
                                    packs = (selectedPacks.packs + packCodes).toImmutableSet()
                                )
                                onPacksChange(newCollection)
                            }
                            CardSectionHeaderIconButton(
                                iconGlyph = AppIcon.MinusButton,
                            ) {
                                val newCollection = selectedPacks.copy(
                                    packs = (selectedPacks.packs - packCodes).toImmutableSet()
                                )
                                onPacksChange(newCollection)
                            }
                        }
                    }

                    items(packs, key = { it.code }) { pack ->
                        val packIcon = PackIcon.fromPackCode(pack.code)
                        val selected = selectedPacks.packs.contains(pack.code)

                        ArkhamCheckboxButton(
                            title = pack.name,
                            iconGlyph = packIcon,
                            isSelected = selected,
                            isPackRow = true,
                            modifier = Modifier.padding(8.dp)
                        ) { value ->
                            val newCollection = if (value) {
                                selectedPacks.copy(
                                    packs = (selectedPacks.packs + pack.code).toImmutableSet()
                                )
                            } else {
                                selectedPacks.copy(
                                    packs = (selectedPacks.packs - pack.code).toImmutableSet()
                                )
                            }

                            onPacksChange(newCollection)
                        }
                        HorizontalDivider(color = CustomTheme.colors.divider)
                    }
                }
            }
        }
    }
}