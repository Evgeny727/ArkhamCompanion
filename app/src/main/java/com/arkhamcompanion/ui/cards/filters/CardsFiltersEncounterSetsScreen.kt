package com.arkhamcompanion.ui.cards.filters

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.ui.cards.CardsFiltersViewModel
import com.arkhamcompanion.ui.components.ArkhamCheckboxButton
import com.arkhamcompanion.ui.components.ArkhamSearchBox
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.applyScaffoldPaddings
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun CardsFiltersEncounterSetsScreen(
    selectedEncounterSets: ImmutableSet<String>,
    cardsFiltersViewModel: CardsFiltersViewModel,
    onEncounterSetChange: (String) -> Unit,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val encounterSets by cardsFiltersViewModel.encounterSets.collectAsState()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val filteredEncounterSets = remember(encounterSets, searchQuery) {
        val query = searchQuery.trim()

        if (query.isBlank()) encounterSets
        else encounterSets.filter { (_, name) ->
            name.contains(query, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .applyScaffoldPaddings(innerPadding)
            .fillMaxSize(),
    ) {
        ArkhamSearchBox(
            searchQuery = searchQuery,
            onQueryChange = { searchQuery = it },
            onClearQuery = { searchQuery = "" },
            searchPlaceholder = stringResource(R.string.search_for_encouter_sets)
        )

        AnimatedVisibility(encounterSets.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = CustomTheme.colors.m
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 8.dp)
        ) {
            filteredEncounterSets.forEach { (code, text) ->
                item(code, "encounter_set") {
                    ArkhamCheckboxButton(
                        title = text,
                        isSelected = code in selectedEncounterSets,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) { onEncounterSetChange(code) }

                    Spacer(modifier = Modifier.height(8.dp))

                    HorizontalDivider(color = CustomTheme.colors.divider)
                }
            }
        }
    }
}