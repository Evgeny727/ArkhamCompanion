package com.arkhamcompanion.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.arkhamcompanion.AppViewModel
import com.arkhamcompanion.CardsCacheState
import com.arkhamcompanion.CardsSyncState
import com.arkhamcompanion.R
import com.arkhamcompanion.ui.campaigns.Campaigns
import com.arkhamcompanion.ui.campaigns.CampaignsScreen
import com.arkhamcompanion.ui.cards.CardDetailsScreen
import com.arkhamcompanion.ui.cards.CardDetailsViewModel
import com.arkhamcompanion.ui.cards.Cards
import com.arkhamcompanion.ui.cards.CardsFiltersScreen
import com.arkhamcompanion.ui.cards.CardsFiltersViewModel
import com.arkhamcompanion.ui.cards.CardsScreen
import com.arkhamcompanion.ui.cards.CardsSortScreen
import com.arkhamcompanion.ui.cards.CardsSortViewModel
import com.arkhamcompanion.ui.cards.CardsViewModel
import com.arkhamcompanion.ui.components.ArkhamAlertButton
import com.arkhamcompanion.ui.components.ArkhamAlertButtonStyle
import com.arkhamcompanion.ui.components.ArkhamAlertDialog
import com.arkhamcompanion.ui.components.ArkhamSwitch
import com.arkhamcompanion.ui.decks.Decks
import com.arkhamcompanion.ui.decks.DecksScreen
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
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.theme.LocalLanguage
import com.arkhamcompanion.ui.utils.applyScaffoldPaddings
import com.arkhamcompanion.ui.utils.resolveExceptionToStringResId

@Composable
fun ArkhamNavHost(viewModel: AppViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val cardsState by viewModel.cardsSyncState.collectAsState()
    val cardsCacheState by viewModel.cardsCacheState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val activity = LocalActivity.current
    BackHandler {
        if (!navController.navigateUp()) activity?.finish()
    }

    //TopAppBar values
    var title by rememberSaveable { mutableStateOf("") }
    var subtitle by rememberSaveable { mutableStateOf<String?>(null) }
    val baseColor = CustomTheme.colors.background
    val baseContentColor = CustomTheme.colors.d30
    var color by remember { mutableStateOf(baseColor) }
    var contentColor by remember { mutableStateOf(baseContentColor) }
    var rightActions: @Composable (RowScope.(Color) -> Unit)? by remember { mutableStateOf(null) }
    var leftAction: @Composable ((Color) -> Unit)? by remember { mutableStateOf(null) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars.union(WindowInsets.navigationBars)),
        containerColor = CustomTheme.colors.background,
        topBar = {
            ArkhamTopAppBar(
                title = title,
                subtitle = subtitle,
                color = color,
                contentColor = contentColor,
                leftAction = leftAction,
                rightActions = rightActions,
            )
        },
        bottomBar = {
            ArkhamNavigationBar(
                navController = navController,
                currentDestination = currentDestination
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) { data ->
            Snackbar(
                data,
                containerColor = CustomTheme.colors.d30,
                contentColor = CustomTheme.colors.l30
            )
        } },
    ) { innerPadding ->
        val languageTag = LocalLanguage.current.languageTag
        val resources = LocalResources.current
        LaunchedEffect(Unit) {
            viewModel.checkIfCardsReady(languageTag)
            viewModel.errors.collect { error ->
                val message = when (val id = error.exception.resolveExceptionToStringResId()) {
                    null -> error.exception.localizedMessage
                    else -> resources.getString(id)
                }
                snackbarHostState.showSnackbar(message)
            }
        }
        if (cardsState is CardsSyncState.UpdateAvailable) {
            ArkhamAlertDialog(
                title = stringResource(R.string.new_cards_available),
                description = stringResource(R.string.these_cards_might_have_been_updated),
                onDismiss = viewModel::cancelCardsUpdate,
            ) {
                ArkhamAlertButton(
                    text = stringResource(R.string.not_now),
                    style = ArkhamAlertButtonStyle.CANCEL,
                    loading = cardsCacheState is CardsCacheState.Loading,
                    onClick = viewModel::cancelCardsUpdate
                )
                ArkhamAlertButton(
                    text = stringResource(R.string.download_cards),
                    loading = cardsCacheState is CardsCacheState.Loading,
                ) { viewModel.confirmCardsUpdate(languageTag) }
            }
        }
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            NavHost(
                navController = navController,
                startDestination = BottomBarItem.Cards,
                enterTransition = {
                    if (initialState.destination.parent == targetState.destination.parent) {
                        fadeIn(
                            animationSpec = tween(300, easing = LinearEasing)
                        ) + slideIntoContainer(
                            animationSpec = tween(300, easing = EaseIn),
                            towards = AnimatedContentTransitionScope.SlideDirection.Start
                        )
                    } else {
                        EnterTransition.None
                    }
                },
                exitTransition = {
                    if (initialState.destination.parent == targetState.destination.parent) {
                        fadeOut(
                            animationSpec = tween(400, easing = LinearEasing)
                        ) + slideOutOfContainer(
                            animationSpec = tween(400, easing = EaseOut),
                            towards = AnimatedContentTransitionScope.SlideDirection.End
                        )
                    } else {
                        ExitTransition.None
                    }
                }
            ) {
                navigation<BottomBarItem.Settings>(
                    startDestination = BottomBarItem.Settings.startDestination
                ) {
                    composable<Settings> {
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

                        title = stringResource(BottomBarItem.Settings.label)
                        subtitle = null
                        color = baseColor
                        contentColor = baseContentColor
                        rightActions = null
                        leftAction = null
                    }
                    composable<SettingsCollection> { backStackEntry ->
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

                        title = stringResource(R.string.edit_collection)
                        subtitle = null
                        color = baseColor
                        contentColor = baseContentColor
                        rightActions = null
                        leftAction = { color ->
                            ArkhamAppBarAction(
                                contentColor = color,
                                onClick = navController::navigateUp,
                                iconGlyph = AppIcon.ArrowBack,
                            )
                        }
                    }
                    composable<SettingsAbout> {

                        AboutScreen(innerPadding)

                        title = stringResource(R.string.about_arkham_companion)
                        subtitle = null
                        color = baseColor
                        contentColor = baseContentColor
                        rightActions = null
                        leftAction = { color ->
                            ArkhamAppBarAction(
                                contentColor = color,
                                onClick = navController::navigateUp,
                                iconGlyph = AppIcon.ArrowBack,
                            )
                        }
                    }
                    composable<SettingsBackup> {

                        //TODO: add backup screen
                        BackUpScreen(innerPadding)

                        title = stringResource(R.string.backup_data)
                        subtitle = null
                        color = baseColor
                        contentColor = baseContentColor
                        rightActions = null
                        leftAction = { color ->
                            ArkhamAppBarAction(
                                contentColor = color,
                                onClick = navController::navigateUp,
                                iconGlyph = AppIcon.ArrowBack,
                            )
                        }
                    }
                    composable<SettingsDiagnostics> { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry<Settings>()
                        }
                        val settingsViewModel = hiltViewModel<SettingsViewModel>(parentEntry)

                        DiagnosticsScreen(
                            settingsViewModel = settingsViewModel,
                            recreateCache = viewModel::recreateCardsCache,
                            innerPadding = innerPadding
                        )

                        title = stringResource(R.string.diagnostics)
                        subtitle = null
                        color = baseColor
                        contentColor = baseContentColor
                        rightActions = null
                        leftAction = { color ->
                            ArkhamAppBarAction(
                                contentColor = color,
                                onClick = navController::navigateUp,
                                iconGlyph = AppIcon.ArrowBack,
                            )
                        }
                    }
                }
                navigation<BottomBarItem.Cards>(
                    startDestination = BottomBarItem.Cards.startDestination
                ) {
                    composable<Cards> {

                        val cardsViewModel = hiltViewModel<CardsViewModel>()
                        val spoilerState by cardsViewModel.spoilerState.collectAsState()

                        CardsScreen(
                            viewModel = cardsViewModel,
                            emitError = viewModel::emitError,
                            onCardClick = { code ->
                                navController.navigateSingleTop(CardDetailsScreen(code))
                            },
                            innerPadding = innerPadding
                        )

                        title = stringResource(if (spoilerState) R.string.encounter_cards
                            else R.string.player_cards)
                        subtitle = null
                        color = baseColor
                        contentColor = baseContentColor
                        rightActions = {
                            ArkhamAppBarAction(
                                contentColor = CustomTheme.colors.m,
                                onClick = { navController.navigateSingleTop(CardsFiltersScreen) },
                                iconGlyph = AppIcon.Filter,
                            )
                            ArkhamAppBarAction(
                                contentColor = CustomTheme.colors.m,
                                onClick = { navController.navigateSingleTop(CardsSortScreen) },
                                iconGlyph = AppIcon.Sort,
                            )
                        }
                        leftAction = {
                            ArkhamSwitch(
                                value = spoilerState,
                                onValueChange = cardsViewModel::toggleSpoiler
                            )
                        }
                    }
                    composable<CardsSortScreen> { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry<Cards>()
                        }
                        val cardsViewModel: CardsViewModel = hiltViewModel(parentEntry)
                        val spoilerState by cardsViewModel.spoilerState.collectAsState()
                        val cardsSortViewModel = hiltViewModel<CardsSortViewModel>()

                        CardsSortScreen(
                            spoilerState = spoilerState,
                            navigateUp = navController::navigateUp,
                            cardsSortViewModel = cardsSortViewModel,
                            onApply = { newSortOptions ->
                                cardsSortViewModel.applyNewSortOptions(newSortOptions, spoilerState)
                                navController.navigateUp()
                            },
                            innerPadding = innerPadding
                        )

                        title = stringResource(R.string.sort)
                        subtitle = null
                        color = baseColor
                        contentColor = baseContentColor
                        rightActions = {
                            ArkhamAppBarAction(
                                contentColor = CustomTheme.colors.m,
                                onClick = cardsSortViewModel::clearSortOptions,
                                iconGlyph = AppIcon.Trash,
                            )
                        }
                        leftAction = { color ->
                            ArkhamAppBarAction(
                                contentColor = color,
                                onClick = navController::navigateUp,
                                iconGlyph = AppIcon.ArrowBack,
                            )
                        }
                    }
                    composable<CardsFiltersScreen> {backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry<Cards>()
                        }
                        val cardsViewModel: CardsViewModel = hiltViewModel(parentEntry)
                        val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel()
                        val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()

                        CardsFiltersScreen(
                            cardsViewModel = cardsViewModel,
                            cardsFiltersViewModel = cardsFiltersViewModel,
                            innerPadding = innerPadding
                        )

                        title = stringResource(R.string.filters)
                        subtitle = pluralStringResource(
                            R.plurals.count_card,
                            allCardCodes.size,
                            allCardCodes.size
                        )
                        color = baseColor
                        contentColor = baseContentColor
                        rightActions = {
                            ArkhamAppBarAction(
                                contentColor = CustomTheme.colors.m,
                                onClick = cardsViewModel::clearCardFilters,
                                iconGlyph = AppIcon.FilterClear,
                            )
                        }
                        leftAction = { color ->
                            ArkhamAppBarAction(
                                contentColor = color,
                                onClick = navController::navigateUp,
                                iconGlyph = AppIcon.ArrowBack,
                            )
                        }
                    }
                    composable<CardDetailsScreen> { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry<Cards>()
                        }
                        val destination = backStackEntry.toRoute<CardDetailsScreen>()

                        val cardsViewModel: CardsViewModel = hiltViewModel(parentEntry)
                        val cardDetailsViewModel: CardDetailsViewModel = hiltViewModel()

                        val cardsLazyCodes by cardsViewModel.searchResultCodes.collectAsState()

                        CardDetailsScreen(
                            cardCode = destination.cardCode,
                            cardCodes = cardsLazyCodes,
                            cardDetailsViewModel = cardDetailsViewModel,
                            innerPadding = innerPadding
                        )

                        title = ""
                        subtitle = null
                        color = baseColor
                        contentColor = baseContentColor
                        rightActions = null
                        leftAction = { color ->
                            ArkhamAppBarAction(
                                contentColor = color,
                                onClick = navController::navigateUp,
                                iconGlyph = AppIcon.ArrowBack,
                            )
                        }
                    }
                }
                navigation<BottomBarItem.Decks>(
                    startDestination = BottomBarItem.Decks.startDestination
                ) {
                    composable<Decks> {

                        DecksScreen(innerPadding)

                        title = stringResource(BottomBarItem.Decks.label)
                        subtitle = null
                        color = baseColor
                        contentColor = baseContentColor
                        rightActions = null
                        leftAction = null
                    }
                }
                navigation<BottomBarItem.Campaigns>(
                    startDestination = BottomBarItem.Campaigns.startDestination
                ) {
                    composable<Campaigns> {

                        CampaignsScreen(innerPadding)

                        title = stringResource(BottomBarItem.Campaigns.label)
                        subtitle = null
                        color = baseColor
                        contentColor = baseContentColor
                        rightActions = null
                        leftAction = null
                    }
                }
            }

            if (cardsState is CardsSyncState.Loading) {
                CardsDownloadingProgressIndicator((cardsState as CardsSyncState.Loading).progress)
            }

            AnimatedVisibility(
                modifier = Modifier.align(Alignment.BottomEnd),
                visible = cardsCacheState is CardsCacheState.Creating
            ) {
                CardsCacheLoading(innerPadding)
            }
        }
    }
}

internal fun <T: Any> NavHostController.navigateSingleTop(route: T) = navigate(route) {
    launchSingleTop = true
}

@Composable
private fun CardsCacheLoading(paddingValues: PaddingValues) {
    Surface(
        modifier = Modifier.applyScaffoldPaddings(paddingValues).padding(8.dp),
        color = CustomTheme.colors.d30,
        shape = CustomTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = CustomTheme.colors.m
            )
            Text(
                text = stringResource(R.string.creating_cards_cache),
                color = CustomTheme.colors.l30,
                style = CustomTheme.typography.text
            )
        }
    }
}

@Composable
private fun CardsDownloadingProgressIndicator(progress: Float) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CustomTheme.colors.background
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.loading_latest_cards),
                color = CustomTheme.colors.d30,
                style = CustomTheme.typography.text
            )
            Spacer(modifier = Modifier.height(8.dp))
            val animatedProgress by animateFloatAsState(
                targetValue = progress,
                animationSpec = tween(),
                label = ""
            )
            CustomLinearProgressBar(animatedProgress)
        }
    }
}

@Composable
private fun CustomLinearProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 20.dp,
) {
    Box(
        modifier = modifier
            .fillMaxWidth(0.6f)
            .height(height)
            .border(
                width = 2.dp,
                color = CustomTheme.colors.d10,
                shape = CustomTheme.shapes.small
            )
    ) {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .clip(CustomTheme.shapes.small)
                .background(CustomTheme.colors.d10)
        )
    }
}