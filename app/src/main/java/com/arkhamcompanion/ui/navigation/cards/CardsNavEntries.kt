package com.arkhamcompanion.ui.navigation.cards

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.arkhamcompanion.R
import com.arkhamcompanion.ui.cards.CardDetailsScreen
import com.arkhamcompanion.ui.cards.CardDetailsViewModel
import com.arkhamcompanion.ui.cards.CardTabooHistoryScreen
import com.arkhamcompanion.ui.cards.CardTabooHistoryViewModel
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
import com.arkhamcompanion.ui.navigation.ArkhamAppBarAction
import com.arkhamcompanion.ui.navigation.LocalGraphSharedViewModelStoreOwner
import com.arkhamcompanion.ui.navigation.LocalSharedViewModelStoreOwner
import com.arkhamcompanion.ui.navigation.LocalTopAppBarState
import com.arkhamcompanion.ui.navigation.Navigator
import com.arkhamcompanion.ui.navigation.SharedViewModelStoreNavEntryDecorator
import com.arkhamcompanion.ui.navigation.TopLevelRoute
import com.arkhamcompanion.ui.navigation.hiltAssistedViewModel
import com.arkhamcompanion.ui.navigation.navigateSingleTop
import com.arkhamcompanion.ui.navigation.toContentKey
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.ARKHAM_BUILD_CARD_URL
import com.arkhamcompanion.ui.utils.openLink

fun EntryProviderScope<NavKey>.cardsEntries(navigator: Navigator) {
    entry<TopLevelRoute.Cards>(
        clazzContentKey = { key -> key.toContentKey() }
    ) { backStackEntry ->
        val topAppBarState = LocalTopAppBarState.current

        val graphState = backStackEntry.graphState

        val cardsViewModel = hiltAssistedViewModel<CardsViewModel, CardsViewModel.Factory, CardsGraphState>(
            key = backStackEntry.graphState
        ) { create(it) }
        val spoilerState by cardsViewModel.spoilerState.collectAsState()
        val isFiltersActive by cardsViewModel.isFiltersActive.collectAsState()

        CardsScreen(
            viewModel = cardsViewModel,
            onCardClick = { code ->
                navigator.navigateSingleTop(CardDetailsScreen(graphState, code))
            }
        )

        topAppBarState.update(
            title = stringResource(
                id = when (graphState) {
                    is CardsGraphState.Investigator -> R.string.allowed_cards

                    is CardsGraphState.Card -> R.string.investigators

                    is CardsGraphState.Main -> {
                        if (spoilerState) R.string.encounter_cards else R.string.player_cards
                    }
                }
            ),
            subtitle = null,
            color = null,
            contentColor = null,
            rightActions = {
                ArkhamAppBarAction(
                    contentColor = CustomTheme.colors.m,
                    onClick = { navigator.navigateSingleTop(CardsFiltersScreen(graphState)) },
                    iconGlyph = AppIcon.Filter,
                    isActive = isFiltersActive
                )

                ArkhamAppBarAction(
                    contentColor = CustomTheme.colors.m,
                    onClick = { navigator.navigateSingleTop(CardsSortScreen(spoilerState)) },
                    iconGlyph = AppIcon.Sort,
                )
            },
            leftAction = when (graphState) {
                is CardsGraphState.Main -> {
                    {
                        ArkhamSwitch(
                            value = spoilerState,
                            onValueChange = cardsViewModel::toggleSpoiler
                        )
                    }
                }

                else -> { color ->
                    ArkhamAppBarAction(
                        contentColor = color,
                        onClick = navigator::goBack,
                        iconGlyph = AppIcon.ArrowBack,
                    )
                }
            }
        )
    }

    entry<CardsSortScreen> { backStackEntry ->
        val topAppBarState = LocalTopAppBarState.current
        val spoilerState = backStackEntry.spoilerState
        val cardsSortViewModel = hiltViewModel<CardsSortViewModel>()

        CardsSortScreen(
            spoilerState = spoilerState,
            navigateUp = navigator::goBack,
            cardsSortViewModel = cardsSortViewModel,
            onApply = { newSortOptions ->
                cardsSortViewModel.applyNewSortOptions(newSortOptions, spoilerState)
                navigator.goBack()
            }
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
                    onClick = navigator::goBack,
                    iconGlyph = AppIcon.ArrowBack,
                )
            },
        )
    }

    entry<CardsFiltersScreen>(
        clazzContentKey = { key -> key.toContentKey() },
        metadata = { key -> SharedViewModelStoreNavEntryDecorator.parent(
            TopLevelRoute.Cards(key.parentConfig).toContentKey()
        )}
    ) { backStackEntry ->
        val topAppBarState = LocalTopAppBarState.current
        val cardsViewModel = hiltAssistedViewModel<CardsViewModel, CardsViewModel.Factory, CardsGraphState>(
            key = backStackEntry.parentConfig,
            viewModelStoreOwner = LocalSharedViewModelStoreOwner.current
        ) { create(it) }
        val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel()
        val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()

        CardsFiltersScreen(
            cardsViewModel = cardsViewModel,
            cardsFiltersViewModel = cardsFiltersViewModel,
            parentConfig = backStackEntry.parentConfig,
            navigateTo = { navigator.navigateSingleTop(it) }
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
                    onClick = navigator::goBack,
                    iconGlyph = AppIcon.ArrowBack,
                )
            },
        )
    }

    entry<CardsFiltersTypesScreen>(
        metadata = { key -> SharedViewModelStoreNavEntryDecorator.graphWithParent(
            graphKey = TopLevelRoute.Cards(key.parentConfig).toContentKey(),
            parentKey = CardsFiltersScreen(key.parentConfig).toContentKey()
        )}
    ) { backStackEntry ->
        val topAppBarState = LocalTopAppBarState.current
        val cardsViewModel = hiltAssistedViewModel<CardsViewModel, CardsViewModel.Factory, CardsGraphState>(
            key = backStackEntry.parentConfig,
            viewModelStoreOwner = LocalGraphSharedViewModelStoreOwner.current
        ) { create(it) }
        val filters by cardsViewModel.cardFilters.collectAsState()
        val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()
        val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel(
            LocalSharedViewModelStoreOwner.current
        )

        CardsFiltersTypesScreen(
            selectedTypes = filters.types,
            cardsFiltersViewModel = cardsFiltersViewModel,
            onTypeChange = cardsViewModel::updateTypes,
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
                    onClick = navigator::goBack,
                    iconGlyph = AppIcon.ArrowBack,
                )
            },
        )
    }

    entry<CardsFiltersSubTypesScreen>(
        metadata = { key -> SharedViewModelStoreNavEntryDecorator.graphWithParent(
            graphKey = TopLevelRoute.Cards(key.parentConfig).toContentKey(),
            parentKey = CardsFiltersScreen(key.parentConfig).toContentKey()
        )}
    ) { backStackEntry ->
        val topAppBarState = LocalTopAppBarState.current
        val cardsViewModel = hiltAssistedViewModel<CardsViewModel, CardsViewModel.Factory, CardsGraphState>(
            key = backStackEntry.parentConfig,
            viewModelStoreOwner = LocalGraphSharedViewModelStoreOwner.current
        ) { create(it) }
        val filters by cardsViewModel.cardFilters.collectAsState()
        val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()
        val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel(
            LocalSharedViewModelStoreOwner.current
        )

        CardsFiltersSubTypesScreen(
            selectedSubTypes = filters.subTypes,
            cardsFiltersViewModel = cardsFiltersViewModel,
            onSubTypeChange = cardsViewModel::updateSubTypes,
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
                    onClick = navigator::goBack,
                    iconGlyph = AppIcon.ArrowBack,
                )
            },
        )
    }

    entry<CardsFiltersInvestigatorAccessScreen>(
        metadata = { key -> SharedViewModelStoreNavEntryDecorator.graphWithParent(
            graphKey = TopLevelRoute.Cards(key.parentConfig).toContentKey(),
            parentKey = CardsFiltersScreen(key.parentConfig).toContentKey()
        )}
    ) { backStackEntry ->
        val topAppBarState = LocalTopAppBarState.current
        val cardsViewModel = hiltAssistedViewModel<CardsViewModel, CardsViewModel.Factory, CardsGraphState>(
            key = backStackEntry.parentConfig,
            viewModelStoreOwner = LocalGraphSharedViewModelStoreOwner.current
        ) { create(it) }
        val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()
        val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel(
            LocalSharedViewModelStoreOwner.current
        )

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

                navigator.goBack()
            },
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
                    onClick = navigator::goBack,
                    iconGlyph = AppIcon.ArrowBack,
                )
            },
        )
    }

    entry<CardsFiltersCardsAccessScreen>(
        metadata = { key -> SharedViewModelStoreNavEntryDecorator.graphWithParent(
            graphKey = TopLevelRoute.Cards(key.parentConfig).toContentKey(),
            parentKey = CardsFiltersScreen(key.parentConfig).toContentKey()
        )}
    ) { backStackEntry ->
        val topAppBarState = LocalTopAppBarState.current
        val cardsViewModel = hiltAssistedViewModel<CardsViewModel, CardsViewModel.Factory, CardsGraphState>(
            key = backStackEntry.parentConfig,
            viewModelStoreOwner = LocalGraphSharedViewModelStoreOwner.current
        ) { create(it) }
        val filters by cardsViewModel.cardFilters.collectAsState()
        val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()
        val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel(
            LocalSharedViewModelStoreOwner.current
        )

        CardsFiltersCardsAccessScreen(
            selectedCards = filters.whoCanTakeCard,
            cardsFiltersViewModel = cardsFiltersViewModel,
            onCardToggle = cardsViewModel::toggleWhoCanTakeCardFilter,
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
                    onClick = navigator::goBack,
                    iconGlyph = AppIcon.ArrowBack,
                )
            },
        )
    }

    entry<CardsFiltersActionsScreen>(
        metadata = { key -> SharedViewModelStoreNavEntryDecorator.graphWithParent(
            graphKey = TopLevelRoute.Cards(key.parentConfig).toContentKey(),
            parentKey = CardsFiltersScreen(key.parentConfig).toContentKey()
        )}
    ) { backStackEntry ->
        val topAppBarState = LocalTopAppBarState.current
        val cardsViewModel = hiltAssistedViewModel<CardsViewModel, CardsViewModel.Factory, CardsGraphState>(
            key = backStackEntry.parentConfig,
            viewModelStoreOwner = LocalGraphSharedViewModelStoreOwner.current
        ) { create(it) }
        val filters by cardsViewModel.cardFilters.collectAsState()
        val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()
        val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel(
            LocalSharedViewModelStoreOwner.current
        )

        CardsFiltersActionsScreen(
            selectedActions = filters.actions,
            cardsFiltersViewModel = cardsFiltersViewModel,
            onActionChange = cardsViewModel::updateActions
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
                    onClick = navigator::goBack,
                    iconGlyph = AppIcon.ArrowBack,
                )
            },
        )
    }

    entry<CardsFiltersTraitsScreen>(
        metadata = { key -> SharedViewModelStoreNavEntryDecorator.graphWithParent(
            graphKey = TopLevelRoute.Cards(key.parentConfig).toContentKey(),
            parentKey = CardsFiltersScreen(key.parentConfig).toContentKey()
        )}
    ) { backStackEntry ->
        val topAppBarState = LocalTopAppBarState.current
        val cardsViewModel = hiltAssistedViewModel<CardsViewModel, CardsViewModel.Factory, CardsGraphState>(
            key = backStackEntry.parentConfig,
            viewModelStoreOwner = LocalGraphSharedViewModelStoreOwner.current
        ) { create(it) }
        val filters by cardsViewModel.cardFilters.collectAsState()
        val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()
        val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel(
            LocalSharedViewModelStoreOwner.current
        )

        CardsFiltersTraitsScreen(
            selectedTraits = filters.traits,
            cardsFiltersViewModel = cardsFiltersViewModel,
            onTraitChange = cardsViewModel::updateTraits
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
                    onClick = navigator::goBack,
                    iconGlyph = AppIcon.ArrowBack,
                )
            },
        )
    }

    entry<CardsFiltersAssetsScreen>(
        metadata = { key -> SharedViewModelStoreNavEntryDecorator.parent(
            TopLevelRoute.Cards(key.parentConfig).toContentKey()
        )}
    ) { backStackEntry ->
        val topAppBarState = LocalTopAppBarState.current
        val cardsViewModel = hiltAssistedViewModel<CardsViewModel, CardsViewModel.Factory, CardsGraphState>(
            key = backStackEntry.parentConfig,
            viewModelStoreOwner = LocalSharedViewModelStoreOwner.current
        ) { create(it) }
        val filters by cardsViewModel.cardFilters.collectAsState()
        val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()

        CardsFiltersAssetsScreen(
            assetFilter = filters.assetFilter,
            onSkillBoostChange = cardsViewModel::updateSkillBoostsFilter,
            parentConfig = backStackEntry.parentConfig,
            navigateTo = { navigator.navigateSingleTop(it) },
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
                    onClick = navigator::goBack,
                    iconGlyph = AppIcon.ArrowBack,
                )
            },
        )
    }

    entry<CardsFiltersSlotsScreen>(
        metadata = { key -> SharedViewModelStoreNavEntryDecorator.graphWithParent(
            graphKey = TopLevelRoute.Cards(key.parentConfig).toContentKey(),
            parentKey = CardsFiltersScreen(key.parentConfig).toContentKey()
        )}
    ) { backStackEntry ->
        val topAppBarState = LocalTopAppBarState.current
        val cardsViewModel = hiltAssistedViewModel<CardsViewModel, CardsViewModel.Factory, CardsGraphState>(
            key = backStackEntry.parentConfig,
            viewModelStoreOwner = LocalGraphSharedViewModelStoreOwner.current
        ) { create(it) }
        val filters by cardsViewModel.cardFilters.collectAsState()
        val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()
        val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel(
            LocalSharedViewModelStoreOwner.current
        )

        CardsFiltersSlotsScreen(
            selectedSlots = filters.assetFilter.slots,
            cardsFiltersViewModel = cardsFiltersViewModel,
            onSlotChange = cardsViewModel::updateSlots,
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
                    onClick = navigator::goBack,
                    iconGlyph = AppIcon.ArrowBack,
                )
            },
        )
    }

    entry<CardsFiltersUsesScreen>(
        metadata = { key -> SharedViewModelStoreNavEntryDecorator.graphWithParent(
            graphKey = TopLevelRoute.Cards(key.parentConfig).toContentKey(),
            parentKey = CardsFiltersScreen(key.parentConfig).toContentKey()
        )}
    ) { backStackEntry ->
        val topAppBarState = LocalTopAppBarState.current
        val cardsViewModel = hiltAssistedViewModel<CardsViewModel, CardsViewModel.Factory, CardsGraphState>(
            key = backStackEntry.parentConfig,
            viewModelStoreOwner = LocalGraphSharedViewModelStoreOwner.current
        ) { create(it) }
        val filters by cardsViewModel.cardFilters.collectAsState()
        val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()
        val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel(
            LocalSharedViewModelStoreOwner.current
        )

        CardsFiltersUsesScreen(
            selectedUses = filters.assetFilter.uses,
            cardsFiltersViewModel = cardsFiltersViewModel,
            onUseChange = cardsViewModel::updateUses,
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
                    onClick = navigator::goBack,
                    iconGlyph = AppIcon.ArrowBack,
                )
            },
        )
    }

    entry<CardsFiltersEnemiesScreen>(
        metadata = { key -> SharedViewModelStoreNavEntryDecorator.graphWithParent(
            graphKey = TopLevelRoute.Cards(key.parentConfig).toContentKey(),
            parentKey = CardsFiltersScreen(key.parentConfig).toContentKey()
        )}
    ) { backStackEntry ->
        val topAppBarState = LocalTopAppBarState.current
        val cardsViewModel = hiltAssistedViewModel<CardsViewModel, CardsViewModel.Factory, CardsGraphState>(
            key = backStackEntry.parentConfig,
            viewModelStoreOwner = LocalGraphSharedViewModelStoreOwner.current
        ) { create(it) }
        val filters by cardsViewModel.cardFilters.collectAsState()
        val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()
        val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel(
            LocalSharedViewModelStoreOwner.current
        )

        CardsFiltersEnemiesScreen(
            enemyFilter = filters.enemyFilter,
            cardsFiltersViewModel = cardsFiltersViewModel,
            onEnemyFilterChange = cardsViewModel::updateEnemiesFilter,
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
                    onClick = navigator::goBack,
                    iconGlyph = AppIcon.ArrowBack,
                )
            },
        )
    }

    entry<CardsFiltersLocationsScreen>(
        metadata = { key -> SharedViewModelStoreNavEntryDecorator.graphWithParent(
            graphKey = TopLevelRoute.Cards(key.parentConfig).toContentKey(),
            parentKey = CardsFiltersScreen(key.parentConfig).toContentKey()
        )}
    ) { backStackEntry ->
        val topAppBarState = LocalTopAppBarState.current
        val cardsViewModel = hiltAssistedViewModel<CardsViewModel, CardsViewModel.Factory, CardsGraphState>(
            key = backStackEntry.parentConfig,
            viewModelStoreOwner = LocalGraphSharedViewModelStoreOwner.current
        ) { create(it) }
        val filters by cardsViewModel.cardFilters.collectAsState()
        val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()
        val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel(
            LocalSharedViewModelStoreOwner.current
        )

        CardsFiltersLocationsScreen(
            locationFilter = filters.locationFilter,
            cardsFiltersViewModel = cardsFiltersViewModel,
            onLocationFilterChange = cardsViewModel::updateLocationsFilter,
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
                    onClick = navigator::goBack,
                    iconGlyph = AppIcon.ArrowBack,
                )
            },
        )
    }

    entry<CardsFiltersEncountersScreen>(
        metadata = { key -> SharedViewModelStoreNavEntryDecorator.graphWithParent(
            graphKey = TopLevelRoute.Cards(key.parentConfig).toContentKey(),
            parentKey = CardsFiltersScreen(key.parentConfig).toContentKey()
        )}
    ) { backStackEntry ->
        val topAppBarState = LocalTopAppBarState.current
        val cardsViewModel = hiltAssistedViewModel<CardsViewModel, CardsViewModel.Factory, CardsGraphState>(
            key = backStackEntry.parentConfig,
            viewModelStoreOwner = LocalGraphSharedViewModelStoreOwner.current
        ) { create(it) }
        val filters by cardsViewModel.cardFilters.collectAsState()
        val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()
        val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel(
            LocalSharedViewModelStoreOwner.current
        )

        CardsFiltersEncounterSetsScreen(
            selectedEncounterSets = filters.encounterSets,
            cardsFiltersViewModel = cardsFiltersViewModel,
            onEncounterSetChange = cardsViewModel::updateEncounterSetsFilter,
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
                    onClick = navigator::goBack,
                    iconGlyph = AppIcon.ArrowBack,
                )
            },
        )
    }

    entry<CardsFiltersPacksScreen>(
        metadata = { key -> SharedViewModelStoreNavEntryDecorator.graphWithParent(
            graphKey = TopLevelRoute.Cards(key.parentConfig).toContentKey(),
            parentKey = CardsFiltersScreen(key.parentConfig).toContentKey()
        )}
    ) { backStackEntry ->
        val topAppBarState = LocalTopAppBarState.current
        val cardsViewModel = hiltAssistedViewModel<CardsViewModel, CardsViewModel.Factory, CardsGraphState>(
            key = backStackEntry.parentConfig,
            viewModelStoreOwner = LocalGraphSharedViewModelStoreOwner.current
        ) { create(it) }
        val filters by cardsViewModel.cardFilters.collectAsState()
        val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()
        val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel(
            LocalSharedViewModelStoreOwner.current
        )
        val allPacks by cardsFiltersViewModel.packs.collectAsState()

        CardsFiltersPacksScreen(
            selectedPacks = filters.packs,
            allPacks = allPacks,
            onPacksChange = cardsViewModel::updatePacksFilter
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
                    onClick = navigator::goBack,
                    iconGlyph = AppIcon.ArrowBack,
                )
            },
        )
    }

    entry<CardsFiltersIllustratorsScreen>(
        metadata = { key -> SharedViewModelStoreNavEntryDecorator.graphWithParent(
            graphKey = TopLevelRoute.Cards(key.parentConfig).toContentKey(),
            parentKey = CardsFiltersScreen(key.parentConfig).toContentKey()
        )}
    ) { backStackEntry ->
        val topAppBarState = LocalTopAppBarState.current
        val cardsViewModel = hiltAssistedViewModel<CardsViewModel, CardsViewModel.Factory, CardsGraphState>(
            key = backStackEntry.parentConfig,
            viewModelStoreOwner = LocalGraphSharedViewModelStoreOwner.current
        ) { create(it) }
        val filters by cardsViewModel.cardFilters.collectAsState()
        val allCardCodes by cardsViewModel.searchResultCodes.collectAsState()
        val cardsFiltersViewModel: CardsFiltersViewModel = hiltViewModel(
            LocalSharedViewModelStoreOwner.current
        )

        CardsFiltersIllustratorsScreen(
            selectedIllustrators = filters.illustrators,
            cardsFiltersViewModel = cardsFiltersViewModel,
            onIllustratorChange = cardsViewModel::updateIllustratorsFilter
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
                    onClick = navigator::goBack,
                    iconGlyph = AppIcon.ArrowBack,
                )
            },
        )
    }

    entry<CardDetailsScreen>(
        metadata = { key -> SharedViewModelStoreNavEntryDecorator.parent(
            TopLevelRoute.Cards(key.parentConfig).toContentKey()
        )}
    ) { backStackEntry ->
        val topAppBarState = LocalTopAppBarState.current

        val cardsViewModel = hiltAssistedViewModel<CardsViewModel, CardsViewModel.Factory, CardsGraphState>(
            key = backStackEntry.parentConfig,
            viewModelStoreOwner = LocalSharedViewModelStoreOwner.current
        ) { create(it) }
        val cardDetailsViewModel: CardDetailsViewModel = hiltViewModel()

        val cardsLazyCodes by cardsViewModel.searchResultCodes.collectAsState()

        var currentCardCode by rememberSaveable {
            mutableStateOf<String?>(backStackEntry.cardCode)
        }
        val context = LocalContext.current

        CardDetailsScreen(
            cardCode = backStackEntry.cardCode,
            cardCodes = cardsLazyCodes,
            cardDetailsViewModel = cardDetailsViewModel,
            onCurrentCardCodeChanged = { currentCardCode = it },
            onTabooNavigation = { code, name ->
                navigator.navigateSingleTop(
                    CardTabooHistoryScreen( code, name)
                )
            },
            onShowInvestigatorCardpool = { investigatorId, parallelCode ->
                navigator.navigateSingleTop(TopLevelRoute.Cards(
                    CardsGraphState.Investigator(investigatorId, parallelCode)
                ))
            },
            onShowWhoCanTakeCard = { id ->
                id?.let {
                    navigator.navigateSingleTop(TopLevelRoute.Cards(
                        CardsGraphState.Card(it)
                    ))
                }
            }
        )

        topAppBarState.update(
            title = "",
            subtitle = null,
            color = null,
            contentColor = null,
            leftAction = { color ->
                ArkhamAppBarAction(
                    contentColor = color,
                    onClick = navigator::goBack,
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

    entry<CardTabooHistoryScreen> { backStackEntry ->
        val topAppBarState = LocalTopAppBarState.current

        val cardTabooHistoryViewModel: CardTabooHistoryViewModel = hiltViewModel()

        CardTabooHistoryScreen(
            cardCode = backStackEntry.cardCode,
            cardTabooHistoryViewModel = cardTabooHistoryViewModel
        )

        topAppBarState.update(
            title = backStackEntry.cardName,
            subtitle = stringResource(R.string.taboos),
            color = null,
            contentColor = null,
            leftAction = { color ->
                ArkhamAppBarAction(
                    contentColor = color,
                    onClick = navigator::goBack,
                    iconGlyph = AppIcon.ArrowBack,
                )
            },
            rightActions = null,
        )
    }
}