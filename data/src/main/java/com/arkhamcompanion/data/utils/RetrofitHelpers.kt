package com.arkhamcompanion.data.utils

import com.arkhamcompanion.domain.exceptions.ArkhamBuildException
import com.arkhamcompanion.domain.exceptions.UnknownApiException
import com.arkhamcompanion.domain.exceptions.getArkhamBuildErrorByType
import com.arkhamcompanion.domain.exceptions.getRemoteOAuthErrorByType
import okio.IOException
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

internal val String.bearerToken
    get() = "Bearer $this"

internal fun <T : Any> Response<T>.requireBody(): T {
    ensureSuccessful()

    return body() ?: throw ArkhamBuildException.EmptyBody(
        message = "Arkham Build returned an empty response"
    )
}

internal fun Response<*>.ensureSuccessful() {
    if (isSuccessful) return

    val errorPayload = try {
        errorBody()?.string()?.let(::JSONObject)
    } catch (_: JSONException) {
        null
    } catch (_: IOException) {
        null
    }

    val apiError = errorPayload.nonNullField("error")

    val authError = errorPayload.nonNullField("error_description")
    val userError = errorPayload.nonNullField("message")

    val description = authError ?: userError ?: "Arkham.Build request failed (HTTP ${code()})"

    val exception = when {
        authError != null -> getRemoteOAuthErrorByType(apiError, description)
        userError != null -> getArkhamBuildErrorByType(apiError, description)
        else -> null
    }

    exception?.let { throw it }

    throw UnknownApiException(description)
}

private fun JSONObject?.nonNullField(name: String): String? {
    if (this == null || !has(name) || isNull(name)) return null

    return optString(name).takeIf { it != "null" }
}