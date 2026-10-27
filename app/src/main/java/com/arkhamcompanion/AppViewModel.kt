package com.arkhamcompanion

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkhamcompanion.domain.exceptions.ArkhamBuildException
import com.arkhamcompanion.domain.exceptions.OAuthException
import com.arkhamcompanion.domain.model.auth.ArkhamBuildUser
import com.arkhamcompanion.domain.model.auth.ArkhamOAuthCallback
import com.arkhamcompanion.domain.repository.AnalyticsRepository
import com.arkhamcompanion.domain.repository.OAuthRepository
import com.arkhamcompanion.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

data class UiErrorState(val exception: Throwable)

data class AppUserState(
    val arkhamBuildUser: ArkhamBuildUser? = null,
    val exception: Throwable? = null
)

sealed interface AppUiState {
    object Idle: AppUiState
    object OAuthFlowInProcess: AppUiState
    object ArkhamBuildLoading: AppUiState
}

val SUPPORTED_LANGUAGES = setOf("es", "ru", "en", "de", "fr", "pl", "ko", "zh", "zh-cn", "it", "pt", "vi", "uk", "cs")
val AUDIO_LANGUAGES = setOf("es", "ru", "en", "de", "pl")

@HiltViewModel
class AppViewModel @Inject constructor(
    private val cardsSyncManager: CardsSyncManager,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val analyticsRepository: AnalyticsRepository,
    private val oAuthRepository: OAuthRepository
) : ViewModel() {

    private val _userState = MutableStateFlow(AppUserState())
    val userState = _userState.asStateFlow()

    private val _appState = MutableStateFlow<AppUiState>(AppUiState.Idle)
    val appState = _appState.asStateFlow()

    private val _errors = MutableSharedFlow<UiErrorState>(extraBufferCapacity = 1)
    val errors = _errors.asSharedFlow()

    fun emitError(throwable: Throwable) {
        if (throwable is CancellationException) return
        analyticsRepository.logError(throwable)
        _errors.tryEmit(UiErrorState(throwable))
    }

    val cardsSyncState = cardsSyncManager.state
    val cardsCacheState = cardsSyncManager.cacheState

    val scaleFactorState = userPreferencesRepository.scaleFactor.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 1f
    )

    val themeState = userPreferencesRepository.isDarkTheme.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    init {
        observeCardsErrors()
        fetchArkhamBuildUser()
    }

    private fun observeCardsErrors() {
        viewModelScope.launch {
            cardsSyncManager.errors.collect {
                emitError(it)
            }
        }
    }

    private fun fetchArkhamBuildUser() {
        viewModelScope.launch {
            _appState.value = AppUiState.ArkhamBuildLoading

            oAuthRepository.getCurrentUser()
                .fold(
                    onSuccess = { user ->
                        _userState.update {
                            it.copy(
                                arkhamBuildUser = user,
                                exception = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        when (exception) {
                            is OAuthException.InvalidClient,
                            is OAuthException.InvalidGrant,
                            is OAuthException.InvalidTokensStoreData,
                            is OAuthException.UnauthorizedClient,
                            is ArkhamBuildException.AccountBanned -> {
                                _userState.update {
                                    it.copy(
                                        arkhamBuildUser = null,
                                        exception = exception
                                    )
                                }

                                analyticsRepository.logError(exception)
                            }

                            else -> emitError(exception)
                        }
                    }
                )

            _appState.value = AppUiState.Idle
        }
    }

    fun oAuthLogIn() {
        viewModelScope.launch {
            updateAuthLoading(true)

            try {
                // The repository builds the request and opens the browser.
                oAuthRepository.startAuthorization()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                updateAuthLoading(false)
                emitError(error)
            }
        }
    }

    fun onOAuthCallback(callback: ArkhamOAuthCallback) {
        viewModelScope.launch {
            when (callback) {
                is ArkhamOAuthCallback.Authorized -> {
                    updateAuthLoading(true)

                    try {
                        oAuthRepository.completeAuthorization(callback.grant)

                        // Token storage has completed before we load the user.
                        fetchArkhamBuildUser()
                    } catch (cancelled: CancellationException) {
                        throw cancelled
                    } catch (error: Exception) {
                        emitError(error)
                    }
                }

                is ArkhamOAuthCallback.Failed -> {
                    oAuthRepository.cancelAuthorization()
                    updateAuthLoading(false)
                    emitError(
                        RuntimeException("Arkham Build authorization failed. Please try again.")
                    )
                }

                ArkhamOAuthCallback.Cancelled -> {
                    oAuthRepository.cancelAuthorization()
                    updateAuthLoading(false)
                }
            }
        }
    }

    private fun updateAuthLoading(loading: Boolean) {
        if (loading) _appState.value = AppUiState.OAuthFlowInProcess
        else _appState.value = AppUiState.Idle
    }

    fun oAuthLogOut() {
        viewModelScope.launch {
            _appState.value = AppUiState.ArkhamBuildLoading

            oAuthRepository.signOut()

            _userState.value = AppUserState()
            _appState.value = AppUiState.Idle
        }
    }

    fun checkIfCardsReady(language: String) {
        viewModelScope.launch {
            cardsSyncManager.ensureCardsReady(language)
        }
    }

    fun confirmCardsUpdate(language: String) {
        viewModelScope.launch {
            cardsSyncManager.download(language)
        }
    }

    fun cancelCardsUpdate() {
        if (cardsCacheState != CardsCacheState.Loading) {
            viewModelScope.launch {
                cardsSyncManager.loadCache()
            }
        }
    }

    fun updateCardsIfAvailable(language: String) {
        if (cardsSyncState.value !is CardsSyncState.Loading) {
            viewModelScope.launch {
                cardsSyncManager.updateCardsIfUpdateAvailable(language)
            }
        }
    }

    fun updateLocale(locale: String) {
        val appLocale = when (locale) {
            "zh-cn" -> "zh-Hans"
            "zh" -> "zh-Hant"
            else -> locale
        }
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(appLocale))
        viewModelScope.launch {
            cardsSyncManager.download(locale)
        }
    }

    fun resolveLanguageTag(language: String): String {
        return if (language.startsWith("zh-Hans") ||
            language.startsWith("zh-CN") ||
            language.startsWith("zh-SG")) "zh-cn"
        else if (language.startsWith("zh-Hant") ||
            language.startsWith("zh-TW") ||
            language.startsWith("zh-HK") ||
            language.startsWith("zh-MO")) "zh"
        else language.substringBefore("-").takeIf { it in SUPPORTED_LANGUAGES } ?: "en"
    }

    fun recreateCardsCache() {
        if (cardsCacheState.value is CardsCacheState.Loading) return

        viewModelScope.launch {
            cardsSyncManager.recreateCache()
        }
    }

}