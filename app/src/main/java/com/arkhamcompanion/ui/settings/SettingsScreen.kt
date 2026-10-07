package com.arkhamcompanion.ui.settings

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.ui.settings.components.CardsCard
import com.arkhamcompanion.ui.settings.components.SettingsCard
import com.arkhamcompanion.ui.settings.components.SocialsCard
import com.arkhamcompanion.ui.settings.components.SupportCard
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.theme.LocalLanguage

@Composable
fun SettingsScreen(
    theme: Int,
    viewModel: SettingsViewModel,
    onLanguageChange: (String) -> Unit,
    navigateToCollection: () -> Unit,
    updateCards: (String) -> Unit,
    navigateToAbout: () -> Unit,
    navigateToBackup: () -> Unit,
    navigateToDiagnostics: () -> Unit,
    emitError: (Throwable) -> Unit,
    modifier: Modifier = Modifier,
) {
    val settingsUiState by viewModel.settingsUiState.collectAsState()
    val showFanmadeCards by viewModel.showFanmadeCardsState.collectAsState()
    val showPreviewCards by viewModel.showPreviewCardsState.collectAsState()
    val includeEnglish by viewModel.isIncludeEnglishSearchResultsState.collectAsState()
    val allPacks by viewModel.allPacksState.collectAsState()
    val collection by viewModel.collectionState.collectAsState()
    val ignoreCollection by viewModel.ignoreCollectionState.collectAsState()
    val tabooSetId by viewModel.tabooSetIdState.collectAsState()
    val tabooSetsList by viewModel.tabooSetsListState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.errors.collect {
            emitError(it.exception)
        }
    }

    val activity = LocalActivity.current
    BackHandler {
        activity?.finish()
    }

    val languageTag = LocalLanguage.current.languageTag

    LazyColumn(
        modifier = modifier.fillMaxSize()
            .background(CustomTheme.colors.l10),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        //TODO: add account card
//        item("account_card") {
//            Text(text = "Settings", style = CustomTheme.typography.header)
//        }
        //TODO: add arkham.build account card
//        item("arkhambuild_account_card") {
//            Text(text = "Settings", style = CustomTheme.typography.header)
//        }
        item("cards_card") {
            CardsCard(
                onLanguageChange = onLanguageChange,
                allPacks = allPacks,
                collection = collection,
                ignoreCollection = ignoreCollection,
                navigateToCollection = navigateToCollection,
                setTaboo = viewModel::setTaboo,
                tabooSetId = tabooSetId,
                tabooSetsList = tabooSetsList,
                updateCards = updateCards,
                loading = settingsUiState is SettingsUiState.Loading
            )
        }

        item("settings_card") {
            SettingsCard(
                themeInt = theme,
                onThemeChange = viewModel::selectTheme,
                scaleFactor = CustomTheme.typography.scaleFactor,
                onScaleChange = viewModel::setScaleFactor,
                showFanmadeCards = showFanmadeCards,
                onFanmadeCardsChange = viewModel::setFanmadeCards,
                showPreviewCards = showPreviewCards,
                onPreviewCardsChange = viewModel::setPreviewCards,
                includeEnglishSearchResults = includeEnglish,
                onIncludeEnglishResultsChange = viewModel::setEnglishSearchResults,
                isLoading = settingsUiState is SettingsUiState.Loading,
            )
        }

        //TODO: add narration card
//        item("narration_card") {
//            Text(text = "Settings", style = CustomTheme.typography.header)
//        }

        item("socials_card") {
            SocialsCard(languageTag)
        }

        item("support_card") {
            SupportCard(
                navigateToAbout = navigateToAbout,
                navigateToBackUp = navigateToBackup,
                navigateToDiagnostics = navigateToDiagnostics,
            )
        }
    }
}