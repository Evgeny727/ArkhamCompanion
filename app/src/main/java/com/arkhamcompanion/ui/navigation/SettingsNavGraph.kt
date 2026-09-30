package com.arkhamcompanion.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.arkhamcompanion.AppViewModel
import com.arkhamcompanion.R
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.settings.AboutScreen
import com.arkhamcompanion.ui.settings.BackUpScreen
import com.arkhamcompanion.ui.settings.CollectionScreen
import com.arkhamcompanion.ui.settings.DiagnosticsScreen
import com.arkhamcompanion.ui.settings.Settings
import com.arkhamcompanion.ui.settings.SettingsAbout
import com.arkhamcompanion.ui.settings.SettingsBackup
import com.arkhamcompanion.ui.settings.SettingsCollection
import com.arkhamcompanion.ui.settings.SettingsDiagnostics
import com.arkhamcompanion.ui.settings.SettingsScreen
import com.arkhamcompanion.ui.settings.SettingsViewModel

fun NavGraphBuilder.settingsGraph(
    viewModel: AppViewModel,
    navController: NavHostController,
    innerPadding: PaddingValues,
) {
    navigation<BottomBarItem.Settings>(
        startDestination = BottomBarItem.Settings.startDestination
    ) {
        composable<Settings> {
            val topAppBarState = LocalTopAppBarState.current
            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            val theme by viewModel.themeState.collectAsState()

            SettingsScreen(
                theme = theme ?: 2,
                viewModel = settingsViewModel,
                onLanguageChange = viewModel::updateLocale,
                updateCards = viewModel::updateCardsIfAvailable,
                navigateToCollection = { navController.navigateSingleTop(SettingsCollection) },
                navigateToAbout = { navController.navigateSingleTop(SettingsAbout) },
                navigateToBackup = { navController.navigateSingleTop(SettingsBackup) },
                navigateToDiagnostics = { navController.navigateSingleTop(SettingsDiagnostics) },
                emitError = viewModel::emitError,
                innerPadding = innerPadding
            )

            topAppBarState.update(
                title = stringResource(BottomBarItem.Settings.label),
                subtitle = null,
                color = null,
                contentColor = null,
                rightActions = null,
                leftAction = null,
            )
        }

        composable<SettingsCollection> { backStackEntry ->
            val topAppBarState = LocalTopAppBarState.current
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Settings>()
            }
            val settingsViewModel = hiltViewModel<SettingsViewModel>(parentEntry)
            val ignoreCollection by settingsViewModel.ignoreCollectionState.collectAsState()
            val collection by settingsViewModel.collectionState.collectAsState()
            val allPacks by settingsViewModel.allPacksState.collectAsState()

            LaunchedEffect(Unit) {
                settingsViewModel.errors.collect {
                    viewModel.emitError(it.exception)
                }
            }

            CollectionScreen(
                ignoreCollection = ignoreCollection,
                collection = collection,
                allPacks = allPacks,
                onIgnoreChange = settingsViewModel::setIgnoreCollection,
                onCollectionChange = settingsViewModel::setCollection,
                innerPadding = innerPadding
            )

            topAppBarState.update(
                title = stringResource(R.string.edit_collection),
                subtitle = null,
                color = null,
                contentColor = null,
                rightActions = null,
                leftAction = { color ->
                    ArkhamAppBarAction(
                        contentColor = color,
                        onClick = navController::navigateUp,
                        iconGlyph = AppIcon.ArrowBack,
                    )
                },
            )
        }

        composable<SettingsAbout> {
            val topAppBarState = LocalTopAppBarState.current

            AboutScreen(innerPadding)

            topAppBarState.update(
                title = stringResource(R.string.about_arkham_companion),
                subtitle = null,
                color = null,
                contentColor = null,
                rightActions = null,
                leftAction = { color ->
                    ArkhamAppBarAction(
                        contentColor = color,
                        onClick = navController::navigateUp,
                        iconGlyph = AppIcon.ArrowBack,
                    )
                },
            )
        }

        composable<SettingsBackup> {
            val topAppBarState = LocalTopAppBarState.current

            //TODO: add backup screen
            BackUpScreen(innerPadding)

            topAppBarState.update(
                title = stringResource(R.string.backup_data),
                subtitle = null,
                color = null,
                contentColor = null,
                rightActions = null,
                leftAction = { color ->
                    ArkhamAppBarAction(
                        contentColor = color,
                        onClick = navController::navigateUp,
                        iconGlyph = AppIcon.ArrowBack,
                    )
                },
            )
        }

        composable<SettingsDiagnostics> { backStackEntry ->
            val topAppBarState = LocalTopAppBarState.current
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Settings>()
            }
            val settingsViewModel = hiltViewModel<SettingsViewModel>(parentEntry)

            DiagnosticsScreen(
                settingsViewModel = settingsViewModel,
                recreateCache = viewModel::recreateCardsCache,
                innerPadding = innerPadding
            )

            topAppBarState.update(
                title = stringResource(R.string.diagnostics),
                subtitle = null,
                color = null,
                contentColor = null,
                rightActions = null,
                leftAction = { color ->
                    ArkhamAppBarAction(
                        contentColor = color,
                        onClick = navController::navigateUp,
                        iconGlyph = AppIcon.ArrowBack,
                    )
                },
            )
        }
    }
}