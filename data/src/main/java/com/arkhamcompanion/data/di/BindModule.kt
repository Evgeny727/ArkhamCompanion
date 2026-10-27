package com.arkhamcompanion.data.di

import com.arkhamcompanion.data.local.auth.OAuthTokenStore
import com.arkhamcompanion.data.local.auth.OAuthTokenStoreImpl
import com.arkhamcompanion.data.local.auth.PendingAuthorizationStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface BindModule {
    @Binds
    @Singleton
    fun bindOAuthTokenStore(impl: OAuthTokenStoreImpl): OAuthTokenStore

    @Binds
    @Singleton
    fun bindPendingAuthorizationStore(impl: OAuthTokenStoreImpl): PendingAuthorizationStore
}