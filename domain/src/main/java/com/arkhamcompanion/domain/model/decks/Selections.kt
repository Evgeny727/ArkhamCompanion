package com.arkhamcompanion.domain.model.decks

import com.arkhamcompanion.domain.model.cards.OptionSelect

sealed interface Selection {
    val type: String
    val name: String

    data class DeckSizeSelection(
        override val type: String = "deckSize",
        val value: Int,
        val options: List<String>,
        override val name: String,
    ) : Selection

    data class FactionSelection(
        override val type: String = "faction",
        val value: String?,
        val options: List<String>,
        override val name: String,
    ) : Selection

    data class OptionSelection(
        override val type: String = "option",
        val value: OptionSelect?,
        val options: List<OptionSelect>,
        override val name: String,
    ) : Selection
}

// selections, keyed by their `id`, or if not present their `name`.
typealias Selections = Map<String, Selection>