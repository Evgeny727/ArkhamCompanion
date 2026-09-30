package com.arkhamcompanion.data.utils

import com.arkhamcompanion.data.local.cards.CardSearchResultEntity
import com.arkhamcompanion.data.mapper.db.toAccessFields
import com.arkhamcompanion.data.objects.CardCache
import com.arkhamcompanion.domain.enums.CardType
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.domain.model.cards.CardInvestigatorAccessFields
import com.arkhamcompanion.domain.model.cards.CardpoolFilter
import com.arkhamcompanion.domain.model.cards.CardpoolTarget
import com.arkhamcompanion.domain.model.cards.DeckOption
import com.arkhamcompanion.domain.model.cards.InvestigatorAccessConfig
import com.arkhamcompanion.domain.model.decks.Selection
import com.arkhamcompanion.domain.model.decks.Selections
import com.arkhamcompanion.domain.utils.Filter
import com.arkhamcompanion.domain.utils.GENERIC_CUSTOM_INVESTIGATORS
import com.arkhamcompanion.domain.utils.and
import com.arkhamcompanion.domain.utils.not
import com.arkhamcompanion.domain.utils.notUnless
import com.arkhamcompanion.domain.utils.or

internal fun List<CardSearchResultEntity>.filterInvestigatorsByCards(
    cards: Set<CardInvestigatorAccessFields>,
    logMessage: (String) -> Unit
): List<CardSearchResultEntity> {
    return this.filter {
        val investigator = it.front.toAccessFields()

        val cardpoolFilter = CardpoolFilter(
            deckOptions = investigator.deckOptions.orEmpty(),
            requiredCardCodes = investigator.deckRequirements?.card?.flatten().orEmpty().toSet(),
            sideDeckOptions = investigator.sideDeckOptions.orEmpty(),
            sideDeckRequiredCardCodes = investigator.sideDeckRequirements?.card?.flatten().orEmpty().toSet(),
            investigatorConfig = InvestigatorAccessConfig(
                investigatorId = investigator.alternateOfCode
                    ?: investigator.duplicateOfCode
                    ?: investigator.code,
                investigatorName = investigator.name,
                investigatorFaction = investigator.faction,
                investigatorTraits = investigator.realTraits,
            ),
            target = CardpoolTarget.Both,
        )

        cards.canInvestigatorTakeAll(cardpoolFilter, logMessage)
    }
}

private fun Collection<CardInvestigatorAccessFields>.canInvestigatorTakeAll(
    cardpoolFilter: CardpoolFilter,
    logMessage: (String) -> Unit
): Boolean {
    val filter = investigatorCardFilter(cardpoolFilter, logMessage)

    return all(filter)
}

internal fun List<CardSearchResultEntity>.filterByInvestigatorAccess(
    cardpoolFilter: CardpoolFilter,
    logMessage: (String) -> Unit
): List<CardSearchResultEntity> {
    val filter = investigatorCardFilter(cardpoolFilter, logMessage)

    return filter {
        val fields = it.front.toAccessFields()

        fields.type == CardType.Investigator || filter(fields)
    }
}

private fun investigatorCardFilter(
    cardpoolFilter: CardpoolFilter,
    logMessage: (String) -> Unit,
): Filter<CardInvestigatorAccessFields> {
    val playerCardsFilter = playerCardsFilter(cardpoolFilter, logMessage)

    val requiredCardsFilter: Filter<CardInvestigatorAccessFields> = or(
        if (cardpoolFilter.target != CardpoolTarget.ExtraSlots) {
            { card -> card.code in cardpoolFilter.requiredCardCodes }
        } else {
            { false }
        },
        if (cardpoolFilter.target != CardpoolTarget.Slots) {
            { card -> card.code in cardpoolFilter.sideDeckRequiredCardCodes }
        } else {
            { false }
        },
        { card ->
            cardpoolFilter.investigatorConfig.investigatorId in
                    CardCache.restrictedTo[card.code].orEmpty()
        },
    )

    return or(requiredCardsFilter, playerCardsFilter)
}

private fun playerCardsFilter(
    cardpoolFilter: CardpoolFilter,
    logMessage: (String) -> Unit
): Filter<CardInvestigatorAccessFields> {
    val ands = mutableListOf(
        filterRestrictions(cardpoolFilter.investigatorConfig)
    )
    val ors = mutableListOf<Filter<CardInvestigatorAccessFields>>()

    if (cardpoolFilter.target != CardpoolTarget.ExtraSlots) {
        ors.add { card -> card.subType != null
                && CardCache.bonded[card.code].orEmpty().isEmpty() }
    }

    if (cardpoolFilter.target != CardpoolTarget.ExtraSlots) {
        cardpoolFilter.deckOptions.forEach { deckOption ->
            val filter = if ((deckOption.limit ?: 0) == 0 || cardpoolFilter.showLimitedAccess) {
                optionFilter(deckOption, cardpoolFilter.investigatorConfig.selections, logMessage)
            } else {
                { false }
            }

            if (filter == null) return@forEach

            if (deckOption.not) {
                // When encountering a NOT, every filter that comes before can be considered an "unless".
                val newAnd = if (ors.isNotEmpty()) {
                    notUnless(filter, ors.toList())
                } else {
                    not(filter)
                }

                ands.add(newAnd)
            } else {
                ors.add(filter)
            }
        }
    }

    if (cardpoolFilter.target != CardpoolTarget.Slots) {
        cardpoolFilter.sideDeckOptions.forEach { deckOption ->
            val filter = optionFilter(deckOption, cardpoolFilter.investigatorConfig.selections, logMessage)
                ?: return@forEach

            if (deckOption.not) {
                // When encountering a NOT, every filter that comes before can be considered an "unless".
                val newAnd = if (ors.isNotEmpty()) {
                    notUnless(filter, ors.toList())
                } else {
                    not(filter)
                }

                ands.add(newAnd)
            } else {
                ors.add(filter)
            }
        }
    }

    if (cardpoolFilter.target != CardpoolTarget.ExtraSlots) {
        cardpoolFilter.additionalDeckOptions.forEach { deckOption ->
            val filter = if ((deckOption.limit ?: 0) == 0 || cardpoolFilter.showLimitedAccess) {
                optionFilter(deckOption, cardpoolFilter.investigatorConfig.selections, logMessage)
            } else {
                { false }
            }

            if (filter == null) return@forEach

            if (deckOption.not) {
                ands.add(not(filter))
            } else {
                ors.add(filter)
            }
        }
    }

    return and(
        *ands.toTypedArray(),
        or(*ors.toTypedArray())
    )
}

private fun filterRestrictions(
    config: InvestigatorAccessConfig
) : Filter<CardInvestigatorAccessFields> = {
    val faction = it.restrictions?.faction.orEmpty()
    val investigator = it.restrictions?.investigator.orEmpty()
    val trait = it.restrictions?.trait.orEmpty()

    when {
        investigator.isNotEmpty() -> config.investigatorId in investigator

        faction.isNotEmpty() -> config.investigatorFaction in faction

        trait.isNotEmpty() -> {
            config.investigatorId in GENERIC_CUSTOM_INVESTIGATORS
                    || trait.any { trait -> trait.lowercase() in config.investigatorTraits }
        }

        else -> true
    }
}

private fun optionFilter(
    option: DeckOption,
    selections: Selections?,
    logMessage: (String) -> Unit
): Filter<CardInvestigatorAccessFields>? {
    // Unknown rules or duplicate rules.
    if (
        option.deckSizeSelect.isNotEmpty() || option.tag.contains("st") || option.tag.contains("uc")
    ) {
        return null
    }

    val optionFilters = mutableListOf<Filter<CardInvestigatorAccessFields>>()
    var filterCount = 0

    if (option.factionSelect.isNotEmpty()) {
        filterCount++

        val targetKey = option.id ?: "faction_selected"

        val selection = selections
            ?.get(targetKey) as? Selection.FactionSelection

        val value = selection?.value

        optionFilters += if (value is String) {
            filterFactions(listOf(Faction.byFaction(value)))
        } else {
            filterFactions(option.factionSelect)
        }
    }

    // parallel Wendy + Marion
    if (option.optionSelect.isNotEmpty()) {
        val selectFilters = mutableListOf<Filter<CardInvestigatorAccessFields>>()

        val selection = selections
            ?.get(option.id ?: "option_selected") as? Selection.OptionSelection

        val value = selection?.value?.id

        for (select in option.optionSelect) {
            if (value != null && select.id != value) {
                continue
            }

            val parsed = parseOption(option = DeckOption(
                id = select.id,
                level = select.level,
                name = select.name,
                trait = select.trait,
                type = select.type
            ))

            if (parsed.filterCount <= 1) {
                logMessage("optionFilter: unknown option select $select")
            }

            selectFilters += and(*parsed.optionFilters.toTypedArray())
        }

        filterCount += selectFilters.size + 1
        optionFilters += or(*selectFilters.toTypedArray())
    }

    val parsed = parseOption(option = option)

    optionFilters += parsed.optionFilters
    filterCount += parsed.filterCount

    if (filterCount <= 1 && option.atLeast == null) {
        logMessage("optionFilter: unknown deck requirement $option")
    }

    return if (filterCount > 1) {
        and(*optionFilters.toTypedArray())
    } else {
        null
    }
}

private data class ParsedOption(
    val filterCount: Int,
    val optionFilters: List<Filter<CardInvestigatorAccessFields>>,
)

private fun parseOption(option: DeckOption): ParsedOption {
    val optionFilters = mutableListOf<Filter<CardInvestigatorAccessFields>>()
    var filterCount = 0

    if (option.limit != null || option.not) {
        filterCount++
    }

    if (option.faction.isNotEmpty()) {
        filterCount++
        optionFilters += filterFactions(option.faction)
    }

    //Level
    filterCount++
    optionFilters += filterCardLevel(option.level)

    if (option.permanent == true) {
        optionFilters += filterPermanent()
    } else if (option.permanent == false) {
        optionFilters += not(filterPermanent())
    }

    if (option.trait.isNotEmpty()) {
        filterCount++
        optionFilters += filterTraits(option.trait)
    }

    if (option.uses.isNotEmpty()) {
        filterCount++
        optionFilters += or(
            *option.uses.map { filterUses(it) }.toTypedArray()
        )
    }

    if (option.type.isNotEmpty()) {
        filterCount++
        optionFilters += filterType(option.type)
    }

    // Tag-based access
    if (option.tag.isNotEmpty()) {
        filterCount++

        optionFilters += or(
            *option.tag.map { tag ->
                filterTag(tag)
            }.toTypedArray()
        )
    }

    // Text-based access
    if (option.text.isNotEmpty() && option.tag.isEmpty()) {
        filterCount++

        optionFilters += or(
            *option.text.map { text ->
                filterText(text)
            }.toTypedArray()
        )
    }

    // Slot-based access
    if (option.slot.isNotEmpty()) {
        filterCount++
        optionFilters += or(
            *option.slot.map { slot ->
                filterSlots(slot)
            }.toTypedArray()
        )
    }

    return ParsedOption(
        filterCount = filterCount,
        optionFilters = optionFilters,
    )
}

private fun filterFactions(factions: List<Faction>): Filter<CardInvestigatorAccessFields> =
    or(*factions.map { faction ->
        filterFaction(faction)
    }.toTypedArray())

private fun filterFaction(faction: Faction): Filter<CardInvestigatorAccessFields> = { card ->
    card.faction == faction || card.faction2 == faction || card.faction3 == faction
}

private fun filterUses(uses: String): Filter<CardInvestigatorAccessFields> = { card ->
    val codes = CardCache.uses[uses].orEmpty()

    card.code in codes
}

private fun filterSlots(slot: String): Filter<CardInvestigatorAccessFields> = { card ->
    val codes = CardCache.slots[slot].orEmpty()

    card.code in codes
}

private fun filterCardLevel(levelRange: IntRange): Filter<CardInvestigatorAccessFields> = { card ->
    val level = if (card.realCustomizationText != null) 0 else card.xp

    level in levelRange
}

private fun filterPermanent(): Filter<CardInvestigatorAccessFields> = { card -> card.permanent }

private fun filterTag(tag: String): Filter<CardInvestigatorAccessFields> = { card ->
    val codes = CardCache.tags[tag].orEmpty()

    card.code in codes || card.customizationOptions?.any { tag in it.tags } == true
}

private fun filterText(regex: Regex): Filter<CardInvestigatorAccessFields> = { card ->
    card.realText?.matches(regex)  == true || card.realBackText?.matches(regex) == true
            || card.realCustomizationText?.matches(regex) == true
}

private fun filterTraits(traits: List<String>): Filter<CardInvestigatorAccessFields> =
    or(*traits.map { trait ->
        filterTrait(trait)
    }.toTypedArray())

private fun filterTrait(trait: String): Filter<CardInvestigatorAccessFields> = { card ->
    val customizationTraits = card.customizationOptions?.mapNotNull {
        option -> option.realTraits?.split(".")?.map { it.trim().lowercase() }
    }
        ?.flatten()
        ?.toSet()
        .orEmpty()

    trait in card.realTraits || trait in customizationTraits
}

private fun filterType(types: List<CardType>): Filter<CardInvestigatorAccessFields> =
    { card -> card.type in types }