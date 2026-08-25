package com.arkhamcompanion.ui.cards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkhamcompanion.domain.repository.MetaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

enum class FilterSection {
    Level, Cost, Skills, HealthSanity, Properties, Official
}

data class FiltersUiState(
    val collapsedSections: Map<FilterSection, Boolean> =
        FilterSection.entries.associateWith { true }
)

@HiltViewModel
class CardsFiltersViewModel @Inject constructor(
    private val metaRepository: MetaRepository
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

    val skillBoostCodes = metaRepository.getAllSkillBoosts().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = emptyArray()
    )

    val encounterSets = metaRepository.getAllEncounterSets().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = persistentMapOf()
    )

    val packs = metaRepository.getAllPacks().map {
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
}