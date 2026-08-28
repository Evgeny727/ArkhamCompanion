package com.arkhamcompanion.ui.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.enums.CardType
import com.arkhamcompanion.domain.model.cards.CardSearchResult
import com.arkhamcompanion.ui.cards.components.details.cardDetailsDeckbuildingSection
import com.arkhamcompanion.ui.cards.components.details.cardDetailsRelationSection
import com.arkhamcompanion.ui.cards.components.details.cardDetailsRelationSectionSingle
import com.arkhamcompanion.ui.cards.components.details.cardDetailsWithLinkedBack
import com.arkhamcompanion.ui.cards.components.details.rememberCardTextStyles
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.CardTextStyleResolver
import com.arkhamcompanion.ui.utils.applyScaffoldPaddings
import kotlinx.collections.immutable.ImmutableList

@Composable
fun CardDetailsScreen(
    cardCode: String,
    cardCodes: ImmutableList<CardSearchResult>,
    cardDetailsViewModel: CardDetailsViewModel,
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues
) {
    val collection by cardDetailsViewModel.collectionFlow.collectAsState()
    val ignoreCollection by cardDetailsViewModel.ignoreCollectionFlow.collectAsState()
    val showFanmade by cardDetailsViewModel.showFanmadeFlow.collectAsState()

    val pagerState = rememberPagerState { cardCodes.size }
    var pagerReady by rememberSaveable { mutableStateOf(false) }

    /*
     * The last cardCode for which we intentionally positioned the pager.
     * The value survives, so we don't jump back to the original cardCode.
     */
    var handledCardCode by rememberSaveable { mutableStateOf<String?>(null) }

    /*
     * Synchronize the pager with the requested cardCode.
     *
     * The effect also depends on cardCodes because the requested card might
     * not exist in the new list immediately after a spoiler change.
     *
     * We don't care how many times cardCodes changes because we only perform
     * the scroll while handledCardCode != cardCode.
     */
    LaunchedEffect(cardCode, cardCodes) {
        val targetPage = cardCodes.indexOfFirst { it.code == cardCode }

        if (targetPage < 0) {
            // The requested card isn't available in the current result yet.
            pagerReady = false
            return@LaunchedEffect
        }

        if (handledCardCode != cardCode) {
            pagerReady = false

            pagerState.scrollToPage(targetPage)

            handledCardCode = cardCode
        }

        pagerReady = true
    }

    val styles = rememberCardTextStyles(flavorText = false)
    val flavorStyles = rememberCardTextStyles(flavorText = true)
    val styleResolver = remember(styles) {
        CardTextStyleResolver(styles)
    }
    val flavorStyleResolver = remember(flavorStyles) {
        CardTextStyleResolver(flavorStyles)
    }

    HorizontalPager(
        state = pagerState,
        key = { page -> cardCodes[page].code },
        modifier = modifier
            .fillMaxSize()
            .applyScaffoldPaddings(innerPadding),
        beyondViewportPageCount = 1
    ) { page ->
        val item = cardCodes[page]
        val cardDetailsWithRelations by cardDetailsViewModel.getCardDetailsWithRelations(
            item.code,
            item.tabooSetId
        ).collectAsState(null)

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AnimatedVisibility(visible = !pagerReady || cardDetailsWithRelations == null) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = CustomTheme.colors.m)
                }
            }

            if (pagerReady) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(
                        start = 8.dp,
                        end = 8.dp,
                        top = 8.dp,
                        bottom = 32.dp
                    )
                ) {
                    val isInvestigator = cardDetailsWithRelations?.card?.details?.cardDetails?.type ==
                            CardType.Investigator

                    val isParallel = cardDetailsWithRelations?.cardRelations?.parallel != null
                    val isBase = cardDetailsWithRelations?.cardRelations?.base != null

                    cardDetailsWithRelations?.card?.let { relatedCard ->
                        cardDetailsWithLinkedBack(relatedCard, "main", collection, styleResolver, flavorStyleResolver)
                    }

                    if (isInvestigator) {
                        if (isParallel) {
                            cardDetailsWithRelations?.cardRelations?.parallel?.let { relatedCard ->
                                cardDetailsRelationSectionSingle(
                                    relatedCard = relatedCard,
                                    prefix = "parallel",
                                    sectionTitleResId = R.string.parallel_investigator,
                                    collection = collection,
                                    showFanmade = showFanmade,
                                    ignoreCollection = ignoreCollection,
                                    styleResolver = styleResolver,
                                    flavorStyleResolver = flavorStyleResolver
                                )
                            }
                        }

                        if (isBase) {
                            cardDetailsWithRelations?.cardRelations?.base?.let { relatedCard ->
                                cardDetailsRelationSectionSingle(
                                    relatedCard = relatedCard,
                                    prefix = "base",
                                    sectionTitleResId = R.string.base_investigator,
                                    collection = collection,
                                    showFanmade = showFanmade,
                                    ignoreCollection = ignoreCollection,
                                    styleResolver = styleResolver,
                                    flavorStyleResolver = flavorStyleResolver
                                )
                            }
                        }

                        cardDetailsWithRelations?.cardRelations?.otherVersions.run {
                            if (isNullOrEmpty()) return@run

                            cardDetailsRelationSection(
                                relatedCards = this,
                                prefix = "other_versions",
                                sectionTitleResId = R.string.other_versions,
                                collection = collection,
                                showFanmade = showFanmade,
                                ignoreCollection = ignoreCollection,
                                styleResolver = styleResolver,
                                flavorStyleResolver = flavorStyleResolver
                            )
                        }

                        if (cardDetailsWithRelations?.card?.details?.cardDetails?.encounterCode == null) {
                            cardDetailsDeckbuildingSection(
                                onShowBaseInvestigatorCards = { /*TODO:Show base cardpool*/ },
                                onShowParallelInvestigatorCards = if (isParallel || isBase) { {
                                    /*TODO:Show parallel cardpool*/
                                } } else null,
                                isBase = !isBase
                            ) {
                                /*TODO:Create new deck*/
                            }
                        }
                    }

                    cardDetailsWithRelations?.cardRelations?.restrictedTo.run {
                        if (isNullOrEmpty()) return@run

                        cardDetailsRelationSection(
                            relatedCards = this,
                            prefix = "restricted_to",
                            sectionTitleResId = R.string.restricted_to,
                            collection = collection,
                            showFanmade = showFanmade,
                            ignoreCollection = ignoreCollection,
                            styleResolver = styleResolver,
                            flavorStyleResolver = flavorStyleResolver
                        )
                    }

                    cardDetailsWithRelations?.cardRelations?.requiredCards.run {
                        if (isNullOrEmpty()) return@run

                        cardDetailsRelationSection(
                            relatedCards = this,
                            prefix = "required",
                            sectionTitleResId = R.string.required_cards,
                            collection = collection,
                            showFanmade = showFanmade,
                            ignoreCollection = ignoreCollection,
                            styleResolver = styleResolver,
                            flavorStyleResolver = flavorStyleResolver
                        )
                    }

                    cardDetailsWithRelations?.cardRelations?.sideDeckRequiredCards.run {
                        if (isNullOrEmpty()) return@run

                        cardDetailsRelationSection(
                            relatedCards = this,
                            prefix = "side_required_to",
                            sectionTitleResId = R.string.side_required_cards,
                            collection = collection,
                            showFanmade = showFanmade,
                            ignoreCollection = ignoreCollection,
                            styleResolver = styleResolver,
                            flavorStyleResolver = flavorStyleResolver
                        )
                    }

                    cardDetailsWithRelations?.cardRelations?.advanced.run {
                        if (isNullOrEmpty()) return@run

                        cardDetailsRelationSection(
                            relatedCards = this,
                            prefix = "advanced",
                            sectionTitleResId = R.string.advanced_cards,
                            collection = collection,
                            showFanmade = showFanmade,
                            ignoreCollection = ignoreCollection,
                            styleResolver = styleResolver,
                            flavorStyleResolver = flavorStyleResolver
                        )
                    }

                    cardDetailsWithRelations?.cardRelations?.replacement.run {
                        if (isNullOrEmpty()) return@run

                        cardDetailsRelationSection(
                            relatedCards = this,
                            prefix = "replacement",
                            sectionTitleResId = R.string.replacement_cards,
                            collection = collection,
                            showFanmade = showFanmade,
                            ignoreCollection = ignoreCollection,
                            styleResolver = styleResolver,
                            flavorStyleResolver = flavorStyleResolver
                        )
                    }

                    cardDetailsWithRelations?.cardRelations?.parallelCards.run {
                        if (isNullOrEmpty()) return@run

                        cardDetailsRelationSection(
                            relatedCards = this,
                            prefix = "parallel_card",
                            sectionTitleResId = R.string.parallel_cards,
                            collection = collection,
                            showFanmade = showFanmade,
                            ignoreCollection = ignoreCollection,
                            styleResolver = styleResolver,
                            flavorStyleResolver = flavorStyleResolver
                        )
                    }

                    cardDetailsWithRelations?.cardRelations?.otherSignatures.run {
                        if (isNullOrEmpty()) return@run

                        cardDetailsRelationSection(
                            relatedCards = this,
                            prefix = "other_signature",
                            sectionTitleResId = R.string.other_signature_cards,
                            collection = collection,
                            showFanmade = showFanmade,
                            ignoreCollection = ignoreCollection,
                            styleResolver = styleResolver,
                            flavorStyleResolver = flavorStyleResolver
                        )
                    }

                    cardDetailsWithRelations?.cardRelations?.bound.run {
                        if (isNullOrEmpty()) return@run

                        cardDetailsRelationSection(
                            relatedCards = this,
                            prefix = "bound",
                            sectionTitleResId = R.string.bound_cards,
                            collection = collection,
                            showFanmade = showFanmade,
                            ignoreCollection = ignoreCollection,
                            styleResolver = styleResolver,
                            flavorStyleResolver = flavorStyleResolver
                        )
                    }

                    cardDetailsWithRelations?.cardRelations?.bonded.run {
                        if (isNullOrEmpty()) return@run

                        cardDetailsRelationSection(
                            relatedCards = this,
                            prefix = "bonded",
                            sectionTitleResId = R.string.bonded,
                            collection = collection,
                            showFanmade = showFanmade,
                            ignoreCollection = ignoreCollection,
                            styleResolver = styleResolver,
                            flavorStyleResolver = flavorStyleResolver
                        )
                    }

                    cardDetailsWithRelations?.cardRelations?.level.run {
                        if (isNullOrEmpty()) return@run

                        cardDetailsRelationSection(
                            relatedCards = this,
                            prefix = "level",
                            sectionTitleResId = R.string.other_level_cards,
                            collection = collection,
                            showFanmade = showFanmade,
                            ignoreCollection = ignoreCollection,
                            styleResolver = styleResolver,
                            flavorStyleResolver = flavorStyleResolver
                        )
                    }
                }
            }
        }
    }
}