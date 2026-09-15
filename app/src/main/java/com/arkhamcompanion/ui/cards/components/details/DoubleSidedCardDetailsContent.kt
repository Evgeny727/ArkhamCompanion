package com.arkhamcompanion.ui.cards.components.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.enums.CardBackType
import com.arkhamcompanion.domain.enums.CardType
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.domain.model.cards.CardDetails
import com.arkhamcompanion.domain.model.cards.CardDetailsWithPackInfo
import com.arkhamcompanion.domain.model.settings.Collection
import com.arkhamcompanion.ui.components.ArkhamRoundedFactionCard
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.CardTextStyleResolver

fun LazyListScope.doubleSidedCardDetails(
    cardDetailsWithPackInfo: CardDetailsWithPackInfo,
    prefix: String,
    collection: Collection,
    suffix: String = "",
    styleResolver: CardTextStyleResolver,
    flavorStyleResolver: CardTextStyleResolver,
    toggleFavorite: (String, Boolean) -> Unit,
) {
    cardDetailsWithPackInfo.run {
        val firstPackInCollection = firstPackIn(collection)
        val isBackFirst = cardDetails.isBackFirst()
        val noBackHeader = cardDetails.type == CardType.Investigator
        var isFirst = true

        if (isBackFirst) {
            //Back
            item(
                key = "${prefix}_${cardDetails.id}${suffix}_backside",
                contentType = "card_details"
            ) {
                ArkhamRoundedFactionCard(
                    faction = if (cardDetails.faction2 != null) Faction.Dual
                    else cardDetails.faction,
                    modifier = Modifier.fillMaxWidth(),
                    header = if (noBackHeader) null else {
                        {
                            CardDetailsHeader(
                                cardDetails,
                                firstPackInCollection = firstPackInCollection,
                                isBack = true
                            )
                        }
                    },
                    footer = if (!isFirst) null else {
                        {
                            CardDetailsFooter(
                                cardDetails.official,
                                onFaq = { /*TODO:navigate to FAQ screen*/ },
                                cardDetails.tabooSetId,
                                cardDetails.tabooPlaceholder,
                                onTaboo = { /*TODO:navigate to taboo history screen*/ },
                                cardDetails.isFavorite,
                                onFavorite = {
                                    toggleFavorite(
                                        cardDetails.code,
                                        !cardDetails.isFavorite
                                    )
                                }
                            )
                        }
                    }
                ) {
                    CardDetailsBackContent(
                        cardDetailsWithPackInfo,
                        styleResolver,
                        flavorStyleResolver
                    )
                }
            }

            isFirst = false
        }

        //Front
        item(
            key = "${prefix}_${cardDetails.id}$suffix",
            contentType = "card_details"
        ) {
            ArkhamRoundedFactionCard(
                faction = if (cardDetails.faction2 != null) Faction.Dual
                else cardDetails.faction,
                modifier = Modifier.fillMaxWidth(),
                header = {
                    CardDetailsHeader(
                        cardDetails,
                        firstPackInCollection = firstPackInCollection
                    )
                },
                footer = if (!isFirst) null else {
                    {
                        CardDetailsFooter(
                            cardDetails.official,
                            onFaq = { /*TODO:navigate to FAQ screen*/ },
                            cardDetails.tabooSetId,
                            cardDetails.tabooPlaceholder,
                            onTaboo = { /*TODO:navigate to taboo history screen*/ },
                            cardDetails.isFavorite,
                            onFavorite = {
                                toggleFavorite(
                                    cardDetails.code,
                                    !cardDetails.isFavorite
                                )
                            }
                        )
                    }
                }
            ) {
                CardDetailsFrontContent(
                    cardDetailsWithPackInfo,
                    firstPackInCollection?.code,
                    styleResolver,
                    flavorStyleResolver
                )
            }

            isFirst = false
        }

        cardDetails.parsedCustomizationText?.let { customizationText ->
            item(
                key = "${prefix}_${cardDetails.id}${suffix}_customization",
                contentType = "card_details"
            ) {
                ArkhamRoundedFactionCard(
                    faction = if (cardDetails.faction2 != null) Faction.Dual
                    else cardDetails.faction,
                    modifier = Modifier.fillMaxWidth(),
                    header = {
                        CardDetailsHeader(
                            cardDetails,
                            firstPackInCollection = null,
                            isCustomizableSheet = true
                        )
                    }
                ) {
                    ParsedCardText(customizationText, styleResolver, isCustomizationText = true)
                }
            }
        }

        if (!isBackFirst && cardDetails.doubleSided) {
            //Back
            item(
                key = "${prefix}_${cardDetails.id}${suffix}_backside",
                contentType = "card_details"
            ) {
                ArkhamRoundedFactionCard(
                    faction = if (cardDetails.faction2 != null) Faction.Dual
                    else cardDetails.faction,
                    modifier = Modifier.fillMaxWidth(),
                    header = if (noBackHeader) null else {
                        {
                            CardDetailsHeader(
                                cardDetails,
                                firstPackInCollection = firstPackInCollection,
                                isBack = true
                            )
                        }
                    }
                ) {
                    CardDetailsBackContent(
                        cardDetailsWithPackInfo,
                        styleResolver,
                        flavorStyleResolver
                    )
                }
            }
        }
    }
}

private fun CardDetails.isBackFirst(): Boolean {
    return doubleSided && type.isLocationLike() && encounterCode != null
}

@Composable
fun CardDetailsFrontContent(
    cardDetailsWithPackInfo: CardDetailsWithPackInfo,
    firstPackInCollection: String?,
    styleResolver: CardTextStyleResolver,
    flavorStyleResolver: CardTextStyleResolver
) {
    cardDetailsWithPackInfo.run {
        val flavorFirst = with(cardDetails) {
            type == CardType.Agenda || type == CardType.Act || type == CardType.Story
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CardDetailsMetaBlock(cardDetails)

            cardDetails.realSlot?.let { slots ->
                CardDetailsSlotsBlock(slots)
            }

            if (cardDetails.type != CardType.Story) cardDetails.run {
                CardDetailsClickableThumbnail(
                    thumbnailUrl = thumbnailUrl,
                    imageUrl = imageUrl,
                    backImageUrl = backInfo?.imageUrl ?: backImageUrl,
                    taboSetId = if (tabooPlaceholder) null else tabooSetId,
                    backTaboSetId = backInfo?.tabooPlaceholder?.let { if (it) null else backInfo?.tabooSetId },
                    code = code,
                    backCode = backInfo?.code,
                    type = type,
                    backCardType = backInfo?.type,
                    backType = backType,
                    encounterCode = encounterCode,
                    subType = subType,
                    faction = faction,
                    faction2 = faction2,
                )
            }
        }

        if (flavorFirst) {
            cardDetails.parsedFlavor?.let {
                ParsedCardText(it, flavorStyleResolver, isFlavor = true)
            }
        }

        cardDetails.parsedText?.let {
            ParsedCardText(it, styleResolver)
        }

        cardDetails.victory?.let {
            Text(
                text = stringResource(R.string.victory_value, it),
                style = CustomTheme.typography.run { small + bold }
            )
        }

        cardDetails.vengeance?.let {
            Text(
                text = stringResource(R.string.vengeance_value, it),
                style = CustomTheme.typography.run { small + bold }
            )
        }

        if (!flavorFirst) {
            cardDetails.parsedFlavor?.let {
                ParsedCardText(it, flavorStyleResolver, isFlavor = true)
            }
        }

        if (cardDetails.tabooSetId != null && !cardDetails.tabooPlaceholder) {
            CardDetailsTabooBlock(
                tabooXp = cardDetails.tabooXp,
                tabooOriginalText = cardDetails.parsedTabooOriginalText,
                tabooOriginalBackText = cardDetails.parsedTabooOriginalBackText,
                styleResolver = styleResolver,
                deckLimit = cardDetails.deckLimit ?: 0
            )
        }
    }

    CardDetailsPackInfoBlock(
        cardDetailsWithPackInfo = cardDetailsWithPackInfo,
        firstPackInCollection = firstPackInCollection
    )
}

@Composable
fun CardDetailsBackContent(
    cardDetailsWithPackInfo: CardDetailsWithPackInfo,
    styleResolver: CardTextStyleResolver,
    flavorStyleResolver: CardTextStyleResolver
) {
    cardDetailsWithPackInfo.run {
        val flavorFirst = with(cardDetails) {
            type == CardType.Agenda || type == CardType.Act || type == CardType.Story
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CardDetailsMetaBlock(cardDetails, simpleBack = true)

            cardDetails.realSlot?.let { slots ->
                CardDetailsSlotsBlock(slots)
            }

            if (cardDetails.type != CardType.Investigator && cardDetails.type != CardType.Story) {
                cardDetails.run {
                    CardDetailsClickableThumbnail(
                        thumbnailUrl = backThumbnailUrl,
                        imageUrl = imageUrl,
                        backImageUrl = backImageUrl,
                        taboSetId = if (tabooPlaceholder) null else tabooSetId,
                        backTaboSetId = if (tabooPlaceholder) null else tabooSetId,
                        code = code,
                        backCode = null,
                        type = type,
                        backCardType = null,
                        backType = CardBackType.Card,
                        encounterCode = encounterCode,
                        subType = subType,
                        faction = faction,
                        faction2 = faction2,
                        isBackFirst = true
                    )
                }
            }
        }

        if (flavorFirst) {
            cardDetails.parsedBackFlavor?.let {
                ParsedCardText(it, flavorStyleResolver, isFlavor = true)
            }
        }

        cardDetails.parsedBackText?.let {
            ParsedCardText(it, styleResolver)
        }

        if (!flavorFirst) {
            cardDetails.parsedBackFlavor?.let {
                ParsedCardText(it, flavorStyleResolver, isFlavor = true)
            }
        }

        if (cardDetails.illustrator != cardDetails.backIllustrator && cardDetails.backIllustrator != null) {
            CardDetailsPackInfoBlock(
                cardDetailsWithPackInfo = cardDetailsWithPackInfo,
                onlyIllustrator = true
            )
        }
    }
}