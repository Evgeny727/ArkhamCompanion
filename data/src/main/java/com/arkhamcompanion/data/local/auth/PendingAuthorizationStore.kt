package com.arkhamcompanion.data.local.auth

interface PendingAuthorizationStore {
    suspend fun saveRequest(serializedRequest: String)
    suspend fun readRequest(): String?
    suspend fun clearRequest()
}