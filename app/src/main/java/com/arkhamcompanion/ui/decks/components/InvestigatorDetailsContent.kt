package com.arkhamcompanion.ui.decks.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.domain.model.cards.CardDetailsWithPackInfo
import com.arkhamcompanion.domain.model.settings.Collection
import com.arkhamcompanion.ui.cards.components.details.CardDetailsBackContent
import com.arkhamcompanion.ui.cards.components.details.CardDetailsFrontContent
import com.arkhamcompanion.ui.cards.components.details.CardDetailsHeader
import com.arkhamcompanion.ui.components.ArkhamRoundedFactionCard
import com.arkhamcompanion.ui.utils.CardTextStyleResolver

fun LazyListScope.investigatorDetailsFront(
    cardDetailsWithPackInfo: CardDetailsWithPackInfo,
    collection: Collection,
    styleResolver: CardTextStyleResolver,
    flavorStyleResolver: CardTextStyleResolver,
) {
    cardDetailsWithPackInfo.run {
        val firstPackInCollection = firstPackIn(collection)

        item(
            key = "front_${cardDetails.id}",
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
            ) {
                CardDetailsFrontContent(
                    cardDetailsWithPackInfo,
                    firstPackInCollection?.code,
                    styleResolver,
                    flavorStyleResolver,
                    isDeckInvestigatorView = true
                )
            }
        }
    }
}

fun LazyListScope.investigatorDetailsBack(
    cardDetailsWithPackInfo: CardDetailsWithPackInfo,
    styleResolver: CardTextStyleResolver,
    flavorStyleResolver: CardTextStyleResolver,
) {
    cardDetailsWithPackInfo.run {

        item(
            key = "back_${cardDetails.id}",
            contentType = "card_details"
        ) {
            ArkhamRoundedFactionCard(
                faction = if (cardDetails.faction2 != null) Faction.Dual
                else cardDetails.faction,
                modifier = Modifier.fillMaxWidth(),
            ) {
                CardDetailsBackContent(
                    cardDetailsWithPackInfo,
                    styleResolver,
                    flavorStyleResolver,
                    isDeckInvestigatorView = true
                )
            }
        }
    }
}