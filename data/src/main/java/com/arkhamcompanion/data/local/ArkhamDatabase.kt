package com.arkhamcompanion.data.local

import androidx.room3.ColumnTypeConverters
import androidx.room3.DaoReturnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.paging.PagingSourceDaoReturnTypeConverter
import com.arkhamcompanion.data.local.cards.CardEntity
import com.arkhamcompanion.data.local.cards.CardSubtypeEntity
import com.arkhamcompanion.data.local.cards.CardTypeEntity
import com.arkhamcompanion.data.local.dao.CardsDao
import com.arkhamcompanion.data.local.dao.MetaDao
import com.arkhamcompanion.data.local.meta.CycleEntity
import com.arkhamcompanion.data.local.meta.EncounterSetEntity
import com.arkhamcompanion.data.local.meta.FactionEntity
import com.arkhamcompanion.data.local.meta.PackEntity
import com.arkhamcompanion.data.local.meta.TabooSetEntity
import com.arkhamcompanion.data.objects.JsonElementConverter

@Database(entities = [CardEntity::class, CycleEntity::class, PackEntity::class, EncounterSetEntity::class, TabooSetEntity::class,
    FactionEntity::class, CardTypeEntity::class, CardSubtypeEntity::class],
    version = 2,
    exportSchema = false)
@ColumnTypeConverters(JsonElementConverter::class)
@DaoReturnTypeConverters(PagingSourceDaoReturnTypeConverter::class)
abstract class ArkhamDatabase : RoomDatabase() {
    abstract fun cardsDao(): CardsDao
    abstract fun metaDao(): MetaDao
}