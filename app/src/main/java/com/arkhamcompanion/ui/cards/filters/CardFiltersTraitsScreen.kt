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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.ui.cards.CardsFiltersViewModel
import com.arkhamcompanion.ui.components.ArkhamCheckboxButton
import com.arkhamcompanion.ui.components.ArkhamSearchBox
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.applyScaffoldPaddings
import com.arkhamcompanion.ui.utils.getLocalizedTrait
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun CardsFiltersTraitsScreen(
    selectedTraits: ImmutableSet<String>,
    cardsFiltersViewModel: CardsFiltersViewModel,
    onTraitChange: (String) -> Unit,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val traitCodes by cardsFiltersViewModel.traitCodes.collectAsState()
    val resources = LocalResources.current
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val filteredTraits = remember(traitCodes, resources, searchQuery) {
        val query = searchQuery.trim()

        traitCodes
            .map { code ->
                val traitId = getLocalizedTrait(code)
                code to resources.getString(traitId) + if (traitId == R.string.unknown) {
                    " ($code)"
                } else ""
            }
            .filter { (_, name) ->
                query.isEmpty() || name.contains(query, ignoreCase = true)
            }
            .sortedBy { (_, name) -> name }
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
            searchPlaceholder = stringResource(R.string.search_for_traits)
        )

        AnimatedVisibility(traitCodes.isEmpty()) {
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
            items(
                items = filteredTraits,
                key = { it.first },
                contentType = { "trait" }
            ) { (trait, text) ->

                ArkhamCheckboxButton(
                    title = text,
                    isSelected = trait in selectedTraits,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) { onTraitChange(trait) }

                Spacer(modifier = Modifier.height(8.dp))

                HorizontalDivider(color = CustomTheme.colors.divider)
            }
        }
    }
}