package com.arkhamcompanion.ui.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.AppUiState
import com.arkhamcompanion.AppUserState
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.ui.components.ArkhamIconText
import com.arkhamcompanion.ui.components.ArkhamRoundedCardHeader
import com.arkhamcompanion.ui.components.ArkhamRoundedFactionCard
import com.arkhamcompanion.ui.components.ArkhamSquareButton
import com.arkhamcompanion.ui.components.ArkhamSurfaceButton
import com.arkhamcompanion.ui.components.ArkhamSurfaceButtonGroup
import com.arkhamcompanion.ui.components.iconSize
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.theme.CustomTheme

@Composable
fun ArkhamBuildAccountCard(
    appUiState: AppUiState,
    userState: AppUserState,
    onLogIn: () -> Unit,
    onLogOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val loading = appUiState is AppUiState.ArkhamBuildLoading
            || appUiState is AppUiState.OAuthFlowInProcess

    ArkhamRoundedFactionCard(
        faction = Faction.Neutral,
        header = {
            ArkhamRoundedCardHeader(
                title = stringResource(R.string.arkham_build_account),
                faction = Faction.Neutral,
            )
        },
    ) {
        userState.exception?.let {
            Surface(
                modifier = modifier.fillMaxWidth(),
                shape = CustomTheme.shapes.large,
                color = CustomTheme.colors.warn
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.account_error),
                        style = CustomTheme.typography.small,
                        color = CustomTheme.colors.l30,
                    )

                    Text(
                        text = stringResource(R.string.reconnect_account),
                        style = CustomTheme.typography.small,
                        color = CustomTheme.colors.l30,
                    )
                }
            }
        }

        userState.arkhamBuildUser?.let { user ->
            ArkhamSurfaceButtonGroup(modifier) {
                ArkhamSurfaceButton(
                    title = stringResource(R.string.account_name),
                    icon = AppIcon.Name,
                    valueLabel = user.username,
                    editable = false
                )
            }
        }

        ArkhamSquareButton(
            title = stringResource(when {
                userState.arkhamBuildUser == null -> R.string.log_in
                userState.exception != null -> R.string.reconnect
                else -> R.string.log_out
            }),
            onClick = {
                if (userState.exception != null || userState.arkhamBuildUser == null) onLogIn()
                else onLogOut()
            },
            loading = loading
        ) { color ->
            ArkhamIconText(
                iconGlyph = AppIcon.BuildLogo,
                color = color,
                size = iconSize(AppIcon.BuildLogo),
            )
        }
    }
}