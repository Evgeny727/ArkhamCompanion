package com.arkhamcompanion.ui.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.arkhamcompanion.AppViewModel
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.model.cards.CardTextSegment
import com.arkhamcompanion.ui.cards.StandaloneCardDetailsScreen
import com.arkhamcompanion.ui.cards.components.details.resolveTextSegmentWithoutStyles
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.settings.AboutScreen
import com.arkhamcompanion.ui.settings.BackUpScreen
import com.arkhamcompanion.ui.settings.CollectionScreen
import com.arkhamcompanion.ui.settings.DiagnosticsScreen
import com.arkhamcompanion.ui.settings.RewardsScreen
import com.arkhamcompanion.ui.settings.RulesSection
import com.arkhamcompanion.ui.settings.SettingsAbout
import com.arkhamcompanion.ui.settings.SettingsBackup
import com.arkhamcompanion.ui.settings.SettingsCollection
import com.arkhamcompanion.ui.settings.SettingsDiagnostics
import com.arkhamcompanion.ui.settings.SettingsRewards
import com.arkhamcompanion.ui.settings.SettingsRules
import com.arkhamcompanion.ui.settings.SettingsScreen
import com.arkhamcompanion.ui.settings.SettingsViewModel
import com.arkhamcompanion.ui.settings.rules.RuleSectionScreen
import com.arkhamcompanion.ui.settings.rules.RulesScreen
import com.arkhamcompanion.ui.settings.rules.RulesViewModel
import com.arkhamcompanion.ui.theme.LocalLanguage

fun EntryProviderScope<NavKey>.settingsEntries(
    navigator: Navigator,
    viewModel: AppViewModel,
) {
    entry<TopLevelRoute.Settings>(
        clazzContentKey = { key -> key.toContentKey() }
    ) {
        val topAppBarState = LocalTopAppBarState.current
        val settingsViewModel = hiltViewModel<SettingsViewModel>()
        val theme by viewModel.themeState.collectAsState()

        SettingsScreen(
            theme = theme ?: 2,
            viewModel = settingsViewModel,
            onLanguageChange = viewModel::updateLocale,
            updateCards = viewModel::updateCardsIfAvailable,
            navigateToRules = { navigator.navigateSingleTop(SettingsRules) },
            navigateToCollection = { navigator.navigateSingleTop(SettingsCollection) },
            navigateToRewards = { navigator.navigateSingleTop(SettingsRewards) },
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

    entry<SettingsRewards>(
        metadata = { _ -> SharedViewModelStoreNavEntryDecorator.parent(
            TopLevelRoute.Settings.toContentKey()
        )}
    ) {
        val topAppBarState = LocalTopAppBarState.current
        val settingsViewModel = hiltViewModel<SettingsViewModel>(
            LocalSharedViewModelStoreOwner.current
        )

        val rewards by settingsViewModel.rewardsState.collectAsState()
        val unlockAllRewards by settingsViewModel.unlockAllRewardsState.collectAsState()
        val rewardCards by settingsViewModel.rewardCards.collectAsState()

        LaunchedEffect(Unit) {
            settingsViewModel.errors.collect {
                viewModel.emitError(it.exception)
            }
        }

        RewardsScreen(
            rewards = rewards,
            unlockAllRewards = unlockAllRewards,
            rewardCards = rewardCards,
            onUnlockAllChange = settingsViewModel::setUnlockAllRewards,
            onRewardsChange = settingsViewModel::setRewards,
            onCardClick = { code, tabooSetId ->
                navigator.navigateSingleTop(
                    StandaloneCardDetailsScreen(code, tabooSetId)
                )
            }
        )

        topAppBarState.update(
            title = stringResource(R.string.edit_unlocked_rewards),
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

    entry<SettingsRules> {
        val topAppBarState = LocalTopAppBarState.current

        val locale = LocalLanguage.current.languageTag
        val rulesViewModel = hiltAssistedViewModel<RulesViewModel, RulesViewModel.Factory, String>(
            locale
        ) { create(it) }

        RulesScreen(
            rulesViewModel = rulesViewModel,
            navigateToSection = { id ->
                navigator.navigateSingleTop(RulesSection(id))
            }
        )

        topAppBarState.update(
            title = stringResource(R.string.rules),
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

    entry<RulesSection>(
        metadata = { _ -> SharedViewModelStoreNavEntryDecorator.parent(
            SettingsRules.toContentKey()
        )}
    ) { backStackEntry ->
        val topAppBarState = LocalTopAppBarState.current

        val locale = LocalLanguage.current.languageTag
        val rulesViewModel = hiltAssistedViewModel<RulesViewModel, RulesViewModel.Factory, String>(
            locale,
            LocalSharedViewModelStoreOwner.current
        ) { create(it) }

        val section by rulesViewModel.getTopLevelSectionById(backStackEntry.id).collectAsState(null)

        section?.let {
            RuleSectionScreen(
                rulesViewModel = rulesViewModel,
                section = it,
                scrollToId = if (it.id == backStackEntry.id) null else backStackEntry.id,
                navigateToSection = { id ->
                    navigator.navigateSingleTop(RulesSection(id))
                },
                navigateToCard = { code ->
                    navigator.navigateSingleTop(
                        StandaloneCardDetailsScreen(code, null)
                    )
                }
            )
        }

        val title = section?.let { section ->
            val paragraph = section.title.paragraphs.first()

            buildAnnotatedString {
                paragraph.segments.forEach { segment ->
                    if (segment is CardTextSegment.Text) {
                        resolveTextSegmentWithoutStyles(section.title.text, segment)
                    }
                }
            }.toString()
        } ?: ""

        topAppBarState.update(
            title = title,
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