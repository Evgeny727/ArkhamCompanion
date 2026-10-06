package com.arkhamcompanion.ui.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
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
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.FlowPreview

@OptIn(FlowPreview::class)
@Composable
fun CollectionScreen(
    ignoreCollection: Boolean,
    collection: Collection,
    allPacks: ImmutableList<Pack>,
    onIgnoreChange: (Boolean) -> Unit,
    onCollectionChange: (Collection) -> Unit,
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

    LazyColumn(modifier = modifier.fillMaxSize()) {
        item("text", contentType = "text") {
            Text(
                text = stringResource(R.string.set_collection_to_limit_cards),
                style = CustomTheme.typography.small,
                modifier = Modifier.padding(8.dp)
            )
        }
        item("ignore_button", contentType = "button") {
            ArkhamCheckboxButton(
                title = stringResource(R.string.show_all_cards),
                description = stringResource(
                    if (ignoreCollection) R.string.disable_to_choose_individual_packs_to_show
                    else R.string.all_cards_are_shown_throughout_the_app
                ),
                isSelected = ignoreCollection,
                iconGlyph = AppIcon.Show,
                onValueChange = onIgnoreChange,
                modifier = Modifier.padding(8.dp)
            )
        }
        if (!ignoreCollection) {
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
                            val newCollection = collection.copy(
                                reprintPacks = (collection.reprintPacks + reprintCodes).toImmutableSet(),
                                packs = (collection.packs + packCodes).toImmutableSet()
                            )
                            onCollectionChange(newCollection)
                        }
                        CardSectionHeaderIconButton(
                            iconGlyph = AppIcon.MinusButton,
                        ) {
                            val newCollection = collection.copy(
                                reprintPacks = (collection.reprintPacks - reprintCodes).toImmutableSet(),
                                packs = (collection.packs - packCodes).toImmutableSet()
                            )
                            onCollectionChange(newCollection)
                        }
                    }
                }
                cycles.forEach { (cycleName, reprintPacks, reprintCodes, packs, packCodes) ->
                    val isReprint = reprintPacks.isNotEmpty()

                    val isCommonPacksExpanded =
                        !isReprint || cycleName in expandedReprintCycles
                                || packs.any { it.code in collection.packs }

                    if (isReprint) {
                        item(key = "cycle_${cycleName}_new", contentType = "cycle_header") {
                            CardSectionHeader(
                                title = "$cycleName (1 + 1)",
                            ) {
                                CardSectionHeaderIconButton(
                                    iconGlyph = AppIcon.PlusButton,
                                ) {
                                    val newCollection = collection.copy(
                                        reprintPacks = (collection.reprintPacks + reprintCodes).toImmutableSet()
                                    )
                                    onCollectionChange(newCollection)
                                }
                                CardSectionHeaderIconButton(
                                    iconGlyph = AppIcon.MinusButton,
                                ) {
                                    val newCollection = collection.copy(
                                        reprintPacks = (collection.reprintPacks - reprintCodes).toImmutableSet()
                                    )
                                    onCollectionChange(newCollection)
                                }
                            }
                        }
                    }

                    items(reprintPacks, key = { it.code }) { pack ->
                        val packIcon = PackIcon.fromPackCode(pack.code)
                        val selected = collection.reprintPacks.contains(pack.code)

                        ArkhamCheckboxButton(
                            title = pack.name,
                            iconGlyph = packIcon,
                            isSelected = selected,
                            isPackRow = true,
                            modifier = Modifier.padding(8.dp)
                        ) { value ->
                            val newCollection = if (value) {
                                collection.copy(
                                    reprintPacks = (collection.reprintPacks + pack.code).toImmutableSet()
                                )
                            } else {
                                collection.copy(
                                    reprintPacks = (collection.reprintPacks - pack.code).toImmutableSet()
                                )
                            }

                            onCollectionChange(newCollection)
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
                                    val newCollection = collection.copy(
                                        packs = (collection.packs + packCodes).toImmutableSet()
                                    )
                                    onCollectionChange(newCollection)
                                }
                                CardSectionHeaderIconButton(
                                    iconGlyph = AppIcon.MinusButton,
                                ) {
                                    val newCollection = collection.copy(
                                        packs = (collection.packs - packCodes).toImmutableSet()
                                    )
                                    onCollectionChange(newCollection)
                                }
                            }
                        }

                        items(packs, key = { it.code }) { pack ->
                            val packIcon = PackIcon.fromPackCode(pack.code)
                            val selected = collection.packs.contains(pack.code)

                            ArkhamCheckboxButton(
                                title = if (pack.code != "core2") pack.name else stringResource(R.string.second_core_set),
                                iconGlyph = packIcon,
                                isSelected = selected,
                                isPackRow = true,
                                modifier = Modifier.padding(8.dp)
                            ) { value ->
                                val newCollection = if (value) {
                                    val newPacks = if (pack.code == "core2") {
                                        setOf("core", pack.code)
                                    } else {
                                        setOf(pack.code)
                                    }
                                    collection.copy(
                                        packs = (collection.packs + newPacks).toImmutableSet()
                                    )
                                } else {
                                    val newPacks = if (pack.code == "core") {
                                        setOf(pack.code, "core2")
                                    } else {
                                        setOf(pack.code)
                                    }
                                    collection.copy(
                                        packs = (collection.packs - newPacks).toImmutableSet()
                                    )
                                }

                                onCollectionChange(newCollection)
                            }
                            HorizontalDivider(color = CustomTheme.colors.divider)
                        }
                    }
                }
            }
        }
    }
}