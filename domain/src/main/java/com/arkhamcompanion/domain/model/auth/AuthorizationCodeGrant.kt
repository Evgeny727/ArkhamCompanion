package com.arkhamcompanion.domain.model.auth

data class AuthorizationCodeGrant(
    val code: String,
    val codeVerifier: String,
    val state: String
)
