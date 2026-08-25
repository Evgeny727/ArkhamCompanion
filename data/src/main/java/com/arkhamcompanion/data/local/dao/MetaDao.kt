package com.arkhamcompanion.data.local.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.RewriteQueriesToDropUnusedColumns
import androidx.room3.Upsert
import com.arkhamcompanion.data.local.cards.CardSubtypeEntity
import com.arkhamcompanion.data.local.cards.CardTypeEntity
import com.arkhamcompanion.data.local.meta.CycleEntity
import com.arkhamcompanion.data.local.meta.EncounterSetEntity
import com.arkhamcompanion.data.local.meta.FactionEntity
import com.arkhamcompanion.data.local.meta.FullPackEntity
import com.arkhamcompanion.data.local.meta.PackEntity
import com.arkhamcompanion.data.local.meta.TabooSetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MetaDao {
    @Upsert
    suspend fun upsertCycles(cycles: List<CycleEntity>)

    @Upsert
    suspend fun upsertPacks(packs: List<PackEntity>)

    @Upsert
    suspend fun upsertEncounterSets(encounterSets: List<EncounterSetEntity>)

    @Upsert
    suspend fun upsertTabooSets(tabooSets: List<TabooSetEntity>)

    @Upsert
    suspend fun upsertFactions(factions: List<FactionEntity>)

    @Query("DELETE FROM cycle")
    suspend fun deleteAllCycles()

    @Query("DELETE FROM pack")
    suspend fun deleteAllPacks()

    @Query("DELETE FROM encounter_set")
    suspend fun deleteAllEncounterSets()

    @Query("DELETE FROM taboo_set")
    suspend fun deleteAllTabooSets()

    @Query("DELETE FROM faction")
    suspend fun deleteAllFactions()

    suspend fun deleteAll() {
        deleteAllPacks()
        deleteAllCycles()
        deleteAllEncounterSets()
        deleteAllTabooSets()
        deleteAllFactions()
    }

    @Query("SELECT * FROM taboo_set ORDER BY date DESC")
    fun getTaboos(): Flow<List<TabooSetEntity>>

    @RewriteQueriesToDropUnusedColumns
    @Query("""
        SELECT p.*, c.position as cyclePosition, c.name AS cycleName 
        FROM pack p JOIN cycle c ON p.cycle_code = c.code 
        ORDER BY p.chapter DESC, c.position, p.reprint DESC, p.position
    """)
    fun getAllPacks(): Flow<List<FullPackEntity>>

    @Query("SELECT * FROM faction")
    fun getAllFactions(): Flow<List<FactionEntity>>

    @Query("SELECT * FROM card_type")
    fun getAllTypes(): Flow<List<CardTypeEntity>>

    @Query("SELECT * FROM card_subtype")
    fun getAllSubTypes(): Flow<List<CardSubtypeEntity>>

    @Query("SELECT * FROM encounter_set")
    fun getAllEncounterSets(): Flow<List<EncounterSetEntity>>

    @Query("""
        SELECT illustrator
        FROM card
        WHERE illustrator IS NOT NULL
    
        UNION
    
        SELECT back_illustrator
        FROM card
        WHERE back_illustrator IS NOT NULL
        
        ORDER BY illustrator COLLATE NOCASE
    """)
    fun getAllIllustrators(): Flow<List<String>>
}
