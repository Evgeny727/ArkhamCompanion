package com.arkhamcompanion.data.remote

import com.apollographql.apollo.ApolloClient
import com.arkhamcompanion.GetCardFaqQuery
import com.arkhamcompanion.GetCardsUpdatedAtQuery
import com.arkhamcompanion.GetEncounterCardsQuery
import com.arkhamcompanion.GetPlayerCardsQuery
import com.arkhamcompanion.GetTranslationDataQuery
import javax.inject.Inject

const val CARDS_SCHEMA_VERSION = 9

class CardsRemoteDataSource @Inject constructor(
    private val apolloClient: ApolloClient
) {
    suspend fun fetchAllTranslationData(locale: String) = apolloClient
        .query(GetTranslationDataQuery(locale))
        .execute()

    suspend fun fetchAllPlayerCards(locale: String) = apolloClient
        .query(GetPlayerCardsQuery(locale, CARDS_SCHEMA_VERSION))
        .execute()

    suspend fun fetchAllEncounterCards(locale: String) = apolloClient
        .query(GetEncounterCardsQuery(locale, CARDS_SCHEMA_VERSION))
        .execute()

    suspend fun fetchCardsUpdatedAt(locale: String) = apolloClient
        .query(GetCardsUpdatedAtQuery(locale, CARDS_SCHEMA_VERSION))
        .execute()

    suspend fun fetchCardFaqByCode(code: String) = apolloClient
        .query(GetCardFaqQuery(code))
        .execute()
}
