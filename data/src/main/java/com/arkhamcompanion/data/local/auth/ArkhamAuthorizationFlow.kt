package com.arkhamcompanion.data.local.auth

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.net.toUri
import com.arkhamcompanion.data.objects.ArkhamBuildOAuthConfig
import com.arkhamcompanion.data.objects.ArkhamBuildOAuthConfig.API_BASE_URL
import com.arkhamcompanion.data.objects.ArkhamBuildOAuthConfig.AUTHORIZATION_ENDPOINT
import com.arkhamcompanion.data.objects.ArkhamBuildOAuthConfig.TOKEN_ENDPOINT
import com.arkhamcompanion.domain.model.auth.ArkhamOAuthCallbackActions
import com.arkhamcompanion.domain.model.auth.OAuthCallbackTargetProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArkhamAuthorizationFlow @Inject constructor(
    @ApplicationContext private val context: Context,
    private val callbackTargetProvider: OAuthCallbackTargetProvider,
    private val pendingAuthorizationStore: PendingAuthorizationStore,
) {

    suspend fun startAuthorization() =
        withContext(Dispatchers.Main.immediate) {
            val configuration = AuthorizationServiceConfiguration(
                (API_BASE_URL + AUTHORIZATION_ENDPOINT).toUri(),
                (API_BASE_URL + TOKEN_ENDPOINT).toUri(),
            )

            val request = AuthorizationRequest.Builder(
                configuration,
                ArkhamBuildOAuthConfig.clientId,
                ResponseTypeValues.CODE,
                ArkhamBuildOAuthConfig.redirectUri.toUri(),
            )
                .setScope(ArkhamBuildOAuthConfig.SCOPES)
                .build()

            check(!request.state.isNullOrBlank()) {
                "AppAuth did not generate OAuth state"
            }
            check(!request.codeVerifier.isNullOrBlank()) {
                "AppAuth did not generate a PKCE verifier"
            }

            // Persist before opening the browser. This is also used later
            // to verify that the callback belongs to this auth attempt.
            pendingAuthorizationStore.saveRequest(
                request.jsonSerializeString()
            )

            val completePendingIntent = callbackPendingIntent(
                action = ArkhamOAuthCallbackActions.COMPLETED,
                requestCode = 4101,
            )

            val cancelPendingIntent = callbackPendingIntent(
                action = ArkhamOAuthCallbackActions.CANCELLED,
                requestCode = 4102,
            )

            val service = AuthorizationService(context)

            try {
                service.performAuthorizationRequest(
                    request,
                    completePendingIntent,
                    cancelPendingIntent,
                )
            } catch (error: Exception) {
                pendingAuthorizationStore.clearRequest()
                throw error
            } finally {
                service.dispose()
            }
        }

    private fun callbackPendingIntent(
        action: String,
        requestCode: Int,
    ): PendingIntent {
        val callbackIntent = Intent().apply {
            component = ComponentName(
                context.packageName,
                callbackTargetProvider.activityClassName(),
            )
            this.action = action

            // Reuse the existing MainActivity when possible.
            addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
        }

        val mutableFlag =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_MUTABLE
            } else {
                0
            }

        return PendingIntent.getActivity(
            context,
            requestCode,
            callbackIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or mutableFlag,
        )
    }
}