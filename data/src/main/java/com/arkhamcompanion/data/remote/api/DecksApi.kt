package com.arkhamcompanion.data.remote.api

import com.arkhamcompanion.data.remote.dto.DeckBatchRequestDto
import com.arkhamcompanion.data.remote.dto.DeckWriteRequestDto
import com.arkhamcompanion.data.remote.dto.DeckDto
import com.arkhamcompanion.data.remote.dto.DecksDto
import com.arkhamcompanion.data.remote.dto.DecksManifestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface DecksApi {
    @GET("v2/user/decks/manifest")
    suspend fun getDecksManifest(
        @Header("Authorization") authorization: String,
    ): Response<DecksManifestDto>

    @POST("v2/user/decks/batch")
    suspend fun batchDecks(
        @Header("Authorization") authorization: String,
        @Body request: DeckBatchRequestDto
    ): Response<DecksDto>

    @POST("v2/user/decks/{source}")
    suspend fun createDeck(
        @Path("source") source: String = "account",
        @Header("Authorization") authorization: String,
        @Body request: DeckWriteRequestDto
    ): Response<DeckDto>
}