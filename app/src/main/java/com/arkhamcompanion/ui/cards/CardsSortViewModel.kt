package com.arkhamcompanion.ui.cards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkhamcompanion.domain.repository.DEFAULT_INVESTIGATOR_SORT_ORDER
import com.arkhamcompanion.domain.repository.DEFAULT_MYTHOS_SORT_ORDER
import com.arkhamcompanion.domain.repository.DEFAULT_PLAYER_SORT_ORDER
import com.arkhamcompanion.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardsSortViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val playerSortOptions = userPreferencesRepository.sortOrderPlayer.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DEFAULT_PLAYER_SORT_ORDER
    )

    val mythosSortOptions = userPreferencesRepository.sortOrderMythos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DEFAULT_MYTHOS_SORT_ORDER
    )

    val investigatorSortOption = userPreferencesRepository.sortOrderInvestigators.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DEFAULT_INVESTIGATOR_SORT_ORDER
    )

    private val _clearClicked = MutableSharedFlow<Unit>()
    val clearClicked = _clearClicked.asSharedFlow()

    fun applyNewSortOptions(newSortOptions: List<String>, spoiler: Boolean) {
        viewModelScope.launch {
            if (spoiler) userPreferencesRepository.saveSortOrderMythosPreference(newSortOptions)
            else userPreferencesRepository.saveSortOrderPlayerPreference(newSortOptions)
        }
    }

    fun applyNewSortOption(newSortOption:String) {
        viewModelScope.launch {
            userPreferencesRepository.saveSortOrderInvestigatorsPreference(newSortOption)
        }
    }

    fun clearSortOptions() {
        viewModelScope.launch {
            _clearClicked.emit(Unit)
        }
    }

}