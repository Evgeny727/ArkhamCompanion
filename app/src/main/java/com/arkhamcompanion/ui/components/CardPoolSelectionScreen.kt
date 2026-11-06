package com.arkhamcompanion.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.model.meta.Pack
import com.arkhamcompanion.ui.cards.components.CardSectionHeader
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.icons.PackIcon
import com.arkhamcompanion.ui.settings.components.groupPacks
import com.arkhamcompanion.ui.theme.CustomTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableList

@Composable
fun CardPoolSelectionScreen(
    selectedPacks: ImmutableSet<String>,
    deckbuildingPacks: ImmutableList<Pack>,
    onPackAdd: (Pack) -> Unit,
    onPackRemove: (Pack) -> Unit,
    applyCurrent: () -> Unit,
    applyLegacy: () -> Unit,
    applyLimited: (List<Pack>) -> Unit,
    applyChapter: (Int) -> Unit,
    applyCPA: (String, Int) -> Unit,
    applyCollection: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val groupedPacks = remember(deckbuildingPacks) {
        groupPacks(deckbuildingPacks)
    }
    var showConfigureEnvironmentDialog by remember { mutableStateOf(false) }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        item(key = "configure_environment_button", contentType = "button") {
            ArkhamSquareButton(
                title = stringResource(R.string.configure_environment),
                detail = stringResource(R.string.edit),
                onClick = { showConfigureEnvironmentDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 16.dp),
                colors = ArkhamButtonColor.LightGray,
            ) { color ->
                ArkhamIconText(
                    iconGlyph = AppIcon.Edit,
                    size = 28.dp,
                    color = color,
                )
            }
        }

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

    if (showConfigureEnvironmentDialog) {
        ArkhamDialog(
            title = stringResource(R.string.configure_environment),
            onDismiss = { showConfigureEnvironmentDialog = false },
        ) {
            var selectedCardPoolSet by remember { mutableStateOf(CardPoolSet.Legacy) }
            var selectedCPAChapter by remember { mutableIntStateOf(2) }
            val selectedPacks = remember { mutableStateListOf<Pack>() }
            var showPacksSelectionDialog by remember { mutableStateOf(false) }

            LazyColumn(
                modifier = Modifier.fillMaxHeight(0.8f),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item(key = "legacy", contentType = "checkbox_button") {
                    ArkhamCheckboxButton(
                        title = stringResource(R.string.legacy),
                        isSelected = selectedCardPoolSet == CardPoolSet.Legacy,
                        enabled = selectedCardPoolSet != CardPoolSet.Legacy,
                        isRadio = true
                    ) {
                        selectedCardPoolSet = CardPoolSet.Legacy
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    HorizontalDivider(color = CustomTheme.colors.divider)
                }

                item(key = "current", contentType = "checkbox_button") {
                    ArkhamCheckboxButton(
                        title = stringResource(R.string.current),
                        isSelected = selectedCardPoolSet == CardPoolSet.Current,
                        enabled = selectedCardPoolSet != CardPoolSet.Current,
                        isRadio = true
                    ) {
                        selectedCardPoolSet = CardPoolSet.Current
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    HorizontalDivider(color = CustomTheme.colors.divider)
                }

                item(key = "limited", contentType = "checkbox_button") {
                    ArkhamCheckboxButton(
                        title = stringResource(R.string.limited),
                        isSelected = selectedCardPoolSet == CardPoolSet.Limited,
                        enabled = selectedCardPoolSet != CardPoolSet.Limited,
                        isRadio = true
                    ) {
                        selectedCardPoolSet = CardPoolSet.Limited
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    HorizontalDivider(color = CustomTheme.colors.divider)
                }

                item(key = "chapter_1", contentType = "checkbox_button") {
                    ArkhamCheckboxButton(
                        title = stringResource(R.string.chapter_1),
                        isSelected = selectedCardPoolSet == CardPoolSet.Chapter1,
                        enabled = selectedCardPoolSet != CardPoolSet.Chapter1,
                        isRadio = true
                    ) {
                        selectedCardPoolSet = CardPoolSet.Chapter1
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    HorizontalDivider(color = CustomTheme.colors.divider)
                }

                item(key = "chapter_2", contentType = "checkbox_button") {
                    ArkhamCheckboxButton(
                        title = stringResource(R.string.chapter_2),
                        isSelected = selectedCardPoolSet == CardPoolSet.Chapter2,
                        enabled = selectedCardPoolSet != CardPoolSet.Chapter2,
                        isRadio = true
                    ) {
                        selectedCardPoolSet = CardPoolSet.Chapter2
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    HorizontalDivider(color = CustomTheme.colors.divider)
                }

                item(key = "cpa", contentType = "checkbox_button") {
                    ArkhamCheckboxButton(
                        title = stringResource(R.string.cpa_environment),
                        isSelected = selectedCardPoolSet == CardPoolSet.CPA,
                        enabled = selectedCardPoolSet != CardPoolSet.CPA,
                        isRadio = true
                    ) {
                        selectedCardPoolSet = CardPoolSet.CPA
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    HorizontalDivider(color = CustomTheme.colors.divider)
                }

                item(key = "collection", contentType = "checkbox_button") {
                    ArkhamCheckboxButton(
                        title = stringResource(R.string.collection_environment),
                        isSelected = selectedCardPoolSet == CardPoolSet.Collection,
                        enabled = selectedCardPoolSet != CardPoolSet.Collection,
                        isRadio = true
                    ) {
                        selectedCardPoolSet = CardPoolSet.Collection
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    HorizontalDivider(color = CustomTheme.colors.divider)
                }

                item(key = "description", contentType = "text") {
                    Text(
                        text = stringResource(when (selectedCardPoolSet) {
                            CardPoolSet.Legacy -> R.string.legacy_environment_description
                            CardPoolSet.Current -> R.string.current_environment_description
                            CardPoolSet.Limited -> R.string.limited_environment_description
                            CardPoolSet.Chapter1 -> R.string.chapter_1_environment_description
                            CardPoolSet.Chapter2 -> R.string.chapter_2_environment_description
                            CardPoolSet.CPA -> R.string.cpa_environment_description
                            CardPoolSet.Collection -> R.string.collection_environment_description
                        }),
                        style = CustomTheme.typography.text,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                if (
                    selectedCardPoolSet == CardPoolSet.Limited ||
                    selectedCardPoolSet == CardPoolSet.CPA
                ) {
                    item(key = "edit_selection", contentType = "selection_button") {
                        ArkhamSquareButton(
                            title = stringResource(R.string.select_packs),
                            detail = stringResource(R.string.selected_text) + " (${selectedPacks.size})",
                            onClick = { showPacksSelectionDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 16.dp),
                            colors = ArkhamButtonColor.LightGray,
                        ) { color ->
                            ArkhamIconText(
                                iconGlyph = AppIcon.Edit,
                                size = 28.dp,
                                color = color,
                            )
                        }
                    }
                }

                if (selectedCardPoolSet == CardPoolSet.CPA) {
                    item(key = "cpa_chapter_selection", contentType = "selection_button") {
                        ArkhamCheckboxButton(
                            title = stringResource(R.string.replace_chapter_2_with_1),
                            isSelected = selectedCPAChapter == 1,
                            isRegularText = true,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) { value ->
                            selectedCPAChapter = if (value) {
                                1
                            } else {
                                2
                            }
                        }
                    }
                }

                item(key = "apply_button", contentType = "button") {
                    ArkhamSquareButton(
                        title = stringResource(R.string.apply_environment, when (selectedCardPoolSet) {
                            CardPoolSet.Legacy -> stringResource(R.string.legacy)
                            CardPoolSet.Current -> stringResource(R.string.current)
                            CardPoolSet.Limited -> stringResource(R.string.limited)
                            CardPoolSet.Chapter1 -> stringResource(R.string.chapter_1)
                            CardPoolSet.Chapter2 -> stringResource(R.string.chapter_2)
                            CardPoolSet.CPA -> stringResource(R.string.cpa_environment)
                            CardPoolSet.Collection -> stringResource(R.string.collection_environment)
                        }),
                        enabled = when (selectedCardPoolSet) {
                            CardPoolSet.Limited -> selectedPacks.size == 3
                            CardPoolSet.CPA -> selectedPacks.size == 1
                            else -> true
                        },
                        onClick = {
                            when (selectedCardPoolSet) {
                                CardPoolSet.Legacy -> applyLegacy()
                                CardPoolSet.Current -> applyCurrent()
                                CardPoolSet.Limited -> applyLimited(selectedPacks)
                                CardPoolSet.Chapter1 -> applyChapter(1)
                                CardPoolSet.Chapter2 -> applyChapter(2)
                                CardPoolSet.CPA -> applyCPA(selectedPacks.first().cycleCode, selectedCPAChapter)
                                CardPoolSet.Collection -> applyCollection()
                            }

                            showConfigureEnvironmentDialog = false
                        },
                    )
                }
            }

            if (showPacksSelectionDialog) {
                ArkhamDialog(
                    title = stringResource(R.string.select_packs),
                    onDismiss = { showPacksSelectionDialog = false },
                ) {
                    val packs = remember(selectedCardPoolSet) {
                        deckbuildingPacks.filter { pack ->
                            if (selectedCardPoolSet == CardPoolSet.Limited) {
                                pack.chapter != 2 && pack.cycleCode != "return"
                            } else {
                                pack.chapter != null &&
                                        pack.cycleCode != "return" &&
                                        pack.cycleCode != "investigator_decks_ch2" &&
                                        pack.cycleCode != "investigator" &&
                                        pack.code != "rcore"
                            }
                        }.toImmutableList()
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxHeight(0.6f),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = packs,
                            key = { it.code },
                            contentType = { "pack" }
                        ) { pack ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val selected = pack in selectedPacks

                                ArkhamCheckboxButton(
                                    title = if (selectedCardPoolSet == CardPoolSet.CPA) {
                                        pack.cycleName
                                    } else {
                                        pack.name
                                    },
                                    iconGlyph = PackIcon.fromPackCode(pack.code),
                                    isSelected = selected,
                                    enabled = if (selectedCardPoolSet == CardPoolSet.CPA) {
                                        selectedPacks.isEmpty()
                                    } else {
                                        selectedPacks.size < 3
                                    } || selected,
                                    isRadio = selectedCardPoolSet == CardPoolSet.CPA,
                                    isPackRow = true
                                ) { value ->
                                    if (value) {
                                        selectedPacks.add(pack)

                                        if (selectedCardPoolSet == CardPoolSet.CPA) {
                                            showPacksSelectionDialog = false
                                        } else if (selectedPacks.size == 3) {
                                            showPacksSelectionDialog = false
                                        }
                                    } else {
                                        selectedPacks.remove(pack)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            HorizontalDivider(color = CustomTheme.colors.divider)
                        }
                    }
                }
            }
        }
    }
}

private enum class CardPoolSet {
    Legacy, Current, Limited, Chapter1, Chapter2, CPA, Collection
}