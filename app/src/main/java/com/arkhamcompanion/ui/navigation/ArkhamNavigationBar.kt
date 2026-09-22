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
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.arkhamcompanion.ui.components.ArkhamIconText
import com.arkhamcompanion.ui.theme.Alegreya
import com.arkhamcompanion.ui.theme.CustomTheme

val bottomBarItems = listOf(BottomBarItem.Cards, BottomBarItem.Decks, BottomBarItem.Campaigns, BottomBarItem.Settings)

@Composable
fun ArkhamNavigationBar(
    navController: NavHostController,
    currentDestination: NavDestination?
) {
    Column {
        HorizontalDivider(color = CustomTheme.colors.divider)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            bottomBarItems.forEach { bottomNavItem ->
                val selected = when (bottomNavItem) {
                    BottomBarItem.Cards -> currentDestination?.isInHierarchy<BottomBarItem.Cards>() == true
                    BottomBarItem.Decks -> currentDestination?.isInHierarchy<BottomBarItem.Decks>() == true
                    BottomBarItem.Campaigns -> currentDestination?.isInHierarchy<BottomBarItem.Campaigns>() == true
                    BottomBarItem.Settings -> currentDestination?.isInHierarchy<BottomBarItem.Settings>() == true
                }

                ArkhamNavigationBarItem(
                    onClick = { if (selected) {
                        navController.popBackStack(bottomNavItem.startDestination, false)
                    } else {
                        navController.navigate(bottomNavItem) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
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

private inline fun <reified T : Any> NavDestination?.isInHierarchy(): Boolean =
    this?.hierarchy?.any { it.hasRoute<T>() } == true

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