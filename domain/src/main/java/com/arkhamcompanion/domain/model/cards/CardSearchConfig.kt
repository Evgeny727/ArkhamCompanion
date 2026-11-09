package com.arkhamcompanion.domain.model.cards

import com.arkhamcompanion.domain.enums.CardsSearchTarget

data class CardSearchConfig(
    val spoiler: Boolean,
    val options: CardSearchOptions,
    val preferences: CardSearchPreferences,
    val filters: CardFilters,
    val searchTarget: CardsSearchTarget
)
