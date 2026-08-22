package com.arkhamcompanion.ui.cards

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.domain.model.cards.CardFilters
import com.arkhamcompanion.domain.model.cards.NullableIntRange
import com.arkhamcompanion.ui.cards.components.factionIcon
import com.arkhamcompanion.ui.components.ArkhamIconText
import com.arkhamcompanion.ui.cards.components.ArkhamSingleToggleButtonGroup
import com.arkhamcompanion.ui.cards.components.ArkhamToggleButtonGroup
import com.arkhamcompanion.ui.cards.components.CollapsableFiltersSection
import com.arkhamcompanion.ui.components.factionColor
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.applyScaffoldPaddings
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableMap

@Composable
fun CardsFiltersScreen(
    cardsViewModel: CardsViewModel,
    cardsFiltersViewModel: CardsFiltersViewModel,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val defaultFilters = remember { CardFilters() }
    val filters by cardsViewModel.cardFilters.collectAsState()
    val spoilerState by cardsViewModel.spoilerState.collectAsState()
    val filtersUiState by cardsFiltersViewModel.uiState.collectAsState()
    val allFactions by cardsFiltersViewModel.factions.collectAsState()
    val factions = remember(allFactions, spoilerState) {
        allFactions.filter { faction ->
            spoilerState || faction.key != Faction.Mythos
        }.toImmutableMap()
    }

    LazyColumn(
        modifier = modifier
            .applyScaffoldPaddings(innerPadding)
            .fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        item("factions_filter", "segmented_button") {
            ArkhamToggleButtonGroup(
                values = factions.keys,
                selectedValues = filters.factions,
                onValueToggle = cardsViewModel::updateFactions,
                content = { faction, selected ->
                    ArkhamIconText(
                        iconGlyph = factionIcon(faction),
                        size = when (faction) {
                            Faction.Mythos, Faction.Neutral -> 28.dp
                            else -> 32.dp
                        },
                        color = if (selected) factionColor(faction).text else CustomTheme.colors.m,
                        modifier = Modifier.align(
                            alignment = if (faction == Faction.Neutral || faction == Faction.Mythos) Alignment.BottomCenter
                                else Alignment.Center
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )
        }

        item("level_section", "section") {
            val isDefaultValues = filters.levelFilter.range == defaultFilters.levelFilter.range
            val label = stringResource(R.string.level)

            CollapsableFiltersSection(
                label = if (isDefaultValues) stringResource(R.string.label_all, label)
                    else label,
                isCollapsed = filtersUiState.collapsedSections[FilterSection.Level] == true,
                modifier = Modifier.fillMaxWidth()
            ) {

            }
        }

        if (filtersUiState.collapsedSections[FilterSection.Level] == true) {
            item("level_short_filter", "segmented_button") {
                ArkhamSingleToggleButtonGroup(
                    values = persistentListOf(
                        NullableIntRange(0, 0), NullableIntRange(1, 5)
                    ),
                    selectedValue = filters.levelFilter.forcedRange,
                    onValueToggle = cardsViewModel::toggleForcedLevelRange,
                    content = { range ->
                        val text = if (range.min == 0)
                            stringResource(R.string.level_start, range.min!!)
                        else stringResource(R.string.level_start_end, range.min!!, range.max!!)
                        Text(
                            text = text,
                            style = CustomTheme.typography.small
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )

                HorizontalDivider(color = CustomTheme.colors.divider)
            }
        }
    }
}