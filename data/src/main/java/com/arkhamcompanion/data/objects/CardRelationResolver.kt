package com.arkhamcompanion.data.objects

import com.arkhamcompanion.domain.enums.CardType
import com.arkhamcompanion.domain.model.cards.CardDetailsWithPackInfo
import com.arkhamcompanion.domain.model.cards.CardDetailsWithRelations
import com.arkhamcompanion.domain.model.cards.CardRelations
import com.arkhamcompanion.domain.model.cards.RelatedCard
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

object CardRelationResolver {

    private data class Node(
        val code: String,
        val withRelations: Boolean,
    )

    fun resolveCardCodesWithRelations(code: String): Collection<String> {

        CardCache.relationsCache[code]?.let {
            if (it.isNotEmpty()) return@resolveCardCodesWithRelations it
        }

        val visited = mutableSetOf<String>()
        val result = mutableSetOf<String>()
        val stack = ArrayDeque<Node>()

        stack.addLast(Node(code, true))

        while (stack.isNotEmpty()) {
            val (currentCode, includeRelations) = stack.removeLast()

            if (!visited.add(currentCode)) continue

            result += currentCode

            CardCache.backs[currentCode]?.let {
                stack.addLast(Node(it, true))
            }

            CardCache.fronts[currentCode]?.let {
                stack.addLast(Node(it, true))
            }

            if (includeRelations) {
                stack.pushRelations(currentCode, RelationType.Advanced)
                stack.pushRelations(currentCode, RelationType.Base, true)
                stack.pushRelations(currentCode, RelationType.Parallel, true)
                stack.pushRelations(currentCode, RelationType.Replacement)
                stack.pushRelations(currentCode, RelationType.RequiredCards)
                stack.pushRelations(currentCode, RelationType.SideDeckRequiredCards)
                stack.pushRelations(currentCode, RelationType.ParallelCards)
                stack.pushRelations(currentCode, RelationType.OtherVersions)
                stack.pushRelations(currentCode, RelationType.RestrictedTo)
                stack.pushRelations(currentCode, RelationType.Level)

                val restrictedToInvestigator = getRelations(currentCode, RelationType.RestrictedTo)
                restrictedToInvestigator.firstOrNull()?.let {
                    stack.pushRelations(it, RelationType.Advanced)
                    stack.pushRelations(it, RelationType.RequiredCards)
                    stack.pushRelations(it, RelationType.Replacement)
                }
            }

            stack.pushRelations(currentCode, RelationType.Duplicates)
            stack.pushRelations(currentCode, RelationType.Reprints)
            stack.pushRelations(currentCode, RelationType.Bound)
            stack.pushRelations(currentCode, RelationType.Bonded)
        }

        CardCache.setRelationsByCode(code, result)
        return result
    }

    private fun ArrayDeque<Node>.pushRelations(
        code: String,
        relationType: RelationType,
        singleRelation: Boolean = false
    ) {
        val relations = getRelations(code, relationType)
        if (singleRelation) relations.firstOrNull()?.let { addLast(Node(it, false)) }
        else relations.forEach { addLast(Node(it, false)) }
    }

    private fun getRelations(
        code: String,
        relationType: RelationType,
    ): Set<String> =
        when (relationType) {
            RelationType.Advanced -> CardCache.advanced[code]
            RelationType.Base -> CardCache.base[code]
            RelationType.Parallel -> CardCache.parallel[code]
            RelationType.Replacement -> CardCache.replacement[code]
            RelationType.RequiredCards -> CardCache.requiredCards[code]
            RelationType.SideDeckRequiredCards -> CardCache.sideDeckRequiredCards[code]
            RelationType.ParallelCards -> CardCache.parallelCards[code]
            RelationType.OtherVersions -> CardCache.otherVersions[code]
            RelationType.RestrictedTo -> CardCache.restrictedTo[code]
            RelationType.Level -> CardCache.level[code]
            RelationType.Duplicates -> CardCache.duplicates[code]
            RelationType.Reprints -> CardCache.reprints[code]
            RelationType.Bound -> CardCache.bound[code]
            RelationType.Bonded -> CardCache.bonded[code]
        } ?: emptySet()

    fun buildCardWithRelations(
        rootCode: String,
        cardsMap: Map<String, CardDetailsWithPackInfo>
    ): CardDetailsWithRelations {
        val root = cardsMap[rootCode]!!

        val back = CardCache.backs[rootCode]?.let {
            cardsMap[it]
        }


        var restrictedTo: ImmutableList<RelatedCard> = persistentListOf()
        var parallel: RelatedCard? = null
        var base: RelatedCard? = null
        var advanced: ImmutableList<RelatedCard> = persistentListOf()
        var replacement: ImmutableList<RelatedCard> = persistentListOf()
        var requiredCards: ImmutableList<RelatedCard> = persistentListOf()
        var sideDeckRequiredCards: ImmutableList<RelatedCard> = persistentListOf()
        var parallelCards: ImmutableList<RelatedCard> = persistentListOf()
        var otherVersions: ImmutableList<RelatedCard> = persistentListOf()
        var level: ImmutableList<RelatedCard> = persistentListOf()
        var otherSignatures: ImmutableList<RelatedCard> = persistentListOf()

        if (root.cardDetails.type == CardType.Investigator) {
            advanced = cardsMap.buildRelationList(rootCode, RelationType.Advanced)
            base = cardsMap.buildSingleRelation(rootCode, RelationType.Base)
            parallel = cardsMap.buildSingleRelation(rootCode, RelationType.Parallel)
            replacement = cardsMap.buildRelationList(rootCode, RelationType.Replacement)
            requiredCards = cardsMap.buildRelationList(rootCode, RelationType.RequiredCards)
            sideDeckRequiredCards = cardsMap.buildRelationList(rootCode, RelationType.SideDeckRequiredCards)
            parallelCards = cardsMap.buildRelationList(rootCode, RelationType.ParallelCards)
            otherVersions = cardsMap.buildRelationList(rootCode, RelationType.OtherVersions)
        } else {
            restrictedTo = cardsMap.buildRelationList(rootCode, RelationType.RestrictedTo)
            level = cardsMap.buildRelationList(rootCode, RelationType.Level)

            restrictedTo.firstOrNull()?.let { investigator ->
                val otherAdvanced = cardsMap.buildRelationList(
                    investigator.details.cardDetails.code,
                    RelationType.Advanced
                )
                val otherRequired = cardsMap.buildRelationList(
                    investigator.details.cardDetails.code,
                    RelationType.RequiredCards
                )
                val otherReplacement = cardsMap.buildRelationList(
                    investigator.details.cardDetails.code,
                    RelationType.Replacement
                )

                val duplicateCodes = getRelations(rootCode, RelationType.Duplicates) +
                        getRelations(rootCode, RelationType.Bound) + getRelations(rootCode, RelationType.Bonded)

                val seenCodes = mutableSetOf<String>()

                val matched = buildList {
                    otherAdvanced
                        .asSequence()
                        .plus(otherRequired)
                        .plus(otherReplacement)
                        .forEach { card ->
                            val code = card.details.cardDetails.code

                            if (
                                code != rootCode &&
                                code !in duplicateCodes &&
                                card.details.cardDetails.subType == root.cardDetails.subType &&
                                seenCodes.add(code)
                            ) {
                                add(card)
                            }
                        }
                }.sortedBy { it.details.cardDetails.name }

                otherSignatures = matched.toImmutableList()
            }
        }

        val bound: ImmutableList<RelatedCard> =
            cardsMap.buildRelationList(rootCode, RelationType.Bound)
        val bonded: ImmutableList<RelatedCard> =
            cardsMap.buildRelationList(rootCode, RelationType.Bonded)

        return CardDetailsWithRelations(
            card = RelatedCard(
                details = root,
                backDetails = back,
            ),
            cardRelations = CardRelations(
                bound = bound,
                bonded = bonded,
                restrictedTo = restrictedTo,
                parallel = parallel,
                base = base,
                advanced = advanced,
                replacement = replacement,
                requiredCards = requiredCards,
                sideDeckRequiredCards = sideDeckRequiredCards,
                parallelCards = parallelCards,
                otherVersions = otherVersions,
                level = level,
                otherSignatures = otherSignatures,
            )
        )
    }

    private val relationComparator =
        compareBy<RelatedCard>(
            {
                CardSortOrder.sortByTypeOrder(
                    it.details.cardDetails.type.name.lowercase(),
                    it.details.cardDetails.subType?.name?.lowercase()
                )
            },
            { it.details.cardDetails.name },
            { it.details.cardDetails.xp ?: -1 },
            { it.details.cardDetails.packPosition },
        )

    private fun Map<String, CardDetailsWithPackInfo>.buildRelationList(
        rootCode: String,
        relation: RelationType,
    ): ImmutableList<RelatedCard> {
        return buildList {
            getRelations(rootCode, relation).forEach { code ->

                val front = this@buildRelationList[code]
                    ?.takeIf { it.cardDetails.duplicateOfCode == null }
                    ?: return@forEach

                add(
                    RelatedCard(
                        details = front,
                        backDetails = (CardCache.backs[code] ?: CardCache.fronts[code])
                            ?.let(this@buildRelationList::get)
                    )
                )
            }
        }.sortedWith(relationComparator).toImmutableList()
    }

    private fun Map<String, CardDetailsWithPackInfo>.buildSingleRelation(
        rootCode: String,
        relation: RelationType,
    ): RelatedCard? =
        buildRelationList(rootCode, relation).firstOrNull()
}

enum class RelationType {
    Advanced, Base, Parallel, Replacement, RequiredCards, SideDeckRequiredCards, ParallelCards,
    OtherVersions, RestrictedTo, Level, Duplicates, Reprints, Bound, Bonded
}