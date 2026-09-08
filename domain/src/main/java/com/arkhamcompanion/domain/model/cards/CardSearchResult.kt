package com.arkhamcompanion.domain.model.cards

import com.arkhamcompanion.domain.arkhamql.QueryError
import kotlinx.collections.immutable.ImmutableList

data class CardSearchResult(
    val error: QueryError?,
    val cards: ImmutableList<CardSearchResultItem>,
)

data class CardSearchResultItem(
    val id: String,
    val code: String,
    val tabooSetId: Int?
)
