package com.arkhamcompanion.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.arkhamcompanion.AppViewModel
import com.arkhamcompanion.CardsCacheState
import com.arkhamcompanion.CardsSyncState
import com.arkhamcompanion.R
import com.arkhamcompanion.ui.components.ArkhamAlertButton
import com.arkhamcompanion.ui.components.ArkhamAlertButtonStyle
import com.arkhamcompanion.ui.components.ArkhamAlertDialog
import com.arkhamcompanion.ui.navigation.cards.cardsEntries
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.theme.LocalLanguage
import com.arkhamcompanion.ui.utils.applyScaffoldPaddings
import com.arkhamcompanion.ui.utils.resolveExceptionToStringResId

val LocalTopAppBarState = compositionLocalOf { TopAppBarState() }

@Composable
fun ArkhamNavHost(viewModel: AppViewModel) {
    val navigationState = rememberNavigationState(
        startRoute = TopLevelRoute.Cards(),
        topLevelRoutes = topLevelRoutes,
    )
    val navigator = remember { Navigator(navigationState) }

    val cardsState by viewModel.cardsSyncState.collectAsState()
    val cardsCacheState by viewModel.cardsCacheState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val activity = LocalActivity.current
    BackHandler {
        if (!navigator.goBack()) activity?.finish()
    }

    //TopAppBar values
    val baseColor = CustomTheme.colors.background
    val baseContentColor = CustomTheme.colors.d30
    val topAppBarState = remember { TopAppBarState() }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars.union(WindowInsets.navigationBars)),
        containerColor = CustomTheme.colors.background,
        topBar = {
            ArkhamTopAppBar(
                title = topAppBarState.title,
                subtitle = topAppBarState.subtitle,
                color = topAppBarState.color ?: baseColor,
                contentColor = topAppBarState.contentColor ?: baseContentColor,
                leftAction = topAppBarState.leftAction,
                rightActions = topAppBarState.rightActions,
            )
        },
        bottomBar = {
            ArkhamNavigationBar(navigator = navigator)
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

        val entryProvider = entryProvider {
            settingsEntries(
                viewModel = viewModel,
                navigator = navigator,
            )

            cardsEntries(navigator = navigator)

            decksEntries(navigator = navigator)

            campaignsEntries(navigator = navigator)
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            CompositionLocalProvider(LocalTopAppBarState provides topAppBarState) {
                NavDisplay(
                    entries = navigationState.toEntries(entryProvider),
                    onBack = navigator::goBack,
                    transitionSpec = {
                        val targetKey = targetState.entries.lastOrNull()?.contentKey

                        val isTopLevelNavigation =
                            targetKey in topLevelRoutes.map { it.toContentKey() }

                        if (isTopLevelNavigation) {
                            fadeIn(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = LinearEasing,
                                )
                            ) togetherWith fadeOut(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = LinearEasing,
                                )
                            )
                        } else {
                            ContentTransform(
                                targetContentEnter = fadeIn(
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        easing = LinearEasing,
                                    )
                                ) + slideIntoContainer(
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        easing = EaseIn,
                                    ),
                                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                                ),
                                initialContentExit = fadeOut(
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        easing = LinearEasing,
                                    )
                                ) + slideOutOfContainer(
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        easing = EaseIn,
                                    ),
                                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                                )
                            )
                        }
                    },
                    popTransitionSpec = {
                        ContentTransform(
                            targetContentEnter = fadeIn(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = LinearEasing,
                                )
                            ) + slideIntoContainer(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = EaseIn,
                                ),
                                towards = AnimatedContentTransitionScope.SlideDirection.End,
                            ),
                            initialContentExit = fadeOut(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = LinearEasing,
                                )
                            ) + slideOutOfContainer(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = EaseIn,
                                ),
                                towards = AnimatedContentTransitionScope.SlideDirection.End,
                            )
                        )
                    },
                    predictivePopTransitionSpec = {
                        ContentTransform(
                            targetContentEnter = fadeIn(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = LinearEasing,
                                )
                            ) + slideIntoContainer(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = EaseIn,
                                ),
                                towards = AnimatedContentTransitionScope.SlideDirection.End,
                            ),
                            initialContentExit = fadeOut(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = LinearEasing,
                                )
                            ) + slideOutOfContainer(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = EaseIn,
                                ),
                                towards = AnimatedContentTransitionScope.SlideDirection.End,
                            )
                        )
                    },
                    modifier = Modifier.applyScaffoldPaddings(innerPadding),
                )
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

@Composable
inline fun <reified VM : ViewModel, reified Factory, K: Any> hiltAssistedViewModel(
    key: K,
    viewModelStoreOwner: ViewModelStoreOwner = LocalViewModelStoreOwner.current!!,
    crossinline create: Factory.(K) -> VM
): VM {
    return hiltViewModel<VM, Factory>(
        viewModelStoreOwner = viewModelStoreOwner,
        creationCallback = { it.create(key) }
    )
}

@Composable
private fun CardsCacheLoading(paddingValues: PaddingValues) {
    Surface(
        modifier = Modifier
            .applyScaffoldPaddings(paddingValues)
            .padding(8.dp),
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