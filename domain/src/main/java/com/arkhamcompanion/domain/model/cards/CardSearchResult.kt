package com.arkhamcompanion.domain.model.cards

import kotlinx.collections.immutable.ImmutableList

data class CardSearchResult(
    val errorMessage: String?,
    val cards: ImmutableList<CardSearchResultItem>,
)

data class CardSearchResultItem(
    val id: String,
    val code: String,
    val tabooSetId: Int?
)
