package com.arkhamcompanion.data.di

import android.content.Context
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.annotations.ApolloExperimental
import com.apollographql.apollo.api.http.HttpRequest
import com.apollographql.apollo.api.http.HttpResponse
import com.apollographql.apollo.interceptor.RetryOnErrorInterceptor
import com.apollographql.apollo.network.NetworkMonitor
import com.apollographql.apollo.network.http.HttpInterceptor
import com.apollographql.apollo.network.http.HttpInterceptorChain
import com.arkhamcompanion.data.objects.JsonElementAdapter
import com.arkhamcompanion.data.remote.api.CardsFaqApi
import com.arkhamcompanion.type.Jsonb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

const val SERVER_URL = "gapi.arkhamcards.com/v1/graphql"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @OptIn(ApolloExperimental::class)
    @Provides
    @Singleton
    fun provideApolloClient(@ApplicationContext context: Context): ApolloClient = ApolloClient.Builder()
        .serverUrl("https://$SERVER_URL")
        .addHttpInterceptor( object : HttpInterceptor {
            override suspend fun intercept(
                request: HttpRequest,
                chain: HttpInterceptorChain
            ): HttpResponse {
                val token = null // authTokenProvider.getToken()
                val newRequest = if (token.isNullOrBlank()) {
                    request
                } else {
                    request.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                }

                return chain.proceed(newRequest)
            }
        })
        .retryOnErrorInterceptor(RetryOnErrorInterceptor(NetworkMonitor(context)))
        .failFastIfOffline(true)
        .addCustomScalarAdapter(Jsonb.type, JsonElementAdapter)
        .build()

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .callTimeout(60, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, json: Json): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.arkham.build/")
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideCardsFaqApi(retrofit: Retrofit): CardsFaqApi =
        retrofit.create(CardsFaqApi::class.java)

}