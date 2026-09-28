package com.arkhamcompanion.data.utils

import android.util.Log
import com.arkhamcompanion.data.local.cards.CardSearchQLFields
import com.arkhamcompanion.data.local.cards.CardSearchResultEntity
import com.arkhamcompanion.data.mapper.domain.cards.toCustomizationOptions
import com.arkhamcompanion.data.mapper.domain.cards.toRestrictions
import com.arkhamcompanion.data.objects.CardCache
import com.arkhamcompanion.domain.enums.CardType
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.domain.model.cards.CardpoolFilter
import com.arkhamcompanion.domain.model.cards.CardpoolTarget
import com.arkhamcompanion.domain.model.cards.DeckOption
import com.arkhamcompanion.domain.model.cards.InvestigatorAccessConfig
import com.arkhamcompanion.domain.model.decks.Selection
import com.arkhamcompanion.domain.utils.Filter
import com.arkhamcompanion.domain.utils.GENERIC_CUSTOM_INVESTIGATORS
import com.arkhamcompanion.domain.utils.and
import com.arkhamcompanion.domain.utils.not
import com.arkhamcompanion.domain.utils.notUnless
import com.arkhamcompanion.domain.utils.or

internal fun List<CardSearchResultEntity>.filterByCardpool(
    cardpoolFilter: CardpoolFilter,
    logMessage: (String) -> Unit
): List<CardSearchResultEntity> {
    val playerCardsFilter = playerCardsFilter(cardpoolFilter, logMessage)

    val requiredCardsFilter: Filter<CardSearchQLFields> = or(
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
        { card -> cardpoolFilter.investigatorConfig.investigatorId in CardCache.restrictedTo[card.code].orEmpty() }
    )

    return this.filter {
        or(requiredCardsFilter, playerCardsFilter)(it.front)
    }
}

private fun playerCardsFilter(
    cardpoolFilter: CardpoolFilter,
    logMessage: (String) -> Unit
): Filter<CardSearchQLFields> {
    val ands = mutableListOf(
        filterRestrictions(cardpoolFilter.investigatorConfig)
    )
    val ors = mutableListOf<Filter<CardSearchQLFields>>()

    if (cardpoolFilter.target != CardpoolTarget.ExtraSlots) {
        ors.add { card -> card.subTypeCode != null
                && CardCache.bonded[card.code].orEmpty().isEmpty() }
    }

    if (cardpoolFilter.target != CardpoolTarget.ExtraSlots) {
        cardpoolFilter.deckOptions.forEach { deckOption ->
            val filter = if ((deckOption.limit ?: 0) == 0 || cardpoolFilter.showLimitedAccess) {
                optionFilter(deckOption, cardpoolFilter.investigatorConfig, logMessage)
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
            val filter = optionFilter(deckOption, cardpoolFilter.investigatorConfig, logMessage)
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
                optionFilter(deckOption, cardpoolFilter.investigatorConfig, logMessage)
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
) : Filter<CardSearchQLFields> = {
    val restrictions = it.restrictions?.toRestrictions()
    val faction = restrictions?.faction.orEmpty()
    val investigator = restrictions?.investigator.orEmpty()
    val trait = restrictions?.trait.orEmpty()

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
    config: InvestigatorAccessConfig,
    logMessage: (String) -> Unit
): Filter<CardSearchQLFields>? {
    Log.e("test", option.toString())
    // Unknown rules or duplicate rules.
    if (
        option.deckSizeSelect.isNotEmpty() || option.tag.contains("st") || option.tag.contains("uc")
    ) {
        return null
    }

    val optionFilters = mutableListOf<Filter<CardSearchQLFields>>()
    var filterCount = 0

    if (option.factionSelect.isNotEmpty()) {
        filterCount++

        val targetKey = option.id ?: "faction_selected"

        val selection = config.selections
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
        val selectFilters = mutableListOf<Filter<CardSearchQLFields>>()

        val selection = config.selections
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
    val optionFilters: List<Filter<CardSearchQLFields>>,
)

private fun parseOption(option: DeckOption): ParsedOption {
    val optionFilters = mutableListOf<Filter<CardSearchQLFields>>()
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

private fun filterFactions(factions: List<Faction>): Filter<CardSearchQLFields> =
    or(*factions.map { faction ->
        filterFaction(faction.name.lowercase())
    }.toTypedArray())

private fun filterFaction(faction: String): Filter<CardSearchQLFields> = { card ->
    card.factionCode == faction || card.faction2Code == faction || card.faction3Code == faction
}

private fun filterUses(uses: String): Filter<CardSearchQLFields> = { card ->
    val codes = CardCache.uses[uses].orEmpty()

    card.code in codes
}

private fun filterSlots(slot: String): Filter<CardSearchQLFields> = { card ->
    val codes = CardCache.slots[slot].orEmpty()

    card.code in codes
}

private fun filterCardLevel(levelRange: IntRange): Filter<CardSearchQLFields> = { card ->
    val level = if (card.realCustomizationText != null) 0 else card.xp

    level in levelRange
}

private fun filterPermanent(): Filter<CardSearchQLFields> = { card -> card.permanent }

private fun filterTag(tag: String): Filter<CardSearchQLFields> = { card ->
    val customizationOptions = card.customizationOptions?.toCustomizationOptions()
    val codes = CardCache.tags[tag].orEmpty()

    card.code in codes || customizationOptions?.any { tag in it.tags } == true
}

private fun filterText(regex: Regex): Filter<CardSearchQLFields> = { card ->
    card.realText?.matches(regex)  == true || card.realBackText?.matches(regex) == true
            || card.realCustomizationText?.matches(regex) == true
}

private fun filterTraits(traits: List<String>): Filter<CardSearchQLFields> =
    or(*traits.map { trait ->
        filterTrait(trait)
    }.toTypedArray())

private fun filterTrait(trait: String): Filter<CardSearchQLFields> = { card ->
    val traits = card.realTraits?.split(".")?.map { it.trim().lowercase() }.orEmpty().toSet()
    val backTraits = card.realBackTraits?.split(".")?.map { it.trim().lowercase() }.orEmpty().toSet()
    val customizationTraits = card.customizationOptions?.toCustomizationOptions()
        ?.mapNotNull { option -> option.realTraits?.split(".")?.map { it.trim().lowercase() } }
        ?.flatten()
        ?.toSet()
        .orEmpty()

    trait in traits || trait in backTraits || trait in customizationTraits
}

private fun filterType(types: List<CardType>): Filter<CardSearchQLFields> = { card ->
    CardType.byType(card.typeCode) in types
}