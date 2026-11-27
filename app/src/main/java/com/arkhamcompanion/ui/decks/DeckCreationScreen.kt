package com.arkhamcompanion.ui.decks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.AppUserState
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.domain.model.cards.CardDetailsWithRelations
import com.arkhamcompanion.domain.utils.EnvironmentClassifier
import com.arkhamcompanion.domain.utils.SpecialCardCodes.PARALLEL_JIM
import com.arkhamcompanion.domain.utils.SpecialCardCodes.PARALLEL_ROLAND
import com.arkhamcompanion.domain.utils.SpecialCardCodes.PARALLEL_WENDY
import com.arkhamcompanion.domain.utils.orEmpty
import com.arkhamcompanion.domain.utils.toListItem
import com.arkhamcompanion.ui.cards.components.CardListItem
import com.arkhamcompanion.ui.cards.components.details.rememberCardTextStyles
import com.arkhamcompanion.ui.components.ArkhamButtonColor
import com.arkhamcompanion.ui.components.ArkhamCheckboxButton
import com.arkhamcompanion.ui.components.ArkhamDialog
import com.arkhamcompanion.ui.components.ArkhamIconText
import com.arkhamcompanion.ui.components.ArkhamRoundedFactionCard
import com.arkhamcompanion.ui.components.ArkhamSquareButton
import com.arkhamcompanion.ui.components.ArkhamSurfaceButton
import com.arkhamcompanion.ui.components.ArkhamSurfaceButtonGroup
import com.arkhamcompanion.ui.components.ArkhamTabooSetButton
import com.arkhamcompanion.ui.components.CardPoolSurfaceButton
import com.arkhamcompanion.ui.components.iconSize
import com.arkhamcompanion.ui.decks.components.DeckOptionSelectionSurfaceButton
import com.arkhamcompanion.ui.decks.components.EditDeckNameDialog
import com.arkhamcompanion.ui.decks.components.ParallelSelectionSurfaceButton
import com.arkhamcompanion.ui.decks.components.investigatorDetailsBack
import com.arkhamcompanion.ui.decks.components.investigatorDetailsFront
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.icons.PackIcon
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.CardTextStyleResolver
import com.arkhamcompanion.ui.utils.applyNavigationBarInsets
import com.arkhamcompanion.ui.utils.getCardRowHeight
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList

@Composable
fun DeckCreationScreen(
    userState: AppUserState,
    investigator: CardDetailsWithRelations?,
    decksCreationViewModel: DecksCreationViewModel,
    onDismiss: () -> Unit,
    onNavigateToCardPool: () -> Unit,
    onNavigateToCardDetails: (String, Int?) -> Unit,
    onNavigateToNewDeck: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val parallelInvestigator by decksCreationViewModel.parallelInvestigatorFlow.collectAsState()
    val tabooSetId by decksCreationViewModel.tabooSetId.collectAsState()
    val tabooSetsList by decksCreationViewModel.tabooSetIds.collectAsState()
    val parallelSelection by decksCreationViewModel.parallelSelection.collectAsState()
    val uploadToArkhamBuild by decksCreationViewModel.uploadToArkhamBuild.collectAsState()
    val useStarterDeck by decksCreationViewModel.useStarterDeck.collectAsState()
    val starterDeck by decksCreationViewModel.starterDeck.collectAsState()
    val name by decksCreationViewModel.name.collectAsState()
    val cardPool by decksCreationViewModel.cardPoolState.collectAsState()
    val collection by decksCreationViewModel.collection.collectAsState()
    val deckbuildingPacks by decksCreationViewModel.deckbuildingPacks.collectAsState()
    val deckSelections by decksCreationViewModel.deckSelections.collectAsState()

    val deckOptions = remember(investigator, parallelInvestigator, parallelSelection) {
        if (parallelSelection.second) {
            parallelInvestigator?.card?.details?.cardDetails?.deckOptions.orEmpty() +
                    parallelInvestigator?.card?.details?.cardDetails?.sideDeckOptions.orEmpty()
        } else {
            investigator?.card?.details?.cardDetails?.deckOptions.orEmpty() +
                    investigator?.card?.details?.cardDetails?.sideDeckOptions.orEmpty()
        }.filter { option ->
            option.deckSizeSelect.isNotEmpty() ||
                    option.factionSelect.isNotEmpty() ||
                    option.optionSelect.isNotEmpty()
        }.toImmutableList()
    }

    val investigatorRelations = remember(investigator, parallelInvestigator, parallelSelection) {
        if (parallelSelection.second) {
            parallelInvestigator?.cardRelations
        } else {
            investigator?.cardRelations
        }
    }

    val environmentClassifier = remember(deckbuildingPacks) {
        EnvironmentClassifier(deckbuildingPacks)
    }
    val resolvedEnvironment = remember(cardPool, environmentClassifier) {
        environmentClassifier.resolve(cardPool)
    }

    var showNameDialog by rememberSaveable { mutableStateOf(false) }
    var showVersionSelectDialog by rememberSaveable { mutableStateOf(false) }

    val styles = rememberCardTextStyles(flavorText = false)
    val flavorStyles = rememberCardTextStyles(flavorText = true)
    val styleResolver = remember(styles) {
        CardTextStyleResolver(styles)
    }
    val flavorStyleResolver = remember(flavorStyles) {
        CardTextStyleResolver(flavorStyles)
    }

    val lazyListState = rememberLazyListState()

    val cardRowHeight = getCardRowHeight()

    val faction = investigator?.card?.details?.cardDetails?.run {
        when {
            faction2 != null -> Faction.Dual
            else -> faction
        }
    } ?: Faction.Neutral

    val defaultName = getDefaultDeckName(
        faction,
        investigator?.card?.details?.cardDetails?.name.orEmpty()
    )

    val parallelRules = remember(parallelSelection) {
        persistentMapOf(
            PARALLEL_ROLAND to parallelSelection.first,
            PARALLEL_WENDY to parallelSelection.first,
            PARALLEL_JIM to parallelSelection.second,
        )
    }
    val shouldShowParallelCards = rememberSaveable(investigator, parallelRules) {
        val code = investigator?.cardRelations?.parallel?.details?.cardDetails?.code
            ?: return@rememberSaveable false

        parallelRules[code] ?: false
    }

    var requiredSelected by rememberSaveable { mutableStateOf(true) }
    var advancedSelected by rememberSaveable { mutableStateOf(false) }
    var replacementSelected by rememberSaveable { mutableStateOf(false) }

    val isCreationEnabled = investigator != null && deckOptions.size == deckSelections.size &&
            (useStarterDeck || requiredSelected || advancedSelected || replacementSelected)

    var isLoading by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        decksCreationViewModel.deckCreationState.collect { state ->
            when (state) {
                is DeckCreationUiState.Loading -> isLoading = true
                is DeckCreationUiState.Success -> onNavigateToNewDeck(state.id)
                else -> isLoading = false
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(
                vertical = 16.dp,
                horizontal = 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(key = "surface_buttons_group", contentType = "buttons_group") {
                ArkhamSurfaceButtonGroup {
                    ArkhamSurfaceButton(
                        title = stringResource(R.string.name),
                        icon = AppIcon.Name,
                        valueLabel = name.ifBlank { defaultName },
                        editable = !isLoading
                    ) { showNameDialog = true }

                    HorizontalDivider(color = CustomTheme.colors.divider)

                    if (
                        investigator?.card?.details?.duplicates.orEmpty().isNotEmpty() ||
                        investigator?.card?.details?.reprints.orEmpty().isNotEmpty() ||
                        investigator?.cardRelations?.otherVersions.orEmpty().isNotEmpty()
                    ) {
                        ArkhamSurfaceButton(
                            title = stringResource(R.string.alternate_version),
                            icon = AppIcon.CardOutline,
                            valueLabel = investigator?.card?.details?.cardDetails?.packName.orEmpty(),
                            editable = !isLoading
                        ) { showVersionSelectDialog = true }

                        HorizontalDivider(color = CustomTheme.colors.divider)
                    }

                    CardPoolSurfaceButton(
                        resolvedEnvironment = resolvedEnvironment,
                        onClick = onNavigateToCardPool,
                        editable = !isLoading
                    )

                    HorizontalDivider(color = CustomTheme.colors.divider)

                    ArkhamTabooSetButton(
                        tabooSetId,
                        tabooSetsList,
                        onTabooSetChange = decksCreationViewModel::selectTabooSet,
                        editable = !isLoading
                    )

                    deckOptions.forEach { option ->
                        HorizontalDivider(color = CustomTheme.colors.divider)

                        DeckOptionSelectionSurfaceButton(
                            option = option,
                            selections = deckSelections,
                            onClick = decksCreationViewModel::putNewSelection,
                            editable = !isLoading
                        )
                    }

                    if (investigator?.cardRelations?.parallel != null) {
                        HorizontalDivider(color = CustomTheme.colors.divider)

                        ParallelSelectionSurfaceButton(
                            parallelSelection = parallelSelection,
                            onClick = decksCreationViewModel::selectParallel,
                            editable = !isLoading
                        )
                    }
                }
            }

            if (userState.arkhamBuildUser != null) {
                item(key = "upload_to_arkham_bild", contentType = "checkbox") {
                    ArkhamCheckboxButton(
                        title = stringResource(R.string.upload_to_arkham_build),
                        isSelected = uploadToArkhamBuild,
                        enabled = !isLoading,
                        onValueChange = decksCreationViewModel::updateUploadToArkhamBuild,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            if (starterDeck != null && !parallelSelection.second) {
                item(key = "use_starter_deck", contentType = "checkbox") {
                    ArkhamCheckboxButton(
                        title = stringResource(R.string.use_starter_deck),
                        isSelected = useStarterDeck,
                        enabled = !isLoading,
                        onValueChange = decksCreationViewModel::updateUseStarterDeck,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            investigator?.run {
                investigatorDetailsFront(
                    cardDetailsWithPackInfo = if (parallelSelection.first) {
                        cardRelations.parallel?.details ?: return@run
                    } else {
                        card.details
                    },
                    collection = collection,
                    styleResolver = styleResolver,
                    flavorStyleResolver = flavorStyleResolver
                )

                investigatorDetailsBack(
                    cardDetailsWithPackInfo = if (parallelSelection.second) {
                        cardRelations.parallel?.details ?: return@run
                    } else {
                        card.details
                    },
                    styleResolver = styleResolver,
                    flavorStyleResolver = flavorStyleResolver
                )
            }

            investigatorRelations?.run {
                if (!useStarterDeck || starterDeck == null) {
                    if (requiredCards.isNotEmpty()) {
                        item(key = "required_cards", contentType = "cards") {
                            ArkhamRoundedFactionCard(
                                faction = faction,
                                color = if (requiredSelected) null else CustomTheme.colors.background
                            ) {
                                ArkhamCheckboxButton(
                                    title = stringResource(R.string.required_cards),
                                    isSelected = requiredSelected,
                                    onValueChange = {
                                        if (it) {
                                            requiredSelected = true
                                            advancedSelected = false
                                        } else {
                                            requiredSelected = false
                                        }
                                    },
                                    enabled = !isLoading,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                requiredCards.forEach { card ->
                                    val cardDetails = card.details.cardDetails

                                    if (investigator?.card?.details?.cardDetails?.official == true &&
                                        !cardDetails.official) return@forEach

                                    CardListItem(
                                        cardListItem = cardDetails.toListItem(),
                                        rowHeight = cardRowHeight,
                                        isFavorite = cardDetails.isFavorite,
                                    ) {
                                        onNavigateToCardDetails(
                                            cardDetails.code,
                                            cardDetails.tabooSetId
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (advanced.isNotEmpty()) {
                        item(key = "advanced_cards", contentType = "cards") {
                            ArkhamRoundedFactionCard(
                                faction = faction,
                                color = if (advancedSelected) null else CustomTheme.colors.background
                            ) {
                                ArkhamCheckboxButton(
                                    title = stringResource(R.string.advanced_cards),
                                    isSelected = advancedSelected,
                                    onValueChange = {
                                        if (it) {
                                            requiredSelected = false
                                            advancedSelected = true
                                        } else {
                                            advancedSelected = false
                                        }
                                    },
                                    enabled = !isLoading,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                advanced.forEach { card ->
                                    val cardDetails = card.details.cardDetails

                                    if (investigator?.card?.details?.cardDetails?.official == true &&
                                        !cardDetails.official) return@forEach

                                    CardListItem(
                                        cardListItem = cardDetails.toListItem(),
                                        rowHeight = cardRowHeight,
                                        isFavorite = cardDetails.isFavorite,
                                    ) {
                                        onNavigateToCardDetails(
                                            cardDetails.code,
                                            cardDetails.tabooSetId
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (replacement.isNotEmpty()) {
                        item(key = "replacement_cards", contentType = "cards") {
                            ArkhamRoundedFactionCard(
                                faction = faction,
                                color = if (replacementSelected) null else CustomTheme.colors.background
                            ) {
                                ArkhamCheckboxButton(
                                    title = stringResource(R.string.replacement_cards),
                                    isSelected = replacementSelected,
                                    onValueChange = { replacementSelected = it },
                                    enabled = !isLoading,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                replacement.forEach { card ->
                                    val cardDetails = card.details.cardDetails

                                    if (investigator?.card?.details?.cardDetails?.official == true &&
                                        !cardDetails.official) return@forEach

                                    CardListItem(
                                        cardListItem = cardDetails.toListItem(),
                                        rowHeight = cardRowHeight,
                                        isFavorite = cardDetails.isFavorite,
                                    ) {
                                        onNavigateToCardDetails(
                                            cardDetails.code,
                                            cardDetails.tabooSetId
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (sideDeckRequiredCards.isNotEmpty()) {
                        item(key = "side_required_cards", contentType = "cards") {
                            ArkhamRoundedFactionCard(faction = faction) {
                                ArkhamCheckboxButton(
                                    title = stringResource(R.string.other_signature_cards),
                                    isSelected = true,
                                    enabled = false,
                                    onValueChange = {},
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                sideDeckRequiredCards.forEach { card ->
                                    val cardDetails = card.details.cardDetails

                                    if (investigator?.card?.details?.cardDetails?.official == true &&
                                        !cardDetails.official) return@forEach

                                    CardListItem(
                                        cardListItem = cardDetails.toListItem(),
                                        rowHeight = cardRowHeight,
                                        isFavorite = cardDetails.isFavorite,
                                    ) {
                                        onNavigateToCardDetails(
                                            cardDetails.code,
                                            cardDetails.tabooSetId
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (shouldShowParallelCards) {
                investigator?.cardRelations?.run {
                    item(key = "parallel_cards", contentType = "cards") {
                        ArkhamRoundedFactionCard(faction = faction) {
                            ArkhamCheckboxButton(
                                title = stringResource(R.string.parallel_cards),
                                isSelected = true,
                                enabled = false,
                                onValueChange = {},
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            parallelCards.forEach { card ->
                                val cardDetails = card.details.cardDetails

                                if (investigator.card.details.cardDetails.official &&
                                    !cardDetails.official) return@forEach

                                CardListItem(
                                    cardListItem = cardDetails.toListItem(),
                                    rowHeight = cardRowHeight,
                                    isFavorite = cardDetails.isFavorite,
                                ) {
                                    onNavigateToCardDetails(
                                        cardDetails.code,
                                        cardDetails.tabooSetId
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CustomTheme.colors.l10)
                .applyNavigationBarInsets()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ArkhamSquareButton(
                title = stringResource(R.string.cancel),
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                colors = ArkhamButtonColor.Red,
                enabled = !isLoading
            ) { color ->
                ArkhamIconText(
                    iconGlyph = AppIcon.Dismiss,
                    color = color,
                    size = iconSize(AppIcon.Dismiss)
                )
            }

            ArkhamSquareButton(
                title = stringResource(R.string.create_deck),
                onClick = {
                    decksCreationViewModel.createDeck(
                        selections = deckSelections,
                        name = name.ifBlank { defaultName },
                        requiredSelected = requiredSelected,
                        advancedSelected = advancedSelected,
                        replacementSelected = replacementSelected,
                        includeParallelCards = shouldShowParallelCards,
                        faction = faction
                    )
                },
                modifier = Modifier.weight(1f),
                enabled = isCreationEnabled && !isLoading,
                loading = isLoading
            ) { color ->
                ArkhamIconText(
                    iconGlyph = AppIcon.PlusButton,
                    color = color,
                    size = iconSize(AppIcon.PlusButton)
                )
            }
        }
    }

    if (showNameDialog) {
        EditDeckNameDialog(
            initialName = name,
            onDismiss = { showNameDialog = false },
            onApply = {
                decksCreationViewModel.updateName(it)
                showNameDialog = false
            }
        )
    }

    if (showVersionSelectDialog) {
        ArkhamDialog(
            title = stringResource(R.string.select_version),
            onDismiss = { showVersionSelectDialog = false },
        ) {
            investigator?.run {
                ArkhamCheckboxButton(
                    title = card.details.cardDetails.packName + " #${card.details.cardDetails.packPosition}",
                    iconGlyph = PackIcon.fromPackCode(
                        card.details.cardDetails.packCode,
                        returnDefaultCore = false
                    ),
                    isSelected = true,
                    enabled = false,
                    isRadio = true,
                    onValueChange = {},
                )

                HorizontalDivider(color = CustomTheme.colors.divider)

                cardRelations.otherVersions.forEach { card ->
                    ArkhamCheckboxButton(
                        title = card.details.cardDetails.packName + " #${card.details.cardDetails.packPosition}",
                        iconGlyph = PackIcon.fromPackCode(
                            card.details.cardDetails.packCode,
                            returnDefaultCore = false
                        ),
                        isSelected = false,
                        isRadio = true,
                        onValueChange = {
                            decksCreationViewModel.selectInvestigatorVersion(
                                card.details.cardDetails.code
                            )

                            showVersionSelectDialog = false
                        },
                    )

                    HorizontalDivider(color = CustomTheme.colors.divider)
                }

                card.details.duplicates.forEach { cardPackInfo ->
                    ArkhamCheckboxButton(
                        title = cardPackInfo.name + " #${cardPackInfo.position}",
                        iconGlyph = PackIcon.fromPackCode(
                            cardPackInfo.code,
                            returnDefaultCore = false
                        ),
                        isSelected = false,
                        isRadio = true,
                        onValueChange = {
                            decksCreationViewModel.selectInvestigatorVersion(cardPackInfo.cardCode)

                            showVersionSelectDialog = false
                        },
                    )

                    HorizontalDivider(color = CustomTheme.colors.divider)
                }

                card.details.reprints.forEach { cardPackInfo ->
                    ArkhamCheckboxButton(
                        title = cardPackInfo.name + " #${cardPackInfo.position}",
                        iconGlyph = PackIcon.fromPackCode(
                            cardPackInfo.code,
                            returnDefaultCore = false
                        ),
                        isSelected = false,
                        isRadio = true,
                        onValueChange = {
                            decksCreationViewModel.selectInvestigatorVersion(cardPackInfo.cardCode)

                            showVersionSelectDialog = false
                        },
                    )

                    HorizontalDivider(color = CustomTheme.colors.divider)
                }
            }
        }
    }
}

@ReadOnlyComposable
@Composable
private fun getDefaultDeckName(faction: Faction, investigatorName: String) = stringResource(
    when (faction) {
        Faction.Guardian -> R.string.the_adventures_of_investigator
        Faction.Seeker -> R.string.investigator_investigates
        Faction.Rogue -> R.string.the_investigator_job
        Faction.Mystic -> R.string.the_investigator_mysteries
        Faction.Survivor -> R.string.investigator_on_the_road
        else -> R.string.investigator_does_it_all
    },
    investigatorName
)