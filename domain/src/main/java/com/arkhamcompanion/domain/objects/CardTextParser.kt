package com.arkhamcompanion.domain.objects

import com.arkhamcompanion.domain.model.cards.CardText
import com.arkhamcompanion.domain.model.cards.CardTextStyleFlags
import com.arkhamcompanion.domain.model.cards.IconRegistry
import com.arkhamcompanion.domain.model.cards.ParagraphAlignment

object CardTextParser {

    fun parse(
        text: String,
    ): CardText {

        val paragraphs = ParagraphBuilder()

        parseSegment(
            text = text,
            index = 0,
            endTag = null,
            styleFlags = CardTextStyleFlags(0),
            paragraphs = paragraphs
        )

        paragraphs.finishParagraph(text.length)

        return CardText(
            text = text,
            paragraphs = paragraphs.build(),
        )
    }

    private fun parseSegment(
        text: String,
        index: Int,
        endTag: String?,
        styleFlags: CardTextStyleFlags,
        paragraphs: ParagraphBuilder,
    ): Int {
        var current = index

        while (current < text.length) {

            if (endTag != null && text.startsWith(endTag, current)) {
                return current + endTag.length
            }

            current = when (text[current]) {

                '<' -> parseTag(
                    text = text,
                    index = current,
                    styleFlags = styleFlags,
                    paragraphs = paragraphs,
                )

                '[' -> parseBracket(
                    text = text,
                    index = current,
                    styleFlags = styleFlags,
                    paragraphs = paragraphs,
                )

                else -> parsePlainText(
                    text = text,
                    index = current,
                    styleFlags = styleFlags,
                    paragraphs = paragraphs,
                )
            }
        }

        return current
    }

    private fun parsePlainText(
        text: String,
        index: Int,
        styleFlags: CardTextStyleFlags,
        paragraphs: ParagraphBuilder,
    ): Int {
        var end = index

        while (
            end < text.length &&
            text[end] != '<' &&
            text[end] != '['
        ) {
            end++
        }

        paragraphs.appendText(
            start = index,
            end = end,
            styleFlags = styleFlags,
        )

        return end
    }

    private fun parseTag(
        text: String,
        index: Int,
        styleFlags: CardTextStyleFlags,
        paragraphs: ParagraphBuilder,
    ): Int {
        val end = text.indexOf('>', index)

        if (end == -1) {
            paragraphs.appendText(
                start = index,
                end = index + 1,
                styleFlags = styleFlags,
            )

            return index + 1
        }

        val tag = text.substring(
            index + 1,
            end,
        )

        if (tag.startsWith('/')) {
            // Closing tags are normally consumed by parseSegment.
            // If we get here, this is an unmatched closing tag.
            paragraphs.appendText(
                start = index,
                end = end + 1,
                styleFlags = styleFlags,
            )

            return end + 1
        }

        return when (tag) {

            "p" -> {
                paragraphs.finishParagraph(end)

                end + 1
            }

            "hr", "hr/" -> {
                paragraphs.horizontalRule(end)

                end + 1
            }

            "center" -> parseParagraphTag(
                text = text,
                start = end + 1,
                closingTag = "</center>",
                alignment = ParagraphAlignment.Center,
                styleFlags = styleFlags,
                paragraphs = paragraphs,
            )

            "right" -> parseParagraphTag(
                text = text,
                start = end + 1,
                closingTag = "</right>",
                alignment = ParagraphAlignment.End,
                styleFlags = styleFlags,
                paragraphs = paragraphs,
            )

            "blockquote" -> parseBlockQuote(
                text = text,
                start = end + 1,
                styleFlags = styleFlags,
                paragraphs = paragraphs,
            )

            "b", "strong" -> parseStyledTag(
                text = text,
                start = end + 1,
                closingTag = "</$tag>",
                styleFlags = styleFlags + CardTextStyleFlags.BOLD,
                paragraphs = paragraphs,
            )

            "i", "em" -> parseStyledTag(
                text = text,
                start = end + 1,
                closingTag = "</$tag>",
                styleFlags = styleFlags + CardTextStyleFlags.ITALIC,
                paragraphs = paragraphs,
            )

            "u" -> parseStyledTag(
                text = text,
                start = end + 1,
                closingTag = "</u>",
                styleFlags = styleFlags + CardTextStyleFlags.UNDERLINE,
                paragraphs = paragraphs,
            )

            "strike", "del" -> parseStyledTag(
                text = text,
                start = end + 1,
                closingTag = "</$tag>",
                styleFlags = styleFlags + CardTextStyleFlags.STRIKE,
                paragraphs = paragraphs,
            )

            "cite" -> parseStyledTag(
                text = text,
                start = end + 1,
                closingTag = "</cite>",
                styleFlags = styleFlags + CardTextStyleFlags.CITE,
                paragraphs = paragraphs,
            )

            "trait" -> parseStyledTag(
                text = text,
                start = end + 1,
                closingTag = "</trait>",
                styleFlags = styleFlags +
                        CardTextStyleFlags.BOLD +
                        CardTextStyleFlags.ITALIC,
                paragraphs = paragraphs,
            )

            "red" -> parseStyledTag(
                text = text,
                start = end + 1,
                closingTag = "</red>",
                styleFlags = styleFlags + CardTextStyleFlags.RED,
                paragraphs = paragraphs,
            )

            else -> {
                // Unknown tag → literal text.
                paragraphs.appendText(
                    start = index,
                    end = index + 1,
                    styleFlags = styleFlags,
                )

                index + 1
            }
        }
    }

    private fun parseParagraphTag(
        text: String,
        start: Int,
        closingTag: String,
        alignment: ParagraphAlignment,
        styleFlags: CardTextStyleFlags,
        paragraphs: ParagraphBuilder,
    ): Int {
        paragraphs.finishParagraph(start)

        val previousAlignment = paragraphs.alignment

        paragraphs.alignment = alignment

        val next = parseSegment(
            text = text,
            index = start,
            endTag = closingTag,
            styleFlags = styleFlags,
            paragraphs = paragraphs,
        )

        paragraphs.finishParagraph(next)

        paragraphs.alignment = previousAlignment

        return next
    }

    private fun parseBlockQuote(
        text: String,
        start: Int,
        styleFlags: CardTextStyleFlags,
        paragraphs: ParagraphBuilder,
    ): Int {
        paragraphs.finishParagraph(start)

        val previousBlockQuote = paragraphs.blockQuote

        paragraphs.blockQuote = true

        val next = parseSegment(
            text = text,
            index = start,
            endTag = "</blockquote>",
            styleFlags = styleFlags,
            paragraphs = paragraphs,
        )

        paragraphs.finishParagraph(next)

        paragraphs.blockQuote = previousBlockQuote

        return next
    }

    private fun parseStyledTag(
        text: String,
        start: Int,
        closingTag: String,
        styleFlags: CardTextStyleFlags,
        paragraphs: ParagraphBuilder,
    ): Int {
        return parseSegment(
            text = text,
            index = start,
            endTag = closingTag,
            styleFlags = styleFlags,
            paragraphs = paragraphs,
        )
    }

    private fun parseBracket(
        text: String,
        index: Int,
        styleFlags: CardTextStyleFlags,
        paragraphs: ParagraphBuilder,
    ): Int {
        val end = text.indexOf(']', index)

        if (end == -1) {
            paragraphs.appendText(
                start = index,
                end = index + 1,
                styleFlags = styleFlags,
            )

            return index + 1
        }

        val key = text.substring(
            index + 1,
            end,
        )

        val glyph = IconRegistry.glyph(key)

        if (glyph != null) {
            paragraphs.appendIcon(
                start = index,
                end = end + 1,
                glyph = glyph,
            )

            return end + 1
        } else {
            paragraphs.appendText(
                start = index,
                end = index + 1,
                styleFlags = styleFlags,
            )

            return index + 1
        }
    }
}