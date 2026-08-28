package com.arkhamcompanion.data.local.dao

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.RawQuery
import androidx.room3.RewriteQueriesToDropUnusedColumns
import androidx.room3.RoomRawQuery
import androidx.room3.Upsert
import com.arkhamcompanion.data.local.cards.CardDetailsEntity
import com.arkhamcompanion.data.local.cards.CardEntity
import com.arkhamcompanion.data.local.cards.CardListItemEntity
import com.arkhamcompanion.data.local.cards.CardSearchResultEntity
import com.arkhamcompanion.data.local.cards.CardSubtypeEntity
import com.arkhamcompanion.data.local.cards.CardTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardsDao {
    @Upsert
    suspend fun upsertAllCards(cards: List<CardEntity>)

    @Upsert
    suspend fun upsertCardTypes(types: List<CardTypeEntity>)

    @Upsert
    suspend fun upsertCardSubtypes(subtypes: List<CardSubtypeEntity>)

    @Query("SELECT EXISTS(SELECT 1 FROM card)")
    suspend fun isExists(): Boolean

    @Query("DELETE FROM card")
    suspend fun deleteAllCards()

    @RawQuery(observedEntities = [CardEntity::class])
    fun getPagedCardsByIds(query: RoomRawQuery): PagingSource<Int, CardListItemEntity>

    @RawQuery(observedEntities = [CardEntity::class])
    fun getSearchedCardCodesRaw(query: RoomRawQuery): Flow<List<CardSearchResultEntity>>

    @RewriteQueriesToDropUnusedColumns
    @Query("""
        WITH selected_taboo AS (
            SELECT CASE
                WHEN :tabooSetId = 100 THEN (SELECT MAX(id) FROM taboo_set)
                ELSE :tabooSetId
            END AS id
        )
        
        SELECT c.*, p.name AS packName, rp.name AS reprintPackName, 
            st.name AS subTypeName, t.name AS typeName, e.name AS encounterName FROM card c 
        JOIN card_type t ON c.type_code = t.code
        LEFT JOIN card_subtype st ON c.subtype_code = st.code
        JOIN pack p ON c.pack_code = p.code
        LEFT JOIN pack rp ON c.reprint_pack_code = rp.code
        LEFT JOIN encounter_set e ON c.encounter_code = e.code
        CROSS JOIN selected_taboo taboo
        WHERE c.code IN (:codes) AND (
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
    """)
    fun getCardsByCodeFlow(codes: Collection<String>, tabooSetId: Int?): Flow<List<CardDetailsEntity>>

    @Query("SELECT * FROM card")
    suspend fun getAllCards(): List<CardEntity>
}