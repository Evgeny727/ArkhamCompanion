package com.arkhamcompanion.ui.decks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.arkhamcompanion.UiErrorState
import com.arkhamcompanion.domain.enums.DeckSource
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.domain.model.cards.CardInvestigatorAccessFields
import com.arkhamcompanion.domain.model.decks.DeckFilters
import com.arkhamcompanion.domain.repository.CardsRepository
import com.arkhamcompanion.domain.repository.DEFAULT_DECK_SORT_ORDER
import com.arkhamcompanion.domain.repository.DecksRepository
import com.arkhamcompanion.domain.repository.MetaRepository
import com.arkhamcompanion.domain.repository.UserPreferencesRepository
import com.arkhamcompanion.domain.utils.EnvironmentClassifier
import com.arkhamcompanion.domain.utils.toggle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
    private val cardsRepository: CardsRepository,
    private val metaRepository: MetaRepository,
    private val userPreferencesRepository: UserPreferencesRepository
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

    private val allPacks = metaRepository.getAllPacks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = persistentListOf()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val environmentClassifier = allPacks
        .filter { it.isNotEmpty() }
        .mapLatest(::EnvironmentClassifier)

    val selectedSort = userPreferencesRepository.sortOrderDecks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = DEFAULT_DECK_SORT_ORDER
    )

    val defaultDeckFilters = DeckFilters()

    private val _deckFilters = MutableStateFlow(defaultDeckFilters)
    val deckFilters = _deckFilters.asStateFlow()

    val isFiltersActive = deckFilters
        .map { it != defaultDeckFilters }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults = combine(
        debouncedSearchQuery,
        environmentClassifier,
        selectedSort,
        deckFilters
    ) { query, environmentClassifier, sort, filters ->
        DeckSearchConfig(
            query = query,
            environmentClassifier = environmentClassifier,
            sort = sort,
            deckFilters = filters
        )
    }.flatMapLatest { (query, environmentClassifier, sort, filters) ->
        decksRepository.searchPaginatedDecks(query, environmentClassifier, sort, filters)
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

    fun applyNewSort(newSort: String) {
        viewModelScope.launch {
            userPreferencesRepository.saveSortOrderDecksPreference(newSort)
        }
    }

    fun clearSort() {
        viewModelScope.launch {
            userPreferencesRepository.saveSortOrderDecksPreference("")
        }
    }

    val factions = metaRepository.getAllFactions().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = persistentMapOf()
    )

    val allTags = decksRepository.getAllTags().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = null
    )

    private val _cardSearchQuery = MutableStateFlow("")
    val cardSearchQuery: StateFlow<String> = _cardSearchQuery.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val playableCards = _cardSearchQuery
        .map { it.trim().lowercase() }
        .debounce(200.milliseconds)
        .flatMapLatest { query ->
            cardsRepository.getAllPlayableCardsByName(query)
        }

    fun updateCardSearchQuery(query: String) {
        _cardSearchQuery.value = query
    }

    fun clearCardSearchQuery() {
        _cardSearchQuery.value = ""
    }

    fun toggleDeckFactions(value: Faction) {
        _deckFilters.update {
            it.copy(factions = it.factions.toggle(value))
        }
    }

    fun toggleDeckTags(value: String) {
        _deckFilters.update {
            it.copy(tags = it.tags.toggle(value))
        }
    }

    fun clearTagsFilter() {
        _deckFilters.update {
            it.copy(tags = defaultDeckFilters.tags)
        }
    }

    fun toggleDeckCardsFilter(value: CardInvestigatorAccessFields) {
        _deckFilters.update {
            it.copy(cardsInDeck = it.cardsInDeck.toggle(value))
        }
    }

    fun clearDeckCardsFilter() {
        _deckFilters.update {
            it.copy(cardsInDeck = defaultDeckFilters.cardsInDeck)
        }
    }

    fun toggleDeckSourceFilter(value: DeckSource) {
        _deckFilters.update {
            it.copy(source = it.source.toggle(value))
        }
    }

    fun selectAlternateInvestigatorsFilter(value: Boolean) {
        _deckFilters.update {
            it.copy(alternateInvestigatorsOnly = value)
        }
    }

    fun clearFilters() {
        _deckFilters.value = defaultDeckFilters
    }
}

private data class DeckSearchConfig(
    val query: String,
    val environmentClassifier: EnvironmentClassifier,
    val sort: String,
    val deckFilters: DeckFilters
)