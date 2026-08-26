package com.arkhamcompanion.ui.cards.filters

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.model.cards.EnemyFilter
import com.arkhamcompanion.ui.cards.CardsFiltersViewModel
import com.arkhamcompanion.ui.cards.FilterSection
import com.arkhamcompanion.ui.cards.components.filters.ArkhamFiltersCheckboxOption
import com.arkhamcompanion.ui.cards.components.filters.ArkhamRangeSlider
import com.arkhamcompanion.ui.cards.components.filters.CollapsableFiltersSection
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.theme.LocalLanguage
import com.arkhamcompanion.ui.utils.applyScaffoldPaddings

@Composable
fun CardsFiltersEnemiesScreen(
    enemyFilter: EnemyFilter,
    cardsFiltersViewModel: CardsFiltersViewModel,
    onEnemyFilterChange: (EnemyFilter) -> Unit,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val defaultFilters = remember { EnemyFilter() }
    val filtersUiState by cardsFiltersViewModel.uiState.collectAsState()
    val colon = LocalLanguage.current.colon
    val nullText = stringResource(R.string.none)

    val labelFight = stringResource(R.string.fight)
    val isFightCollapsed = filtersUiState.collapsedSections[FilterSection.Fight] ?: true
    val isFightDefault = enemyFilter.fight == defaultFilters.fight

    val labelEvade = stringResource(R.string.evade)
    val isEvadeCollapsed = filtersUiState.collapsedSections[FilterSection.Evade] ?: true
    val isEvadeDefault = enemyFilter.evade == defaultFilters.evade

    val labelDamage = stringResource(R.string.damage)
    val isDamageCollapsed = filtersUiState.collapsedSections[FilterSection.Damage] ?: true
    val isDamageDefault = enemyFilter.damage == defaultFilters.damage

    val labelHorror = stringResource(R.string.horror)
    val isHorrorCollapsed = filtersUiState.collapsedSections[FilterSection.Horror] ?: true
    val isHorrorDefault = enemyFilter.horror == defaultFilters.horror

    Column(
        modifier = modifier
            .applyScaffoldPaddings(innerPadding)
            .fillMaxSize(),
    ) {
        CollapsableFiltersSection(
            label = if (isFightDefault) stringResource(R.string.label_all, labelFight)
            else "$labelFight$colon${enemyFilter.fight.format(nullText)}",
            isNotCollapsed = !isFightCollapsed,
            onCollapseChange = {
                cardsFiltersViewModel.toggleSection(FilterSection.Fight)
            },
            onSectionClear = { onEnemyFilterChange(enemyFilter.copy(
                fight = defaultFilters.fight
            )) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .animateContentSize()
        ) {
            ArkhamRangeSlider(
                range = enemyFilter.fight,
                maxRange = defaultFilters.fight,
                onUpdateRange = { onEnemyFilterChange(enemyFilter.copy(
                    fight = it
                )) },
                nullText = nullText
            )
        }

        HorizontalDivider(color = CustomTheme.colors.divider)

        CollapsableFiltersSection(
            label = if (isEvadeDefault) stringResource(R.string.label_all, labelEvade)
            else "$labelEvade$colon${enemyFilter.evade.format(nullText)}",
            isNotCollapsed = !isEvadeCollapsed,
            onCollapseChange = {
                cardsFiltersViewModel.toggleSection(FilterSection.Evade)
            },
            onSectionClear = { onEnemyFilterChange(enemyFilter.copy(
                evade = defaultFilters.evade
            )) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .animateContentSize()
        ) {
            ArkhamRangeSlider(
                range = enemyFilter.evade,
                maxRange = defaultFilters.evade,
                onUpdateRange = { onEnemyFilterChange(enemyFilter.copy(
                    evade = it
                )) },
                nullText = nullText
            )
        }

        HorizontalDivider(color = CustomTheme.colors.divider)

        CollapsableFiltersSection(
            label = if (isDamageDefault) stringResource(R.string.label_all, labelDamage)
            else "$labelDamage$colon${enemyFilter.damage.format(nullText)}",
            isNotCollapsed = !isDamageCollapsed,
            onCollapseChange = {
                cardsFiltersViewModel.toggleSection(FilterSection.Damage)
            },
            onSectionClear = { onEnemyFilterChange(enemyFilter.copy(
                damage = defaultFilters.damage
            )) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .animateContentSize()
        ) {
            ArkhamRangeSlider(
                range = enemyFilter.damage,
                maxRange = defaultFilters.damage,
                onUpdateRange = { onEnemyFilterChange(enemyFilter.copy(
                    damage = it
                )) },
                nullText = nullText
            )
        }

        HorizontalDivider(color = CustomTheme.colors.divider)

        CollapsableFiltersSection(
            label = if (isHorrorDefault) stringResource(R.string.label_all, labelHorror)
            else "$labelHorror$colon${enemyFilter.horror.format(nullText)}",
            isNotCollapsed = !isHorrorCollapsed,
            onCollapseChange = {
                cardsFiltersViewModel.toggleSection(FilterSection.Horror)
            },
            onSectionClear = { onEnemyFilterChange(enemyFilter.copy(
                horror = defaultFilters.horror
            )) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .animateContentSize()
        ) {
            ArkhamRangeSlider(
                range = enemyFilter.horror,
                maxRange = defaultFilters.horror,
                onUpdateRange = { onEnemyFilterChange(enemyFilter.copy(
                    horror = it
                )) },
                nullText = nullText
            )
        }

        HorizontalDivider(color = CustomTheme.colors.divider)

        ArkhamFiltersCheckboxOption(
            title = stringResource(R.string.vengeance),
            isSelected = enemyFilter.vengeance,
            modifier = Modifier.padding(8.dp)
        ) {
            onEnemyFilterChange(enemyFilter.copy(vengeance = it))
        }
    }
}