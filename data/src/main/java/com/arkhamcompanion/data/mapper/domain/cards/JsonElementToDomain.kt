package com.arkhamcompanion.data.mapper.domain.cards

import com.arkhamcompanion.domain.enums.CardSubType
import com.arkhamcompanion.domain.enums.CardType
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.domain.model.cards.AtLeastOption
import com.arkhamcompanion.domain.model.cards.CustomizationChoice
import com.arkhamcompanion.domain.model.cards.CustomizationOption
import com.arkhamcompanion.domain.model.cards.CustomizationOptionCard
import com.arkhamcompanion.domain.model.cards.CustomizationTextChange
import com.arkhamcompanion.domain.model.cards.DeckOption
import com.arkhamcompanion.domain.model.cards.DeckRequirements
import com.arkhamcompanion.domain.model.cards.OptionSelect
import com.arkhamcompanion.domain.model.cards.RandomCard
import com.arkhamcompanion.domain.model.cards.Restrictions
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal fun JsonElement.toDeckOptions(): ImmutableList<DeckOption> =
    this.jsonArray.map { it.jsonObject.toDeckOption() }.toImmutableList()

internal fun JsonObject.toDeckOption(): DeckOption = DeckOption(
    atLeast = this["atleast"]?.jsonObject?.toAtLeastOption(),
    deckSizeSelect = this["deck_size_select"]?.jsonArray?.map {
        it.jsonPrimitive.content
    }.orEmpty().toImmutableList(),
    error = this["error"]?.jsonPrimitive?.content,
    factionSelect = this["faction_select"]?.jsonArray?.map {
        Faction.byFaction(it.jsonPrimitive.content.lowercase())
    }.orEmpty().toImmutableList(),
    faction = this["faction"]?.jsonArray?.map {
        Faction.byFaction(it.jsonPrimitive.content.lowercase())
    }.orEmpty().toImmutableList(),
    id = this["id"]?.jsonPrimitive?.content,
    level = this["level"].run {
        val level = this?.jsonObject
        IntRange(
            level?.get("min")?.jsonPrimitive?.int?.coerceAtLeast(0) ?: 0,
            level?.get("max")?.jsonPrimitive?.int?.coerceAtMost(5) ?: 5
        )
    },
    limit = this["limit"]?.jsonPrimitive?.intOrNull,
    name = this["name"]?.jsonPrimitive?.content,
    not = this["not"]?.jsonPrimitive?.booleanOrNull ?: false,
    optionSelect = this["option_select"]?.jsonArray?.map {
        it.jsonObject.toOptionSelect()
    }.orEmpty().toImmutableList(),
    permanent = this["permanent"]?.jsonPrimitive?.booleanOrNull,
    slot = this["slot"]?.jsonArray?.map {
        it.jsonPrimitive.content.lowercase()
    }.orEmpty().toImmutableList(),
    tag = this["tag"]?.jsonArray?.map {
        it.jsonPrimitive.content.lowercase()
    }.orEmpty().toImmutableList(),
    text = this["text"]?.jsonArray?.map {
        it.jsonPrimitive.content.toRegex()
    }.orEmpty().toImmutableList(),
    trait = this["trait"]?.jsonArray?.map {
        it.jsonPrimitive.content.lowercase()
    }.orEmpty().toImmutableList(),
    type = this["type"]?.jsonArray?.map {
        CardType.byType(it.jsonPrimitive.content.lowercase())
    }.orEmpty().toImmutableList(),
    uses = this["uses"]?.jsonArray?.map {
        it.jsonPrimitive.content.lowercase()
    }.orEmpty().toImmutableList(),
    virtual = this["virtual"]?.jsonPrimitive?.booleanOrNull ?: false
)

private fun JsonObject.toAtLeastOption(): AtLeastOption = AtLeastOption(
    factions = this["factions"]?.jsonPrimitive?.intOrNull,
    min = this["min"]!!.jsonPrimitive.int,
    types = this["types"]?.jsonPrimitive?.intOrNull,
    traits = this["traits"]?.jsonPrimitive?.intOrNull
)

private fun JsonObject.toOptionSelect(): OptionSelect = OptionSelect(
    id = this["id"]!!.jsonPrimitive.content,
    level = this["level"].run {
        val level = this?.jsonObject
        IntRange(
            level?.get("min")?.jsonPrimitive?.int?.coerceAtLeast(0) ?: 0,
            level?.get("max")?.jsonPrimitive?.int?.coerceAtMost(5) ?: 5
        )
    },
    name = this["name"]!!.jsonPrimitive.content,
    size = this["size"]?.jsonPrimitive?.intOrNull,
    trait = this["trait"]?.jsonArray?.map {
        it.jsonPrimitive.content.lowercase()
    }.orEmpty().toImmutableList(),
    type = this["type"]?.jsonArray?.map {
        CardType.byType(it.jsonPrimitive.content.lowercase())
    }.orEmpty().toImmutableList(),
)

internal fun JsonElement.toDeckRequirements(): DeckRequirements {
    val requirements = this.jsonObject
    val card = requirements["card"]?.jsonArray
    val size = requirements["size"]!!.jsonPrimitive.int
    val random = requirements["random"]?.jsonArray

    return DeckRequirements(
        card = card?.map { entry ->
            entry.jsonArray.map { it.jsonPrimitive.content }.toImmutableList()
        }.orEmpty().toImmutableList(),
        size = size,
        random = random?.map {
            val obj = it.jsonObject
            val value = obj["value"]!!.jsonPrimitive.content
            val target = obj["target"]!!.jsonPrimitive.content

            RandomCard(
                value = value,
                target = when (target) {
                    "subtype" -> CardSubType.bySubType(value)
                    else -> target
                },
            )
        }.orEmpty().toImmutableList()
    )
}

internal fun JsonElement.toRestrictions(): Restrictions {
    val restrictions = this.jsonObject

    return Restrictions(
        faction = restrictions["faction"]?.jsonArray?.map {
            Faction.byFaction(it.jsonPrimitive.content.lowercase())
        }.orEmpty().toImmutableList(),
        investigator = restrictions["investigator"]?.jsonObject?.keys.orEmpty().toImmutableList(),
        trait = restrictions["trait"]?.jsonArray?.map {
            it.jsonPrimitive.content.lowercase()
        }.orEmpty().toImmutableList()
    )
}

internal fun JsonElement.toCustomizationOptions(): ImmutableList<CustomizationOption> =
    this.jsonArray.map { it.jsonObject.toCustomizationOption() }.toImmutableList()

private fun JsonObject.toCustomizationOption(): CustomizationOption = CustomizationOption(
    card = this["card"]?.jsonObject?.toCard(),
    choice = CustomizationChoice.byCode(this["choice"]?.jsonPrimitive?.content),
    cost = this["cost"]?.jsonPrimitive?.intOrNull,
    deckLimit = this["deck_limit"]?.jsonPrimitive?.intOrNull,
    health = this["health"]?.jsonPrimitive?.intOrNull,
    position = this["position"]?.jsonPrimitive?.intOrNull,
    quantity = this["quantity"]?.jsonPrimitive?.intOrNull,
    realSlot = this["real_slot"]?.jsonPrimitive?.contentOrNull,
    realText = this["real_text"]?.jsonPrimitive?.contentOrNull,
    realTraits = this["real_traits"]?.jsonPrimitive?.contentOrNull,
    sanity = this["sanity"]?.jsonPrimitive?.intOrNull,
    tags = this["tags"]?.jsonArray?.map { it.jsonPrimitive.content }.orEmpty(),
    textChange = CustomizationTextChange.byCode(this["text_change"]?.jsonPrimitive?.content),
    xp = this["xp"]!!.jsonPrimitive.int
)

private fun JsonObject.toCard(): CustomizationOptionCard {
    val type = this["type"]?.jsonArray?.map { it.jsonPrimitive.content }.orEmpty()
    val trait = this["trait"]?.jsonArray?.map { it.jsonPrimitive.content }.orEmpty()

    return CustomizationOptionCard(type, trait)
}