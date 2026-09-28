package com.arkhamcompanion.domain.model.cards

import kotlinx.collections.immutable.ImmutableList

data class DeckRequirements(
    val card: ImmutableList<ImmutableList<String>>,
    val size: Int,
    val random: ImmutableList<RandomCard>,
)

data class RandomCard(
    val value: String,
    val target: Any,
)