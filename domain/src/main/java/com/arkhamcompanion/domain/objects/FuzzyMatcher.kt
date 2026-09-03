package com.arkhamcompanion.domain.objects

object FuzzyMatcher {

    fun matchesFuzzy(
        text: String?,
        queryWords: List<String>,
    ): Boolean {
        if (text == null) return false
        if (queryWords.isEmpty()) return true
        if (queryWords.size == 1) return text.contains(queryWords[0])

        var queryIndex = 0

        var wordStart = 0
        var i = 0

        while (i <= text.length) {
            val isEnd = i == text.length
            val isSeparator = !isEnd && (text[i] == ' ' || text[i] == '\n')

            if (isEnd || isSeparator) {
                if (wordStart < i) {
                    val wordEnd = i
                    val queryWord = queryWords[queryIndex]

                    if (matchesWord(text, wordStart, wordEnd, queryWord)) {
                        queryIndex++

                        if (queryIndex == queryWords.size) {
                            return true
                        }
                    }
                }

                // Paragraph boundary.
                if (!isEnd && text[i] == '\n') {
                    queryIndex = 0
                }

                wordStart = i + 1
            }

            i++
        }

        return false
    }

    private fun matchesWord(
        text: String,
        start: Int,
        end: Int,
        queryWord: String,
    ): Boolean {
        val wordLength = end - start

        // Equivalent to LIKE '%word%', substring matching inside a word.
        if (wordLength < queryWord.length) return false

        for (offset in 0..wordLength - queryWord.length) {
            var matched = true

            for (j in queryWord.indices) {
                if (text[start + offset + j] != queryWord[j]) {
                    matched = false
                    break
                }
            }

            if (matched) return true
        }

        return false
    }

}