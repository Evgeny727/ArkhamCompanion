package com.arkhamcompanion.domain.objects

import java.text.Normalizer

private val SEARCH_REGEX = Regex("""["“”‹›«»〞〝〟„＂❝❞‘’❛❜‛",‚❮❯\(\)\-\.…¡!?¿]""")
private val COMBINING_MARKS = "\\p{Mn}+".toRegex()
val WHITESPACE_REGEX = "\\s+".toRegex()

fun String.normalizeForSearch(): String {
    if (isBlank()) return ""
    return Normalizer.normalize(this, Normalizer.Form.NFKD)
        .replace(COMBINING_MARKS, "")
        .replace(SEARCH_REGEX, "")
        .lowercase()
}

fun String.splitToWords(): List<String> {
    return this.split(WHITESPACE_REGEX)
        .filter(String::isNotBlank)
}