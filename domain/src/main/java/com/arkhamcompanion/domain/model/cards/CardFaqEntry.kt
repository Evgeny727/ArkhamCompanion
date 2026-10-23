package com.arkhamcompanion.domain.model.cards

data class CardFaqEntry(
    val id: Int,
    val question: CardText,
    val ruling: CardText,
    val citation: String,
    val position: Int,
    val date: String?
)