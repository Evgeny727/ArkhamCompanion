package com.arkhamcompanion.domain.repository

import com.arkhamcompanion.domain.model.auth.ArkhamBuildUser
import com.arkhamcompanion.domain.model.auth.AuthorizationCodeGrant

interface OAuthRepository {
    suspend fun startAuthorization()

    suspend fun completeAuthorization(grant: AuthorizationCodeGrant): Result<Unit>

    suspend fun cancelAuthorization()

    suspend fun getCurrentUser(): Result<ArkhamBuildUser?>

    suspend fun signOut()
}