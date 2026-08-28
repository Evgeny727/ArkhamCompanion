package com.arkhamcompanion.domain.model.cards

data class CardSearchResult(
    val id: String,
    val code: String,
    val tabooSetId: Int?
)
