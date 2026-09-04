package com.arkhamcompanion.domain.model.cards

import com.arkhamcompanion.domain.model.settings.Collection
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class CardDetailsWithRelations(
    val card: RelatedCard,
    val cardRelations: CardRelations,
)

data class CardRelations(

    val bound: ImmutableList<RelatedCard>,
    val bonded: ImmutableList<RelatedCard>,

    val restrictedTo: ImmutableList<RelatedCard>,

    val parallel: RelatedCard?,
    val base: RelatedCard?,

    val advanced: ImmutableList<RelatedCard>,
    val replacement: ImmutableList<RelatedCard>,
    val requiredCards: ImmutableList<RelatedCard>,
    val sideDeckRequiredCards: ImmutableList<RelatedCard>,
    val parallelCards: ImmutableList<RelatedCard>,
    val otherVersions: ImmutableList<RelatedCard>,
    val level: ImmutableList<RelatedCard>,
    val otherSignatures: ImmutableList<RelatedCard>,
)

data class RelatedCard(
    val details: CardDetailsWithPackInfo,
    val backDetails: CardDetailsWithPackInfo? = null,
)

data class CardDetailsWithPackInfo(
    val cardDetails: CardDetails,
    val duplicates: ImmutableList<CardPackInfo>,
    val reprints: ImmutableList<CardPackInfo>,
    val reprintDuplicates: ImmutableList<CardPackInfo>,
) {
    val allPacks: ImmutableList<CardPack> by lazy(LazyThreadSafetyMode.NONE) {
        buildList {
            addAll(packsWithoutReprints)
            reprintDuplicates.forEach(::addPackInfo)
            reprints.forEach(::addPackInfo)
        }.toImmutableList()
    }

    private val packsWithoutReprints: ImmutableList<CardPack> by lazy(LazyThreadSafetyMode.NONE) {
        buildList {
            addCard(cardDetails)
            duplicates.forEach(::addPackInfo)
        }.toImmutableList()
    }

    fun firstPackIn(collection: Collection): CardPack? =
        allPacks.firstOrNull { it.code in collection.packs || it.code in collection.reprintPacks }

    fun firstPackInWithoutReprints(collection: Collection): CardPack? =
        packsWithoutReprints.firstOrNull { it.code in collection.packs || it.code in collection.reprintPacks }
}

data class CardPack(
    val code: String,
    val name: String,
    val quantity: Int,
    val position: Int,
)

private fun MutableList<CardPack>.addCard(card: CardDetails) {
    card.reprintPackCode?.let { code ->
        add(
            CardPack(
                code = code,
                name = card.reprintPackName!!,
                quantity = card.quantity,
                position = card.packPosition,
            )
        )
    }

    add(
        CardPack(
            code = card.packCode,
            name = card.packName,
            quantity = card.quantity,
            position = card.packPosition,
        )
    )
}

private fun MutableList<CardPack>.addPackInfo(info: CardPackInfo) {
    info.reprintCode?.let { code ->
        add(
            CardPack(
                code = code,
                name = info.reprintName!!,
                quantity = info.quantity,
                position = info.position,
            )
        )
    }

    add(
        CardPack(
            code = info.code,
            name = info.name,
            quantity = info.quantity,
            position = info.position,
        )
    )
}