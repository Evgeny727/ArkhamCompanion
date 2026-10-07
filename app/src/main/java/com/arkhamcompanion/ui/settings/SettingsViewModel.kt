package com.arkhamcompanion.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import com.arkhamcompanion.UiErrorState
import com.arkhamcompanion.domain.exceptions.ClearCardsDatabaseException
import com.arkhamcompanion.domain.exceptions.ClearFavoriteCardsException
import com.arkhamcompanion.domain.model.settings.Collection
import com.arkhamcompanion.domain.repository.CardsRepository
import com.arkhamcompanion.domain.repository.MetaRepository
import com.arkhamcompanion.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val metaRepository: MetaRepository,
    private val cardsRepository: CardsRepository,
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
        initialValue = Collection(persistentSetOf(), persistentSetOf())
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

    fun setFanmadeCards(showFanmadeCards: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveShowFanmadeCards(showFanmadeCards)
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
}