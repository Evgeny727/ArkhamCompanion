package com.arkhamcompanion.ui.cards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.arkhamcompanion.UiErrorState
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.domain.model.cards.CardFilters
import com.arkhamcompanion.domain.model.cards.CardSearchConfig
import com.arkhamcompanion.domain.model.cards.CardSearchOptions
import com.arkhamcompanion.domain.model.cards.CardSearchPreferences
import com.arkhamcompanion.domain.model.cards.NullableIntRange
import com.arkhamcompanion.domain.repository.CardsRepository
import com.arkhamcompanion.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class CardsViewModel @Inject constructor(
    private val cardsRepository: CardsRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _errors = MutableSharedFlow<UiErrorState>(extraBufferCapacity = 1)
    val errors: SharedFlow<UiErrorState> = _errors

    fun emitError(throwable: Throwable) {
        _errors.tryEmit(UiErrorState(throwable))
    }

    private val _spoilerState = MutableStateFlow(false)
    val spoilerState = _spoilerState.asStateFlow()

    fun toggleSpoiler(value: Boolean) {
        _spoilerState.value = value
    }

    private val _searchOptions = MutableStateFlow(CardSearchOptions())
    val searchOptions = _searchOptions.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchOptions.update { it.copy(searchQuery = query.trim()) }
    }

    fun clearSearchQuery() {
        _searchOptions.update { it.copy(searchQuery = "") }
    }

    fun onSearchGameTextChange(state: Boolean) {
        _searchOptions.update { it.copy(searchGame = state) }
    }

    fun onSearchFlavorTextChange(state: Boolean) {
        _searchOptions.update { it.copy(searchFlavor = state) }
    }

    fun onSearchBackTextChange(state: Boolean) {
        _searchOptions.update { it.copy(searchBack = state) }
    }

    private val _cardSearchPreferences = userPreferencesRepository.cardSearchPreferences.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = CardSearchPreferences()
    )

    private val _cardFilters = MutableStateFlow(CardFilters())
    val cardFilters = _cardFilters.asStateFlow()

    @OptIn(FlowPreview::class)
    private val _searchConfig = combine(
        _spoilerState,
        _searchOptions,
        _cardSearchPreferences,
        cardFilters
    ) { spoilerState, searchOptions, cardsSearchPreferences, cardFilters ->
        CardSearchConfig(
            spoilerState,
            searchOptions,
            cardsSearchPreferences,
            cardFilters
        )
    }.debounce(200.milliseconds)
        .distinctUntilChanged()


    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults = _searchConfig.flatMapLatest { searchConfig ->
        cardsRepository.searchPaginatedCardsFlow(searchConfig)
    }.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResultCodes = _searchConfig.flatMapLatest { searchConfig ->
        cardsRepository.searchCardCodesFlow(searchConfig)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = persistentListOf()
    )

    private fun updateCardFilters(update: (CardFilters) -> CardFilters) {
        _cardFilters.update { update(it) }
    }

    fun clearCardFilters() {
        _cardFilters.value = CardFilters()
    }

    fun updateFactions(value: Faction) =
        updateCardFilters {
            it.copy(factions = it.factions.toggle(value))
        }

    fun updateLevelRange(range: NullableIntRange) {
        updateCardFilters {
            it.copy(
                levelFilter = it.levelFilter.copy(
                    range = range,
                    forcedRange = null,
                )
            )
        }
    }

    fun toggleForcedLevelRange(value: NullableIntRange) {
        updateCardFilters {
            val level = it.levelFilter

            it.copy(
                levelFilter = level.copy(
                    forcedRange = if (level.forcedRange == value) {
                        null
                    } else {
                        value
                    }
                )
            )
        }
    }

    private fun <T> ImmutableSet<T>.toggle(value: T): ImmutableSet<T> =
        (if (value in this) minus(value) else plus(value))
            .toImmutableSet()

}
