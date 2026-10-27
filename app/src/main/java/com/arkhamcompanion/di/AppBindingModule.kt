package com.arkhamcompanion.di

import com.arkhamcompanion.auth.MainActivityOAuthCallbackTarget
import com.arkhamcompanion.domain.model.auth.OAuthCallbackTargetProvider
import com.arkhamcompanion.domain.repository.AnalyticsRepository
import com.arkhamcompanion.domain.repository.PerformanceRepository
import com.arkhamcompanion.firebase.AnalyticsRepositoryImpl
import com.arkhamcompanion.firebase.PerformanceRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppBindingModule {
    @Binds
    @Singleton
    fun bindAnalyticsRepository(impl: AnalyticsRepositoryImpl): AnalyticsRepository

    @Binds
    @Singleton
    fun bindPerformanceRepository(impl: PerformanceRepositoryImpl): PerformanceRepository

    @Binds
    @Singleton
    fun bindOAuthCallbackTargetProvider(impl: MainActivityOAuthCallbackTarget): OAuthCallbackTargetProvider
}