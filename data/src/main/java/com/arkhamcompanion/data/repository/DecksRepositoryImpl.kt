package com.arkhamcompanion.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map
import androidx.room3.withWriteTransaction
import com.arkhamcompanion.data.local.ArkhamDatabase
import com.arkhamcompanion.data.local.LoggingPagingSource
import com.arkhamcompanion.data.local.decks.DeckEntity
import com.arkhamcompanion.data.local.decks.starterDecks
import com.arkhamcompanion.data.mapper.db.toEntity
import com.arkhamcompanion.data.mapper.domain.decks.toDomain
import com.arkhamcompanion.data.remote.datasources.DecksRemoteDataSource
import com.arkhamcompanion.data.remote.dto.DeckBatchRequestDto
import com.arkhamcompanion.data.remote.dto.DeckWriteRequestDto
import com.arkhamcompanion.data.remote.dto.toRequestDto
import com.arkhamcompanion.data.remote.oauth.AccessTokenProvider
import com.arkhamcompanion.data.utils.buildDecksListItemsQuery
import com.arkhamcompanion.data.utils.putAlternateBack
import com.arkhamcompanion.data.utils.putAlternateFront
import com.arkhamcompanion.data.utils.putCardPool
import com.arkhamcompanion.data.utils.putExtraDeck
import com.arkhamcompanion.data.utils.putSelections
import com.arkhamcompanion.data.utils.putSlots
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.domain.model.decks.DeckFilters
import com.arkhamcompanion.domain.model.decks.DeckListItem
import com.arkhamcompanion.domain.model.decks.DeckSlots
import com.arkhamcompanion.domain.model.decks.Selections
import com.arkhamcompanion.domain.objects.FuzzyMatcher.matchesFuzzy
import com.arkhamcompanion.domain.objects.TimestampNormalizer.compareTimestamps
import com.arkhamcompanion.domain.objects.TimestampNormalizer.getCurrentDateTime
import com.arkhamcompanion.domain.objects.normalizeForSearch
import com.arkhamcompanion.domain.objects.splitToWords
import com.arkhamcompanion.domain.repository.AnalyticsRepository
import com.arkhamcompanion.domain.repository.DecksRepository
import com.arkhamcompanion.domain.repository.PerformanceRepository
import com.arkhamcompanion.domain.utils.EnvironmentClassifier
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import javax.inject.Inject
import kotlin.uuid.Uuid

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
        environmentClassifier: EnvironmentClassifier,
        sort: String,
        filters: DeckFilters
    ): Flow<PagingData<DeckListItem>> {
        val queryWords = query.normalizeForSearch().splitToWords()

        val rawQuery = buildDecksListItemsQuery(sort, filters)

        return Pager(
            config = PagingConfig(
                pageSize = 15,
                prefetchDistance = 30,
                enablePlaceholders = true,
                initialLoadSize = 50,
            ),
            pagingSourceFactory = {
                LoggingPagingSource(
                    delegate = decksDao.searchPaginatedDecks(rawQuery),
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

    override fun getAllTags(): Flow<ImmutableSet<String>> =
        decksDao.getAllTags()
            .map { tags ->
                tags.asSequence()
                    .flatMap { it.splitToWords() }
                    .distinct()
                    .toImmutableSet()
            }

    override fun getStarterDeck(investigatorCode: String): DeckSlots? = starterDecks(investigatorCode)

    override suspend fun createDeck(
        upload: Boolean,
        investigatorCode: String,
        parallelFront: String?,
        parallelBack: String?,
        cardPool: String,
        selections: Selections,
        extraSlots: List<String>,
        deckName: String,
        slots: DeckSlots,
        tabooSetId: Int?,
        faction: Faction
    ): Result<String> = runCatching {
        val meta = buildJsonObject {
            parallelFront?.let {
                putAlternateFront(it)
            }

            parallelBack?.let {
                putAlternateBack(it)
            }

            if (cardPool.isNotBlank()) {
                putCardPool(cardPool)
            }

            putSelections(selections)

            if (extraSlots.isNotEmpty()) putExtraDeck(extraSlots)
        }

        val slots = buildJsonObject {
            putSlots(slots)
        }

        val tags = faction.name.lowercase()

        val deckId = if (upload) {
           createArkhamBuildDeck(
               investigatorCode = investigatorCode,
               meta = meta,
               deckName = deckName,
               slots = slots,
               tabooSetId = tabooSetId,
               tags = tags
           )
        } else {
           createLocalDeck(
               investigatorCode = investigatorCode,
               alternateFrontCode = parallelFront,
               alternateBackCode = parallelBack,
               meta = meta,
               deckName = deckName,
               slots = slots,
               extraDeck = extraSlots.joinToString(","),
               cardPool = cardPool,
               tabooSetId = tabooSetId,
               tags = tags
           )
        }

        return@runCatching deckId
    }

    suspend fun createArkhamBuildDeck(
        investigatorCode: String,
        meta: JsonElement,
        deckName: String,
        slots: JsonObject,
        tabooSetId: Int?,
        tags: String,
    ): String  {
        val accessToken = accessTokenProvider.getValidAccessToken()!!

        val requestDeck = DeckWriteRequestDto(
            investigatorCode = investigatorCode,
            meta = meta,
            name = deckName,
            slots = slots,
            tabooId = tabooSetId,
            tags = tags
        )

        val createdDeck = decksRemoteDataSource.createDeck(accessToken, requestDeck)

        decksDao.upsertDeck(createdDeck.toEntity())

        return createdDeck.id
    }

    suspend fun createLocalDeck(
        investigatorCode: String,
        alternateFrontCode: String?,
        alternateBackCode: String?,
        meta: JsonElement,
        deckName: String,
        slots: JsonObject,
        extraDeck: String,
        cardPool: String,
        tabooSetId: Int?,
        tags: String,
    ): String {
        val currentTime = getCurrentDateTime()

        val localDeck = DeckEntity(
            dateCreation = currentTime,
            dateUpdate = currentTime,
            descriptionMd = "",
            id = Uuid.random().toString(),
            investigatorCode = investigatorCode,
            alternateFrontCode = alternateFrontCode,
            alternateBackCode = alternateBackCode,
            meta = meta,
            name = deckName,
            slots = slots,
            extraDeck = extraDeck,
            cardPool = cardPool,
            tabooId = tabooSetId,
            tags = tags,
            version = "0.1",
        )

        decksDao.upsertDeck(localDeck)

        return localDeck.id
    }

}