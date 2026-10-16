package com.arkhamcompanion.ui.settings.rules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkhamcompanion.domain.model.settings.RulesSections
import com.arkhamcompanion.domain.model.settings.findTopLevelRule
import com.arkhamcompanion.domain.model.settings.search
import com.arkhamcompanion.domain.repository.SettingsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

sealed interface RulesUiState {
    object Loading : RulesUiState
    object Idle : RulesUiState
}

@HiltViewModel(assistedFactory = RulesViewModel.Factory::class)
class RulesViewModel @AssistedInject constructor(
    @Assisted private val locale: String,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _rulesUiState = MutableStateFlow<RulesUiState>(RulesUiState.Idle)
    val rulesUiState = _rulesUiState.asStateFlow()

    private val chapterOneRules: MutableStateFlow<RulesSections> = MutableStateFlow(persistentMapOf())

    init {
        fetchRules(locale)
    }

    fun fetchRules(locale: String) {
        viewModelScope.launch {
            _rulesUiState.value = RulesUiState.Loading

            val rules = settingsRepository.getChapterOneRules(locale)
            chapterOneRules.value = rules

            _rulesUiState.value = RulesUiState.Idle
        }
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearchQuery() {
        _searchQuery.value = ""
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val sections = combine(
        chapterOneRules, searchQuery
    ) { rules, query -> rules to query.trim() }
        .debounce(200.milliseconds)
        .distinctUntilChanged()
        .mapLatest { (rules, query) ->
            buildSet {
                if (query.isBlank()) {
                    addAll(rules.values.filter { it.level < 3 })
                } else {
                    addAll(
                        rules.search(query).mapNotNull { section ->
                            if (section.level < 3) section
                            else rules[section.parentId]
                        }
                    )
                }
            }.toImmutableList()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = persistentListOf()
        )

    fun getTopLevelSectionById(id: String) = chapterOneRules.mapNotNull {
        it.findTopLevelRule(id)
    }

    fun getChildSections(id: String) = chapterOneRules.map {
        it.values.filter { section ->
            section.parentId == id || it[section.parentId]?.parentId == id
        }.toImmutableList()
    }

    @AssistedFactory
    interface Factory {
        fun create(locale: String): RulesViewModel
    }
}