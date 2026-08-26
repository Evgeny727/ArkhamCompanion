package com.arkhamcompanion.data.objects

import android.util.Log
import com.arkhamcompanion.data.local.cards.CardCacheData
import com.arkhamcompanion.data.local.cards.CardEntity
import com.arkhamcompanion.domain.repository.AnalyticsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private val REGEX_SKILL_BOOST = Regex("""\+\d+?\s\[(.+?)]""")
private val USES_REGEX = Regex("""Uses\s\(\d+?\s(\w+?)\)""")
private val BONDED_REGEX = Regex("""Bonded\s\((.*?)\)(\.|\s)""")
private val REGEX_SUCCEED_BY = Regex("""succe(ssful|ed(?:s?|ed?))(:? at a skill test)? by(?! 0)""")
private val TAG_REGEX_FALLBACKS: Map<String, Regex> = mapOf(
    "fa" to Regex("""[Ff]irearm"""),
    "hd" to Regex("""[Hh]eal(?!ed)(?!th)(?! in excess of)[^.!?]*?damage"""),
    "hh" to Regex("""[Hh]eal(?!ed)(?!th)(?! in excess of)[^.!?]*?horror"""),
    "pa" to Regex("""[Pp]arley"""),
    "se" to Regex("""[Ss]eal(?! of the)"""),
)
private val ACTION_REGEX =
    Regex("<b>(Fight|Engage|Investigate|Draw|Resource|Move|Evade|Parley|Resign)")

object CardCache {

    //Flows for filters
    private val _actions = MutableStateFlow<Array<String>>(emptyArray())
    val actionsFlow = _actions.asStateFlow()

    private val _traits = MutableStateFlow<Array<String>>(emptyArray())
    val traitsFlow = _traits.asStateFlow()

    private val _uses = MutableStateFlow<Array<String>>(emptyArray())
    val usesFlow = _uses.asStateFlow()

    private val _slots = MutableStateFlow<Array<String>>(emptyArray())
    val slotsFlow = _slots.asStateFlow()

    var traits: MutableMap<String, MutableSet<String>> = mutableMapOf()
        private set
    var actions: MutableMap<String, MutableSet<String>> = mutableMapOf()
        private set
    //keys: fast and succeeds_by
    var properties: MutableMap<String, MutableSet<String>> = mutableMapOf()
        private set
    var skillBoosts: MutableMap<String, MutableSet<String>> = mutableMapOf()
        private set
    var uses: MutableMap<String, MutableSet<String>> = mutableMapOf()
        private set
    var slots: MutableMap<String, MutableSet<String>> = mutableMapOf()
        private set
    var tags: MutableMap<String, MutableSet<String>> = mutableMapOf()
        private set

    // `Daisy Walker`'s requires `Daisy's Tote Bag`.
    var requiredCards: MutableMap<String, MutableSet<String>> = mutableMapOf()
        private set
    // Jim Culver requires Vengeful Shade in the spirit deck.
    var sideDeckRequiredCards: MutableMap<String, MutableSet<String>> = mutableMapOf()
        private set
    // `Daisy's Tote Bag` is restrictory to `Daisy Walker`.
    var restrictedTo: MutableMap<String, MutableSet<String>> = mutableMapOf()
        private set
    // Advanced requiredCards for an investigator.
    var advanced: MutableMap<String, MutableSet<String>> = mutableMapOf()
        private set
    // Replacement requiredCards for an investigator.
    var replacement: MutableMap<String, MutableSet<String>> = mutableMapOf()
        private set
    // Roland banks has parallel card "Directive".
    var parallelCards: MutableMap<String, MutableSet<String>> = mutableMapOf()
        private set
    // Parallel versions of an investigator.
    var parallel: MutableMap<String, MutableSet<String>> = mutableMapOf()
        private set
    // Base version for a parallel investigator.
    var base: MutableMap<String, MutableSet<String>> = mutableMapOf()
        private set
    // Revised core "First Aid (3)" is a duplicate of Pallid Mask "First Aid (3)".
    var duplicates: MutableMap<String, MutableSet<String>> = mutableMapOf()
        private set
    // Laboratory Assistant is a chapter two reprint of Laboratory Assistant.
    var reprints: MutableMap<String, MutableSet<String>> = mutableMapOf()
        private set
    // Any card can have `n` different level version. (e.g. Ancient Stone)
    var level: MutableMap<String, MutableSet<String>> = mutableMapOf()
        private set
    // `Hallowed Mirror` has bound `Soothing Melody`.
    var bound: MutableMap<String, MutableSet<String>> = mutableMapOf()
        private set
    // `Soothing Melody` is bonded to `Hallowed Mirror`.
    var bonded: MutableMap<String, MutableSet<String>> = mutableMapOf()
        private set
    // `Predator or Prey?` is the front for `The Masked Hunter`.
    var fronts: MutableMap<String, String> = mutableMapOf()
        private set
    var backs: MutableMap<String, String> = mutableMapOf()
        private set
    // Agatha Crane exists both as a mystic and a seeker card.
    var otherVersions: MutableMap<String, MutableSet<String>> = mutableMapOf()
        private set
    // TCU Laboratory Assistant is a baseprint of Laboratory Assistant.
    var basePrints: MutableMap<String, MutableSet<String>> = mutableMapOf()
        private set

    //All card's relations by code
    var relationsCache: MutableMap<String, List<String>> = mutableMapOf()

    private fun clearCache() {
        traits = mutableMapOf()
        actions = mutableMapOf()
        properties = mutableMapOf()
        skillBoosts = mutableMapOf()
        uses = mutableMapOf()
        slots = mutableMapOf()
        tags = mutableMapOf()
        requiredCards = mutableMapOf()
        sideDeckRequiredCards = mutableMapOf()
        restrictedTo = mutableMapOf()
        advanced = mutableMapOf()
        replacement = mutableMapOf()
        parallelCards = mutableMapOf()
        parallel = mutableMapOf()
        base = mutableMapOf()
        duplicates = mutableMapOf()
        reprints = mutableMapOf()
        level = mutableMapOf()
        bound = mutableMapOf()
        bonded = mutableMapOf()
        fronts = mutableMapOf()
        backs = mutableMapOf()
        otherVersions = mutableMapOf()
        basePrints = mutableMapOf()
        relationsCache = mutableMapOf()
    }

    private data class Upgrade(
        val code: String,
        val subname: String?,
        val xp: Int,
    )

    suspend fun createCache(
        cardsList: List<CardEntity>,
        analyticsRepository: AnalyticsRepository
    ) = withContext(Dispatchers.Default) {
        clearCache()

        val cardsMap = cardsList.associateBy { it.code }

        val localBonded: MutableMap<String, MutableSet<String>> = mutableMapOf()
        val upgrades: MutableMap<String, MutableSet<Upgrade>> = mutableMapOf()
        val localBacks: MutableMap<String, String> = mutableMapOf()
        val investigatorsByName: MutableMap<String, MutableSet<String>> = mutableMapOf()
        val canonicalInvestigatorCodes: MutableSet<String> = mutableSetOf()
        val requiredCardCodes: MutableSet<String> = mutableSetOf()

        var missingInvestigators = 0

        // first pass: identify target cards.
        for (card in cardsList) {
            addIndices(card)

            if ((card.xp ?: -1) >= 0) {
                var xp = card.xp ?: 0
                if (card.exceptional) xp *= 2

                upgrades.addToSet(card.realName, Upgrade(
                    card.code,
                    card.realSubname,
                    xp
                ))
            }

            val bondedMatch = BONDED_REGEX.find(card.realText.orEmpty())
            if (bondedMatch != null) {
                val bondedName = bondedMatch.groupValues.getOrNull(1)
                if (!bondedName.isNullOrEmpty()) {
                    localBonded.addToSet(bondedName, card.code)
                }
            }

            card.backLinkId?.let { backLinkId ->
                localBacks[backLinkId] = card.code
            }

            if (
                card.typeCode == "investigator" &&
                card.deckLimit != null &&
                card.duplicateOfCode == null &&
                !card.altArtInvestigator &&
                card.alternateOfCode == null &&
                (card.encounterCode.isNullOrEmpty() || card.xp != null)
            ) {
                investigatorsByName.addToSet(card.realName, card.code)
                canonicalInvestigatorCodes.add(card.code)
            }

            card.deckRequirements?.jsonObject["card"]?.jsonArray?.forEach {
                it.jsonArray.forEach { code ->
                    requiredCardCodes.add(code.jsonPrimitive.content)
                }
            }

            card.sideDeckRequirements?.jsonObject["card"]?.jsonArray?.forEach {
                it.jsonArray.forEach { code ->
                    requiredCardCodes.add(code.jsonPrimitive.content)
                }
            }
        }

        // second pass: construct lookup tables.
        for (card in cardsList) {
            card.deckRequirements?.jsonObject["card"]?.jsonArray?.firstOrNull()?.let {
                it.jsonArray.forEach { code ->
                    requiredCards.addToSet(card.code, code.jsonPrimitive.content)
                }
            }

            card.sideDeckRequirements?.jsonObject["card"]?.jsonArray?.firstOrNull()?.let {
                it.jsonArray.forEach { code ->
                    sideDeckRequiredCards.addToSet(card.code, code.jsonPrimitive.content)
                }
            }

            val investigatorRestrictions = card.restrictions?.jsonObject["investigator"]?.jsonObject
            if (investigatorRestrictions != null && !card.hidden) {
                // Can have multiple entries (alternate arts).
                for (key in investigatorRestrictions.keys) {
                    val investigator = cardsMap[key]

                    if (investigator == null) {
                        analyticsRepository.logMessage("Missing investigator: $key")
                        missingInvestigators++
                        continue
                    }

                    if (investigator.duplicateOfCode != null) {
                        continue
                    }

                    restrictedTo.addToSet(card.code, key)

                    when {
                        card.realText?.contains("Advanced.") == true ->
                            advanced.addToSet(key, card.code)

                        card.realText?.contains("Replacement.") == true ->
                            replacement.addToSet(key, card.code)

                        else -> {
                            if (card.parallel && !requiredCardCodes.contains(card.code)) {
                                parallelCards.addToSet(key, card.code)
                            } else if (
                                !requiredCardCodes.contains(card.code) &&
                                card.duplicateOfCode == null &&
                                (card.deckLimit ?: 0) > 0
                            ) {
                                requiredCards.addToSet(key, card.code)
                            }
                        }
                    }
                }
            }

            if (card.parallelOfCode != null) {
                parallel.addToSet(card.parallelOfCode, card.code)

                base.addToSet(card.code, card.parallelOfCode)
            }

            card.duplicateOfCode?.let { duplicateOfCode ->
                duplicates.addToSet(duplicateOfCode, card.code)
                duplicates.addToSet(card.code, duplicateOfCode)

                val existingDuplicates = duplicates[duplicateOfCode].orEmpty()
                for (key in existingDuplicates) {
                    if (key != card.code) {
                        duplicates.addToSet(key, card.code)
                        duplicates.addToSet(card.code, key)
                    }
                }
            }

            card.reprintOfCode?.let { reprintOf ->
                reprints.addToSet(reprintOf, card.code)
                reprints.addToSet(card.code, reprintOf)
            }

            if (card.xp != null) {
                upgrades[card.realName]?.forEach { upgrade ->
                    if (card.code != upgrade.code){
                        var xp = card.xp
                        if (card.exceptional) xp *= 2

                        if (xp != upgrade.xp || card.realSubname != upgrade.subname) {
                            level.addToSet(card.code, upgrade.code)
                            level.addToSet(upgrade.code, card.code)
                        }
                    }
                }
            }

            // TODO: there is an edge case with Dream-Gate where the front should show when accessing `06015b` via
            //  a bond, but currently does not.
            if (!card.hidden) {
                val bondedCards = localBonded[card.realName]
                if (bondedCards != null) {
                    for (bondedCode in bondedCards) {
                        // beware the great hank samson.
                        if (bondedCode != card.code && !card.realText.orEmpty().startsWith("Bonded")) {
                            bound.addToSet(card.code, bondedCode)
                            bonded.addToSet(bondedCode, card.code)
                        }
                    }
                }
            }

            // Index multi-class investigators.
            if (
                card.typeCode == "investigator" &&
                card.deckLimit != null &&
                (investigatorsByName[card.realName]?.size ?: 0) > 1
            ) {
                for (investigator in investigatorsByName[card.realName].orEmpty()) {
                    if (investigator != card.code) {
                        otherVersions.addToSet(card.code, investigator)
                    }
                }
            }
        }

        if (missingInvestigators > 0) {
            analyticsRepository.logError(NoSuchElementException("Missing $missingInvestigators investigators."))
        }

        for ((back, front) in localBacks) {
            fronts[back] = front
            backs[front] = back
        }

        for ((investigator, entry) in parallel) {
            val parallelEntry = entry.firstOrNull() ?: continue

            advanced[parallelEntry] = advanced[investigator] ?: mutableSetOf()
            replacement[parallelEntry] = replacement[investigator] ?: mutableSetOf()
            bonded[parallelEntry] = bonded[investigator] ?: mutableSetOf()
            parallelCards[parallelEntry] = parallelCards[investigator] ?: mutableSetOf()

            for ((key, value) in restrictedTo) {
                if (value.contains(investigator)) {
                    restrictedTo.addToSet(key, parallelEntry)
                }
            }
        }

        for (code in canonicalInvestigatorCodes) {
            val duplicates = duplicates[code] ?: continue

            for (duplicateCode in duplicates) {
                requiredCards[duplicateCode] = requiredCards[code] ?: mutableSetOf()
                advanced[duplicateCode] = advanced[code] ?: mutableSetOf()
                replacement[duplicateCode] = replacement[code] ?: mutableSetOf()
                bonded[duplicateCode] = bonded[code] ?: mutableSetOf()
                parallelCards[duplicateCode] = parallelCards[code] ?: mutableSetOf()
                parallel[duplicateCode] = parallel[code] ?: mutableSetOf()
            }
        }

        for (code in reprints.keys.toList()) {
            val duplicates = duplicates[code] ?: continue

            for (duplicateCode in duplicates) {
                reprints[duplicateCode] = reprints[code] ?: mutableSetOf()

                for (reprintCode in reprints[code].orEmpty()) {
                    if (reprintCode != duplicateCode) {
                        basePrints.addToSet(reprintCode, duplicateCode)
                    }
                }
            }
        }

        _actions.value = actions.keys.toTypedArray()
        _traits.value = traits.keys.toTypedArray()
        _uses.value = uses.keys.toTypedArray()
        _slots.value = slots.keys.toTypedArray()
    }

    private fun addIndices(card: CardEntity) {
        val text = card.realText.orEmpty()
        val backText = card.realBackText.orEmpty()
        val combinedText = text + backText

        indexByTraits(card)
        indexByActions(card, combinedText)
        indexByFast(card, text)
        indexByTags(card, text)

        // handle additional index based on whether we are dealing with a player card or not.
        if (card.factionCode != "mythos") {
            indexBySucceedsBy(card, text)

            if (card.typeCode == "asset") {
                indexBySkillBoosts(card, text)
                indexByUses(card, text)
                indexBySlots(card)
            }
        }
    }

    private fun MutableMap<String, MutableSet<String>>.addToSet(
        key: String,
        value: String
    ) {
        getOrPut(key) { mutableSetOf() }.add(value)
    }

    private fun MutableMap<String, MutableSet<Upgrade>>.addToSet(
        key: String,
        value: Upgrade
    ) {
        getOrPut(key) { mutableSetOf() }.add(value)
    }

    private fun indexByTraits(card: CardEntity) {
        val cardTraits = (card.realTraits ?: "") + (card.realBackTraits ?: "")
        if (cardTraits.isBlank()) return
        for (trait in cardTraits.split(".")) {
            if (trait.isBlank()) continue
            traits.addToSet(trait.trim(), card.code)
        }
    }

    private fun indexByActions(card: CardEntity, cardText: String) {
        if (cardText.isBlank()) return
        ACTION_REGEX.findAll(cardText).forEach { match ->
            actions.addToSet(match.groupValues[1], card.code)
        }
    }

    private fun indexByFast(card: CardEntity, cardText: String) {
        if (cardText.contains("Fast.") || cardText.contains("gains fast.")) {
            properties.addToSet("fast", card.code)
        }
    }

    private fun indexBySucceedsBy(card: CardEntity, cardText: String) {
        if (REGEX_SUCCEED_BY.containsMatchIn(cardText)) {
            properties.addToSet("succeeds_by", card.code)
        }
    }

    private fun indexBySkillBoosts(card: CardEntity, cardText: String) {
        if (card.customizationOptions?.toString()?.contains("choose_skill") == true) {
            skillBoosts.addToSet("willpower", card.code)
            skillBoosts.addToSet("intellect", card.code)
            skillBoosts.addToSet("combat", card.code)
            skillBoosts.addToSet("agility", card.code)
        }

        REGEX_SKILL_BOOST.findAll(cardText).forEach { match ->
            val value = match.groupValues.getOrNull(1)
            if (!value.isNullOrEmpty()) {
                skillBoosts.addToSet(value, card.code)
            }
        }
    }

    private fun indexByUses(card: CardEntity, cardText: String) {
        val firstLine = cardText.indexOf('\n')
        if (firstLine == -1) return
        val usesMatch = USES_REGEX.find(cardText.substring(0, firstLine))?.groupValues?.getOrNull(1)
            ?.lowercase()

        if (usesMatch != null) {
            val value = if (usesMatch == "charge") "charges" else usesMatch
            uses.addToSet(value, card.code)
        }
    }

    private fun indexBySlots(card: CardEntity) {
        if (card.realSlot == null && card.typeCode != "asset") return
        else if (card.realSlot == null) {
            slots.addToSet("other", card.code)
            return
        }

        for (slot in card.realSlot.split(".")) {
            slots.addToSet(slot.trim().lowercase(), card.code)
        }
    }

    private fun indexByTags(card: CardEntity, cardText: String) {
        if (card.tags is JsonArray) {
            val parsedTags = card.tags.jsonArray.map { it.jsonPrimitive.content }
            for (tag in parsedTags) {
                tags.addToSet(tag, card.code)
            }
        } else if (!card.official) {
            for ((tag, regex) in TAG_REGEX_FALLBACKS) {
                if (regex.containsMatchIn(cardText)) {
                    tags.addToSet(tag, card.code)
                }
            }
        }
    }

    fun setRelationsByCode(code: String, relations: Set<String>) {
        relationsCache[code] = relations.toList()
    }

    fun load(data: CardCacheData) {
        traits = data.traits.mapValues { it.value.toMutableSet() }.toMutableMap()
        actions = data.actions.mapValues { it.value.toMutableSet() }.toMutableMap()
        properties = data.properties.mapValues { it.value.toMutableSet() }.toMutableMap()
        skillBoosts = data.skillBoosts.mapValues { it.value.toMutableSet() }.toMutableMap()
        uses = data.uses.mapValues { it.value.toMutableSet() }.toMutableMap()
        slots = data.slots.mapValues { it.value.toMutableSet() }.toMutableMap()
        tags = data.tags.mapValues { it.value.toMutableSet() }.toMutableMap()
        requiredCards = data.requiredCards.mapValues { it.value.toMutableSet() }.toMutableMap()
        sideDeckRequiredCards = data.sideDeckRequiredCards.mapValues { it.value.toMutableSet() }.toMutableMap()
        restrictedTo = data.restrictedTo.mapValues { it.value.toMutableSet() }.toMutableMap()
        advanced = data.advanced.mapValues { it.value.toMutableSet() }.toMutableMap()
        replacement = data.replacement.mapValues { it.value.toMutableSet() }.toMutableMap()
        parallelCards = data.parallelCards.mapValues { it.value.toMutableSet() }.toMutableMap()
        parallel = data.parallel.mapValues { it.value.toMutableSet() }.toMutableMap()
        base = data.base.mapValues { it.value.toMutableSet() }.toMutableMap()
        duplicates = data.duplicates.mapValues { it.value.toMutableSet() }.toMutableMap()
        reprints = data.reprints.mapValues { it.value.toMutableSet() }.toMutableMap()
        level = data.level.mapValues { it.value.toMutableSet() }.toMutableMap()
        bound = data.bound.mapValues { it.value.toMutableSet() }.toMutableMap()
        bonded = data.bonded.mapValues { it.value.toMutableSet() }.toMutableMap()
        fronts = data.fronts.toMutableMap()
        backs = data.backs.toMutableMap()
        otherVersions = data.otherVersions.mapValues { it.value.toMutableSet() }.toMutableMap()
        basePrints = data.basePrints.mapValues { it.value.toMutableSet() }.toMutableMap()
        relationsCache = mutableMapOf()

        _actions.value = data.actions.keys.toTypedArray()
        _traits.value = data.traits.keys.toTypedArray()
        _uses.value = data.uses.keys.toTypedArray()
        Log.e("slots", data.slots.keys.toString())
        _slots.value = data.slots.keys.toTypedArray()
        Log.e("slotsFlow", _slots.value.contentToString())
    }
}