package com.arkhamcompanion.data.utils

private val WEIRD_BULLET_REGEX = Regex("""\\u2022""")
private val LINEBREAK_REGEX = Regex("""(\/n|<br\/?>)""")
private val INDENTED_BULLET_REGEX = Regex("""(^\s?--|^-—\s+)([^0-9].+)$""", RegexOption.MULTILINE)
private val BULLET_REGEX = Regex("""(^\s?-|^—\s+)([^0-9].+)$""", RegexOption.MULTILINE)
private val GUIDE_BULLET_REGEX = Regex("""(^\s?=|^=\s+)([^0-9].+)$""", RegexOption.MULTILINE)
private val PARAGRAPH_BULLET_REGEX = Regex("""(<p>- )|(<p>–)""", RegexOption.MULTILINE)
private val DOUBLE_BRACKET_REGEX = Regex("""\[\[([^]]+)]]""")
private val TRIPLE_STAR_REGEX = Regex("""\*\*\*([^*]+)\*\*\*""")
private val DOUBLE_STAR_REGEX = Regex("""\*\*([^*]+)\*\*""")
private val SINGLE_STAR_REGEX = Regex("""\*([^*]+)\*""")

internal fun String.preprocessCardText(
    processBullets: Boolean = false,
    processWebFormat: Boolean = false
): String {
    var result = this
        .replace(WEIRD_BULLET_REGEX, "•")
        .replace(LINEBREAK_REGEX, "\n")
        .replace(DOUBLE_BRACKET_REGEX) { match ->
            "<trait>${match.groupValues[1]}</trait>"
        }

    if (processBullets) {
        result = result
            .replace(INDENTED_BULLET_REGEX) { match ->
                "\t[bullet] ${match.groupValues[2]}"
            }
            .replace(BULLET_REGEX) { match ->
                "[bullet] ${match.groupValues[2]}"
            }
            .replace(GUIDE_BULLET_REGEX) { match ->
                "[guide_bullet] ${match.groupValues[2]}"
            }
            .replace(PARAGRAPH_BULLET_REGEX, "<p>[bullet]")
    }

    if (processWebFormat) {
        result = result
            .replace(TRIPLE_STAR_REGEX) { match ->
                "<trait>${match.groupValues[1]}</trait>"
            }
            .replace(DOUBLE_STAR_REGEX) { match ->
                "<b>${match.groupValues[1]}</b>"
            }
            .replace(SINGLE_STAR_REGEX) { match ->
                "<i>${match.groupValues[1]}</i>"
            }
    }

    return result
}