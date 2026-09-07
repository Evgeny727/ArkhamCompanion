package com.arkhamcompanion.data.local.meta

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "encounter_set")
data class EncounterSetEntity(
    @PrimaryKey val code: String,
    val name: String,
    @ColumnInfo("real_name")
    val realName: String,
)
