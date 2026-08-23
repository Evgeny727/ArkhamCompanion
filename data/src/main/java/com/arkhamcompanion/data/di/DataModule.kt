package com.arkhamcompanion.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.arkhamcompanion.data.local.ArkhamDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("arkhamv2_preferences")
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ArkhamDatabase {
        return Room.databaseBuilder<ArkhamDatabase>(context, name = "arkham_database")
            .setDriver(BundledSQLiteDriver())
            .build()
    }

    @Provides
    @Singleton
    fun provideMetaDao(db: ArkhamDatabase) = db.metaDao()

}