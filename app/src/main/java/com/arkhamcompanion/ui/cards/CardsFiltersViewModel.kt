package com.arkhamcompanion.ui.cards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.arkhamcompanion.domain.model.settings.Collection
import com.arkhamcompanion.domain.repository.CardsRepository
import com.arkhamcompanion.domain.repository.MetaRepository
import com.arkhamcompanion.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

enum class FilterSection {
    Level, Cost, Skills, HealthSanity, Properties, Official, Ownership,
    Fight, Evade, Damage, Horror, Shroud, Clues
}

data class FiltersUiState(
    val collapsedSections: Map<FilterSection, Boolean> =
        FilterSection.entries.associateWith { true }
)

@HiltViewModel
class CardsFiltersViewModel @Inject constructor(
    metaRepository: MetaRepository,
    private val cardsRepository: CardsRepository,
    preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FiltersUiState())
    val uiState = _uiState.asStateFlow()

    fun toggleSection(section: FilterSection) {
        _uiState.update { state ->
            state.copy(
                collapsedSections = state.collapsedSections.toMutableMap().apply {
                    this[section] = !(this[section] ?: false)
                }
            )
        }
    }

    val factions = metaRepository.getAllFactions().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = persistentMapOf()
    )

    val types = metaRepository.getAllTypes().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = persistentMapOf()
    )

    val subtypes = metaRepository.getAllSubTypes().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = persistentMapOf()
    )

    val actionCodes = metaRepository.getAllActions().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = emptyArray()
    )

    val traitCodes = metaRepository.getAllTraits().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = emptyArray()
    )

    val slotCodes = metaRepository.getAllSlots().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = emptyArray()
    )

    val useCodes = metaRepository.getAllUses().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = emptyArray()
    )

    val encounterSets = metaRepository.getAllEncounterSets().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = persistentMapOf()
    )

    private val _packsFlow = metaRepository.getAllPacks()

    val packs = _packsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = persistentListOf()
    )

    val packsMap = _packsFlow.map {
        it.associateBy { pack -> pack.code }.toImmutableMap()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = persistentMapOf()
    )

    val taboos = metaRepository.getTaboos().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = persistentListOf()
    )

    val illustrators = metaRepository.getAllIllustrators().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = persistentSetOf()
    )

    private val _dialogQuery = MutableStateFlow("")
    val dialogQuery = _dialogQuery.asStateFlow()

    fun updateSearchQuery(query: String) {
        _dialogQuery.value = query
    }

    fun clearDialogSearchQuery() {
        _dialogQuery.value = ""
    }

    private val collection = preferencesRepository.collection.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = Collection()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val investigators = combine(_dialogQuery, collection) { dialogQuery, collection ->
        dialogQuery.trim().lowercase() to collection
    }
        .distinctUntilChanged()
        .flatMapLatest { (query, collection) ->
            cardsRepository.getAllInvestigatorsByName(query, collection)
        }.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val cards = combine(_dialogQuery, collection) { dialogQuery, collection ->
        dialogQuery.trim().lowercase() to collection
    }
        .distinctUntilChanged()
        .flatMapLatest { (query, collection) ->
            cardsRepository.getAllPlayableCardsByName(query, collection)
        }.cachedIn(viewModelScope)
}