package com.arkhamcompanion.domain.repository

import com.arkhamcompanion.domain.model.cards.CardSearchPreferences
import com.arkhamcompanion.domain.model.settings.Collection
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val isDarkTheme: Flow<Int>
    val scaleFactor: Flow<Float>
    val showFanmadeCards: Flow<Boolean>
    val showPreviewCards: Flow<Boolean>
    val isIncludeEnglishSearchResults: Flow<Boolean>
    val tabooSetId: Flow<Int>
    val collection: Flow<Collection>
    val ignoreCollection: Flow<Boolean>
    val cardsUpdatedAt: Flow<String>
    val sortOrderPlayer: Flow<ImmutableList<String>>
    val sortOrderMythos: Flow<ImmutableList<String>>
    val cardSearchPreferences: Flow<CardSearchPreferences>

    suspend fun saveThemePreference(theme: Int)
    suspend fun saveScaleFactorPreference(scaleFactor: Float)
    suspend fun saveShowFanmadeCards(showFanmadeCards: Boolean)
    suspend fun saveShowPreviewCards(showPreviewCards: Boolean)
    suspend fun saveIncludeEnglishSearchResults(isIncludeEnglishSearchResults: Boolean)
    suspend fun saveTabooSetPreference(tabooSetId: Int)
    suspend fun saveCollectionPreference(collection: Collection)
    suspend fun saveIgnoreCollectionPreference(ignoreCollection: Boolean)
    suspend fun saveCardsUpdatedTimestamp(timestamp: String)
    suspend fun saveSortOrderPlayerPreference(sortOrder: List<String>)
    suspend fun saveSortOrderMythosPreference(sortOrder: List<String>)
}

val DEFAULT_PLAYER_SORT_ORDER = persistentListOf("type", "slot")
val DEFAULT_MYTHOS_SORT_ORDER = persistentListOf("pack", "position")