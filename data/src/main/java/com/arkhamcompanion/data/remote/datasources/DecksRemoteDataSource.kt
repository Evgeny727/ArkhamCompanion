package com.arkhamcompanion.data.remote.datasources

import com.arkhamcompanion.data.remote.api.DecksApi
import com.arkhamcompanion.data.remote.dto.DeckBatchRequestDto
import com.arkhamcompanion.data.remote.dto.DeckDto
import com.arkhamcompanion.data.remote.dto.DecksManifestDto
import com.arkhamcompanion.data.utils.bearerToken
import com.arkhamcompanion.data.utils.requireBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DecksRemoteDataSource @Inject constructor(
    private val decksApi: DecksApi
) {
    suspend fun fetchDecksManifest(accessToken: String): DecksManifestDto =
        decksApi.getDecksManifest(accessToken.bearerToken).requireBody()

    suspend fun fetchDecksBatch(accessToken: String, request: DeckBatchRequestDto): List<DeckDto> =
        decksApi.batchDecks(accessToken.bearerToken, request).requireBody().decks
}