package com.arkhamcompanion.data.repository

import android.content.Context
import android.os.Build
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room3.RoomRawQuery
import androidx.room3.withWriteTransaction
import com.arkhamcompanion.data.local.ArkhamDatabase
import com.arkhamcompanion.data.local.LoggingPagingSource
import com.arkhamcompanion.data.local.cards.CardCacheData
import com.arkhamcompanion.data.local.cards.patches.CardPatchRegistry
import com.arkhamcompanion.data.mapper.db.toData
import com.arkhamcompanion.data.mapper.db.toEntity
import com.arkhamcompanion.data.mapper.domain.cards.toDetailsWithPackInfo
import com.arkhamcompanion.data.mapper.domain.cards.toDomain
import com.arkhamcompanion.data.mapper.domain.cards.withCategoryHeaders
import com.arkhamcompanion.data.objects.CardCache
import com.arkhamcompanion.data.objects.CardCache.createCache
import com.arkhamcompanion.data.objects.CardRelationResolver.buildCardWithRelations
import com.arkhamcompanion.data.objects.CardRelationResolver.resolveCardCodesWithRelations
import com.arkhamcompanion.data.objects.CardSearchQueryBuilder.buildSortClause
import com.arkhamcompanion.data.objects.createSQLSearchQuery
import com.arkhamcompanion.data.objects.normalizeForSearch
import com.arkhamcompanion.data.remote.CardsRemoteDataSource
import com.arkhamcompanion.domain.model.cards.CardDetailsWithRelations
import com.arkhamcompanion.domain.model.cards.CardFilters
import com.arkhamcompanion.domain.model.cards.CardListItemUiModel
import com.arkhamcompanion.domain.model.cards.CardSearchConfig
import com.arkhamcompanion.domain.model.cards.CardSearchOptions
import com.arkhamcompanion.domain.model.cards.CodeWithTaboo
import com.arkhamcompanion.domain.model.settings.isEmpty
import com.arkhamcompanion.domain.model.settings.isNotEmpty
import com.arkhamcompanion.domain.objects.TimestampNormalizer.compareTimestamps
import com.arkhamcompanion.domain.objects.TimestampNormalizer.getCurrentDateTime
import com.arkhamcompanion.domain.objects.TimestampNormalizer.isAtLeastTwoWeeksApart
import com.arkhamcompanion.domain.repository.AnalyticsRepository
import com.arkhamcompanion.domain.repository.CardsRepository
import com.arkhamcompanion.domain.repository.PerformanceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.File
import java.util.Locale
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

    private val supportsWindowFunctions = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    override suspend fun downloadAllCards(locale: String, onProgress: (Float) -> Unit) = runCatching {
        val translationData = cardsRemoteDataSource.fetchAllTranslationData(locale).dataAssertNoErrors
        onProgress(0.15f)
        val playerCards = cardsRemoteDataSource.fetchAllPlayerCards(locale).dataAssertNoErrors
        onProgress(0.30f)
        val encounterCards = cardsRemoteDataSource.fetchAllEncounterCards(locale).dataAssertNoErrors
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
        val translatedEncountersMap = translationData.card_encounter_set.associateBy { it.encounterSet.code }
        val encounterSetEntities = translationData.english_encounters.map {
            it.encounterSet.toEntity(translatedEncountersMap[it.encounterSet.code]?.encounterSet)
        }
        val tabooSetEntities = playerCards.taboo_set.map { it.tabooSet.toEntity() }

        val packMap = packEntities.associateBy { it.code }
        val cycleMap = cycleEntities.associateBy { it.code }

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

        val allCards = playerEntities + encounterEntities

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

    override suspend fun isCardsTableExists(): Boolean = cardsDao.isExists()

    override suspend fun isCardsUpdateAvailable(locale: String, savedTimestamp: String?, forced: Boolean) = runCatching {
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

            if (!file.exists()) { return@withContext false }

            val data: CardCacheData =
                file.inputStream().buffered().use{
                    json.decodeFromStream<CardCacheData>(it)
                }

            CardCache.load(data)

            return@withContext true
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
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

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun clearCardsDatabase() = runCatching {
        cardsDao.deleteAllCards()
        metaDao.deleteAll()
    }

    override fun searchPaginatedCardsFlow(
        searchConfig: CardSearchConfig
    ): Flow<PagingData<CardListItemUiModel>> {
        val rawQuery = buildSearchCardsQuery(searchConfig)

        return Pager(
            config = PagingConfig(
                pageSize = 70,
                prefetchDistance = 140,
                enablePlaceholders = true,
                initialLoadSize = 300,
            ),
            pagingSourceFactory = {
                LoggingPagingSource(
                    delegate = cardsDao.searchCardsRaw(rawQuery),
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

    override fun searchCardCodesFlow(
        searchConfig: CardSearchConfig
    ): Flow<ImmutableList<CodeWithTaboo>> {
        val rawQuery = buildSearchCardsQuery(
            searchConfig,
            projection = "code, taboo_set_id"
        )

        return cardsDao.getSearchedCardCodesRaw(rawQuery).map { it.toDomain() }
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

    private fun buildSearchCardsQuery(
        searchConfig: CardSearchConfig,
        projection: String? = null
    ): RoomRawQuery {
        val sortClause = buildSortClause(
            with(searchConfig) {
                if (spoiler) preferences.mythosSortOrder else preferences.playerSortOrder
            },
            searchConfig.spoiler
        )

        val filterClause = searchConfig.filters.buildFiltersQuery()

        val searchQuery = buildSearchQuery(
            searchConfig.options,
            searchConfig.preferences.includeEnglish
        )

        val isQueryNotBlank = searchQuery.sqlQuery.isNotBlank()

        val (packsQuery, reprintsQuery) = if (searchConfig.preferences.ignoreCollection) "" to ""
        else {
            val packs = searchConfig.preferences.collection.packs.joinToString(",") { "'$it'" }
            val reprints = searchConfig.preferences.collection.reprintPacks.joinToString(",") { "'$it'" }
            packs to reprints
        }

        val rankedQueryPart = if (supportsWindowFunctions)
            """
                SELECT *, ROW_NUMBER() OVER (
                    PARTITION BY
                        COALESCE(duplicate_of_code, code)
                    ORDER BY
                        CASE
                            WHEN duplicate_of_code IS NULL THEN 0
                            ELSE 1
                        END,
                        code
                ) AS duplicate_rank FROM filtered_cards
            """.trimIndent()
        else """
            WITH deduplicated_cards AS (
                SELECT
                    CASE
                        WHEN COUNT(
                            CASE WHEN duplicate_of_code IS NULL THEN 1 END
                        ) > 0
                        THEN MIN(
                            CASE WHEN duplicate_of_code IS NULL THEN code END
                        )
                        ELSE MIN(code)
                    END AS winner_code
                FROM filtered_cards
                GROUP BY COALESCE(duplicate_of_code, code)
            )
            SELECT fc.*
            FROM filtered_cards fc
            JOIN deduplicated_cards dc
                ON dc.winner_code = fc.code
        """.trimIndent()

        val spoilerQueryPart = if (searchConfig.spoiler) {
            if (supportsWindowFunctions) "SELECT ${projection ?: "*"}, MIN(pack_position) OVER (" +
                    "PARTITION BY encounter_code" +
                    ") AS encounter_group FROM ranked_cards "
            else """
                SELECT ${projection ?: "*"} FROM (
                    SELECT rc.*, eg.encounter_group
                    FROM ranked_cards rc
                    JOIN (
                        SELECT encounter_code, MIN(pack_position) AS encounter_group
                        FROM ranked_cards 
                        GROUP BY encounter_code
                    ) eg
                        ON eg.encounter_code = rc.encounter_code
                )
            """.trimIndent()
        }
        else "SELECT ${projection ?: "*"} FROM ranked_cards "

        val finalQueryPart = spoilerQueryPart +
                (if (supportsWindowFunctions) "WHERE duplicate_rank = 1" else "") +
                if (sortClause.isNotEmpty()) " ORDER BY $sortClause" else ""

        return RoomRawQuery(
            sql = """
                WITH filtered_cards AS (
                    WITH selected_taboo AS (
                        SELECT CASE
                            WHEN ? = 0 THEN NULL
                            WHEN ? = 100 THEN (SELECT MAX(id) FROM taboo_set)
                            ELSE ?
                        END AS id
                    )
                    
                    SELECT
                        c.id,
                        c.code,
                        c.duplicate_of_code,
                        c.thumbnailurl,
                    
                        c.cost,
                        c.xp,
                        c.permanent,
                    
                        c.taboo_xp,
                        c.taboo_set_id,
                        c.taboo_placeholder,
                    
                        c.type_code,
                        t.name AS typeName,
                    
                        c.subtype_code,
                        st.name AS subTypeName,
                    
                        c.faction_code,
                        f.name AS factionName,
                        c.faction2_code,
                        c.faction3_code,
                    
                        c.pack_code,
                        p.name AS packName,
                        c.pack_position,
                    
                        c.encounter_code,
                        e.name AS encounterName,
                        c.encounter_position,
                    
                        c.cycle_code,
                        cy.name AS cycleName,
                        cy.position as cyclePosition,
                        
                        c.reprint_pack_code,
                    
                        c.name,
                        c.subname,
                    
                        c.skill_willpower,
                        c.skill_intellect,
                        c.skill_combat,
                        c.skill_agility,
                        c.skill_wild,
                    
                        c.parallel,
                        c.is_unique,
                        c.slot,
                        c.stage,
                        
                        c.sort_by_type,
                        c.sort_by_faction,
                        c.sort_by_pack,
                        c.sort_by_cycle,
                        c.sort_by_slot
                    FROM card c
                    JOIN card_type t
                        ON c.type_code = t.code
                    LEFT JOIN card_subtype st
                        ON c.subtype_code = st.code
                    JOIN faction f
                        ON c.faction_code = f.code
                    JOIN pack p
                        ON c.pack_code = p.code
                    JOIN cycle cy
                        ON c.cycle_code = cy.code
                    LEFT JOIN encounter_set e
                        ON c.encounter_code = e.code
                    CROSS JOIN selected_taboo taboo
                    WHERE c.encounter_code IS ${if (searchConfig.spoiler) "NOT NULL" else "NULL"} 
                    ${if (filterClause.isNotBlank())
                        """ AND (
                            $filterClause 
                            OR EXISTS (
                                SELECT 1
                                FROM card back
                                WHERE back.code = c.back_link_id AND $filterClause
                            )
                        )""".trimIndent() else ""
                    }
                    ${ if (searchConfig.preferences.ignoreCollection 
                        || searchConfig.filters.packs.isNotEmpty()) "" 
                    else """ AND (
                        c.pack_code IN ($packsQuery) 
                        OR c.reprint_pack_code IN ($reprintsQuery)
                    )""".trimIndent()
                    }
                    ${ if (searchConfig.spoiler || searchConfig.filters.tabooSetId != null) "" 
                    else """ AND
                     (
                        -- No taboo selected -> originals only
                        (taboo.id IS NULL AND c.taboo_set_id IS NULL)
        
                        OR
                        
                        (taboo.id IS NOT NULL AND 
                            (
                                -- Selected taboo version
                                c.taboo_set_id = taboo.id
                
                                OR
                
                                -- Original version if no taboo override exists
                                (c.taboo_set_id IS NULL
                                    AND NOT EXISTS (
                                        SELECT 1 FROM card t WHERE t.taboo_set_id = taboo.id AND t.code = c.code
                                    )
                                )
                            )
                        )
                     )
                    """.trimIndent() 
                    }
                     AND c.hidden = 0 ${ if (searchConfig.preferences.showFanMade) "" 
                         else { " AND (" +
                            if (searchConfig.filters.officialFilter == null) "c.official = 1 AND " else ""} +
                            "c.preview = 0)" 
                         }
                    ${if (isQueryNotBlank)
                        """AND ((${searchQuery.searchFieldsQuery}) 
                            ${if (searchConfig.options.searchBack)
                                """OR EXISTS (
                                    SELECT 1
                                    FROM card back
                                    WHERE back.code = c.back_link_id
                                    AND (${searchQuery.backLinkCardFieldsQuery})
                                )""".trimIndent() else ""}
                        )""".trimIndent() else ""}
                ),
                
                ranked_cards AS ($rankedQueryPart)

                $finalQueryPart
            """.trimIndent(),
            onBindStatement = { statement ->
                var index = 1
                with(searchConfig.preferences) {
                    repeat(3) {
                        statement.bindInt(index++, tabooSetId)
                    }
                }
                if (isQueryNotBlank) {
                    repeat(searchQuery.searchFieldsAmount) {
                        statement.bindText(index++, searchQuery.sqlQuery)
                        if (searchConfig.options.searchBack) {
                            statement.bindText(index++, searchQuery.sqlQuery)
                        }
                    }
                }
            }
        )
    }

    private fun buildSearchQuery(
        searchOptions: CardSearchOptions,
        includeEnglish: Boolean
    ): SqlSearchOptions {
        val language = Locale.getDefault().toLanguageTag().substringBefore("-")
        val shouldIncludeRealFields = language != "en" && includeEnglish

        val sqlQuery = searchOptions.searchQuery
            .normalizeForSearch()
            .createSQLSearchQuery()

        if (sqlQuery.isBlank()) return SqlSearchOptions()

        val searchFields = searchOptions.buildSearchFields(shouldIncludeRealFields)

        val searchFieldsQuery = searchFields.joinToString(" OR ") { "c.$it LIKE ?" }
        val backLinkCardFieldsQuery = searchFields.joinToString(" OR ") { "back.$it LIKE ?" }

        return SqlSearchOptions(
            sqlQuery,
            searchFieldsQuery,
            backLinkCardFieldsQuery,
            searchFields.size
        )
    }

    private fun CardSearchOptions.buildSearchFields(
        shouldIncludeRealFields: Boolean
    ): List<String> {
        val fields = buildList {
            add("name")
            if (searchBack) add("name_back")

            if (searchGame) {
                add("game")
                if (searchBack) add("game_back")
            }

            if (searchFlavor) {
                add("flavor")
                if (searchBack) add("flavor_back")
            }
        }

        return buildList {
            fields.forEach {
                add("search_$it")
                if (shouldIncludeRealFields) add("search_real_$it")
            }
        }
    }

    private val defaultFilters = CardFilters()

    private fun CardFilters.buildFiltersQuery(): String {
        if (this == defaultFilters) return ""

        val filtersListBuilder = buildList {
            var codes: MutableSet<String>? = null

            fun applyCodes(otherCodes: Set<String>) {
                if (codes == null) {
                    codes = otherCodes.toMutableSet()
                } else {
                    if (codes.isNotEmpty()) codes.retainAll(otherCodes)
                }
            }

            /*
            *  First build filters with indexed fields
            */

            propertiesFilter.run {
                if (this == defaultFilters.propertiesFilter) return@run

                if (fast) {
                    applyCodes(CardCache.properties["fast"].orEmpty())
                }
                if (healsDamage) {
                    applyCodes(CardCache.tags["hd"].orEmpty())
                }
                if (healsHorror) {
                    applyCodes(CardCache.tags["hh"].orEmpty())
                }
                if (seal) {
                    applyCodes(CardCache.tags["se"].orEmpty())
                }
                if (succeedBy) {
                    applyCodes(CardCache.properties["succeeds_by"].orEmpty())
                }
            }

            assetFilter.run {
                if (this == defaultFilters.assetFilter) return@run

                applyCodes(
                    skillBoosts.flatMap {
                        CardCache.skillBoosts[it].orEmpty()
                    }.toSet()
                )

                applyCodes(
                    uses.flatMap {
                        CardCache.uses[it].orEmpty()
                    }.toSet()
                )

                applyCodes(
                    slots.flatMap {
                        CardCache.slots[it].orEmpty()
                    }.toSet()
                )
            }

            if (actions.isNotEmpty()) {
                applyCodes(
                    actions.flatMap {
                        CardCache.actions[it].orEmpty()
                    }.toSet()
                )
            }

            if (traits.isNotEmpty()) {
                applyCodes(
                    traits.flatMap {
                        CardCache.traits[it].orEmpty()
                    }.toSet()
                )
            }

            codes?.let { codes ->
                if (codes.isEmpty()) {
                    add("1 = 0")
                    return@buildList
                }
                val cardCodesString = codes.joinToString(",") { "'$it'" }
                add("c.code IN ($cardCodesString)")
            }

            if (factions.isNotEmpty()) {
                val factionsString = factions.joinToString(",") { "'${it.name.lowercase()}'" }
                add("""
                    (
                        c.faction_code IN ($factionsString) 
                        OR c.faction2_code IN ($factionsString) 
                        OR c.faction3_code IN ($factionsString)
                    )
                """.trimIndent())
            }

            levelFilter.run {
                if (this == defaultFilters.levelFilter) return@run

                val result = forcedRange ?: range
                val (min, max) = result
                add(
                    when {
                        max == null -> "(c.xp IS NULL)"
                        min == null -> "(c.xp IS NULL OR c.xp <= $max)"
                        else -> "(c.xp BETWEEN $min AND $max)"
                    }
                )
            }

            if (types.isNotEmpty()) {
                val typesString = types.joinToString(",") { "'${it.code}'" }
                add("c.type_code IN ($typesString)")
            }

            if (subTypes.isNotEmpty()) {
                val nonNullable = subTypes.filterNotNull()
                val haveNull = null in subTypes
                val nonNullableString = nonNullable.joinToString(",") { "'${it.name.lowercase()}'" }
                add("""
                    (
                        ${if (haveNull) "c.subtype_code IS NULL" else ""}
                        ${if (nonNullable.isNotEmpty()) {
                            (if (haveNull) " OR " else "") +
                            "c.subtype_code IN ($nonNullableString)"
                        } else ""}
                    )
                """.trimIndent())

            }

            if (encounterSets.isNotEmpty()) {
                val encounterSetsString = encounterSets.joinToString(",") { "'$it'" }
                add("c.encounter_code IN ($encounterSetsString)")
            }

            packs.run {
                if (isEmpty()) return@run

                val packsString = packs.joinToString(",") { "'$it'" }
                val reprintsString = reprintPacks.joinToString(",") { "'$it'" }
                add("""
                    (
                        c.pack_code IN ($packsString) 
                        OR c.reprint_pack_code IN ($reprintsString)
                    )
                """.trimIndent())
            }

            tabooSetId?.let {
                add("(c.taboo_set_id = $it AND c.taboo_placeholder = 0)")
            }

            /*
            *  Non-indexed filters
            */

            costFilter.run {
                if (this == defaultFilters.costFilter) return@run

                val (min, max) = range
                add("""
                    (
                        ${if (xCost) "c.cost = -2 OR " else ""}
                        (
                            (${
                                when {
                                    max == null -> "c.cost IS NULL"
                                    min == null -> "c.cost IS NULL OR c.cost <= $max"
                                    else -> "c.cost BETWEEN $min AND $max"
                                }
                            }) 
                            ${if (evenCost || oddCost) """
                                AND c.cost % 2 = ${if (evenCost) "0" else "1"}
                            """.trimIndent() else ""}
                        )
                    )
                """.trimIndent())
            }

            skillsFilter.run {
                if (this == defaultFilters.skillsFilter) return@run

                willpower?.let { add("c.skill_willpower >= $it") }
                intellect?.let { add("c.skill_intellect >= $it") }
                combat?.let { add("c.skill_combat >= $it") }
                agility?.let { add("c.skill_agility >= $it") }
                wild?.let { add("c.skill_wild >= $it") }
                any?.let {
                    add("""
                        (
                            c.skill_willpower >= $it
                            OR c.skill_intellect >= $it
                            OR c.skill_combat >= $it
                            OR c.skill_agility >= $it
                            OR c.skill_wild >= $it
                        )
                    """.trimIndent())
                }
            }

            healthSanityFilter.run {
                if (this == defaultFilters.healthSanityFilter) return@run

                health.run {
                    add("""
                        (
                            ${if (includeXHealthOrSanity) "c.health = -2 OR " else ""}
                            (${
                                when {
                                    max == null -> "c.health IS NULL"
                                    min == null -> "c.health IS NULL OR c.health <= $max"
                                    else -> "c.health BETWEEN $min AND $max"
                                } + if (healthPerInvestigator) " AND c.health_per_investigator = 1" else ""
                            })
                        )
                    """.trimIndent())
                }

                sanity.run {
                    add("""
                        (
                            ${if (includeXHealthOrSanity) "c.sanity = -2 OR " else ""}
                            (${
                                when {
                                    max == null -> "c.sanity IS NULL"
                                    min == null -> "c.sanity IS NULL OR c.sanity <= $max"
                                    else -> "c.sanity BETWEEN $min AND $max"
                                }
                            })
                        )
                    """.trimIndent())
                }
            }

            propertiesFilter.run {
                if (this == defaultFilters.propertiesFilter) return@run

                if (customizable) add("c.customization_text IS NOT NULL")
                if (exile) add("c.exile = 1")
                if (exceptional) add("c.exceptional = 1")
                if (multiclass) add("c.faction2_code IS NOT NULL")
                if (myriad) add("c.myriad = 1")
                if (permanent) add("c.permanent = 1")
                if (specialist) add("c.restrictions LIKE '{\"trait\":%'")
                if (unique) add("c.is_unique = 1")
                if (victory) add("c.victory IS NOT NULL")
            }

            enemyFilter.run {
                if (this == defaultFilters.enemyFilter) return@run

                fight.run {
                    add("""
                        (${
                            when {
                                max == null -> "c.enemy_fight IS NULL"
                                min == null -> "c.enemy_fight IS NULL OR c.enemy_fight <= $max"
                                else -> "c.enemy_fight BETWEEN $min AND $max"
                            }
                        })
                    """.trimIndent())
                }

                evade.run {
                    add("""
                        (${
                            when {
                                max == null -> "c.enemy_evade IS NULL"
                                min == null -> "c.enemy_evade IS NULL OR c.enemy_evade <= $max"
                                else -> "c.enemy_evade BETWEEN $min AND $max"
                            }
                        })
                    """.trimIndent())
                }

                damage.run {
                    add("""
                        (${
                            when {
                                max == null -> "c.enemy_damage IS NULL"
                                min == null -> "c.enemy_damage IS NULL OR c.enemy_damage <= $max"
                                else -> "c.enemy_damage BETWEEN $min AND $max"
                            }
                        })
                    """.trimIndent())
                }

                horror.run {
                    add("""
                        (${
                            when {
                                max == null -> "c.enemy_horror IS NULL"
                                min == null -> "c.enemy_horror IS NULL OR c.enemy_horror <= $max"
                                else -> "c.enemy_horror BETWEEN $min AND $max"
                            }
                        })
                    """.trimIndent())
                }

                if (vengeance) add("c.vengeance IS NOT NULL")
            }

            locationFilter.run {
                if (this == defaultFilters.locationFilter) return@run

                shroud.run {
                    add("""
                        (
                            ${if (xShroud) "c.shroud = -2 OR " else ""}
                            (${
                                when {
                                    max == null -> "c.shroud IS NULL"
                                    min == null -> "c.shroud IS NULL OR c.shroud <= $max"
                                    else -> "c.shroud BETWEEN $min AND $max"
                                }
                            })
                        )
                    """.trimIndent())
                }

                clues.run {
                    add("""
                        (${
                            when {
                                max == null -> "c.clues IS NULL"
                                min == null -> "c.clues IS NULL OR c.clues <= $max"
                                else -> "c.clues BETWEEN $min AND $max"
                            } + if (perInvestigatorClues) " AND c.clues_fixed = 0" else ""
                        })
                    """.trimIndent())
                }
            }

            officialFilter?.let { official ->
                if (official) add("c.official = 1")
                else add("c.official = 0")
            }

            if (illustrators.isNotEmpty()) {
                val illustratorsString = illustrators.joinToString(",") { "'$it'" }
                add("(c.illustrator IN ($illustratorsString) OR c.back_illustrator IN ($illustratorsString))")
            }
        }

        return filtersListBuilder.joinToString(" AND ")
    }
}

private data class SqlSearchOptions(
    val sqlQuery: String = "",
    val searchFieldsQuery: String = "",
    val backLinkCardFieldsQuery: String = "",
    val searchFieldsAmount: Int = 0
)