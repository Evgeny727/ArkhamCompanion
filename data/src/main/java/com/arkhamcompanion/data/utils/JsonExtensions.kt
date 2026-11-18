package com.arkhamcompanion.data.utils

import com.arkhamcompanion.domain.model.decks.DeckSlots
import com.arkhamcompanion.domain.model.decks.Selection
import com.arkhamcompanion.domain.model.decks.Selections
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.put

fun JsonObjectBuilder.putAlternateFront(code: String) = put("alternate_front", code)

fun JsonObjectBuilder.putAlternateBack(code: String) = put("alternate_back", code)

fun JsonObjectBuilder.putCardPool(pool: String) = put("card_pool", pool)

fun JsonObjectBuilder.putSelections(selections: Selections) = selections.forEach { (key, selection) ->
    put(key, when (selection) {
        is Selection.DeckSizeSelection -> selection.value.toString()
        is Selection.FactionSelection -> selection.value
        is Selection.OptionSelection -> selection.value.id
    })
}

fun JsonObjectBuilder.putExtraDeck(extraDeck: List<String>) =
    put("extra_deck", extraDeck.joinToString(","))

fun JsonObjectBuilder.putTransformInto(code: String) = put("transform_into", code)

fun JsonObjectBuilder.putSlots(slots: DeckSlots) = slots.forEach { put(it.key, it.value) }