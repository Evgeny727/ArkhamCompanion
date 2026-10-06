package com.arkhamcompanion.ui.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkhamcompanion.ui.components.ArkhamIconText
import com.arkhamcompanion.ui.theme.Alegreya
import com.arkhamcompanion.ui.theme.CustomTheme

val topLevelRoutes = setOf(TopLevelRoute.Cards(), TopLevelRoute.Decks, TopLevelRoute.Campaigns, TopLevelRoute.Settings)

@Composable
fun ArkhamNavigationBar(navigator: Navigator) {
    Column {
        HorizontalDivider(color = CustomTheme.colors.divider)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            topLevelRoutes.forEach { bottomNavItem ->
                val selected = bottomNavItem == navigator.state.topLevelRoute

                ArkhamNavigationBarItem(
                    onClick = { if (selected) {
                        navigator.goBackToTopLevelRoute(bottomNavItem)
                    } else {
                        navigator.navigate(bottomNavItem)
                    } },
                    icon = { ArkhamIconText(
                        iconGlyph = bottomNavItem.icon,
                        size = 26.dp,
                        color = if (selected) CustomTheme.colors.d30 else CustomTheme.colors.m
                    ) },
                    label = { Text(
                        text = stringResource(bottomNavItem.label),
                        fontFamily = Alegreya,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = if (selected) CustomTheme.colors.d30 else CustomTheme.colors.m
                    ) }
                )
            }
        }
    }
}

@Composable
fun RowScope.ArkhamNavigationBarItem(
    onClick: () -> Unit,
    icon: @Composable (() -> Unit),
    label: @Composable (() -> Unit),
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .weight(1f)
            .clickable(
                enabled = enabled,
                indication = ripple(
                    color = CustomTheme.colors.m,
                    radius = 40.dp
                ),
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon()
            label()
        }
    }
}