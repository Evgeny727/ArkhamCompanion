package com.arkhamcompanion.data.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room3.RoomRawQuery
import androidx.room3.withWriteTransaction
import com.arkhamcompanion.data.local.ArkhamDatabase
import com.arkhamcompanion.data.local.LoggingPagingSource
import com.arkhamcompanion.data.local.arkhamql.QueryFieldResolverImpl
import com.arkhamcompanion.data.local.cards.CardCacheData
import com.arkhamcompanion.data.local.cards.CardEntity
import com.arkhamcompanion.data.local.cards.CardSubtypeEntity
import com.arkhamcompanion.data.local.cards.CardTypeEntity
import com.arkhamcompanion.data.local.cards.FavoriteCardEntity
import com.arkhamcompanion.data.local.cards.patches.CardPatchRegistry
import com.arkhamcompanion.data.local.meta.CycleEntity
import com.arkhamcompanion.data.local.meta.EncounterSetEntity
import com.arkhamcompanion.data.local.meta.FactionEntity
import com.arkhamcompanion.data.local.meta.PackEntity
import com.arkhamcompanion.data.local.meta.TabooSetEntity
import com.arkhamcompanion.data.mapper.db.toData
import com.arkhamcompanion.data.mapper.db.toEntity
import com.arkhamcompanion.data.mapper.domain.cards.toDetailsWithPackInfo
import com.arkhamcompanion.data.mapper.domain.cards.toDomain
import com.arkhamcompanion.data.mapper.domain.cards.withCategoryHeaders
import com.arkhamcompanion.data.objects.CardCache
import com.arkhamcompanion.data.objects.CardCache.createCache
import com.arkhamcompanion.data.objects.CardRelationResolver.buildCardWithRelations
import com.arkhamcompanion.data.objects.CardRelationResolver.resolveCardCodesWithRelations
import com.arkhamcompanion.data.remote.CardsRemoteDataSource
import com.arkhamcompanion.data.utils.buildCardsListItemsQuery
import com.arkhamcompanion.data.utils.buildSearchCardsQuery
import com.arkhamcompanion.data.utils.filterByInvestigatorAccess
import com.arkhamcompanion.data.utils.filterInvestigatorsByCards
import com.arkhamcompanion.data.utils.fuzzySearch
import com.arkhamcompanion.data.utils.prepareWordsForFuzzySearch
import com.arkhamcompanion.data.utils.preprocessCardText
import com.arkhamcompanion.domain.arkhamql.QueryError
import com.arkhamcompanion.domain.arkhamql.QueryParseResult
import com.arkhamcompanion.domain.arkhamql.evaluator.QueryEvaluationException
import com.arkhamcompanion.domain.arkhamql.evaluator.QueryEvaluator
import com.arkhamcompanion.domain.arkhamql.fields.QueryFieldRegistry
import com.arkhamcompanion.domain.arkhamql.fields.QueryFields
import com.arkhamcompanion.domain.arkhamql.lexer.QueryLexer
import com.arkhamcompanion.domain.arkhamql.lexer.QueryLexerException
import com.arkhamcompanion.domain.arkhamql.parser.QueryParser
import com.arkhamcompanion.domain.arkhamql.parser.QueryParserException
import com.arkhamcompanion.domain.model.cards.CardDetailsWithRelations
import com.arkhamcompanion.domain.model.cards.CardInvestigatorAccessFields
import com.arkhamcompanion.domain.model.cards.CardListItemUiModel
import com.arkhamcompanion.domain.model.cards.CardSearchConfig
import com.arkhamcompanion.domain.model.cards.CardSearchResult
import com.arkhamcompanion.domain.model.cards.CardTabooInfo
import com.arkhamcompanion.domain.model.cards.CardText
import com.arkhamcompanion.domain.objects.CardTextParser
import com.arkhamcompanion.domain.objects.TimestampNormalizer.compareTimestamps
import com.arkhamcompanion.domain.objects.TimestampNormalizer.getCurrentDateTime
import com.arkhamcompanion.domain.objects.TimestampNormalizer.isAtLeastTwoWeeksApart
import com.arkhamcompanion.domain.repository.AnalyticsRepository
import com.arkhamcompanion.domain.repository.CardsRepository
import com.arkhamcompanion.domain.repository.PerformanceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.File
import javax.inject.Inject

private val json = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
}

class CardsRepositoryImpl @Inject constructor(
    private val cardsRemoteDataSource: CardsRemoteDataSource,
    private val db: ArkhamDatabase,
    @ApplicationContext private val context: Context,
    private val performanceRepository: PerformanceRepository,
    private val analyticsRepository: AnalyticsRepository,
) : CardsRepository {

    private val cardsDao = db.cardsDao()
    private val metaDao = db.metaDao()

    override suspend fun downloadAllCards(locale: String, onProgress: (Float) -> Unit) =
        runCatching {
            val translationData =
                cardsRemoteDataSource.fetchAllTranslationData(locale).dataAssertNoErrors
            onProgress(0.15f)
            val playerCards = cardsRemoteDataSource.fetchAllPlayerCards(locale).dataAssertNoErrors
            onProgress(0.30f)
            val encounterCards =
                cardsRemoteDataSource.fetchAllEncounterCards(locale).dataAssertNoErrors
            onProgress(0.45f)

            val cardPatches = CardPatchRegistry()

            val cardTypeEntities = translationData.card_type_name.map { it.toEntity() }
            val cardSubtypeEntities = translationData.card_subtype_name.map { it.toEntity() }
            val factionEntities = translationData.faction_name.map { it.toEntity() }
            val cycleEntities = translationData.cycle.map {
                it.cycle.toEntity(it.translations.getOrNull(0)?.name ?: it.cycle.real_name)
            }
            val packEntities = translationData.cycle.flatMap { cycle ->
                cycle.packs.mapNotNull {
                    //Filter out books from packs, there're no cards with such pack
                    if (it.pack.code != "books")
                        it.pack.toEntity(it.translations.getOrNull(0)?.name ?: it.pack.real_name)
                    else null
                }
            }
            val translatedEncountersMap =
                translationData.card_encounter_set.associateBy { it.encounterSet.code }
            val encounterSetEntities = translationData.english_encounters.map {
                it.encounterSet.toEntity(translatedEncountersMap[it.encounterSet.code]?.encounterSet)
            }
            val tabooSetEntities = playerCards.taboo_set.map { it.tabooSet.toEntity() }

            val typeMap = cardTypeEntities.associateBy { it.code }
            val subtypeMap = cardSubtypeEntities.associateBy { it.code }
            val factionMap = factionEntities.associateBy { it.code }
            val packMap = packEntities.associateBy { it.code }
            val cycleMap = cycleEntities.associateBy { it.code }
            val encounterSetMap = encounterSetEntities.associateBy { it.code }
            val tabooSetMap = tabooSetEntities.associateBy { it.id }

            val playerEntities = playerCards.all_card.map {
                val pack = packMap[it.singleCard.pack_code]!!
                val cycle = cycleMap[pack.cycleCode]!!

                it.singleCard.toEntity(
                    it.translations.getOrNull(0)?.coreCardText,
                    cardPatches.resolve(it.singleCard.code),
                    cycle,
                    packMap[it.singleCard.pack_code]!!,
                    locale
                )
            }
            onProgress(0.50f)
            val encounterEntities = encounterCards.all_card.map {
                val pack = packMap[it.singleCard.pack_code]!!
                val cycle = cycleMap[pack.cycleCode]!!

                it.singleCard.toEntity(
                    it.translations.getOrNull(0)?.coreCardText,
                    cardPatches.resolve(it.singleCard.code),
                    cycle,
                    packMap[it.singleCard.pack_code]!!,
                    locale
                )
            }
            onProgress(0.55f)

            var allCards = playerEntities + encounterEntities

            var relationErrors = 0
            //TODO: Replace with card-patches from arkham.build on release
            allCards = allCards.checkRelations(
                typeMap,
                subtypeMap,
                factionMap,
                packMap,
                cycleMap,
                encounterSetMap,
                tabooSetMap,
                onErrorCount = { relationErrors++ }
            )

            if (relationErrors > 0) {
                analyticsRepository.logError(
                    IllegalStateException("$relationErrors relation errors were found")
                )
            }

            onProgress(0.65f)

            db.withWriteTransaction {
                cardsDao.deleteAllCards()
                metaDao.deleteAll()
                metaDao.upsertFactions(factionEntities)
                metaDao.upsertCycles(cycleEntities)
                metaDao.upsertPacks(packEntities)
                metaDao.upsertEncounterSets(encounterSetEntities)
                metaDao.upsertTabooSets(tabooSetEntities)
                cardsDao.upsertCardTypes(cardTypeEntities)
                cardsDao.upsertCardSubtypes(cardSubtypeEntities)
                allCards.chunked(500).forEach {
                    cardsDao.upsertAllCards(it)
                }
            }
            onProgress(0.85f)

            performanceRepository.trace("createCache") {
                createCache(allCards, analyticsRepository)
            }
            onProgress(0.93f)
            saveCache()
            onProgress(0.97f)

            val updatedAt = playerCards.all_card_updated_by_version.getOrNull(0)
            val compared = compareTimestamps(
                updatedAt?.cards_updated_at.toString(),
                updatedAt?.translation_updated_at.toString(),
            )

            onProgress(1.0f)

            if (compared) updatedAt?.translation_updated_at.toString()
            else updatedAt?.cards_updated_at.toString()
        }

    private fun List<CardEntity>.checkRelations(
        typeMap: Map<String, CardTypeEntity>,
        subtypeMap: Map<String, CardSubtypeEntity>,
        factionMap: Map<String, FactionEntity>,
        packMap: Map<String, PackEntity>,
        cycleMap: Map<String, CycleEntity>,
        encounterSetMap: Map<String, EncounterSetEntity>,
        tabooSetMap: Map<Int, TabooSetEntity>,
        onErrorCount: () -> Unit
    ): List<CardEntity> = filter {
        var cardIsOkay = true

        if (typeMap[it.typeCode] == null) {
            analyticsRepository.logMessage("Unknown card type: ${it.typeCode}")
            onErrorCount()
            cardIsOkay = false
        }
        if (it.subTypeCode != null && subtypeMap[it.subTypeCode] == null) {
            analyticsRepository.logMessage("Unknown card subtype: ${it.subTypeCode}")
            onErrorCount()
            cardIsOkay = false
        }
        if (factionMap[it.factionCode] == null) {
            analyticsRepository.logMessage("Unknown card faction: ${it.factionCode}")
            onErrorCount()
            cardIsOkay = false
        }
        if (packMap[it.packCode] == null) {
            analyticsRepository.logMessage("Unknown card pack: ${it.packCode}")
            onErrorCount()
            cardIsOkay = false
        }
        if (cycleMap[it.cycleCode] == null) {
            analyticsRepository.logMessage("Unknown card cycle: ${it.cycleCode}")
            onErrorCount()
            cardIsOkay = false
        }
        if (it.encounterCode != null && encounterSetMap[it.encounterCode] == null) {
            analyticsRepository.logMessage("Unknown card encounter: ${it.encounterCode}")
            onErrorCount()
            cardIsOkay = false
        }
        if (it.tabooSetId != null && tabooSetMap[it.tabooSetId] == null) {
            analyticsRepository.logMessage("Unknown card taboo: ${it.tabooSetId}")
            onErrorCount()
            cardIsOkay = false
        }

        cardIsOkay
    }

    override suspend fun isCardsTableExists(): Boolean = cardsDao.isExists()

    override suspend fun isCardsUpdateAvailable(
        locale: String,
        savedTimestamp: String?,
        forced: Boolean
    ) = runCatching {
        val currentTimestamp = getCurrentDateTime()
        if (!forced && !isAtLeastTwoWeeksApart(savedTimestamp, currentTimestamp))
            return@runCatching false

        val cardsUpdatedAt = cardsRemoteDataSource.fetchCardsUpdatedAt(locale).dataAssertNoErrors
            .all_card_updated.getOrNull(0)

        compareTimestamps(
            savedTimestamp,
            cardsUpdatedAt?.cards_updated_at.toString()
        ) || compareTimestamps(
            savedTimestamp,
            cardsUpdatedAt?.translation_updated_at.toString()
        )
    }

    @OptIn(ExperimentalSerializationApi::class)
    private suspend fun saveCache() = performanceRepository.trace("saveCache") {
        withContext(Dispatchers.IO) {
            File(context.filesDir, "card_cache.json")
                .outputStream()
                .buffered()
                .use { json.encodeToStream(CardCache.toData(), it) }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun loadCache(): Boolean = performanceRepository.trace("loadCache") {
        withContext(Dispatchers.IO) {
            val file = File(context.filesDir, "card_cache.json")

            if (!file.exists()) {
                return@withContext false
            }

            val data: CardCacheData =
                file.inputStream().buffered().use {
                    json.decodeFromStream<CardCacheData>(it)
                }

            CardCache.load(data)

            return@withContext true
        }
    }

    override suspend fun recreateCache(): Boolean = try {
        performanceRepository.trace("recreateCache") {
            withContext(Dispatchers.IO) {
                val allCards = cardsDao.getAllCards()
                createCache(allCards, analyticsRepository)
                saveCache()
            }
        }
        true
    } catch (e: Throwable) {
        analyticsRepository.logError(e)
        false
    }

    override suspend fun clearCardsDatabase() = runCatching {
        cardsDao.deleteAllCards()
        metaDao.deleteAll()
    }

    override suspend fun clearFavoriteCards() = runCatching {
        cardsDao.clearFavoriteCards()
    }

    override fun searchPaginatedCardsFlow(
        ids: List<String>,
        searchConfig: CardSearchConfig
    ): Flow<PagingData<CardListItemUiModel>> {

        val values = ids.mapIndexed { index, id ->
            "('$id', $index)"
        }.joinToString(",\n")

        val requested = if (ids.isEmpty()) {
            "SELECT NULL AS id, NULL AS position WHERE 0"
        } else {
            "VALUES $values"
        }

        val query = buildCardsListItemsQuery(requested)

        return Pager(
            config = PagingConfig(
                pageSize = 70,
                prefetchDistance = 140,
                enablePlaceholders = true,
                initialLoadSize = 300,
            ),
            pagingSourceFactory = {
                LoggingPagingSource(
                    delegate = cardsDao.getPagedCardsByIds(RoomRawQuery(query)),
                    analyticsRepository = analyticsRepository
                )
            }
        ).flow.withCategoryHeaders(
            with(searchConfig) {
                if (spoiler) preferences.mythosSortOrder else preferences.playerSortOrder
            },
            searchConfig.spoiler
        )
    }

    private val queryFieldResolverImpl = QueryFieldResolverImpl()
    private val queryFieldRegistry = QueryFieldRegistry(QueryFields.all)
    private var isInQlMode = false

    override fun searchCardCodesFlow(
        searchConfig: CardSearchConfig
    ): Flow<CardSearchResult> {
        val rawQuery = buildSearchCardsQuery(searchConfig)

        val (words, includeEnglish) = prepareWordsForFuzzySearch(
            searchConfig.options,
            searchConfig.preferences.includeEnglish
        )

        val queryEvaluator = QueryEvaluator(
            queryFieldResolverImpl,
            searchConfig.options.searchBack,
            includeEnglish,
        )

        if (searchConfig.options.searchQuery.isBlank()) {
            isInQlMode = false
        }

        val queryResult = if (searchConfig.options.searchQuery.isNotBlank()) {
            runCatching {
                val tokens = QueryLexer(searchConfig.options.searchQuery).tokenize()
                QueryParser(tokens, queryFieldRegistry).parse()
            }.fold(
                onSuccess = { QueryParseResult.Success(it) },
                onFailure = { error ->
                    when (error) {
                        is QueryLexerException -> QueryParseResult.Error(error.error)
                        is QueryParserException -> QueryParseResult.Error(error.error)
                        else -> QueryParseResult.Error(QueryError.UnknownError(error.message.toString()))
                    }
                },
            )
        } else {
            null
        }

        return cardsDao.getSearchedCardCodesRaw(rawQuery)
            .catch {
                analyticsRepository.logMessage("query: " + searchConfig.options.searchQuery)
                analyticsRepository.logMessage(searchConfig.filters.toString())
                analyticsRepository.logError(it)
            }
            .map { list ->
                val filteredList = when {
                    searchConfig.filters.cardpoolFilter != null ->
                        list.filterByInvestigatorAccess(
                            searchConfig.filters.cardpoolFilter!!,
                            analyticsRepository::logMessage
                        )

                    searchConfig.filters.whoCanTakeCard.isNotEmpty() ->
                        list.filterInvestigatorsByCards(
                            searchConfig.filters.whoCanTakeCard,
                            analyticsRepository::logMessage
                        )

                    else -> list
                }

                when {
                    // Already in QL mode: parsing/evaluation errors are errors.
                    isInQlMode -> {
                        when (queryResult) {
                            is QueryParseResult.Error -> {
                                CardSearchResult(
                                    error = queryResult.error,
                                    cards = filteredList.toDomain(),
                                )
                            }

                            is QueryParseResult.Success -> {
                                runCatching {
                                    filteredList.filter {
                                        queryEvaluator.evaluate(
                                            queryResult.expression,
                                            it,
                                        )
                                    }
                                }.fold(
                                    onSuccess = { filtered ->
                                        CardSearchResult(
                                            error = null,
                                            cards = filtered.toDomain(),
                                        )
                                    },
                                    onFailure = { error ->
                                        CardSearchResult(
                                            error = when (error) {
                                                is QueryEvaluationException -> error.error
                                                else -> QueryError.UnknownError(error.message.toString())
                                            },
                                            cards = filteredList.toDomain(),
                                        )
                                    },
                                )
                            }

                            null -> {
                                CardSearchResult(
                                    error = null,
                                    cards = filteredList.toDomain(),
                                )
                            }
                        }
                    }

                    // Not in QL mode: try the expression, but don't enter QL
                    // until evaluation succeeds.
                    queryResult is QueryParseResult.Success -> {
                        val evaluationResult = runCatching {
                            filteredList.filter {
                                queryEvaluator.evaluate(
                                    queryResult.expression,
                                    it,
                                )
                            }
                        }

                        evaluationResult.fold(
                            onSuccess = { filteredList ->
                                isInQlMode = true

                                CardSearchResult(
                                    error = null,
                                    cards = filteredList.toDomain(),
                                )
                            },
                            onFailure = {
                                CardSearchResult(
                                    error = null,
                                    cards = filteredList
                                        .filter {
                                            it.fuzzySearch(
                                                searchConfig.options,
                                                words,
                                                includeEnglish,
                                            )
                                        }
                                        .toDomain(),
                                )
                            },
                        )
                    }

                    // Not QL and couldn't parse → normal fuzzy search.
                    else -> CardSearchResult(
                        error = null,
                        cards = filteredList
                            .filter {
                                it.fuzzySearch(
                                    searchConfig.options,
                                    words,
                                    includeEnglish,
                                )
                            }
                            .toDomain(),
                    )
                }
            }
    }

    override fun getCardWithRelationsByCodeFlow(
        code: String,
        tabooSetId: Int?
    ): Flow<CardDetailsWithRelations> {
        val codes = resolveCardCodesWithRelations(code)

        return cardsDao.getCardsByCodeFlow(codes, tabooSetId)
            .map { cards ->
                val detailsWithPackInfoMap = cards.toDetailsWithPackInfo()

                buildCardWithRelations(code, detailsWithPackInfoMap)
            }
    }

    override suspend fun addFavorite(code: String) {
        cardsDao.addFavorite(FavoriteCardEntity(code))
    }

    override suspend fun removeFavorite(code: String) {
        cardsDao.removeFavorite(FavoriteCardEntity(code))
    }

    override fun observeFavoriteCodes(): Flow<ImmutableSet<String>> =
        cardsDao.observeFavoriteCodes().map {
            it.toImmutableSet()
        }

    override fun getCardTabooHistoryByCodeFlow(code: String): Flow<ImmutableList<CardTabooInfo>> =
        cardsDao.getTabooHistoryByCodeFlow(code).map {
            it.map { item -> item.toDomain() }.toImmutableList()
        }

    override fun getAllInvestigatorsByName(name: String) = Pager(
        config = PagingConfig(
            pageSize = 70,
            prefetchDistance = 140,
            enablePlaceholders = true,
            initialLoadSize = 300,
        ),
        pagingSourceFactory = {
            LoggingPagingSource(
                delegate = cardsDao.getAllInvestigatorsByNamePaged(name),
                analyticsRepository = analyticsRepository
            )
        }
    ).flow.map { data ->
        data.map { it.toDomain() }
    }

    override fun getAllPlayableCardsByName(name: String) = Pager(
        config = PagingConfig(
            pageSize = 70,
            prefetchDistance = 140,
            enablePlaceholders = true,
            initialLoadSize = 300,
        ),
        pagingSourceFactory = {
            LoggingPagingSource(
                delegate = cardsDao.getAllPlayableCardsByNamePaged(name),
                analyticsRepository = analyticsRepository
            )
        }
    ).flow.map { data ->
        data.map { it.toDomain() }
    }

    override suspend fun getInitialCardFields(cardId: String): CardInvestigatorAccessFields {
        return cardsDao.getInitialCardFieldsById(cardId).toDomain()
    }

    override fun getCardFaqByCodeFlow(code: String): Flow<Result<CardText?>> = flow {
        val faqEntry = runCatching {
            cardsRemoteDataSource.fetchCardFaqByCode(code).dataAssertNoErrors
        }

        val result = if (faqEntry.isSuccess) {
            Result.success(
                faqEntry.getOrNull()?.faq_by_pk?.let {
                    val preprocessedText = it.text.preprocessCardText(
                        processBullets = true,
                        processWebFormat = true
                    )

                    CardTextParser.parse(preprocessedText)
                }
            )
        } else {
            Result.failure(faqEntry.exceptionOrNull()!!)
        }

        emit(result)
    }
}