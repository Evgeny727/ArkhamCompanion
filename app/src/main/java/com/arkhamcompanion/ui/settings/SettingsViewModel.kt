package com.arkhamcompanion.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import com.arkhamcompanion.UiErrorState
import com.arkhamcompanion.domain.exceptions.ClearCardsDatabaseException
import com.arkhamcompanion.domain.exceptions.ClearFavoriteCardsException
import com.arkhamcompanion.domain.exceptions.ClearLocalDecksException
import com.arkhamcompanion.domain.exceptions.ClearRemoteDecksException
import com.arkhamcompanion.domain.model.meta.Pack
import com.arkhamcompanion.domain.model.settings.Collection
import com.arkhamcompanion.domain.repository.CardsRepository
import com.arkhamcompanion.domain.repository.DecksRepository
import com.arkhamcompanion.domain.repository.MetaRepository
import com.arkhamcompanion.domain.repository.UserPreferencesRepository
import com.arkhamcompanion.domain.utils.ArkhamEnvironments.chapter
import com.arkhamcompanion.domain.utils.ArkhamEnvironments.collection
import com.arkhamcompanion.domain.utils.ArkhamEnvironments.cpa
import com.arkhamcompanion.domain.utils.ArkhamEnvironments.current
import com.arkhamcompanion.domain.utils.ArkhamEnvironments.limited
import com.arkhamcompanion.domain.utils.REPRINT_CYCLE_CODES
import com.arkhamcompanion.domain.utils.isDeckbuildingPack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed interface SettingsUiState {
    object Idle : SettingsUiState
    object Loading : SettingsUiState
}

sealed interface SettingsUiEvent {
    object ImageCacheCleared : SettingsUiEvent
    object FavoriteCardsCleared : SettingsUiEvent
    object CardsDatabaseCleared : SettingsUiEvent
    object LocalDecksCleared : SettingsUiEvent
    object RemoteDecksCleared : SettingsUiEvent
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val metaRepository: MetaRepository,
    private val cardsRepository: CardsRepository,
    private val decksRepository: DecksRepository,
    private val imageLoader: ImageLoader
) : ViewModel() {

    private val _settingsUiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Idle)
    val settingsUiState: StateFlow<SettingsUiState> = _settingsUiState.asStateFlow()

    private val _errors = MutableSharedFlow<UiErrorState>(extraBufferCapacity = 1)
    val errors: SharedFlow<UiErrorState> = _errors

    fun emitError(throwable: Throwable) {
        _errors.tryEmit(UiErrorState(throwable))
    }

    private val _events = MutableSharedFlow<SettingsUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<SettingsUiEvent> = _events

    fun emitEvent(event: SettingsUiEvent) {
        _events.tryEmit(event)
    }

    val showFanmadeCardsState: StateFlow<Boolean> =
        userPreferencesRepository.showFanmadeCards.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    val showPreviewCardsState: StateFlow<Boolean> =
        userPreferencesRepository.showPreviewCards.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    val isIncludeEnglishSearchResultsState: StateFlow<Boolean> =
        userPreferencesRepository.isIncludeEnglishSearchResults.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    val allPacksState = metaRepository.getAllPacks(secondCore = true).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = persistentListOf()
    )

    val collectionState: StateFlow<Collection> = userPreferencesRepository.collection.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Collection()
    )

    val ignoreCollectionState: StateFlow<Boolean> = userPreferencesRepository.ignoreCollection.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    //    init {
//        observeCollectionSettings()
//    }
//
//    @OptIn(FlowPreview::class)
//    private fun observeCollectionSettings() {
//        viewModelScope.launch {
//            userPreferencesRepository.collection
//                .drop(1)   // skip initial value
//                .debounce(300.milliseconds)
//                .distinctUntilChanged()
//                .collect { collection ->
//                    _settingsUiState.value = SettingsUiState.Loading
//                    //TODO: save collection to server
//                    _settingsUiState.value = SettingsUiState.Idle
//                }
//        }
//    }

    val rewardsState: StateFlow<ImmutableSet<String>> = userPreferencesRepository.rewards.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = persistentSetOf()
    )

    val unlockAllRewardsState: StateFlow<Boolean> = userPreferencesRepository.unlockAllRewards.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val rewardCards = combine(ignoreCollectionState, collectionState) { ignore, collection ->
        ignore to collection
    }.flatMapLatest { (ignoreCollection, collection) ->
        cardsRepository.getRewardCardsFlow(ignoreCollection, collection.packs)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = persistentListOf()
    )

    val tabooSetIdState: StateFlow<Int> = userPreferencesRepository.tabooSetId.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0
    )

    val tabooSetsListState = metaRepository.getTaboos().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = persistentListOf()
    )

    val deckbuildingPacks = metaRepository.getAllPacks().map {
        it.filter { pack ->
            isDeckbuildingPack(pack.cycleCode) &&
                    (pack.cycleCode !in REPRINT_CYCLE_CODES || pack.reprint) &&
                    pack.code != "${pack.cycleCode}c"
        }.toImmutableList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = persistentListOf()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val cardPoolState = userPreferencesRepository.cardPool.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = persistentSetOf()
    )

    fun selectTheme(theme: Int) {
        viewModelScope.launch {
            userPreferencesRepository.saveThemePreference(theme)
        }
    }

    fun setScaleFactor(scaleFactor: Float) {
        viewModelScope.launch {
            userPreferencesRepository.saveScaleFactorPreference(scaleFactor)
        }
    }

    fun setTaboo(tabooSetId: Int) {
        viewModelScope.launch {
            userPreferencesRepository.saveTabooSetPreference(tabooSetId)
        }
    }

    fun setCollection(collection: Collection) {
        viewModelScope.launch {
            userPreferencesRepository.saveCollectionPreference(collection)
        }
    }

    fun setIgnoreCollection(ignoreCollection: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveIgnoreCollectionPreference(ignoreCollection)
        }
    }

    fun setRewards(rewards: Set<String>) {
        viewModelScope.launch {
            userPreferencesRepository.saveRewardsPreference(rewards)
        }
    }

    fun setUnlockAllRewards(unlockAllRewards: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveUnlockAllRewardsPreference(unlockAllRewards)
        }
    }

    fun addPackInCardPool(pack: Pack) {
        viewModelScope.launch {
            userPreferencesRepository.saveCardPoolPreference(cardPoolState.value + pack.code)
        }
    }

    fun removePackFromCardPool(pack: Pack) {
        viewModelScope.launch {
            val asPack = pack.code in cardPoolState.value
            val asCycle = "cycle:${pack.cycleCode}" in cardPoolState.value

            val newCardPool = if (asPack) {
                cardPoolState.value - pack.code
            } else if (asCycle) {
                val cyclePacks = deckbuildingPacks.value.filter {
                    it.cycleCode == pack.cycleCode && it.code != pack.code
                }.map { it.code }

                cardPoolState.value - "cycle:${pack.cycleCode}" + cyclePacks
            } else {
                emptyList()
            }

            if (asPack || asCycle) userPreferencesRepository.saveCardPoolPreference(newCardPool)
        }
    }

    fun applyCurrentCardPool() {
        viewModelScope.launch {
            userPreferencesRepository.saveCardPoolPreference(current())
        }
    }

    fun applyLimitedCardPool(packs: List<Pack>) {
        viewModelScope.launch {
            userPreferencesRepository.saveCardPoolPreference(limited(packs))
        }
    }

    fun applyCollectionCardPool() {
        viewModelScope.launch {
            userPreferencesRepository.saveCardPoolPreference(
                collection(collectionState.value, allPacksState.value)
            )
        }
    }

    fun applyChapterCardPool(chapter: Int) {
        viewModelScope.launch {
            userPreferencesRepository.saveCardPoolPreference(
                chapter(deckbuildingPacks.value, chapter)
            )
        }
    }

    fun applyCPACardPool(cycleCode: String, chapter: Int) {
        viewModelScope.launch {
            userPreferencesRepository.saveCardPoolPreference(
                cpa(cycleCode, chapter)
            )
        }
    }

    fun clearCardPool() {
        viewModelScope.launch {
            userPreferencesRepository.saveCardPoolPreference(emptyList())
        }
    }

    fun setFanmadeCards(showFanmadeCards: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveShowFanmadeCards(showFanmadeCards)
        }
    }

    fun setPreviewCards(showPreviewCards: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveShowPreviewCards(showPreviewCards)
        }
    }

    fun setEnglishSearchResults(isInclude: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveIncludeEnglishSearchResults(isInclude)
        }
    }

    fun clearImageCache() {
        viewModelScope.launch {
            _settingsUiState.value = SettingsUiState.Loading

            withContext(Dispatchers.IO) {
                imageLoader.memoryCache?.clear()
                imageLoader.diskCache?.clear()
            }

            emitEvent(SettingsUiEvent.ImageCacheCleared)
            _settingsUiState.value = SettingsUiState.Idle
        }
    }

    fun clearFavoriteCards() {
        viewModelScope.launch {
            _settingsUiState.value = SettingsUiState.Loading

            cardsRepository.clearFavoriteCards()
                .onFailure { emitError(ClearFavoriteCardsException()) }
                .onSuccess { emitEvent(SettingsUiEvent.FavoriteCardsCleared) }

            _settingsUiState.value = SettingsUiState.Idle
        }
    }

    fun clearCardsDatabase() {
        viewModelScope.launch {
            _settingsUiState.value = SettingsUiState.Loading

            cardsRepository.clearCardsDatabase()
                .onFailure { emitError(ClearCardsDatabaseException()) }
                .onSuccess { emitEvent(SettingsUiEvent.CardsDatabaseCleared) }

            _settingsUiState.value = SettingsUiState.Idle
        }
    }

    fun clearAllLocalDecks() {
        viewModelScope.launch {
            _settingsUiState.value = SettingsUiState.Loading

            decksRepository.deleteAllLocalDecks()
                .onFailure { emitError(ClearLocalDecksException()) }
                .onSuccess { emitEvent(SettingsUiEvent.LocalDecksCleared) }

            _settingsUiState.value = SettingsUiState.Idle
        }
    }

    fun clearAllRemoteDecks() {
        viewModelScope.launch {
            _settingsUiState.value = SettingsUiState.Loading

            decksRepository.deleteAllRemoteDecks()
                .onFailure { emitError(ClearRemoteDecksException()) }
                .onSuccess { emitEvent(SettingsUiEvent.RemoteDecksCleared) }

            _settingsUiState.value = SettingsUiState.Idle
        }
    }
}