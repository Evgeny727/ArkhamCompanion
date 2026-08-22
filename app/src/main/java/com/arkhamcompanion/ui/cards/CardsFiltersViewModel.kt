package com.arkhamcompanion.ui.cards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkhamcompanion.domain.repository.MetaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
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

}