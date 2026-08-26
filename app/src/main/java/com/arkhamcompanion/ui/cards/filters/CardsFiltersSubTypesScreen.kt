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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.enums.CardSubType
import com.arkhamcompanion.ui.cards.CardsFiltersViewModel
import com.arkhamcompanion.ui.components.ArkhamCheckboxButton
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.applyScaffoldPaddings
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun CardsFiltersSubTypesScreen(
    selectedSubTypes: ImmutableSet<CardSubType?>,
    cardsFiltersViewModel: CardsFiltersViewModel,
    onSubTypeChange: (CardSubType?) -> Unit,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val subtypes by cardsFiltersViewModel.subtypes.collectAsState()

    Column(
        modifier = modifier
            .applyScaffoldPaddings(innerPadding)
            .fillMaxSize(),
    ) {
        AnimatedVisibility(subtypes.isEmpty()) {
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
            item("no_subtype", "subtype") {
                ArkhamCheckboxButton(
                    title = stringResource(R.string.none),
                    isSelected = null in selectedSubTypes,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) { onSubTypeChange(null) }

                Spacer(modifier = Modifier.height(8.dp))

                HorizontalDivider(color = CustomTheme.colors.divider)
            }

            subtypes.forEach { (subtype, text) ->
                item(subtype.name.lowercase(), "subtype") {
                    ArkhamCheckboxButton(
                        title = text,
                        isSelected = subtype in selectedSubTypes,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) { onSubTypeChange(subtype) }

                    Spacer(modifier = Modifier.height(8.dp))

                    HorizontalDivider(color = CustomTheme.colors.divider)
                }
            }
        }
    }
}