package com.arkhamcompanion

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.intl.Locale
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.arkhamcompanion.domain.model.auth.ArkhamOAuthCallback
import com.arkhamcompanion.domain.model.auth.ArkhamOAuthCallbackActions
import com.arkhamcompanion.domain.model.auth.AuthorizationCodeGrant
import com.arkhamcompanion.ui.navigation.ArkhamNavHost
import com.arkhamcompanion.ui.theme.ArkhamCompanionTheme
import com.arkhamcompanion.ui.theme.CustomTheme
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse

val LocalThemeIsDark = compositionLocalOf { false }

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: AppViewModel
    private var appUpdateManager: AppUpdateManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        var isDataLoaded = false
        splashScreen.setKeepOnScreenCondition { !isDataLoaded }
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        handleOAuthCallback(intent)

        val isPlayServicesAvailable = GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(this)
        if (isPlayServicesAvailable == ConnectionResult.SUCCESS) maybeCheckForUpdate()

        setContent {
            viewModel = hiltViewModel<AppViewModel>(this)
            val lang = Locale.current.toLanguageTag()
            val langTag = viewModel.resolveLanguageTag(lang)
            val scaleFactor by viewModel.scaleFactorState.collectAsState()
            val theme by viewModel.themeState.collectAsState()
            // Collecting user's theme from shared preferences via viewmodel - false = light, true = dark
            val currentTheme = when(theme) {
                0 -> false
                1 -> true
                2 -> isSystemInDarkTheme()
                else -> null
            }
            if (currentTheme != null) {
                isDataLoaded = true
                ArkhamCompanionTheme(currentTheme, langTag, scaleFactor) {
                    CompositionLocalProvider(LocalThemeIsDark provides currentTheme) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = CustomTheme.colors.background
                        ) {
                            ArkhamNavHost(viewModel)
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleOAuthCallback(intent)
    }

    private fun handleOAuthCallback(intent: Intent?) {
        when (intent?.action) {
            ArkhamOAuthCallbackActions.CANCELLED -> {
                viewModel.onOAuthCallback(ArkhamOAuthCallback.Cancelled)
            }

            ArkhamOAuthCallbackActions.COMPLETED -> {
                val exception = AuthorizationException.fromIntent(intent)

                val response = AuthorizationResponse.fromIntent(intent)

                val callback = when {
                    exception != null -> {
                        ArkhamOAuthCallback.Failed(errorCode = exception.error)
                    }

                    response == null -> {
                        ArkhamOAuthCallback.Failed(errorCode = "missing_response")
                    }

                    response.state != response.request.state -> {
                        ArkhamOAuthCallback.Failed(errorCode = "state_mismatch")
                    }

                    response.authorizationCode.isNullOrBlank() ||
                            response.request.codeVerifier.isNullOrBlank() ||
                            response.state.isNullOrBlank() -> {
                        ArkhamOAuthCallback.Failed(errorCode = "incomplete_response")
                    }

                    else -> {
                        ArkhamOAuthCallback.Authorized(
                            grant = AuthorizationCodeGrant(
                                code = response.authorizationCode!!,
                                codeVerifier = response.request.codeVerifier!!,
                                state = response.state!!,
                            )
                        )
                    }
                }

                viewModel.onOAuthCallback(callback)
            }

            else -> return
        }

        /*
         * Don't keep a processed authorization result on the Activity's
         * current Intent. This avoids handling the same callback again
         * after ordinary Activity recreation.
         */
        intent.action = null
        intent.replaceExtras(Bundle())
        intent.data = null
    }

    private fun maybeCheckForUpdate() {
        val manager = runCatching { AppUpdateManagerFactory.create(this) }.getOrNull()
            ?: return

        appUpdateManager = manager

        manager.appUpdateInfo
            .addOnSuccessListener { info ->
                val available = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                val allowed = info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                val stalenessDays = info.clientVersionStalenessDays() ?: -1

                val shouldForceUpdate = available && allowed //&& stalenessDays >= 14 TODO: uncomment at release
                if (shouldForceUpdate) {
                    manager.startUpdateFlow(
                        info,
                        this,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    )
                }
            }
    }

    // Checks that the update is not stalled during 'onResume()'.
    override fun onResume() {
        super.onResume()

        val manager = appUpdateManager ?: return

        manager.appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    // If an in-app update is already running, resume the update.
                    manager.startUpdateFlow(
                        appUpdateInfo,
                        this,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    )
                }
            }
    }
}