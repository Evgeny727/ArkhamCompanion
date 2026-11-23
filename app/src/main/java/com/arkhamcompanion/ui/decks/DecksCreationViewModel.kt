package com.arkhamcompanion.ui.decks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkhamcompanion.UiErrorState
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.domain.model.decks.Selection
import com.arkhamcompanion.domain.model.decks.Selections
import com.arkhamcompanion.domain.model.meta.Pack
import com.arkhamcompanion.domain.model.settings.Collection
import com.arkhamcompanion.domain.repository.CardsRepository
import com.arkhamcompanion.domain.repository.DecksRepository
import com.arkhamcompanion.domain.repository.MetaRepository
import com.arkhamcompanion.domain.repository.UserPreferencesRepository
import com.arkhamcompanion.domain.utils.ArkhamEnvironments.chapter
import com.arkhamcompanion.domain.utils.ArkhamEnvironments.collection
import com.arkhamcompanion.domain.utils.ArkhamEnvironments.cpa
import com.arkhamcompanion.domain.utils.ArkhamEnvironments.current
import com.arkhamcompanion.domain.utils.ArkhamEnvironments.limited
import com.arkhamcompanion.domain.utils.REPRINT_CYCLE_CODES
import com.arkhamcompanion.domain.utils.SpecialCardCodes.MANDY
import com.arkhamcompanion.domain.utils.SpecialCardCodes.OCCULT_EVIDENCE
import com.arkhamcompanion.domain.utils.SpecialCardCodes.RANDOM_BASIC_WEAKNESS
import com.arkhamcompanion.domain.utils.isDeckbuildingPack
import com.arkhamcompanion.domain.utils.orEmpty
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface DeckCreationUiState {
    object Idle : DeckCreationUiState
    object Loading : DeckCreationUiState
    object Error : DeckCreationUiState
    data class Success(val id: String): DeckCreationUiState
}

@HiltViewModel(assistedFactory = DecksCreationViewModel.Factory::class)
class DecksCreationViewModel @AssistedInject constructor(
    private val decksRepository: DecksRepository,
    private val metaRepository: MetaRepository,
    private val cardsRepository: CardsRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    @Assisted code: String,
    @Assisted parallel: Boolean,
) : ViewModel() {

    private val _deckCreationState = MutableStateFlow<DeckCreationUiState>(DeckCreationUiState.Idle)
    val deckCreationState = _deckCreationState.asStateFlow()

    private val _errors = MutableSharedFlow<UiErrorState>(extraBufferCapacity = 1)
    val errors: SharedFlow<UiErrorState> = _errors.asSharedFlow()

    private val _code = MutableStateFlow(code)

    private val _tabooSetId = MutableStateFlow(0)
    val tabooSetId = _tabooSetId.asStateFlow()

    init {
        loadInitialTabooSet()
    }

    fun loadInitialTabooSet() {
        viewModelScope.launch {
            val initialValue = userPreferencesRepository.tabooSetId.firstOrNull() ?: 0

            _tabooSetId.value = initialValue
        }
    }

    private val _parallelSelection = MutableStateFlow(ParallelSelection(parallel, parallel))
    val parallelSelection = _parallelSelection.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val investigatorFlow = combine(
        _code,
        _tabooSetId,
    ) { code, tabooSetId ->
        code to tabooSetId
    }.flatMapLatest { (code, tabooSetId) ->

        _deckSelections.value = emptyMap()

        cardsRepository.getCardWithRelationsByCodeFlow(code, tabooSetId)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _deckSelections = MutableStateFlow<Selections>(emptyMap())
    val deckSelections = _deckSelections.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val parallelInvestigatorFlow = investigatorFlow.flatMapLatest { investigator ->
        val parallelDetails = investigator?.cardRelations?.parallel?.details?.cardDetails
            ?: return@flatMapLatest emptyFlow()

        cardsRepository.getCardWithRelationsByCodeFlow(parallelDetails.code, parallelDetails.tabooSetId)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val tabooSetIds = metaRepository.getTaboos().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = persistentListOf()
    )

    fun selectInvestigatorVersion(code: String) {
        _code.value = code
    }

    fun selectTabooSet(tabooSetId: Int) {
        _tabooSetId.value = tabooSetId
    }

    fun selectParallel(parallelSelection: ParallelSelection) {
        _parallelSelection.value = parallelSelection
        _deckSelections.value = emptyMap()
    }

    fun putNewSelection(key: String, value: Selection) {
        _deckSelections.update {
            it + (key to value)
        }
    }

    private val _uploadToArkhamBuild = MutableStateFlow(false)
    val uploadToArkhamBuild = _uploadToArkhamBuild.asStateFlow()

    fun updateUploadToArkhamBuild(newValue: Boolean) {
        _uploadToArkhamBuild.value = newValue
    }

    private val _useStarterDeck = MutableStateFlow(false)
    val useStarterDeck = _useStarterDeck.asStateFlow()

    fun updateUseStarterDeck(newValue: Boolean) {
        _useStarterDeck.value = newValue
    }

    val starterDeck = investigatorFlow.map { investigator ->
        investigator?.let {
            val investigatorCode = if (it.card.details.cardDetails.packCode == "rcore") {
                it.card.details.cardDetails.code
            } else {
                it.card.details.cardDetails.duplicateOfCode
                    ?: it.card.details.cardDetails.code
            }

            decksRepository.getStarterDeck(investigatorCode)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    fun updateName(newValue: String) {
        _name.value = newValue
    }

    private val _cardPoolState: MutableStateFlow<ImmutableSet<String>> =
        MutableStateFlow(persistentSetOf())
    val cardPoolState = _cardPoolState.asStateFlow()

    val deckbuildingPacks = metaRepository.getAllPacks().map {
        it.filter { pack ->
            isDeckbuildingPack(pack.cycleCode) &&
                    (pack.cycleCode !in REPRINT_CYCLE_CODES || pack.reprint) &&
                    pack.code != "${pack.cycleCode}c"
        }.toImmutableList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = persistentListOf()
    )

    init {
        loadInitialCardPool()
    }

    fun loadInitialCardPool() {
        viewModelScope.launch {
            val initialValue = userPreferencesRepository.cardPool.firstOrNull().orEmpty()
            _cardPoolState.value = initialValue
        }
    }

    fun addPackInCardPool(pack: Pack) {
        _cardPoolState.update {
            (it + pack.code).toImmutableSet()
        }
    }

    fun removePackFromCardPool(pack: Pack) {
        val asPack = pack.code in _cardPoolState.value
        val asCycle = "cycle:${pack.cycleCode}" in _cardPoolState.value

        val newCardPool = if (asPack) {
            _cardPoolState.value - pack.code
        } else if (asCycle) {
            val cyclePacks = deckbuildingPacks.value.filter {
                it.cycleCode == pack.cycleCode && it.code != pack.code
            }.map { it.code }

            _cardPoolState.value - "cycle:${pack.cycleCode}" + cyclePacks
        } else {
            emptyList()
        }

        if (asPack || asCycle) _cardPoolState.value = newCardPool.toImmutableSet()
    }

    fun applyCurrentCardPool() {
        _cardPoolState.value = current()
    }

    fun applyLimitedCardPool(packs: List<Pack>) {
        _cardPoolState.value = limited(packs)
    }

    val collection = userPreferencesRepository.collection.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Collection()
    )

    fun applyCollectionCardPool() {
        viewModelScope.launch {
            _cardPoolState.value = collection(
                collection.value,
                metaRepository.getAllPacks().firstOrNull().orEmpty()
            )
        }
    }

    fun applyChapterCardPool(chapter: Int) {
        _cardPoolState.value = chapter(deckbuildingPacks.value, chapter)
    }

    fun applyCPACardPool(cycleCode: String, chapter: Int) {
        _cardPoolState.value = cpa(cycleCode, chapter)
    }

    fun clearCardPool() {
        _cardPoolState.value = persistentSetOf()
    }

    fun createDeck(
        selections: Selections,
        name: String,
        requiredSelected: Boolean,
        advancedSelected: Boolean,
        replacementSelected: Boolean,
        includeParallelCards: Boolean,
        faction: Faction
    ) {
        viewModelScope.launch {
            _deckCreationState.value = DeckCreationUiState.Loading

            val investigator = investigatorFlow.value ?: return@launch

            val tabooId = when (tabooSetId.value) {
                0 -> null
                100 -> tabooSetIds.value.firstOrNull()?.id
                else -> tabooSetId.value
            }

            val parallelSelection = parallelSelection.value

            val parallelFront = investigator.cardRelations.parallel?.details?.cardDetails?.code?.takeIf {
                parallelSelection.first
            }
            val parallelBack = investigator.cardRelations.parallel?.details?.cardDetails?.code?.takeIf {
                parallelSelection.second
            }

            val investigatorRelations = if (parallelSelection.second) {
                parallelInvestigatorFlow.value?.cardRelations
            } else {
                investigator.cardRelations
            }

            val extraDeck = investigatorRelations?.sideDeckRequiredCards.orEmpty().map {
                it.details.cardDetails.code
            }

            val slots = if (useStarterDeck.value) {
                starterDeck.value.orEmpty().toImmutableMap()
            } else {
                buildMap {
                    if (requiredSelected) {
                        investigatorRelations?.requiredCards.orEmpty().forEach {
                            if (investigator.card.details.cardDetails.official &&
                                !it.details.cardDetails.official) return@forEach

                            if (
                                investigator.card.details.cardDetails.code == MANDY &&
                                it.details.cardDetails.code == OCCULT_EVIDENCE
                            ) {
                                val selectedSize = (selections["deck_size_selected"]
                                        as? Selection.DeckSizeSelection)?.value ?: 50
                                val quantity = when (selectedSize) {
                                    30 -> 1
                                    40 -> 2
                                    else -> 3
                                }

                                put(it.details.cardDetails.code, quantity)
                            } else {
                                put(it.details.cardDetails.code, it.details.cardDetails.quantity)
                            }
                        }
                    }

                    if (advancedSelected) {
                        investigatorRelations?.advanced.orEmpty().forEach {
                            if (investigator.card.details.cardDetails.official &&
                                !it.details.cardDetails.official) return@forEach

                            put(it.details.cardDetails.code, it.details.cardDetails.quantity)
                        }
                    }

                    if (replacementSelected) {
                        investigatorRelations?.replacement.orEmpty().forEach {
                            if (investigator.card.details.cardDetails.official &&
                                !it.details.cardDetails.official) return@forEach

                            put(it.details.cardDetails.code, it.details.cardDetails.quantity)
                        }
                    }

                    if (includeParallelCards) {
                        investigatorRelations?.parallelCards.orEmpty().forEach {
                            if (investigator.card.details.cardDetails.official &&
                                !it.details.cardDetails.official) return@forEach

                            put(it.details.cardDetails.code, it.details.cardDetails.quantity)
                        }
                    }

                    if (investigator.card.details.cardDetails.deckRequirements?.random.orEmpty().isNotEmpty()) {
                        put(RANDOM_BASIC_WEAKNESS, 1)
                    }
                }.toImmutableMap()
            }

            decksRepository.createDeck(
                upload = uploadToArkhamBuild.value,
                investigatorCode = investigator.card.details.cardDetails.code,
                parallelFront = parallelFront,
                parallelBack = parallelBack,
                cardPool = cardPoolState.value.joinToString(","),
                selections = selections,
                extraSlots = extraDeck,
                deckName = name,
                slots = slots,
                tabooSetId = tabooId,
                faction = faction
            ).fold(
                onSuccess = {
                    _deckCreationState.value = DeckCreationUiState.Success(it)
                },
                onFailure = {
                    _deckCreationState.value = DeckCreationUiState.Error
                    _errors.emit(UiErrorState(it))
                }
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            code: String,
            parallel: Boolean
        ): DecksCreationViewModel
    }
}

internal typealias ParallelSelection = Pair<Boolean, Boolean>