package com.arkhamcompanion.domain.utils

import com.arkhamcompanion.domain.enums.DeckEnvironment
import com.arkhamcompanion.domain.model.decks.ResolvedEnvironment
import com.arkhamcompanion.domain.model.meta.Pack
import com.arkhamcompanion.domain.model.settings.Collection
import com.arkhamcompanion.domain.model.settings.contains
import com.arkhamcompanion.domain.model.settings.isEmpty
import com.arkhamcompanion.domain.utils.ArkhamEnvironments.current
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet

private val NON_DECKBUILDING_POOL_CYCLE_CODES = setOf(
    "parallel",
    "promotional",
    "side_stories",
    "small_campaign_expansions",
    "scenario_packs_ch2",
    "zcam", //Fan-made campaigns
    "zsid", //Fan-made side stories
)

fun isDeckbuildingPack(cycleCode: String): Boolean =
    cycleCode !in NON_DECKBUILDING_POOL_CYCLE_CODES

object ArkhamEnvironments {
    fun current(): ImmutableList<String> =
        persistentListOf("cycle:core_ch2", "cycle:investigator_decks_ch2")

    fun limited(packs: List<Pack>): ImmutableList<String> = buildList {
        packs.forEach { pack ->
            RETURN_TO_CYCLES[pack.cycleCode]?.let { returnPack ->
                addAll(listOf(pack.code, returnPack))
            } ?: add(pack.code)
        }
        add("cycle:core_ch2")
        add("cycle:investigator_decks_ch2")
    }.toImmutableList()

    fun collection(collection: Collection, packs: List<Pack>): ImmutableList<String> {
        val packsMap = packs.associateBy { it.code }

        return buildSet {
            (collection.packs + collection.reprintPacks).forEach { packCode ->
                packsMap[packCode]?.let { pack ->
                    if (!isDeckbuildingPack(pack.cycleCode)) return@forEach

                    if (!pack.reprint && pack.cycleCode in REPRINT_CYCLE_CODES) {
                        add("${pack.cycleCode}p")
                    } else {
                        add(pack.code)
                    }
                }
            }
        }.toImmutableList()
    }

    fun chapter(packs: List<Pack>, chapter: Int): ImmutableList<String> = buildSet {
        packs.forEach { pack ->
            if (
                pack.chapter != chapter ||
                !isDeckbuildingPack(pack.cycleCode)
            ) {
                return@forEach
            }

            if (!pack.reprint && pack.cycleCode in REPRINT_CYCLE_CODES) return@forEach

            add(pack.code)
        }
    }.toImmutableList()

    //Campaign Play-Along from Mythos Busters discord
    fun cpa(cycleCode: String, chapter: Int): ImmutableList<String> = buildList {
        when (cycleCode) {
            "core" -> add("rcore")

            "core_ch2" -> add("core_2026")

            else -> add("${cycleCode}p")
        }

        RETURN_TO_CYCLES[cycleCode]?.let { add(it) }

        if (chapter == 2) {
            addAll(listOf("cycle:core_ch2", "cycle:investigator_decks_ch2"))
        } else {
            addAll(listOf("cycle:investigator", "cycle:core", "rtnotz"))
        }
    }.toImmutableList()
}

class EnvironmentClassifier(private val packs: List<Pack>) {

    val packsByCode = packs.associateBy { it.code }

    private data class CyclePacks(
        val reprints: List<String>,
        val regular: List<String>,
    )

    private val packsByCycle: Map<String, CyclePacks> = packs
        .asSequence()
        .filter { isDeckbuildingPack(it.cycleCode) }
        .groupBy { it.cycleCode }
        .mapValues { (_, cyclePacks) ->
            val sorted = cyclePacks.sortedBy { it.position }

            CyclePacks(
                reprints = sorted.filter { it.reprint }.map { it.code },
                regular = sorted.filterNot { it.reprint }.map { it.code },
            )
        }

    private val limitedGroupsByPackCode = packs.associate { pack ->
        val group = when {
            !isDeckbuildingPack(pack.cycleCode) -> null

            pack.cycleCode == "core_ch2" -> null
            pack.cycleCode == "investigator_decks_ch2" -> null
            pack.cycleCode == "return" -> null

            pack.cycleCode == "investigator" -> "investigator"

            else -> pack.code
        }

        pack.code to group
    }

    private val currentCollection = resolveLimitedPoolToCollection(cardPool = current())

    fun resolve(cardPool: Iterable<String>): ResolvedEnvironment =
        resolve(resolveLimitedPoolToCollection(cardPool))

    fun resolve(cardPool: Collection): ResolvedEnvironment {
        if (cardPool.isEmpty()) {
            return ResolvedEnvironment(DeckEnvironment.Legacy)
        }

        if (currentCollection.contains(cardPool)) {
            return ResolvedEnvironment(DeckEnvironment.Current)
        }

        val expansionCodes = findLimitedExpansions(cardPool)

        return if (expansionCodes.size == 3) {
            ResolvedEnvironment(
                type = DeckEnvironment.Limited,
                expansionCodes = expansionCodes.toImmutableList()
            )
        } else {
            ResolvedEnvironment(DeckEnvironment.Custom)
        }
    }

    private fun findLimitedExpansions(collection: Collection): List<String> {
        val groups = mutableSetOf<String>()

        for (packCode in collection.packs) {
            limitedGroupsByPackCode[packCode]?.let(groups::add)
            if (groups.size > 3) return emptyList()
        }

        for (packCode in collection.reprintPacks) {
            limitedGroupsByPackCode[packCode]?.let(groups::add)
            if (groups.size > 3) return emptyList()
        }

        return groups.toList()
    }

    fun resolveLimitedPoolToCollection(cardPool: Iterable<String>): Collection {
        val selectedPacks = mutableSetOf<String>()
        val reprintPacks = mutableSetOf<String>()

        cardPool.forEach { pool ->
            val cycleCode = pool.removePrefix("cycle:")

            if (cycleCode != pool) {
                packsByCycle[cycleCode]?.let { cycle ->
                    if (cycle.regular.isNotEmpty()) {
                        if (cycleCode == "core") {
                            selectedPacks.addAll(cycle.regular)
                        } else {
                            if (cycle.reprints.isNotEmpty()) {
                                reprintPacks.addAll(cycle.reprints)
                            } else {
                                selectedPacks.addAll(cycle.regular)
                            }
                        }
                    }
                }
            } else if (!pool.startsWith("card:")) {
                packsByCode[pool]?.let { pack ->
                    if (pack.reprint) reprintPacks.add(pack.code)
                    else selectedPacks.add(pack.code)
                }
            }
        }

        return Collection(
            packs = selectedPacks.toImmutableSet(),
            reprintPacks = reprintPacks.toImmutableSet()
        )
    }
}