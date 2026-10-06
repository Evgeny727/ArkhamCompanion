package com.arkhamcompanion.ui.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.arkhamcompanion.AppViewModel
import com.arkhamcompanion.R
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.settings.AboutScreen
import com.arkhamcompanion.ui.settings.BackUpScreen
import com.arkhamcompanion.ui.settings.CollectionScreen
import com.arkhamcompanion.ui.settings.DiagnosticsScreen
import com.arkhamcompanion.ui.settings.SettingsAbout
import com.arkhamcompanion.ui.settings.SettingsBackup
import com.arkhamcompanion.ui.settings.SettingsCollection
import com.arkhamcompanion.ui.settings.SettingsDiagnostics
import com.arkhamcompanion.ui.settings.SettingsScreen
import com.arkhamcompanion.ui.settings.SettingsViewModel

fun EntryProviderScope<NavKey>.settingsGraph(
    viewModel: AppViewModel,
    navigator: Navigator,
) {
    entry<TopLevelRoute.Settings> {
        val topAppBarState = LocalTopAppBarState.current
        val settingsViewModel = hiltViewModel<SettingsViewModel>()
        val theme by viewModel.themeState.collectAsState()

        SettingsScreen(
            theme = theme ?: 2,
            viewModel = settingsViewModel,
            onLanguageChange = viewModel::updateLocale,
            updateCards = viewModel::updateCardsIfAvailable,
            navigateToCollection = { navigator.navigateSingleTop(SettingsCollection) },
            navigateToAbout = { navigator.navigateSingleTop(SettingsAbout) },
            navigateToBackup = { navigator.navigateSingleTop(SettingsBackup) },
            navigateToDiagnostics = { navigator.navigateSingleTop(SettingsDiagnostics) },
            emitError = viewModel::emitError,
        )

        topAppBarState.update(
            title = stringResource(TopLevelRoute.Settings.label),
            subtitle = null,
            color = null,
            contentColor = null,
            rightActions = null,
            leftAction = null,
        )
    }

    entry<SettingsCollection>(
        metadata = { _ -> SharedViewModelStoreNavEntryDecorator.parent(
            TopLevelRoute.Settings.toContentKey()
        )}
    ) {
        val topAppBarState = LocalTopAppBarState.current
        val settingsViewModel = hiltViewModel<SettingsViewModel>(
            LocalSharedViewModelStoreOwner.current
        )
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
                    onClick = navigator::goBack,
                    iconGlyph = AppIcon.ArrowBack,
                )
            },
        )
    }

    entry<SettingsAbout> {
        val topAppBarState = LocalTopAppBarState.current

        AboutScreen()

        topAppBarState.update(
            title = stringResource(R.string.about_arkham_companion),
            subtitle = null,
            color = null,
            contentColor = null,
            rightActions = null,
            leftAction = { color ->
                ArkhamAppBarAction(
                    contentColor = color,
                    onClick = navigator::goBack,
                    iconGlyph = AppIcon.ArrowBack,
                )
            },
        )
    }

    entry<SettingsBackup> {
        val topAppBarState = LocalTopAppBarState.current

        //TODO: add backup screen
        BackUpScreen()

        topAppBarState.update(
            title = stringResource(R.string.backup_data),
            subtitle = null,
            color = null,
            contentColor = null,
            rightActions = null,
            leftAction = { color ->
                ArkhamAppBarAction(
                    contentColor = color,
                    onClick = navigator::goBack,
                    iconGlyph = AppIcon.ArrowBack,
                )
            },
        )
    }

    entry<SettingsDiagnostics>(
        metadata = { _ -> SharedViewModelStoreNavEntryDecorator.parent(
            TopLevelRoute.Settings.toContentKey()
        )}
    ) {
        val topAppBarState = LocalTopAppBarState.current
        val settingsViewModel = hiltViewModel<SettingsViewModel>(
            LocalSharedViewModelStoreOwner.current
        )

        DiagnosticsScreen(
            settingsViewModel = settingsViewModel,
            recreateCache = viewModel::recreateCardsCache,
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
                    onClick = navigator::goBack,
                    iconGlyph = AppIcon.ArrowBack,
                )
            },
        )
    }
}