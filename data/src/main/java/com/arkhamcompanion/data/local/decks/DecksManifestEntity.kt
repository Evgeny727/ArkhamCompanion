package com.arkhamcompanion.data.local.decks

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "decks_manifest")
data class DecksManifestEntity(
    @PrimaryKey
    val version: String,
    @ColumnInfo(name = "arkhamdb_sync_token")
    val arkhamdbSyncToken: String? = null,
    @ColumnInfo(name = "user_id")
    val userId: String
)
