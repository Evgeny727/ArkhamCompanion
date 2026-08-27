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
import com.arkhamcompanion.domain.model.cards.LocationFilter
import com.arkhamcompanion.ui.cards.CardsFiltersViewModel
import com.arkhamcompanion.ui.cards.FilterSection
import com.arkhamcompanion.ui.cards.components.filters.ArkhamFiltersCheckboxOption
import com.arkhamcompanion.ui.cards.components.filters.ArkhamRangeSlider
import com.arkhamcompanion.ui.cards.components.filters.CollapsableFiltersSection
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.theme.LocalLanguage
import com.arkhamcompanion.ui.utils.applyScaffoldPaddings

@Composable
fun CardsFiltersLocationsScreen(
    locationFilter: LocationFilter,
    cardsFiltersViewModel: CardsFiltersViewModel,
    onLocationFilterChange: (LocationFilter) -> Unit,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val defaultFilters = remember { LocationFilter() }
    val filtersUiState by cardsFiltersViewModel.uiState.collectAsState()
    val colon = LocalLanguage.current.colon
    val nullText = "—"

    val labelShroud = stringResource(R.string.shroud)
    val isShroudCollapsed = filtersUiState.collapsedSections[FilterSection.Shroud] ?: true
    val isShroudDefault = locationFilter.shroud == defaultFilters.shroud

    val labelClues = stringResource(R.string.clues)
    val isCluesCollapsed = filtersUiState.collapsedSections[FilterSection.Clues] ?: true
    val isCluesDefault = locationFilter.clues == defaultFilters.clues

    Column(
        modifier = modifier
            .applyScaffoldPaddings(innerPadding)
            .fillMaxSize(),
    ) {
        CollapsableFiltersSection(
            label = if (isShroudDefault) stringResource(R.string.label_all, labelShroud)
                else "$labelShroud$colon${locationFilter.shroud.format(nullText)}",
            isNotCollapsed = !isShroudCollapsed,
            onCollapseChange = {
                cardsFiltersViewModel.toggleSection(FilterSection.Shroud)
            },
            onSectionClear = { onLocationFilterChange(locationFilter.copy(
                shroud = defaultFilters.shroud,
                xShroud = false
            )) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .animateContentSize()
        ) {
            ArkhamRangeSlider(
                range = locationFilter.shroud,
                maxRange = defaultFilters.shroud,
                onUpdateRange = { onLocationFilterChange(locationFilter.copy(
                    shroud = it
                )) },
                nullText = nullText
            )

            ArkhamFiltersCheckboxOption(
                title = stringResource(R.string.shroud_x),
                isSelected = locationFilter.xShroud,
            ) {
                onLocationFilterChange(locationFilter.copy(xShroud = it))
            }
        }

        HorizontalDivider(color = CustomTheme.colors.divider)

        CollapsableFiltersSection(
            label = if (isCluesDefault) stringResource(R.string.label_all, labelClues)
                else "$labelClues$colon${locationFilter.clues.format(nullText)}",
            isNotCollapsed = !isCluesCollapsed,
            onCollapseChange = {
                cardsFiltersViewModel.toggleSection(FilterSection.Clues)
            },
            onSectionClear = { onLocationFilterChange(locationFilter.copy(
                clues = defaultFilters.clues,
                perInvestigatorClues = false
            )) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .animateContentSize()
        ) {
            ArkhamRangeSlider(
                range = locationFilter.clues,
                maxRange = defaultFilters.clues,
                onUpdateRange = { onLocationFilterChange(locationFilter.copy(
                    clues = it
                )) },
                nullText = nullText
            )

            ArkhamFiltersCheckboxOption(
                title = stringResource(R.string.per_investigator),
                isSelected = locationFilter.perInvestigatorClues,
            ) {
                onLocationFilterChange(locationFilter.copy(perInvestigatorClues = it))
            }
        }

        HorizontalDivider(color = CustomTheme.colors.divider)
    }
}