package com.arkhamcompanion.domain.repository

import androidx.paging.PagingData
import com.arkhamcompanion.domain.model.decks.DeckFilters
import com.arkhamcompanion.domain.model.decks.DeckListItem
import com.arkhamcompanion.domain.utils.EnvironmentClassifier
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.coroutines.flow.Flow

interface DecksRepository {
    suspend fun deleteAllLocalDecks(): Result<Unit>

    suspend fun deleteAllRemoteDecks(): Result<Unit>

    suspend fun syncRemoteDecks(userId: String): Result<Unit>

    fun searchPaginatedDecks(
        query: String,
        environmentClassifier: EnvironmentClassifier,
        sort: String,
        filters: DeckFilters
    ): Flow<PagingData<DeckListItem>>

    fun getAllTags(): Flow<ImmutableSet<String>>
}