package com.arkhamcompanion.di

import com.arkhamcompanion.data.repository.CardsRepositoryImpl
import com.arkhamcompanion.data.repository.MetaRepositoryImpl
import com.arkhamcompanion.data.repository.SettingsRepositoryImpl
import com.arkhamcompanion.data.repository.UserPreferencesRepositoryImpl
import com.arkhamcompanion.domain.repository.CardsRepository
import com.arkhamcompanion.domain.repository.MetaRepository
import com.arkhamcompanion.domain.repository.SettingsRepository
import com.arkhamcompanion.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryBindModule {
    @Binds
    fun bindCardsRepository(impl: CardsRepositoryImpl): CardsRepository

    @Binds
    fun bindUserPreferencesRepository(impl: UserPreferencesRepositoryImpl): UserPreferencesRepository

    @Binds
    fun bindMetaRepository(impl: MetaRepositoryImpl): MetaRepository

    @Binds
    fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}