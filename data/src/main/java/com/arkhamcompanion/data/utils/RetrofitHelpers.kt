package com.arkhamcompanion.data.utils

import com.arkhamcompanion.domain.exceptions.ArkhamBuildException
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

    val oauthError = errorPayload.nonBlankField("error")

    val description = errorPayload
        .nonBlankField("error_description")
        ?: errorPayload.nonBlankField("message")
        ?: "Arkham.Build request failed (HTTP ${code()})"

    val exception = getRemoteOAuthErrorByType(oauthError, description) ?:
    getArkhamBuildErrorByType(oauthError, description)

    exception?.let { throw it }

    throw IllegalStateException("Arkham.Build request failed (HTTP ${code()})")
}

private fun JSONObject?.nonBlankField(name: String): String? {
    val value = this?.optString(name) ?: return null

    return value.takeIf {
        it.isNotBlank() && it != "null"
    }
}