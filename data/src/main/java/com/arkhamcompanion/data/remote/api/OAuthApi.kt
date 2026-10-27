package com.arkhamcompanion.data.remote.api

import com.arkhamcompanion.data.remote.dto.OAuthProfileResponseDto
import com.arkhamcompanion.data.remote.dto.OAuthTokenResponseDto
import com.arkhamcompanion.data.objects.ArkhamBuildOAuthConfig
import com.arkhamcompanion.data.objects.ArkhamBuildOAuthConfig.REVOKE_ENDPOINT
import com.arkhamcompanion.data.objects.ArkhamBuildOAuthConfig.TOKEN_ENDPOINT
import com.arkhamcompanion.data.objects.ArkhamBuildOAuthConfig.USER_ENDPOINT
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface OAuthApi {
    @FormUrlEncoded
    @POST(TOKEN_ENDPOINT)
    suspend fun exchangeAuthorizationCode(
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("client_id") clientId: String = ArkhamBuildOAuthConfig.clientId,
        @Field("client_secret") clientSecret: String = ArkhamBuildOAuthConfig.clientSecret,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String = ArkhamBuildOAuthConfig.redirectUri,
        @Field("code_verifier") codeVerifier: String,
    ): Response<OAuthTokenResponseDto>

    @FormUrlEncoded
    @POST(TOKEN_ENDPOINT)
    suspend fun refreshAccessToken(
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("client_id") clientId: String = ArkhamBuildOAuthConfig.clientId,
        @Field("client_secret") clientSecret: String = ArkhamBuildOAuthConfig.clientSecret,
        @Field("refresh_token") refreshToken: String,
    ): Response<OAuthTokenResponseDto>

    @FormUrlEncoded
    @POST(REVOKE_ENDPOINT)
    suspend fun revokeRefreshToken(
        @Field("client_id") clientId: String = ArkhamBuildOAuthConfig.clientId,
        @Field("client_secret") clientSecret: String = ArkhamBuildOAuthConfig.clientSecret,
        @Field("token") refreshToken: String,
        @Field("token_type_hint") tokenTypeHint: String? = "refresh_token",
    ): Response<Unit>

    @GET(USER_ENDPOINT)
    suspend fun getCurrentUser(
        @Header("Authorization") authorization: String,
    ): Response<OAuthProfileResponseDto>
}