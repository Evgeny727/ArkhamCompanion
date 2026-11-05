package com.arkhamcompanion.domain.model.decks

import com.arkhamcompanion.domain.enums.DeckEnvironment
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class ResolvedEnvironment(
    val type: DeckEnvironment,
    val expansionCodes: ImmutableList<String> = persistentListOf(),
)