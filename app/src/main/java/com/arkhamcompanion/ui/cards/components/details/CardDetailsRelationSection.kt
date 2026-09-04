package com.arkhamcompanion.ui.cards.components.details

import androidx.annotation.StringRes
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.res.stringResource
import com.arkhamcompanion.domain.model.cards.RelatedCard
import com.arkhamcompanion.domain.model.settings.Collection
import com.arkhamcompanion.ui.utils.CardTextStyleResolver
import kotlinx.collections.immutable.ImmutableList

fun LazyListScope.cardDetailsRelationSection(
    relatedCards: ImmutableList<RelatedCard>,
    prefix: String,
    @StringRes sectionTitleResId: Int,
    collection: Collection,
    showFanmade: Boolean,
    ignoreCollection: Boolean,
    styleResolver: CardTextStyleResolver,
    flavorStyleResolver: CardTextStyleResolver,
) {
    var headerAdded = false

    relatedCards.forEach { relatedCard ->
        if (!relatedCard.shouldShow(collection, ignoreCollection, showFanmade)) return@forEach

        if (!headerAdded) {
            headerAdded = true
            cardDetailsRelationSectionHeader(prefix, sectionTitleResId)
        }

        cardDetailsWithLinkedBack(relatedCard, prefix, collection, styleResolver, flavorStyleResolver)
    }
}

fun LazyListScope.cardDetailsRelationSectionSingle(
    relatedCard: RelatedCard,
    prefix: String,
    @StringRes sectionTitleResId: Int,
    collection: Collection,
    showFanmade: Boolean,
    ignoreCollection: Boolean,
    styleResolver: CardTextStyleResolver,
    flavorStyleResolver: CardTextStyleResolver,
) {
    if (!relatedCard.shouldShow(collection, ignoreCollection, showFanmade)) return

    cardDetailsRelationSectionHeader(prefix, sectionTitleResId)

    cardDetailsWithLinkedBack(relatedCard, prefix, collection, styleResolver, flavorStyleResolver)
}

private fun LazyListScope.cardDetailsRelationSectionHeader(
    prefix: String,
    @StringRes sectionTitleResId: Int,
) {
    item("${prefix}_header", contentType = "header") {
        CardDetailsSectionHeader(
            title = stringResource(sectionTitleResId),
            normalCase = false
        )
    }
}

fun LazyListScope.cardDetailsWithLinkedBack(
    relatedCard: RelatedCard,
    prefix: String,
    collection: Collection,
    styleResolver: CardTextStyleResolver,
    flavorStyleResolver: CardTextStyleResolver,
) {
    relatedCard.details.run {
        doubleSidedCardDetails(
            cardDetailsWithPackInfo = this,
            prefix = prefix,
            collection = collection,
            styleResolver = styleResolver,
            flavorStyleResolver = flavorStyleResolver
        )
    }

    relatedCard.backDetails?.run {
        doubleSidedCardDetails(
            cardDetailsWithPackInfo = this,
            prefix = prefix,
            collection = collection,
            suffix = "_back",
            styleResolver = styleResolver,
            flavorStyleResolver = flavorStyleResolver
        )
    }
}

private fun RelatedCard.shouldShow(
    collection: Collection,
    ignoreCollection: Boolean,
    showFanmade: Boolean
): Boolean {
    if (!showFanmade && !details.cardDetails.official) return false

    return if (ignoreCollection) true else details.firstPackInWithoutReprints(collection) != null
}