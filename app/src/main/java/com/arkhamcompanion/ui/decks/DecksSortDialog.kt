package com.arkhamcompanion.ui.decks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.ui.components.ArkhamCheckboxButton
import com.arkhamcompanion.ui.theme.CustomTheme

@Composable
fun DecksSortDialog(
    selectedSortOption: String,
    onApply: (String) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(key = "date_updated", contentType = "checkbox") {
            ArkhamCheckboxButton(
                title = stringResource(R.string.decks_sort_date_updated),
                isSelected = selectedSortOption == "date_updated",
                enabled = selectedSortOption != "date_updated",
                isRadio = true
            ) { onApply("date_updated") }

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(color = CustomTheme.colors.divider)
        }

        item(key = "date_created", contentType = "checkbox") {
            ArkhamCheckboxButton(
                title = stringResource(R.string.decks_sort_date_created),
                isSelected = selectedSortOption == "date_created",
                enabled = selectedSortOption != "date_created",
                isRadio = true
            ) { onApply("date_created") }

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(color = CustomTheme.colors.divider)
        }

        item(key = "alphabetical_asc", contentType = "checkbox") {
            ArkhamCheckboxButton(
                title = stringResource(R.string.decks_sort_alphabetical_asc),
                isSelected = selectedSortOption == "alphabetical_asc",
                enabled = selectedSortOption != "alphabetical_asc",
                isRadio = true
            ) { onApply("alphabetical_asc") }

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(color = CustomTheme.colors.divider)
        }

        item(key = "alphabetical_desc", contentType = "checkbox") {
            ArkhamCheckboxButton(
                title = stringResource(R.string.decks_sort_alphabetical_desc),
                isSelected = selectedSortOption == "alphabetical_desc",
                enabled = selectedSortOption != "alphabetical_desc",
                isRadio = true
            ) { onApply("alphabetical_desc") }

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(color = CustomTheme.colors.divider)
        }

        item(key = "xp_asc", contentType = "checkbox") {
            ArkhamCheckboxButton(
                title = stringResource(R.string.decks_sort_xp_asc),
                isSelected = selectedSortOption == "xp_asc",
                enabled = selectedSortOption != "xp_asc",
                isRadio = true
            ) { onApply("xp_asc") }

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(color = CustomTheme.colors.divider)
        }

        item(key = "xp_desc", contentType = "checkbox") {
            ArkhamCheckboxButton(
                title = stringResource(R.string.decks_sort_xp_desc),
                isSelected = selectedSortOption == "xp_desc",
                enabled = selectedSortOption != "xp_desc",
                isRadio = true
            ) { onApply("xp_desc") }

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(color = CustomTheme.colors.divider)
        }
    }
}