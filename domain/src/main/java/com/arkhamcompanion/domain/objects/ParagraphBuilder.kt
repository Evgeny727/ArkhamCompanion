package com.arkhamcompanion.domain.objects

import com.arkhamcompanion.domain.model.cards.CardTextParagraph
import com.arkhamcompanion.domain.model.cards.CardTextSegment
import com.arkhamcompanion.domain.model.cards.CardTextStyleFlags
import com.arkhamcompanion.domain.model.cards.ParagraphAlignment
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

internal class ParagraphBuilder {

    private val paragraphs = mutableListOf<CardTextParagraph>()
    private val segments = mutableListOf<CardTextSegment>()

    private var paragraphStart: Int? = null

    var alignment: ParagraphAlignment = ParagraphAlignment.Start
    var blockQuote: Boolean = false
    var horizontalRule: Boolean = false

    /**
     * Adds a text range to the current paragraph.
     *
     * Adjacent text ranges with the same style are merged.
     */
    fun appendText(
        start: Int,
        end: Int,
        link: String? = null,
        styleFlags: CardTextStyleFlags,
    ) {
        if (start >= end) return

        if (paragraphStart == null) {
            paragraphStart = start
        }

        val previous = segments.lastOrNull()

        if (
            previous is CardTextSegment.Text &&
            previous.end == start &&
            previous.styleFlags == styleFlags &&
            previous.link == link
        ) {
            segments[segments.lastIndex] = previous.copy(
                end = end,
            )
        } else {
            segments += CardTextSegment.Text(
                start = start,
                end = end,
                link = link,
                styleFlags = styleFlags,
            )
        }
    }

    /**
     * Adds an icon to the current paragraph.
     */
    fun appendIcon(
        start: Int,
        end: Int,
        glyph: String,
    ) {
        if (paragraphStart == null) {
            paragraphStart = start
        }

        segments += CardTextSegment.Icon(
            start = start,
            end = end,
            glyph = glyph,
        )
    }

    /**
     * Finishes the current paragraph.
     *
     * [end] is the current position in the processed source text.
     */
    fun finishParagraph(end: Int) {
        if (segments.isEmpty()) return

        paragraphs += CardTextParagraph(
            start = paragraphStart ?: end,
            end = end,
            segments = segments.toImmutableList(),
            alignment = alignment,
            blockQuote = blockQuote,
            horizontalRule = horizontalRule
        )

        segments.clear()
        paragraphStart = null
    }

    /**
     * Adds a horizontal rule to the current paragraph and finishes it.
     */
    fun horizontalRule(end: Int) {
        horizontalRule = true

        finishParagraph(end)

        horizontalRule = false
    }

    fun build(): ImmutableList<CardTextParagraph> {
        return paragraphs.toImmutableList()
    }
}