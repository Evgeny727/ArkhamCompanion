package com.arkhamcompanion.ui.decks.components

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.arkhamcompanion.R
import com.arkhamcompanion.ui.components.ArkhamCheckboxButton
import com.arkhamcompanion.ui.components.ArkhamDialog
import com.arkhamcompanion.ui.components.ArkhamSurfaceButton
import com.arkhamcompanion.ui.decks.ParallelSelection
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.theme.CustomTheme

@Composable
fun ParallelSelectionSurfaceButton(
    parallelSelection: ParallelSelection,
    editable: Boolean = true,
    onClick: (Pair<Boolean, Boolean>) -> Unit
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    ArkhamSurfaceButton(
        title = stringResource(R.string.parallel_investigator),
        icon = AppIcon.Parallel1,
        valueLabel = parallelSelection.run {
            stringResource(when {
                first && second -> R.string.parallel_front_parallel_back
                first -> R.string.parallel_front_original_back
                second -> R.string.original_front_parallel_back
                else -> R.string.original_front_original_back
            })
        },
        editable = editable
    ) { showDialog = true }

    if (showDialog) {
        ArkhamDialog(
            title = stringResource(R.string.parallel_investigator),
            onDismiss = { showDialog = false },
        ) {
            ArkhamCheckboxButton(
                title = stringResource(R.string.original_front_original_back),
                isSelected = !parallelSelection.first && !parallelSelection.second,
                enabled = parallelSelection.first || parallelSelection.second,
                isRadio = true,
            ) {
                onClick(false to false)

                showDialog = false
            }

            HorizontalDivider(color = CustomTheme.colors.divider)

            ArkhamCheckboxButton(
                title = stringResource(R.string.parallel_front_original_back),
                isSelected = parallelSelection.first && !parallelSelection.second,
                enabled = !parallelSelection.first || parallelSelection.second,
                isRadio = true,
            ) {
                onClick(true to false)

                showDialog = false
            }

            HorizontalDivider(color = CustomTheme.colors.divider)

            ArkhamCheckboxButton(
                title = stringResource(R.string.original_front_parallel_back),
                isSelected = !parallelSelection.first && parallelSelection.second,
                enabled = parallelSelection.first || !parallelSelection.second,
                isRadio = true,
            ) {
                onClick(false to true)

                showDialog = false
            }

            HorizontalDivider(color = CustomTheme.colors.divider)

            ArkhamCheckboxButton(
                title = stringResource(R.string.parallel_front_parallel_back),
                isSelected = parallelSelection.first && parallelSelection.second,
                enabled = !parallelSelection.first || !parallelSelection.second,
                isRadio = true,
            ) {
                onClick(true to true)

                showDialog = false
            }
        }
    }
}