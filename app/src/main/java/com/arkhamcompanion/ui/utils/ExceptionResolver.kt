package com.arkhamcompanion.ui.utils

import androidx.annotation.StringRes
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.exceptions.ArkhamBuildException
import com.arkhamcompanion.domain.exceptions.ClearCardsDatabaseException
import com.arkhamcompanion.domain.exceptions.ClearFavoriteCardsException
import com.arkhamcompanion.domain.exceptions.ClearLocalDecksException
import com.arkhamcompanion.domain.exceptions.ClearRemoteDecksException
import com.arkhamcompanion.domain.exceptions.FailedToCheckForUpdateException
import com.arkhamcompanion.domain.exceptions.OAuthException
import com.arkhamcompanion.domain.exceptions.UnableCreateCardsCacheException
import com.arkhamcompanion.domain.exceptions.UnableToLoadCardsCacheException
import com.arkhamcompanion.domain.exceptions.UnknownApiException

@StringRes
internal fun Throwable.resolveExceptionToStringResId(): Int? = when (this) {
    is ClearFavoriteCardsException -> R.string.clear_favorite_cards_error
    is ClearCardsDatabaseException -> R.string.clear_cards_database_error
    is UnableToLoadCardsCacheException -> R.string.load_cards_cache_error
    is UnableCreateCardsCacheException -> R.string.create_cards_cache_error
    is ClearLocalDecksException, is ClearRemoteDecksException -> R.string.clear_decks_error
    is FailedToCheckForUpdateException -> R.string.check_for_update_error
    is UnknownApiException -> R.string.unknown_api_error
    is OAuthException -> when (this) {
        is OAuthException.InvalidRequest -> R.string.invalid_request_error
        is OAuthException.InvalidClient -> R.string.invalid_client_error
        is OAuthException.InvalidGrant -> R.string.invalid_grant_error
        is OAuthException.InvalidScope -> R.string.invalid_scope_error
        is OAuthException.UnauthorizedClient -> R.string.unauthorized_client_error
        is OAuthException.UnsupportedGrantType -> R.string.unsupported_type_error
        is OAuthException.UnsupportedResponseType -> R.string.unsupported_type_error
        is OAuthException.InvalidTokensStoreData -> R.string.invalid_tokens_store_data_error
        is OAuthException.InvalidOAuthTransactionException -> R.string.invalid_token_error
    }
    is ArkhamBuildException -> when (this) {
        is ArkhamBuildException.InvalidToken -> R.string.invalid_token_error
        is ArkhamBuildException.InsufficientScope -> R.string.insufficient_scope_error
        is ArkhamBuildException.AccountBanned -> R.string.account_banned_error
        is ArkhamBuildException.InvalidRequest -> R.string.invalid_user_request_error
        is ArkhamBuildException.NotFound -> R.string.not_found_error
        is ArkhamBuildException.Conflict -> R.string.conflict_error
        is ArkhamBuildException.UpstreamUnavailable -> R.string.upstream_unavailable_error
        is ArkhamBuildException.EmptyBody -> R.string.empty_body_error
    }
    else -> {
        if (this.localizedMessage != null) return null
        R.string.unknown_error
    }
}