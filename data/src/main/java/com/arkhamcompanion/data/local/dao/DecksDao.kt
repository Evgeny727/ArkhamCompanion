package com.arkhamcompanion.data.local.dao

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.RawQuery
import androidx.room3.RoomRawQuery
import androidx.room3.Transaction
import androidx.room3.Upsert
import com.arkhamcompanion.data.local.cards.CardEntity
import com.arkhamcompanion.data.local.decks.DeckEntity
import com.arkhamcompanion.data.local.decks.DeckIdWithTimestamp
import com.arkhamcompanion.data.local.decks.DeckListItemEntity
import com.arkhamcompanion.data.local.decks.DecksManifestEntity

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

    @Query("DELETE FROM deck WHERE source IS NULL AND user_id IS NULL")
    suspend fun deleteAllLocalDecks()

    @Query("DELETE FROM deck WHERE source IS NOT NULL AND user_id IS NOT NULL")
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
}