package com.arkhamcompanion.ui.decks

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
import androidx.navigation3.runtime.NavKey
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.enums.DeckSource
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.ui.cards.components.factionIcon
import com.arkhamcompanion.ui.cards.components.filters.ArkhamToggleButtonGroup
import com.arkhamcompanion.ui.cards.components.filters.NavigationFilterButton
import com.arkhamcompanion.ui.cards.filters.selectedFilterLabel
import com.arkhamcompanion.ui.components.ArkhamCheckboxButton
import com.arkhamcompanion.ui.components.ArkhamIconText
import com.arkhamcompanion.ui.components.factionColor
import com.arkhamcompanion.ui.theme.CustomTheme
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toImmutableSet

@Composable
fun DecksFiltersScreen(
    decksViewModel: DecksViewModel,
    navigateTo: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    val filters by decksViewModel.deckFilters.collectAsState()
    val allFactions by decksViewModel.factions.collectAsState()
    val factions = remember(allFactions) {
        allFactions.filter { faction ->
            faction.key != Faction.Mythos
        }.toImmutableMap()
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        item("factions_filter", "segmented_button") {
            ArkhamToggleButtonGroup(
                values = factions.keys,
                selectedValues = filters.factions,
                onValueToggle = decksViewModel::toggleDeckFactions,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 8.dp,
                        end = 8.dp,
                        bottom = 16.dp
                    )
            ) { faction, selected ->
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
            }
        }

        item("choose_cards_filter", "navigation") {
            val label = stringResource(R.string.deck_cards)
            val selectedCardNames = remember(filters.cardsInDeck) {
                filters.cardsInDeck.map { it.name }
            }

            NavigationFilterButton(
                label = if (filters.cardsInDeck.isEmpty()) {
                    stringResource(R.string.label_all, label)
                } else {
                    selectedFilterLabel(label, selectedCardNames)
                },
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                navigateTo(DecksFiltersCardsSelectionScreen)
            }

            HorizontalDivider(color = CustomTheme.colors.divider)
        }

        item("tags_filter", "navigation") {
            val label = stringResource(R.string.tags)

            NavigationFilterButton(
                label = if (filters.cardsInDeck.isEmpty()) {
                    stringResource(R.string.label_all, label)
                } else {
                    selectedFilterLabel(label, filters.tags)
                },
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                navigateTo(DecksFiltersTagsSelectionScreen)
            }

            HorizontalDivider(color = CustomTheme.colors.divider)
        }

        item("alternate_investigators_only_button", "button") {
            ArkhamCheckboxButton(
                title = stringResource(R.string.parallel_investigator),
                isSelected = filters.alternateInvestigatorsOnly,
                isRegularText = true,
                onValueChange = decksViewModel::selectAlternateInvestigatorsFilter,
                modifier = Modifier.padding(8.dp)
            )

            HorizontalDivider(color = CustomTheme.colors.divider)
        }

        item("deck_source_filter", "segmented_button") {
            ArkhamToggleButtonGroup(
                values = DeckSource.entries.toImmutableSet(),
                selectedValues = filters.source,
                onValueToggle = decksViewModel::toggleDeckSourceFilter,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) { source, _ ->
                val text = when (source) {
                    DeckSource.Account -> "arkham.build"
                    DeckSource.Arkhamdb -> "ArkhamDB"
                    DeckSource.Device -> stringResource(R.string.device)
                }

                Text(
                    text = text,
                    style = CustomTheme.typography.small,
                )
            }
        }
    }
}