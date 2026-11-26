package com.arkhamcompanion.data.local.dao

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.RawQuery
import androidx.room3.RewriteQueriesToDropUnusedColumns
import androidx.room3.RoomRawQuery
import androidx.room3.Transaction
import androidx.room3.Upsert
import com.arkhamcompanion.data.local.cards.CardEntity
import com.arkhamcompanion.data.local.decks.DeckDetailsEntity
import com.arkhamcompanion.data.local.decks.DeckEntity
import com.arkhamcompanion.data.local.decks.DeckIdWithTimestamp
import com.arkhamcompanion.data.local.decks.DeckListItemEntity
import com.arkhamcompanion.data.local.decks.DecksManifestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DecksDao {

    @Query("SELECT * FROM decks_manifest where user_id = :userId")
    suspend fun getDecksManifest(userId: String): DecksManifestEntity?

    @Query("DELETE FROM decks_manifest")
    suspend fun deleteAllDecksManifests()

    @Insert
    suspend fun insertDecksManifest(manifest: DecksManifestEntity)

    @Transaction
    suspend fun replaceDecksManifest(manifest: DecksManifestEntity) {
        deleteAllDecksManifests()
        insertDecksManifest(manifest)
    }

    @Query("DELETE FROM deck WHERE source IS NULL")
    suspend fun deleteAllLocalDecks()

    @Query("DELETE FROM deck WHERE source IS NOT NULL")
    suspend fun deleteAllRemoteDecks()

    @Transaction
    suspend fun deleteAllRemoteInfo() {
        deleteAllDecksManifests()
        deleteAllRemoteDecks()
    }

    @Query("DELETE FROM deck WHERE id = :id")
    suspend fun deleteDeckById(id: String)

    @Query("DELETE FROM deck WHERE id IN (:ids)")
    suspend fun deleteDecksByIds(ids: Collection<String>)

    @Upsert
    suspend fun upsertDeck(deck: DeckEntity)

    @Upsert
    suspend fun upsertRemoteDecks(decks: List<DeckEntity>)

    @Query("""
        SELECT id FROM deck WHERE source IS NOT NULL 
        AND (:clearArkhamdbDecks OR source != 'arkhamdb')
    """)
    suspend fun getAllRemoteDeckIds(clearArkhamdbDecks: Boolean): List<String>

    @Transaction
    suspend fun syncRemoteDecks(remoteIds: Set<String>, clearArkhamdbDecks: Boolean) {
        val localDecksIds = getAllRemoteDeckIds(clearArkhamdbDecks)

        val idsToDelete = localDecksIds - remoteIds
        deleteDecksByIds(idsToDelete)
    }

    @Query("SELECT id, date_update, source FROM deck WHERE source IS NOT NULL")
    suspend fun getAllRemoteDeckIdsWithTimestamps(): List<DeckIdWithTimestamp>

    @RawQuery(observedEntities = [DeckEntity::class, CardEntity::class])
    fun searchPaginatedDecks(query: RoomRawQuery): PagingSource<Int, DeckListItemEntity>

    @Query("SELECT tags FROM deck WHERE next_deck IS NULL")
    fun getAllTags(): Flow<List<String>>

    companion object {
        const val DECK_TOTAL_XP_QUERY: String = """
            -- Split extra_deck into individual card codes
            extra_deck_parts AS (
                SELECT
                    id AS deckId,
                    TRIM(
                        SUBSTR(extra_deck || ',', 1,
                            INSTR(extra_deck || ',', ',') - 1)
                    ) AS cardCode,
                    SUBSTR(
                        extra_deck || ',',
                        INSTR(extra_deck || ',', ',') + 1
                    ) AS remaining
                FROM current
                WHERE extra_deck IS NOT NULL
                  AND TRIM(extra_deck) != ''
        
                UNION ALL
        
                SELECT
                    deckId,
                    TRIM(SUBSTR(remaining, 1, INSTR(remaining, ',') - 1)),
                    SUBSTR(remaining, INSTR(remaining, ',') + 1)
                FROM extra_deck_parts
                WHERE remaining != ''
            ),
        
            -- Extract cards from slots and extra_deck
            deck_cards AS (
                SELECT
                    d.id AS deckId,
                    j.key AS cardCode,
                    CAST(j.value AS INTEGER) AS quantity
                FROM current d,
                     json_each(d.slots) j
        
                UNION ALL
        
                SELECT
                    deckId,
                    cardCode,
                    1
                FROM extra_deck_parts
                WHERE cardCode != ''
            ),
        
            -- Combine quantities of identical card codes
            card_counts AS (
                SELECT
                    deckId,
                    cardCode,
                    SUM(quantity) AS quantity
                FROM deck_cards
                GROUP BY deckId, cardCode
            ),
        
            -- Find the appropriate taboo version of every card
            card_candidates AS (
                SELECT
                    *,
                    ROW_NUMBER() OVER (
                        PARTITION BY deckId, cardCode
                        ORDER BY sourceRank, tabooRank
                    ) AS versionRank
                FROM (
                    -- Official cards
                    SELECT
                        cc.deckId,
                        cc.cardCode,
                        cc.quantity,
            
                        c.real_name,
                        COALESCE(c.myriad, 0) AS myriad,
            
                        COALESCE(c.xp, 0) *
                            CASE WHEN c.exceptional = 1 THEN 2 ELSE 1 END
                            + COALESCE(c.taboo_xp, 0) AS unitXp,
            
                        1 AS sourceRank,
            
                        CASE
                            WHEN c.taboo_set_id = d.taboo_id THEN 0
                            ELSE 1
                        END AS tabooRank
            
                    FROM card_counts cc
            
                    INNER JOIN current d
                        ON d.id = cc.deckId
            
                    INNER JOIN card c
                        ON c.code = cc.cardCode
                        AND (
                            c.taboo_set_id = d.taboo_id
                            OR c.taboo_set_id IS NULL
                        )
            
                    UNION ALL
            
                    -- Fan-made cards (only when no official version exists)
                    SELECT
                        cc.deckId,
                        cc.cardCode,
                        cc.quantity,
            
                        json_extract(fc.value, '$.real_name'),
            
                        COALESCE(
                            CAST(COALESCE(json_extract(fc.value, '$.myriad'), 0) AS INTEGER),
                            0
                        ),
            
                        COALESCE(
                            CAST(json_extract(fc.value, '$.xp') AS INTEGER),
                            0
                        ) *
                        CASE
                            WHEN CAST(COALESCE(json_extract(fc.value, '$.exceptional'), 0) AS INTEGER) = 1
                                THEN 2
                            ELSE 1
                        END
                        + COALESCE(
                            CAST(json_extract(fc.value, '$.taboo_xp') AS INTEGER),
                            0
                        ) AS unitXp,
            
                        0 AS sourceRank,
                        0 AS tabooRank
            
                    FROM card_counts cc
            
                    INNER JOIN current d
                        ON d.id = cc.deckId
            
                    INNER JOIN json_each(
                        COALESCE(d.meta, '{}'),
                        '$.fan_made_content.cards'
                    ) fc ON fc.key = cc.cardCode
                )
            ),
        
            -- Keep only the preferred card version
            card_costs AS (
                SELECT *
                FROM card_candidates
                WHERE versionRank = 1
            ),
        
            -- Deduplicate Myriad cards
            ranked_cards AS (
                SELECT
                    *,
                    ROW_NUMBER() OVER (
                        PARTITION BY
                            deckId,
                            CASE
                                WHEN myriad = 1
                                    THEN 'M:' || COALESCE(real_name, cardCode)
                                ELSE 'C:' || cardCode
                            END
                        ORDER BY cardCode
                    ) AS rn
                FROM card_costs
            ),
        
            -- Calculate XP for each deck
            deck_xp AS (
                SELECT
                    deckId,
                    SUM(
                        CASE
                            WHEN myriad = 1 THEN
                                CASE WHEN rn = 1 THEN unitXp ELSE 0 END
                            ELSE
                                unitXp * quantity
                        END
                    ) AS totalXp
                FROM ranked_cards
                GROUP BY deckId
            ),
            
            -- Split customisable card entries from meta
            custom_xp_parts AS (
                SELECT
                    d.id AS deckId,
                    TRIM(
                        SUBSTR(
                            CAST(j.value AS TEXT) || ',',
                            1,
                            INSTR(CAST(j.value AS TEXT) || ',', ',') - 1
                        )
                    ) AS entry,
                    SUBSTR(
                        CAST(j.value AS TEXT) || ',',
                        INSTR(CAST(j.value AS TEXT) || ',', ',') + 1
                    ) AS remaining
                FROM current d,
                     json_each(COALESCE(d.meta, '{}')) j
                WHERE SUBSTR(j.key, 1, 4) = 'cus_'
                  AND j.value IS NOT NULL
                  AND TRIM(CAST(j.value AS TEXT)) != ''
        
                UNION ALL
        
                SELECT
                    deckId,
                    TRIM(SUBSTR(remaining, 1, INSTR(remaining, ',') - 1)),
                    SUBSTR(remaining, INSTR(remaining, ',') + 1)
                FROM custom_xp_parts
                WHERE remaining != ''
            ),
        
            -- Calculate XP spent on customisable cards
            custom_deck_xp AS (
                SELECT
                    deckId,
                    SUM(
                        CAST(
                            SUBSTR(
                                entry,
                                INSTR(entry, '|') + 1,
                                INSTR(
                                    SUBSTR(entry, INSTR(entry, '|') + 1) || '|',
                                    '|'
                                ) - 1
                            ) AS INTEGER
                        )
                    ) AS totalCustomXp
                FROM custom_xp_parts
                WHERE INSTR(entry, '|') > 0
                GROUP BY deckId
            )
        """
    }
}