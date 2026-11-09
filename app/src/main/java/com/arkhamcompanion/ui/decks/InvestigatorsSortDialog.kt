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
import com.arkhamcompanion.ui.components.ArkhamDialog
import com.arkhamcompanion.ui.theme.CustomTheme

@Composable
fun InvestigatorsSortDialog(
    selectedSortOption: String,
    onApply: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ArkhamDialog(
        title = stringResource(R.string.sort),
        onDismiss = onDismiss,
    ) {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item(key = "cycle", contentType = "checkbox") {
                ArkhamCheckboxButton(
                    title = stringResource(R.string.sort_by_cycle),
                    isSelected = selectedSortOption == "cycle",
                    enabled = selectedSortOption != "cycle",
                    isRadio = true
                ) { onApply("cycle") }

                Spacer(modifier = Modifier.height(8.dp))

                HorizontalDivider(color = CustomTheme.colors.divider)
            }

            item(key = "faction", contentType = "checkbox") {
                ArkhamCheckboxButton(
                    title = stringResource(R.string.sort_by_faction),
                    isSelected = selectedSortOption == "faction",
                    enabled = selectedSortOption != "faction",
                    isRadio = true
                ) { onApply("faction") }

                Spacer(modifier = Modifier.height(8.dp))

                HorizontalDivider(color = CustomTheme.colors.divider)
            }

            item(key = "name", contentType = "checkbox") {
                ArkhamCheckboxButton(
                    title = stringResource(R.string.sort_by_name),
                    isSelected = selectedSortOption == "name",
                    enabled = selectedSortOption != "name",
                    isRadio = true
                ) { onApply("name") }
            }
        }
    }
}