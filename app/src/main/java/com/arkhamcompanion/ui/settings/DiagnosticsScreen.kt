package com.arkhamcompanion.ui.settings

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.ui.cards.components.CardSectionHeader
import com.arkhamcompanion.ui.components.ArkhamAlertButton
import com.arkhamcompanion.ui.components.ArkhamAlertButtonStyle
import com.arkhamcompanion.ui.components.ArkhamAlertDialog
import com.arkhamcompanion.ui.theme.CustomTheme

@Composable
fun DiagnosticsScreen(
    settingsViewModel: SettingsViewModel,
    recreateCache: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val settingsUiState by settingsViewModel.settingsUiState.collectAsState()
    val isLoading = settingsUiState is SettingsUiState.Loading
    var event by remember { mutableStateOf<SettingsUiEvent?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        settingsViewModel.events.collect {
            event = it
            showDialog = true
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        CardSectionHeader(
            title = stringResource(R.string.caches),
            isSubTitle = false
        )

        DiagnosticButton(
            title = stringResource(R.string.clear_image_cache),
            isLoading = isLoading,
            onClick = settingsViewModel::clearImageCache,
        )

        DiagnosticButton(
            title = stringResource(R.string.clear_card_cache),
            isLoading = isLoading,
            onClick = recreateCache,
        )

        DiagnosticButton(
            title = stringResource(R.string.reset_favorite_cards),
            isLoading = isLoading,
            onClick = settingsViewModel::clearFavoriteCards,
        )

        DiagnosticButton(
            title = stringResource(R.string.reset_card_database),
            isLoading = isLoading,
            onClick = settingsViewModel::clearCardsDatabase,
        )
    }

    if (showDialog && event != null) {
        DiagnosticsAlertDialog(event!!) {
            showDialog = false
            event = null
        }
    }
}

@Composable
fun DiagnosticButton(
    title: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable(enabled = !isLoading, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AnimatedVisibility(isLoading) {
            CircularProgressIndicator(
                color = CustomTheme.colors.d30,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = title,
            style = CustomTheme.typography.text,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }

    HorizontalDivider(color = CustomTheme.colors.divider)
}

@Composable
private fun DiagnosticsAlertDialog(
    event: SettingsUiEvent,
    onDismiss: () -> Unit,
) {
    val activity = LocalActivity.current

    ArkhamAlertDialog(
        title = stringResource(R.string.diagnostics),
        description = stringResource(
            when (event) {
                SettingsUiEvent.ImageCacheCleared -> R.string.image_cache_cleared
                SettingsUiEvent.FavoriteCardsCleared -> R.string.favorite_cards_cleared
                SettingsUiEvent.CardsDatabaseCleared -> R.string.please_close_and_restart_the_app
            }
        )
    ) {
        when (event) {
            SettingsUiEvent.ImageCacheCleared, SettingsUiEvent.FavoriteCardsCleared -> {
                ArkhamAlertButton(
                    text = stringResource(R.string.okay),
                    style = ArkhamAlertButtonStyle.DEFAULT,
                    onClick = onDismiss
                )
            }

            SettingsUiEvent.CardsDatabaseCleared -> {
                ArkhamAlertButton(
                    text = stringResource(R.string.close_app),
                    style = ArkhamAlertButtonStyle.DEFAULT,
                    onClick = { activity?.finish() }
                )
            }
        }
    }
}