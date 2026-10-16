package com.arkhamcompanion.data.remote.api

import com.arkhamcompanion.data.remote.dto.CardFaqEntryDto
import retrofit2.http.GET
import retrofit2.http.Path

interface CardsFaqApi {
    @GET("v2/public/faq/card/{code}")
    suspend fun getFaqEntries(
        @Path("code") code: String,
    ): List<CardFaqEntryDto>
}