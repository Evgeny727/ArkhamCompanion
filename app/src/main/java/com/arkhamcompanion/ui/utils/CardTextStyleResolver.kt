package com.arkhamcompanion.ui.utils

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.SpanStyle
import com.arkhamcompanion.domain.model.cards.CardTextStyleFlags
import com.arkhamcompanion.ui.cards.components.details.CardTextStyles

@Stable
class CardTextStyleResolver(
    private val styles: CardTextStyles,
) {
    private val cache = arrayOfNulls<SpanStyle>(256)

    fun resolve(flags: CardTextStyleFlags): SpanStyle {
        val key = flags.value

        cache[key]?.let { return it }

        return resolveInternal(flags).also {
            cache[key] = it
        }
    }

    private fun resolveInternal(
        flags: CardTextStyleFlags,
    ): SpanStyle {
        var result = styles.regular

        if (flags has CardTextStyleFlags.BOLD && flags has CardTextStyleFlags.ITALIC) {
            result = result.merge(styles.boldItalic)
        } else {
            if (flags has CardTextStyleFlags.BOLD) {
                result = result.merge(styles.bold)
            }

            if (flags has CardTextStyleFlags.ITALIC) {
                result = result.merge(styles.italic)
            }
        }

        if (flags has CardTextStyleFlags.UNDERLINE) {
            result = result.merge(styles.underline)
        }

        if (flags has CardTextStyleFlags.STRIKE) {
            result = result.merge(styles.strike)
        }

        if (flags has CardTextStyleFlags.GAME) {
            result = result.merge(styles.game)
        }

        if (flags has CardTextStyleFlags.CITE) {
            result = result.merge(styles.cite)
        }

        if (flags has CardTextStyleFlags.RED) {
            result = result.merge(styles.red)
        }

        if (flags has CardTextStyleFlags.BLUE) {
            result = result.merge(styles.blue)
        }

        return result
    }
}