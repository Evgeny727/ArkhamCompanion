package com.arkhamcompanion.domain.repository

import androidx.paging.PagingData
import com.arkhamcompanion.domain.model.cards.CardDetailsWithRelations
import com.arkhamcompanion.domain.model.cards.CardInvestigatorAccessFields
import com.arkhamcompanion.domain.model.cards.CardListItem
import com.arkhamcompanion.domain.model.cards.CardListItemUiModel
import com.arkhamcompanion.domain.model.cards.CardSearchConfig
import com.arkhamcompanion.domain.model.cards.CardSearchResult
import com.arkhamcompanion.domain.model.cards.CardTabooInfo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.coroutines.flow.Flow

interface CardsRepository {

    suspend fun downloadAllCards(locale: String, onProgress: (Float) -> Unit): Result<String>

    suspend fun isCardsTableExists(): Boolean

    suspend fun isCardsUpdateAvailable(locale: String, savedTimestamp: String?, forced: Boolean): Result<Boolean>

    suspend fun loadCache(): Boolean

    suspend fun recreateCache(): Boolean

    suspend fun clearCardsDatabase(): Result<Unit>

    suspend fun clearFavoriteCards(): Result<Unit>

    fun searchPaginatedCardsFlow(
        ids: List<String>,
        searchConfig: CardSearchConfig
    ): Flow<PagingData<CardListItemUiModel>>

    fun searchCardCodesFlow(
        searchConfig: CardSearchConfig
    ): Flow<CardSearchResult>

    fun getCardWithRelationsByCodeFlow(
        code: String,
        tabooSetId: Int?,
    ): Flow<CardDetailsWithRelations>

    suspend fun addFavorite(code: String)

    suspend fun removeFavorite(code: String)

    fun observeFavoriteCodes(): Flow<ImmutableSet<String>>

    fun getCardTabooHistoryByCodeFlow(code: String): Flow<ImmutableList<CardTabooInfo>>

    fun getAllInvestigatorsByName(name: String): Flow<PagingData<CardListItem>>

    fun getAllPlayableCardsByName(name: String): Flow<PagingData<CardListItem>>
    suspend fun getInitialCardFields(cardId: String): CardInvestigatorAccessFields
}