package com.arkhamcompanion.domain.model.cards

import kotlinx.collections.immutable.ImmutableList

data class CardText(
    val text: String,
    val paragraphs: ImmutableList<CardTextParagraph>,
)

data class CardTextParagraph(
    val start: Int,
    val end: Int,
    val segments: ImmutableList<CardTextSegment>,
    val alignment: ParagraphAlignment = ParagraphAlignment.Start,
    val blockQuote: Boolean = false,
    val horizontalRule: Boolean = false
)

sealed interface CardTextSegment {

    data class Text(
        val start: Int,
        val end: Int,
        val styleFlags: CardTextStyleFlags = CardTextStyleFlags(0),
        val link: String? = null,
    ) : CardTextSegment

    data class Icon(
        val start: Int,
        val end: Int,
        val glyph: String,
    ) : CardTextSegment
}

enum class ParagraphAlignment {
    Start,
    Center,
    End
}

@JvmInline
value class CardTextStyleFlags(
    val value: Int
) {
    infix fun has(flag: Int): Boolean =
        value and flag != 0

    operator fun plus(flag: Int): CardTextStyleFlags =
        CardTextStyleFlags(value or flag)

    companion object {
        const val REGULAR = 0
        const val BOLD = 1
        const val ITALIC = 1 shl 1
        const val UNDERLINE = 1 shl 2
        const val STRIKE = 1 shl 3
        const val GAME = 1 shl 4
        const val CITE = 1 shl 5
        const val RED = 1 shl 6
    }
}