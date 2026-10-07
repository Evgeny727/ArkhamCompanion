package com.arkhamcompanion.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.arkhamcompanion.domain.model.cards.CardSearchPreferences
import com.arkhamcompanion.domain.model.settings.Collection
import com.arkhamcompanion.domain.repository.AnalyticsRepository
import com.arkhamcompanion.domain.repository.DEFAULT_MYTHOS_SORT_ORDER
import com.arkhamcompanion.domain.repository.DEFAULT_PLAYER_SORT_ORDER
import com.arkhamcompanion.domain.repository.UserPreferencesRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val analyticsRepository: AnalyticsRepository
) : UserPreferencesRepository {
    override val isDarkTheme: Flow<Int> = dataStore.data
        .catch {
            if (it is IOException) {
                analyticsRepository.logMessage("Error reading theme preferences.")
                analyticsRepository.logError(it)

                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[THEME] ?: 2
        }
    override val scaleFactor: Flow<Float> = dataStore.data
        .catch {
            if (it is IOException) {
                analyticsRepository.logMessage("Error reading scale factor preferences.")
                analyticsRepository.logError(it)

                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[SCALE_FACTOR] ?: 1f
        }
    override val showFanmadeCards: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                analyticsRepository.logMessage("Error reading fanmade cards preferences.")
                analyticsRepository.logError(it)

                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[FANMADE_CARDS] ?: false
        }
    override val showPreviewCards: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                analyticsRepository.logMessage("Error reading preview cards preferences.")
                analyticsRepository.logError(it)

                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[PREVIEW_CARDS] ?: false
        }
    override val isIncludeEnglishSearchResults: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                analyticsRepository.logMessage("Error reading english search preferences.")
                analyticsRepository.logError(it)

                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[INCLUDE_ENGLISH_SEARCH_RESULTS] ?: false
        }
    override val tabooSetId: Flow<Int> = dataStore.data
        .catch {
            if (it is IOException) {
                analyticsRepository.logMessage("Error reading taboo preferences.")
                analyticsRepository.logError(it)

                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[TABOO] ?: 0
        }
    override val collection: Flow<Collection> = dataStore.data
        .catch {
            if (it is IOException) {
                analyticsRepository.logMessage("Error reading collection preferences.")
                analyticsRepository.logError(it)

                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[COLLECTION]?.let {
                json.decodeFromString<Collection>(it)
            } ?: Collection(persistentSetOf(), persistentSetOf())
        }
    override val ignoreCollection: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                analyticsRepository.logMessage("Error reading ignore collection preferences.")
                analyticsRepository.logError(it)

                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[IGNORE_COLLECTION] ?: true
        }
    override val cardsUpdatedAt: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                analyticsRepository.logMessage("Error reading cards updated timestamp preferences.")
                analyticsRepository.logError(it)

                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[CARDS_UPDATED_AT] ?: ""
        }
    override val sortOrderPlayer: Flow<ImmutableList<String>> = dataStore.data
        .catch {
            if (it is IOException) {
                analyticsRepository.logMessage("Error reading player sort order preferences.")
                analyticsRepository.logError(it)

                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[CARDS_SORT_ORDER_PLAYER]?.split(",")?.filter { it.isNotBlank() }
                ?.toImmutableList() ?: DEFAULT_PLAYER_SORT_ORDER
        }
    override val sortOrderMythos: Flow<ImmutableList<String>> = dataStore.data
        .catch {
            if (it is IOException) {
                analyticsRepository.logMessage("Error reading mythos sort order preferences.")
                analyticsRepository.logError(it)

                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[CARDS_SORT_ORDER_MYTHOS]?.split(",")?.filter { it.isNotBlank() }
                ?.toImmutableList() ?: DEFAULT_MYTHOS_SORT_ORDER
        }
    override val cardSearchPreferences: Flow<CardSearchPreferences> = dataStore.data
        .catch {
            if (it is IOException) {
                analyticsRepository.logMessage("Error reading cards search preferences.")
                analyticsRepository.logError(it)

                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            CardSearchPreferences(
                includeEnglish = preferences[INCLUDE_ENGLISH_SEARCH_RESULTS] ?: false,
                showFanMade = preferences[FANMADE_CARDS] ?: false,
                showPreview = preferences[PREVIEW_CARDS] ?: false,
                tabooSetId = preferences[TABOO] ?: 0,
                playerSortOrder = preferences[CARDS_SORT_ORDER_PLAYER]?.split(",")
                    ?.filter { it.isNotBlank() } ?: DEFAULT_PLAYER_SORT_ORDER,
                mythosSortOrder = preferences[CARDS_SORT_ORDER_MYTHOS]?.split(",")
                    ?.filter { it.isNotBlank() } ?: DEFAULT_MYTHOS_SORT_ORDER,
                ignoreCollection = preferences[IGNORE_COLLECTION] ?: true,
                collection = preferences[COLLECTION]?.let { json.decodeFromString<Collection>(it) }
                    ?: Collection( persistentSetOf(), persistentSetOf())
            )
        }

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private companion object {
        val THEME = intPreferencesKey("theme")
        val SCALE_FACTOR = floatPreferencesKey("scale_factor")
        val FANMADE_CARDS = booleanPreferencesKey("fanmade_cards")
        val PREVIEW_CARDS = booleanPreferencesKey("preview_cards")
        val INCLUDE_ENGLISH_SEARCH_RESULTS = booleanPreferencesKey("english_results")
        val TABOO = intPreferencesKey("taboo")
        val COLLECTION = stringPreferencesKey("collection")
        val IGNORE_COLLECTION = booleanPreferencesKey("ignore_collection")
        val CARDS_UPDATED_AT = stringPreferencesKey("cards_updated_at")
        val CARDS_SORT_ORDER_PLAYER = stringPreferencesKey("cards_sort_order_player")
        val CARDS_SORT_ORDER_MYTHOS = stringPreferencesKey("cards_sort_order_mythos")
    }

    override suspend fun saveThemePreference(theme: Int) {
        dataStore.edit { preferences ->
            preferences[THEME] = theme
        }
    }

    override suspend fun saveScaleFactorPreference(scaleFactor: Float) {
        dataStore.edit { preferences ->
            preferences[SCALE_FACTOR] = scaleFactor
        }
    }

    override suspend fun saveShowFanmadeCards(showFanmadeCards: Boolean) {
        dataStore.edit { preferences ->
            preferences[FANMADE_CARDS] = showFanmadeCards
        }
    }

    override suspend fun saveShowPreviewCards(showPreviewCards: Boolean) {
        dataStore.edit { preferences ->
            preferences[PREVIEW_CARDS] = showPreviewCards
        }
    }

    override suspend fun saveIncludeEnglishSearchResults(isIncludeEnglishSearchResults: Boolean) {
        dataStore.edit { preferences ->
            preferences[INCLUDE_ENGLISH_SEARCH_RESULTS] = isIncludeEnglishSearchResults
        }
    }

    override suspend fun saveTabooSetPreference(tabooSetId: Int) {
        dataStore.edit { preferences ->
            preferences[TABOO] = tabooSetId
        }
    }

    override suspend fun saveCollectionPreference(collection: Collection) {
        dataStore.edit { preferences ->
            preferences[COLLECTION] = json.encodeToString(collection)
        }
    }

    override suspend fun saveIgnoreCollectionPreference(ignoreCollection: Boolean) {
        dataStore.edit { preferences ->
            preferences[IGNORE_COLLECTION] = ignoreCollection
        }
    }

    override suspend fun saveCardsUpdatedTimestamp(timestamp: String) {
        dataStore.edit { preferences ->
            preferences[CARDS_UPDATED_AT] = timestamp
        }
    }

    override suspend fun saveSortOrderPlayerPreference(sortOrder: List<String>) {
        dataStore.edit { preferences ->
            preferences[CARDS_SORT_ORDER_PLAYER] = sortOrder.ifEmpty { DEFAULT_PLAYER_SORT_ORDER }.joinToString(",")
        }
    }

    override suspend fun saveSortOrderMythosPreference(sortOrder: List<String>) {
        dataStore.edit { preferences ->
            preferences[CARDS_SORT_ORDER_MYTHOS] = sortOrder.ifEmpty { DEFAULT_MYTHOS_SORT_ORDER }.joinToString(",")
        }
    }
}