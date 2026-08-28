package com.arkhamcompanion.domain.repository

import androidx.paging.PagingData
import com.arkhamcompanion.domain.model.cards.CardDetailsWithRelations
import com.arkhamcompanion.domain.model.cards.CardListItemUiModel
import com.arkhamcompanion.domain.model.cards.CardSearchConfig
import com.arkhamcompanion.domain.model.cards.CardSearchResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface CardsRepository {

    suspend fun downloadAllCards(locale: String, onProgress: (Float) -> Unit): Result<String>

    suspend fun isCardsTableExists(): Boolean

    suspend fun isCardsUpdateAvailable(locale: String, savedTimestamp: String?, forced: Boolean): Result<Boolean>

    suspend fun loadCache(): Boolean

    suspend fun recreateCache(): Boolean

    suspend fun clearCardsDatabase(): Result<Unit>

    fun searchPaginatedCardsFlow(
        ids: List<String>,
        searchConfig: CardSearchConfig
    ): Flow<PagingData<CardListItemUiModel>>

    fun searchCardCodesFlow(
        searchConfig: CardSearchConfig
    ): Flow<ImmutableList<CardSearchResult>>

    fun getCardWithRelationsByCodeFlow(
        code: String,
        tabooSetId: Int?,
    ): Flow<CardDetailsWithRelations>

}