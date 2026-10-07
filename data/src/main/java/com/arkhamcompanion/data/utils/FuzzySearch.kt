package com.arkhamcompanion.data.utils

import com.arkhamcompanion.data.local.cards.CardSearchResultEntity
import com.arkhamcompanion.domain.model.cards.CardSearchOptions
import com.arkhamcompanion.domain.objects.FuzzyMatcher.matchesFuzzy
import com.arkhamcompanion.domain.objects.normalizeForSearch
import com.arkhamcompanion.domain.objects.splitQueryToWords
import java.util.Locale

internal data class PreparedFuzzySearch(
    val words: List<String>,
    val includeEnglish: Boolean
)

internal fun prepareWordsForFuzzySearch(
    searchOptions: CardSearchOptions,
    includeEnglish: Boolean
): PreparedFuzzySearch {
    val words = searchOptions.searchQuery
        .normalizeForSearch()
        .splitQueryToWords()

    val language = Locale.getDefault().toLanguageTag().substringBefore("-")
    val realFields = language != "en" && includeEnglish

    return PreparedFuzzySearch(
        words = words,
        includeEnglish = realFields
    )
}

internal fun CardSearchResultEntity.fuzzySearch(
    searchOptions: CardSearchOptions,
    words: List<String>,
    includeEnglish: Boolean
): Boolean {
    if (words.isEmpty()) return true

    fun matches(
        local: String?,
        english: String?,
        backLocal: String?,
        backLinked: String?,
        backEnglish: String?,
        backEnglishLinked: String?,
    ): Boolean {
        if (matchesFuzzy(local, words)) return true

        if (includeEnglish && matchesFuzzy(english, words)) return true

        if (searchOptions.searchBack) {
            if (matchesFuzzy(backLocal, words)) return true
            if (matchesFuzzy(backLinked, words)) return true

            if (includeEnglish) {
                if (matchesFuzzy(backEnglish, words)) return true

                if (matchesFuzzy(backEnglishLinked, words)) return true
            }
        }

        return false
    }

    //Name search
    if (matches(
            searchFields.searchName,
            searchFields.searchRealName,
            searchFields.searchNameBack,
            searchFieldsBack?.searchName,
            searchFields.searchRealNameBack,
            searchFieldsBack?.searchRealName
        )) return true

    //Game search
    if (searchOptions.searchGame && matches(
            searchFields.searchGame,
            searchFields.searchRealGame,
            searchFields.searchGameBack,
            searchFieldsBack?.searchGame,
            searchFields.searchRealGameBack,
            searchFieldsBack?.searchRealGame
        )) return true

    //Flavor search
    if (searchOptions.searchFlavor && matches(
            searchFields.searchFlavor,
            searchFields.searchRealFlavor,
            searchFields.searchFlavorBack,
            searchFieldsBack?.searchFlavor,
            searchFields.searchRealFlavorBack,
            searchFieldsBack?.searchRealFlavor
        )) return true

    return false
}