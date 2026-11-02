package com.arkhamcompanion.ui.decks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.arkhamcompanion.UiErrorState
import com.arkhamcompanion.domain.repository.DecksRepository
import com.arkhamcompanion.domain.repository.MetaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

sealed interface DecksUiState {
    object Idle : DecksUiState
    object Loading : DecksUiState
}

@HiltViewModel
class DecksViewModel @Inject constructor(
    private val decksRepository: DecksRepository,
    private val metaRepository: MetaRepository
) : ViewModel() {

    private val _decksUiState = MutableStateFlow<DecksUiState>(DecksUiState.Idle)
    val decksUiState = _decksUiState.asStateFlow()

    private val _errors = MutableSharedFlow<UiErrorState>(extraBufferCapacity = 1)
    val errors: SharedFlow<UiErrorState> = _errors

    private fun emitError(throwable: Throwable) {
        _errors.tryEmit(UiErrorState(throwable))
    }

    fun syncAllRemoteDecks(userId: String?) {
        if (_decksUiState.value is DecksUiState.Loading) return

        viewModelScope.launch {
            _decksUiState.value = DecksUiState.Loading

            userId?.let {
                decksRepository.syncRemoteDecks(userId)
                    .onFailure { emitError(it) }
            }

            _decksUiState.value = DecksUiState.Idle
        }
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(FlowPreview::class)
    private val debouncedSearchQuery = _searchQuery
        .map(String::trim)
        .debounce(200.milliseconds)
        .distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults = debouncedSearchQuery.flatMapLatest { query ->
        decksRepository.searchPaginatedDecks(query)
    }.cachedIn(viewModelScope)


    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun clearSearchQuery() {
        _searchQuery.value = ""
    }

    val localizedFactions = metaRepository.getAllFactions().map {
        it.mapKeys { (key, _) -> key.name.lowercase() }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = persistentMapOf()
    )
}