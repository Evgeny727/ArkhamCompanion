package com.arkhamcompanion

import com.arkhamcompanion.domain.exceptions.UnableCreateCardsCacheException
import com.arkhamcompanion.domain.exceptions.UnableToLoadCardsCacheException
import com.arkhamcompanion.domain.repository.CardsRepository
import com.arkhamcompanion.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

sealed interface CardsSyncState {
    object Idle : CardsSyncState
    data class Loading(val progress: Float) : CardsSyncState
    object UpdateAvailable : CardsSyncState
    object Ready : CardsSyncState
}

sealed interface CardsCacheState {
    object Idle : CardsCacheState
    object Loading : CardsCacheState
    object Creating : CardsCacheState
    object Ready : CardsCacheState
}

@Singleton
class CardsSyncManager @Inject constructor(
    private val cardsRepository: CardsRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {

    private val _state = MutableStateFlow<CardsSyncState>(CardsSyncState.Idle)
    val state: StateFlow<CardsSyncState> = _state.asStateFlow()

    private val _cacheState = MutableStateFlow<CardsCacheState>(CardsCacheState.Idle)
    val cacheState: StateFlow<CardsCacheState> = _cacheState.asStateFlow()

    private val _errors = MutableSharedFlow<Throwable>(extraBufferCapacity = 1)
    val errors: SharedFlow<Throwable> = _errors

    private val cardsUpdatedAt = userPreferencesRepository.cardsUpdatedAt

    suspend fun ensureCardsReady(language: String) {
        if (_state.value is CardsSyncState.Loading) return

        if (!cardsRepository.isCardsTableExists()) download(language)
        else checkForUpdate(language)
    }

    suspend fun checkForUpdate(language: String) {
        loadCache()

        fetchCardsUpdate(language) { updateAvailable ->
            if (updateAvailable) _state.value = CardsSyncState.UpdateAvailable
            else _state.value = CardsSyncState.Ready
        }
    }

    suspend fun updateCardsIfUpdateAvailable(language: String) {
        _state.value = CardsSyncState.Loading(0.0f)

        if (!cardsRepository.isCardsTableExists()) download(language)
        else {
            fetchCardsUpdate(language, forced = true) { updateAvailable ->
                if (updateAvailable) {
                    _state.value = CardsSyncState.Loading(0.05f)
                    cardsRepository.downloadAllCards(language) { newValue ->
                        _state.value = CardsSyncState.Loading(newValue)
                    }
                        .onSuccess {
                            userPreferencesRepository.saveCardsUpdatedTimestamp(it)
                            _state.value = CardsSyncState.Ready
                        }
                        .onFailure {
                            _errors.tryEmit(it)
                            _state.value = CardsSyncState.Ready
                        }
                } else {
                    _state.value = CardsSyncState.Ready
                }
            }
        }
    }

    private suspend inline fun fetchCardsUpdate(
        language: String,
        forced: Boolean = false,
        block: suspend (Boolean) -> Unit
    ) {
        cardsRepository.isCardsUpdateAvailable(
            language,
            cardsUpdatedAt.first(),
            forced
        )
            .onSuccess { block(it) }
            .onFailure {
                _errors.tryEmit(it)
                _state.value = CardsSyncState.Ready
            }
    }

    suspend fun download(language: String) {
        _state.value = CardsSyncState.Loading(0.0f)

        cardsRepository.downloadAllCards(language) { newValue ->
            _state.value = CardsSyncState.Loading(newValue)
        }
            .onSuccess {
                userPreferencesRepository.saveCardsUpdatedTimestamp(it)
                _state.value = CardsSyncState.Ready
            }
            .onFailure {
                _errors.tryEmit(it)
                _state.value = CardsSyncState.Ready
            }
    }

    suspend fun loadCache() {
        _cacheState.value = CardsCacheState.Loading

        val cacheReady = cardsRepository.loadCache()

        if (!cacheReady) _errors.tryEmit(UnableToLoadCardsCacheException())

        _cacheState.value = CardsCacheState.Ready
    }

    suspend fun recreateCache() {
        _cacheState.value = CardsCacheState.Creating

        val cacheRecreated = cardsRepository.recreateCache()

        if (!cacheRecreated) _errors.tryEmit(UnableCreateCardsCacheException())

        _cacheState.value = CardsCacheState.Ready
    }
}