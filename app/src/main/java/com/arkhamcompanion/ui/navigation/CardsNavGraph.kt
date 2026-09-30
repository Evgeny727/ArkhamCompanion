package com.arkhamcompanion.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.arkhamcompanion.R
import com.arkhamcompanion.ui.cards.CardDetailsScreen
import com.arkhamcompanion.ui.cards.CardDetailsViewModel
import com.arkhamcompanion.ui.cards.CardTabooHistoryScreen
import com.arkhamcompanion.ui.cards.Cards
import com.arkhamcompanion.ui.cards.CardsFiltersActionsScreen
import com.arkhamcompanion.ui.cards.CardsFiltersAssetsScreen
import com.arkhamcompanion.ui.cards.CardsFiltersCardsAccessScreen
import com.arkhamcompanion.ui.cards.CardsFiltersEncountersScreen
import com.arkhamcompanion.ui.cards.CardsFiltersEnemiesScreen
import com.arkhamcompanion.ui.cards.CardsFiltersIllustratorsScreen
import com.arkhamcompanion.ui.cards.CardsFiltersInvestigatorAccessScreen
import com.arkhamcompanion.ui.cards.CardsFiltersLocationsScreen
import com.arkhamcompanion.ui.cards.CardsFiltersPacksScreen
import com.arkhamcompanion.ui.cards.CardsFiltersScreen
import com.arkhamcompanion.ui.cards.CardsFiltersSlotsScreen
import com.arkhamcompanion.ui.cards.CardsFiltersSubTypesScreen
import com.arkhamcompanion.ui.cards.CardsFiltersTraitsScreen
import com.arkhamcompanion.ui.cards.CardsFiltersTypesScreen
import com.arkhamcompanion.ui.cards.CardsFiltersUsesScreen
import com.arkhamcompanion.ui.cards.CardsFiltersViewModel
import com.arkhamcompanion.ui.cards.CardsScreen
import com.arkhamcompanion.ui.cards.CardsSortScreen
import com.arkhamcompanion.ui.cards.CardsSortViewModel
import com.arkhamcompanion.ui.cards.CardsViewModel
import com.arkhamcompanion.ui.cards.filters.CardsFiltersActionsScreen
import com.arkhamcompanion.ui.cards.filters.CardsFiltersAssetsScreen
import com.arkhamcompanion.ui.cards.filters.CardsFiltersCardsAccessScreen
import com.arkhamcompanion.ui.cards.filters.CardsFiltersEncounterSetsScreen
import com.arkhamcompanion.ui.cards.filters.CardsFiltersEnemiesScreen
import com.arkhamcompanion.ui.cards.filters.CardsFiltersIllustratorsScreen
import com.arkhamcompanion.ui.cards.filters.CardsFiltersInvestigatorAccessScreen
import com.arkhamcompanion.ui.cards.filters.CardsFiltersLocationsScreen
import com.arkhamcompanion.ui.cards.filters.CardsFiltersPacksScreen
import com.arkhamcompanion.ui.cards.filters.CardsFiltersScreen
import com.arkhamcompanion.ui.cards.filters.CardsFiltersSlotsScreen
import com.arkhamcompanion.ui.cards.filters.CardsFiltersSubTypesScreen
import com.arkhamcompanion.ui.cards.filters.CardsFiltersTraitsScreen
import com.arkhamcompanion.ui.cards.filters.CardsFiltersTypesScreen
import com.arkhamcompanion.ui.cards.filters.CardsFiltersUsesScreen
import com.arkhamcompanion.ui.components.ArkhamSwitch
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.ARKHAM_BUILD_CARD_URL
import com.arkhamcompanion.ui.utils.openLink

fun NavGraphBuilder.cardsGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
) {
    navigation<BottomBarItem.Cards>(
        startDestination = BottomBarItem.Cards.startDestination
    ) {
        composable<Cards> {
            val topAppBarState = LocalTopAppBarState.current
            val cardsViewModel = hiltViewModel<CardsViewModel>()
            val spoilerState by cardsViewModel.spoilerState.collectAsState()
            val isFiltersActive by cardsViewModel.isFiltersActive.collectAsState()

            CardsScreen(
                viewModel = cardsViewModel,
                onCardClick = { code ->
                    navController.navigateSingleTop(CardDetailsScreen(code))
                },
                innerPadding = innerPadding
            )

            topAppBarState.update(
                title = stringResource(if (spoilerState) R.string.encounter_cards
                    else R.string.player_cards),
                subtitle = null,
                color = null,
                contentColor = null,
                rightActions = {
                    ArkhamAppBarAction(
                        contentColor = CustomTheme.colors.m,
                        onClick = { navController.navigateSingleTop(CardsFiltersScreen) },
                        iconGlyph = AppIcon.Filter,
                        isActive = isFiltersActive
                    )
                    ArkhamAppBarAction(
                        contentColor = CustomTheme.colors.m,
                        onClick = { navController.navigateSingleTop(CardsSortScreen) },
                        iconGlyph = AppIcon.Sort,
                    )
                },
                leftAction = {
                    ArkhamSwitch(
                        value = spoilerState,
                        onValueChange = cardsViewModel::toggleSpoiler
                    )
                }
            )
        }

        composable<CardsSortScreen> { backStackEntry ->
            val topAppBarState = LocalTopAppBarState.current
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

            topAppBarState.update(
                title = stringResource(R.string.sort),
                subtitle = null,
                color = null,
                contentColor = null,
                rightActions = {
                    ArkhamAppBarAction(
                        contentColor = CustomTheme.colors.m,
                        onClick = cardsSortViewModel::clearSortOptions,
                        iconGlyph = AppIcon.Trash,
                    )
                },
                leftAction = { color ->
                    ArkhamAppBarAction(
                        contentColor = color,
                        onClick = navController::navigateUp,
                        iconGlyph = AppIcon.ArrowBack,
                    )
                },
            )
        }

        composable<CardsFiltersScreen> { backStackEntry ->
            val topAppBarState = LocalTopAppBarState.current
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Cards>()
            }
            val cardsViewModel: CardsViewModel = hiltViewModel(parentEntry)
            val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel()
            val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()

            CardsFiltersScreen(
                cardsViewModel = cardsViewModel,
                cardsFiltersViewModel = cardsFiltersViewModel,
                navigateTo = { navController.navigateSingleTop(it) },
                innerPadding = innerPadding
            )

            topAppBarState.update(
                title = stringResource(R.string.filters),
                subtitle = pluralStringResource(
                    R.plurals.count_card,
                    allCardCodes.size,
                    allCardCodes.size
                ),
                color = null,
                contentColor = null,
                rightActions = {
                    ArkhamAppBarAction(
                        contentColor = CustomTheme.colors.m,
                        onClick = cardsViewModel::clearCardFilters,
                        iconGlyph = AppIcon.FilterClear,
                    )
                },
                leftAction = { color ->
                    ArkhamAppBarAction(
                        contentColor = color,
                        onClick = navController::navigateUp,
                        iconGlyph = AppIcon.ArrowBack,
                    )
                },
            )
        }

        composable<CardsFiltersTypesScreen> { backStackEntry ->
            val topAppBarState = LocalTopAppBarState.current
            val parentCardsEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Cards>()
            }
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<CardsFiltersScreen>()
            }
            val cardsViewModel: CardsViewModel = hiltViewModel(parentCardsEntry)
            val filters by cardsViewModel.cardFilters.collectAsState()
            val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()
            val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel(parentEntry)

            CardsFiltersTypesScreen(
                selectedTypes = filters.types,
                cardsFiltersViewModel = cardsFiltersViewModel,
                onTypeChange = cardsViewModel::updateTypes,
                innerPadding = innerPadding
            )

            topAppBarState.update(
                title = stringResource(R.string.types),
                subtitle = pluralStringResource(
                    R.plurals.count_card,
                    allCardCodes.size,
                    allCardCodes.size
                ),
                rightActions = {
                    ArkhamAppBarAction(
                        contentColor = CustomTheme.colors.m,
                        onClick = cardsViewModel::clearTypesFilter,
                        iconGlyph = AppIcon.FilterClear,
                    )
                },
                leftAction = { color ->
                    ArkhamAppBarAction(
                        contentColor = color,
                        onClick = navController::navigateUp,
                        iconGlyph = AppIcon.ArrowBack,
                    )
                },
            )
        }

        composable<CardsFiltersSubTypesScreen> { backStackEntry ->
            val topAppBarState = LocalTopAppBarState.current
            val parentCardsEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Cards>()
            }
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<CardsFiltersScreen>()
            }
            val cardsViewModel: CardsViewModel = hiltViewModel(parentCardsEntry)
            val filters by cardsViewModel.cardFilters.collectAsState()
            val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()
            val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel(parentEntry)

            CardsFiltersSubTypesScreen(
                selectedSubTypes = filters.subTypes,
                cardsFiltersViewModel = cardsFiltersViewModel,
                onSubTypeChange = cardsViewModel::updateSubTypes,
                innerPadding = innerPadding
            )

            topAppBarState.update(
                title = stringResource(R.string.subtypes),
                subtitle = pluralStringResource(
                    R.plurals.count_card,
                    allCardCodes.size,
                    allCardCodes.size
                ),
                rightActions = {
                    ArkhamAppBarAction(
                        contentColor = CustomTheme.colors.m,
                        onClick = cardsViewModel::clearSubTypesFilter,
                        iconGlyph = AppIcon.FilterClear,
                    )
                },
                leftAction = { color ->
                    ArkhamAppBarAction(
                        contentColor = color,
                        onClick = navController::navigateUp,
                        iconGlyph = AppIcon.ArrowBack,
                    )
                },
            )
        }

        composable<CardsFiltersInvestigatorAccessScreen> { backStackEntry ->
            val topAppBarState = LocalTopAppBarState.current
            val parentCardsEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Cards>()
            }
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<CardsFiltersScreen>()
            }
            val cardsViewModel: CardsViewModel = hiltViewModel(parentCardsEntry)
            val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()
            val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel(parentEntry)

            CardsFiltersInvestigatorAccessScreen(
                cardsFiltersViewModel = cardsFiltersViewModel,
                onInvestigatorSelect = { config, deckOptions, deckRequirements, sideDeckOptions, sideDeckRequirements ->
                    cardsViewModel.setCardpoolFilter(
                        config,
                        deckOptions,
                        deckRequirements,
                        sideDeckOptions,
                        sideDeckRequirements
                    )

                    navController.navigateUp()
                },
                innerPadding = innerPadding
            )

            topAppBarState.update(
                title = stringResource(R.string.investigators),
                subtitle = pluralStringResource(
                    R.plurals.count_card,
                    allCardCodes.size,
                    allCardCodes.size
                ),
                rightActions = {
                    ArkhamAppBarAction(
                        contentColor = CustomTheme.colors.m,
                        onClick = cardsViewModel::clearCardpoolFilter,
                        iconGlyph = AppIcon.FilterClear,
                    )
                },
                leftAction = { color ->
                    ArkhamAppBarAction(
                        contentColor = color,
                        onClick = navController::navigateUp,
                        iconGlyph = AppIcon.ArrowBack,
                    )
                },
            )
        }

        composable<CardsFiltersCardsAccessScreen> { backStackEntry ->
            val topAppBarState = LocalTopAppBarState.current
            val parentCardsEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Cards>()
            }
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<CardsFiltersScreen>()
            }
            val cardsViewModel: CardsViewModel = hiltViewModel(parentCardsEntry)
            val filters by cardsViewModel.cardFilters.collectAsState()
            val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()
            val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel(parentEntry)

            CardsFiltersCardsAccessScreen(
                selectedCards = filters.whoCanTakeCard,
                cardsFiltersViewModel = cardsFiltersViewModel,
                onCardToggle = cardsViewModel::toggleWhoCanTakeCardFilter,
                innerPadding = innerPadding
            )

            topAppBarState.update(
                title = stringResource(R.string.cards),
                subtitle = pluralStringResource(
                    R.plurals.count_card,
                    allCardCodes.size,
                    allCardCodes.size
                ),
                rightActions = {
                    ArkhamAppBarAction(
                        contentColor = CustomTheme.colors.m,
                        onClick = cardsViewModel::clearWhoCanTakeCardFilter,
                        iconGlyph = AppIcon.FilterClear,
                    )
                },
                leftAction = { color ->
                    ArkhamAppBarAction(
                        contentColor = color,
                        onClick = navController::navigateUp,
                        iconGlyph = AppIcon.ArrowBack,
                    )
                },
            )
        }

        composable<CardsFiltersActionsScreen> { backStackEntry ->
            val topAppBarState = LocalTopAppBarState.current
            val parentCardsEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Cards>()
            }
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<CardsFiltersScreen>()
            }
            val cardsViewModel: CardsViewModel = hiltViewModel(parentCardsEntry)
            val filters by cardsViewModel.cardFilters.collectAsState()
            val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()
            val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel(parentEntry)

            CardsFiltersActionsScreen(
                selectedActions = filters.actions,
                cardsFiltersViewModel = cardsFiltersViewModel,
                onActionChange = cardsViewModel::updateActions,
                innerPadding = innerPadding
            )

            topAppBarState.update(
                title = stringResource(R.string.actions),
                subtitle = pluralStringResource(
                    R.plurals.count_card,
                    allCardCodes.size,
                    allCardCodes.size
                ),
                rightActions = {
                    ArkhamAppBarAction(
                        contentColor = CustomTheme.colors.m,
                        onClick = cardsViewModel::clearActionsFilter,
                        iconGlyph = AppIcon.FilterClear,
                    )
                },
                leftAction = { color ->
                    ArkhamAppBarAction(
                        contentColor = color,
                        onClick = navController::navigateUp,
                        iconGlyph = AppIcon.ArrowBack,
                    )
                },
            )
        }

        composable<CardsFiltersTraitsScreen> { backStackEntry ->
            val topAppBarState = LocalTopAppBarState.current
            val parentCardsEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Cards>()
            }
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<CardsFiltersScreen>()
            }
            val cardsViewModel: CardsViewModel = hiltViewModel(parentCardsEntry)
            val filters by cardsViewModel.cardFilters.collectAsState()
            val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()
            val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel(parentEntry)

            CardsFiltersTraitsScreen(
                selectedTraits = filters.traits,
                cardsFiltersViewModel = cardsFiltersViewModel,
                onTraitChange = cardsViewModel::updateTraits,
                innerPadding = innerPadding
            )

            topAppBarState.update(
                title = stringResource(R.string.traits),
                subtitle = pluralStringResource(
                    R.plurals.count_card,
                    allCardCodes.size,
                    allCardCodes.size
                ),
                rightActions = {
                    ArkhamAppBarAction(
                        contentColor = CustomTheme.colors.m,
                        onClick = cardsViewModel::clearTraitsFilter,
                        iconGlyph = AppIcon.FilterClear,
                    )
                },
                leftAction = { color ->
                    ArkhamAppBarAction(
                        contentColor = color,
                        onClick = navController::navigateUp,
                        iconGlyph = AppIcon.ArrowBack,
                    )
                },
            )
        }

        composable<CardsFiltersAssetsScreen> { backStackEntry ->
            val topAppBarState = LocalTopAppBarState.current
            val parentCardsEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Cards>()
            }
            val cardsViewModel: CardsViewModel = hiltViewModel(parentCardsEntry)
            val filters by cardsViewModel.cardFilters.collectAsState()
            val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()

            CardsFiltersAssetsScreen(
                assetFilter = filters.assetFilter,
                onSkillBoostChange = cardsViewModel::updateSkillBoostsFilter,
                navigateTo = { navController.navigateSingleTop(it) },
                innerPadding = innerPadding
            )

            topAppBarState.update(
                title = stringResource(R.string.asset_filters),
                subtitle = pluralStringResource(
                    R.plurals.count_card,
                    allCardCodes.size,
                    allCardCodes.size
                ),
                rightActions = {
                    ArkhamAppBarAction(
                        contentColor = CustomTheme.colors.m,
                        onClick = cardsViewModel::clearAssetsFilter,
                        iconGlyph = AppIcon.FilterClear,
                    )
                },
                leftAction = { color ->
                    ArkhamAppBarAction(
                        contentColor = color,
                        onClick = navController::navigateUp,
                        iconGlyph = AppIcon.ArrowBack,
                    )
                },
            )
        }

        composable<CardsFiltersSlotsScreen> { backStackEntry ->
            val topAppBarState = LocalTopAppBarState.current
            val parentCardsEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Cards>()
            }
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<CardsFiltersScreen>()
            }
            val cardsViewModel: CardsViewModel = hiltViewModel(parentCardsEntry)
            val filters by cardsViewModel.cardFilters.collectAsState()
            val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()
            val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel(parentEntry)

            CardsFiltersSlotsScreen(
                selectedSlots = filters.assetFilter.slots,
                cardsFiltersViewModel = cardsFiltersViewModel,
                onSlotChange = cardsViewModel::updateSlots,
                innerPadding = innerPadding
            )

            topAppBarState.update(
                title = stringResource(R.string.slots),
                subtitle = pluralStringResource(
                    R.plurals.count_card,
                    allCardCodes.size,
                    allCardCodes.size
                ),
                rightActions = {
                    ArkhamAppBarAction(
                        contentColor = CustomTheme.colors.m,
                        onClick = cardsViewModel::clearSlotsFilter,
                        iconGlyph = AppIcon.FilterClear,
                    )
                },
                leftAction = { color ->
                    ArkhamAppBarAction(
                        contentColor = color,
                        onClick = navController::navigateUp,
                        iconGlyph = AppIcon.ArrowBack,
                    )
                },
            )
        }

        composable<CardsFiltersUsesScreen> { backStackEntry ->
            val topAppBarState = LocalTopAppBarState.current
            val parentCardsEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Cards>()
            }
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<CardsFiltersScreen>()
            }
            val cardsViewModel: CardsViewModel = hiltViewModel(parentCardsEntry)
            val filters by cardsViewModel.cardFilters.collectAsState()
            val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()
            val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel(parentEntry)

            CardsFiltersUsesScreen(
                selectedUses = filters.assetFilter.uses,
                cardsFiltersViewModel = cardsFiltersViewModel,
                onUseChange = cardsViewModel::updateUses,
                innerPadding = innerPadding
            )

            topAppBarState.update(
                title = stringResource(R.string.uses),
                subtitle = pluralStringResource(
                    R.plurals.count_card,
                    allCardCodes.size,
                    allCardCodes.size
                ),
                rightActions = {
                    ArkhamAppBarAction(
                        contentColor = CustomTheme.colors.m,
                        onClick = cardsViewModel::clearUsesFilter,
                        iconGlyph = AppIcon.FilterClear,
                    )
                },
                leftAction = { color ->
                    ArkhamAppBarAction(
                        contentColor = color,
                        onClick = navController::navigateUp,
                        iconGlyph = AppIcon.ArrowBack,
                    )
                },
            )
        }

        composable<CardsFiltersEnemiesScreen> { backStackEntry ->
            val topAppBarState = LocalTopAppBarState.current
            val parentCardsEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Cards>()
            }
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<CardsFiltersScreen>()
            }
            val cardsViewModel: CardsViewModel = hiltViewModel(parentCardsEntry)
            val filters by cardsViewModel.cardFilters.collectAsState()
            val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()
            val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel(parentEntry)

            CardsFiltersEnemiesScreen(
                enemyFilter = filters.enemyFilter,
                cardsFiltersViewModel = cardsFiltersViewModel,
                onEnemyFilterChange = cardsViewModel::updateEnemiesFilter,
                innerPadding = innerPadding
            )

            topAppBarState.update(
                title = stringResource(R.string.enemy_filters),
                subtitle = pluralStringResource(
                    R.plurals.count_card,
                    allCardCodes.size,
                    allCardCodes.size
                ),
                rightActions = {
                    ArkhamAppBarAction(
                        contentColor = CustomTheme.colors.m,
                        onClick = cardsViewModel::clearEnemiesFilter,
                        iconGlyph = AppIcon.FilterClear,
                    )
                },
                leftAction = { color ->
                    ArkhamAppBarAction(
                        contentColor = color,
                        onClick = navController::navigateUp,
                        iconGlyph = AppIcon.ArrowBack,
                    )
                },
            )
        }

        composable<CardsFiltersLocationsScreen> { backStackEntry ->
            val topAppBarState = LocalTopAppBarState.current
            val parentCardsEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Cards>()
            }
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<CardsFiltersScreen>()
            }
            val cardsViewModel: CardsViewModel = hiltViewModel(parentCardsEntry)
            val filters by cardsViewModel.cardFilters.collectAsState()
            val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()
            val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel(parentEntry)

            CardsFiltersLocationsScreen(
                locationFilter = filters.locationFilter,
                cardsFiltersViewModel = cardsFiltersViewModel,
                onLocationFilterChange = cardsViewModel::updateLocationsFilter,
                innerPadding = innerPadding
            )

            topAppBarState.update(
                title = stringResource(R.string.location_filters),
                subtitle = pluralStringResource(
                    R.plurals.count_card,
                    allCardCodes.size,
                    allCardCodes.size
                ),
                rightActions = {
                    ArkhamAppBarAction(
                        contentColor = CustomTheme.colors.m,
                        onClick = cardsViewModel::clearLocationsFilter,
                        iconGlyph = AppIcon.FilterClear,
                    )
                },
                leftAction = { color ->
                    ArkhamAppBarAction(
                        contentColor = color,
                        onClick = navController::navigateUp,
                        iconGlyph = AppIcon.ArrowBack,
                    )
                },
            )
        }

        composable<CardsFiltersEncountersScreen> { backStackEntry ->
            val topAppBarState = LocalTopAppBarState.current
            val parentCardsEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Cards>()
            }
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<CardsFiltersScreen>()
            }
            val cardsViewModel: CardsViewModel = hiltViewModel(parentCardsEntry)
            val filters by cardsViewModel.cardFilters.collectAsState()
            val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()
            val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel(parentEntry)

            CardsFiltersEncounterSetsScreen(
                selectedEncounterSets = filters.encounterSets,
                cardsFiltersViewModel = cardsFiltersViewModel,
                onEncounterSetChange = cardsViewModel::updateEncounterSetsFilter,
                innerPadding = innerPadding
            )

            topAppBarState.update(
                title = stringResource(R.string.encounter_sets),
                subtitle = pluralStringResource(
                    R.plurals.count_card,
                    allCardCodes.size,
                    allCardCodes.size
                ),
                rightActions = {
                    ArkhamAppBarAction(
                        contentColor = CustomTheme.colors.m,
                        onClick = cardsViewModel::clearEncounterSetsFilter,
                        iconGlyph = AppIcon.FilterClear,
                    )
                },
                leftAction = { color ->
                    ArkhamAppBarAction(
                        contentColor = color,
                        onClick = navController::navigateUp,
                        iconGlyph = AppIcon.ArrowBack,
                    )
                },
            )
        }

        composable<CardsFiltersPacksScreen> { backStackEntry ->
            val topAppBarState = LocalTopAppBarState.current
            val parentCardsEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Cards>()
            }
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<CardsFiltersScreen>()
            }
            val cardsViewModel: CardsViewModel = hiltViewModel(parentCardsEntry)
            val filters by cardsViewModel.cardFilters.collectAsState()
            val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()
            val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel(parentEntry)
            val allPacks by cardsFiltersViewModel.packs.collectAsState()

            CardsFiltersPacksScreen(
                selectedPacks = filters.packs,
                allPacks = allPacks,
                onPacksChange = cardsViewModel::updatePacksFilter,
                innerPadding = innerPadding
            )

            topAppBarState.update(
                title = stringResource(R.string.pack_filters),
                subtitle = pluralStringResource(
                    R.plurals.count_card,
                    allCardCodes.size,
                    allCardCodes.size
                ),
                rightActions = {
                    ArkhamAppBarAction(
                        contentColor = CustomTheme.colors.m,
                        onClick = cardsViewModel::clearPacksFilter,
                        iconGlyph = AppIcon.FilterClear,
                    )
                },
                leftAction = { color ->
                    ArkhamAppBarAction(
                        contentColor = color,
                        onClick = navController::navigateUp,
                        iconGlyph = AppIcon.ArrowBack,
                    )
                },
            )
        }

        composable<CardsFiltersIllustratorsScreen> { backStackEntry ->
            val topAppBarState = LocalTopAppBarState.current
            val parentCardsEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Cards>()
            }
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<CardsFiltersScreen>()
            }
            val cardsViewModel: CardsViewModel = hiltViewModel(parentCardsEntry)
            val filters by cardsViewModel.cardFilters.collectAsState()
            val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()
            val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel(parentEntry)

            CardsFiltersIllustratorsScreen(
                selectedIllustrators = filters.illustrators,
                cardsFiltersViewModel = cardsFiltersViewModel,
                onIllustratorChange = cardsViewModel::updateIllustratorsFilter,
                innerPadding = innerPadding
            )

            topAppBarState.update(
                title = stringResource(R.string.illustrators),
                subtitle = pluralStringResource(
                    R.plurals.count_card,
                    allCardCodes.size,
                    allCardCodes.size
                ),
                rightActions = {
                    ArkhamAppBarAction(
                        contentColor = CustomTheme.colors.m,
                        onClick = cardsViewModel::clearIllustratorsFilter,
                        iconGlyph = AppIcon.FilterClear,
                    )
                },
                leftAction = { color ->
                    ArkhamAppBarAction(
                        contentColor = color,
                        onClick = navController::navigateUp,
                        iconGlyph = AppIcon.ArrowBack,
                    )
                },
            )
        }

        composable<CardDetailsScreen> { backStackEntry ->
            val topAppBarState = LocalTopAppBarState.current
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Cards>()
            }
            val destination = backStackEntry.toRoute<CardDetailsScreen>()

            val cardsViewModel: CardsViewModel = hiltViewModel(parentEntry)
            val cardDetailsViewModel: CardDetailsViewModel = hiltViewModel()

            val cardsLazyCodes by cardsViewModel.searchResultCodes.collectAsState()

            var currentCardCode by rememberSaveable {
                mutableStateOf<String?>(destination.cardCode)
            }
            val context = LocalContext.current

            CardDetailsScreen(
                cardCode = destination.cardCode,
                cardCodes = cardsLazyCodes,
                cardDetailsViewModel = cardDetailsViewModel,
                onCurrentCardCodeChanged = { currentCardCode = it },
                onTabooNavigation = { code, name ->
                    navController.navigateSingleTop(
                        CardTabooHistoryScreen(code, name)
                    )
                },
                onShowInvestigatorCardpool = { config, deckOptions, deckRequirements, sideDeckOptions, sideDeckRequirements ->
                    cardsViewModel.setCardpoolFilter(
                        config,
                        deckOptions,
                        deckRequirements,
                        sideDeckOptions,
                        sideDeckRequirements
                    )
                    navController.navigateUp()
                },
                onShowWhoCanTakeCard = { fields ->
                    fields?.run { cardsViewModel.toggleWhoCanTakeCardFilter(this) }
                    navController.navigateUp()
                },
                innerPadding = innerPadding
            )

            topAppBarState.update(
                title = "",
                subtitle = null,
                color = null,
                contentColor = null,
                leftAction = { color ->
                    ArkhamAppBarAction(
                        contentColor = color,
                        onClick = navController::navigateUp,
                        iconGlyph = AppIcon.ArrowBack,
                    )
                },
                rightActions = currentCardCode?.takeUnless {
                    //Hide button for fanmade cards and handle legacy investigator
                    it.startsWith("z") || it == "custom_001"
                }?.let {
                    { code ->
                        ArkhamAppBarAction(
                            contentColor = CustomTheme.colors.m,
                            onClick = { context.openLink(ARKHAM_BUILD_CARD_URL + code) },
                            iconGlyph = AppIcon.World,
                        )
                    }
                },
            )
        }

        composable<CardTabooHistoryScreen> { backStackEntry ->
            val topAppBarState = LocalTopAppBarState.current
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<CardDetailsScreen>()
            }
            val destination = backStackEntry.toRoute<CardTabooHistoryScreen>()

            val cardDetailsViewModel: CardDetailsViewModel = hiltViewModel(parentEntry)

            CardTabooHistoryScreen(
                cardCode = destination.cardCode,
                cardDetailsViewModel = cardDetailsViewModel,
                innerPadding = innerPadding
            )

            topAppBarState.update(
                title = destination.cardName,
                subtitle = stringResource(R.string.taboos),
                color = null,
                contentColor = null,
                leftAction = { color ->
                    ArkhamAppBarAction(
                        contentColor = color,
                        onClick = navController::navigateUp,
                        iconGlyph = AppIcon.ArrowBack,
                    )
                },
                rightActions = null,
            )
        }
    }
}