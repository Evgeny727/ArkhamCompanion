package com.arkhamcompanion.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map
import androidx.room3.withWriteTransaction
import com.arkhamcompanion.data.local.ArkhamDatabase
import com.arkhamcompanion.data.local.LoggingPagingSource
import com.arkhamcompanion.data.mapper.db.toEntity
import com.arkhamcompanion.data.mapper.domain.decks.toDomain
import com.arkhamcompanion.data.remote.datasources.DecksRemoteDataSource
import com.arkhamcompanion.data.remote.dto.DeckBatchRequestDto
import com.arkhamcompanion.data.remote.dto.toRequestDto
import com.arkhamcompanion.data.remote.oauth.AccessTokenProvider
import com.arkhamcompanion.domain.model.decks.DeckListItem
import com.arkhamcompanion.domain.objects.FuzzyMatcher.matchesFuzzy
import com.arkhamcompanion.domain.objects.TimestampNormalizer.compareTimestamps
import com.arkhamcompanion.domain.objects.normalizeForSearch
import com.arkhamcompanion.domain.objects.splitToWords
import com.arkhamcompanion.domain.repository.AnalyticsRepository
import com.arkhamcompanion.domain.repository.DecksRepository
import com.arkhamcompanion.domain.repository.PerformanceRepository
import com.arkhamcompanion.domain.utils.EnvironmentClassifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DecksRepositoryImpl @Inject constructor(
    private val decksRemoteDataSource: DecksRemoteDataSource,
    private val accessTokenProvider: AccessTokenProvider,
    private val db: ArkhamDatabase,
    private val performanceRepository: PerformanceRepository,
    private val analyticsRepository: AnalyticsRepository,
) : DecksRepository {

    private val deckBatchSize = 250

    val decksDao = db.decksDao()

    override suspend fun deleteAllLocalDecks() = runCatching {
        decksDao.deleteAllLocalDecks()
    }

    override suspend fun deleteAllRemoteDecks() = runCatching {
        decksDao.deleteAllRemoteInfo()
    }

    override suspend fun syncRemoteDecks(userId: String): Result<Unit> = runCatching {
        val accessToken = accessTokenProvider.getValidAccessToken() ?: return@runCatching

        val remoteManifest = decksRemoteDataSource.fetchDecksManifest(accessToken)
        val localManifest = decksDao.getDecksManifest(userId)

        // No changes in the remote manifest.
        if (localManifest != null && localManifest.version == remoteManifest.version) {
            return@runCatching
        }

        val remoteDecksById = remoteManifest.decks.associateBy { it.id.toString() }

        val localDecks = decksDao.getAllRemoteDeckIdsWithTimestamps()
        val localDecksById = localDecks.associateBy { it.id }

        // Fetch missing and outdated decks.
        val decksToFetch = remoteManifest.decks.filter { remoteDeck ->
            val localDeck = localDecksById[remoteDeck.id.toString()]

            localDeck == null ||
                    compareTimestamps(localDeck.dateUpdate, remoteDeck.updatedAt)
        }

        // Fetch all batches before modifying the database.
        val fetchedDecks = performanceRepository.trace("loadDeckBatches") {
            decksToFetch
                .map { it.toRequestDto() }
                .chunked(deckBatchSize)
                .flatMap { batch ->
                    decksRemoteDataSource.fetchDecksBatch(
                        accessToken = accessToken,
                        request = DeckBatchRequestDto(
                            arkhamdbSyncToken = remoteManifest.arkhamdbSyncToken,
                            decks = batch
                        )
                    )
                }
        }

        //Mark Arkhamdb decks for deletion if user changed or remote sync token is existing.
        val clearArkhamdbDecks = localManifest == null ||
                remoteManifest.arkhamdbSyncToken != null

        // Apply the entire synchronization atomically.
        db.withWriteTransaction {
            decksDao.syncRemoteDecks(remoteDecksById.keys, clearArkhamdbDecks)
            decksDao.upsertRemoteDecks(fetchedDecks.toEntity())
            decksDao.replaceDecksManifest(remoteManifest.toEntity(userId))
        }
    }

    override fun searchPaginatedDecks(
        query: String,
        environmentClassifier: EnvironmentClassifier
    ): Flow<PagingData<DeckListItem>> {
        val queryWords = query.normalizeForSearch().splitToWords()

        return Pager(
            config = PagingConfig(
                pageSize = 15,
                prefetchDistance = 30,
                enablePlaceholders = true,
                initialLoadSize = 50,
            ),
            pagingSourceFactory = {
                LoggingPagingSource(
                    delegate = decksDao.searchPaginatedDecks(),
                    analyticsRepository = analyticsRepository
                )
            }
        ).flow.map { data ->
            if (query.isBlank()) return@map data.map { it.toDomain(environmentClassifier) }

            data.filter { item ->
                matchesFuzzy(item.name.normalizeForSearch(), queryWords) ||
                        matchesFuzzy(item.investigatorName.orEmpty().normalizeForSearch(), queryWords)
            }.map { it.toDomain(environmentClassifier) }
        }
    }

}