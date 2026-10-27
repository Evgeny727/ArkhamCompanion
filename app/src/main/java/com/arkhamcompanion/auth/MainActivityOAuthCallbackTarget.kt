package com.arkhamcompanion.auth

import com.arkhamcompanion.MainActivity
import com.arkhamcompanion.domain.model.auth.OAuthCallbackTargetProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainActivityOAuthCallbackTarget @Inject constructor() : OAuthCallbackTargetProvider {
    override fun activityClassName(): String = MainActivity::class.java.name
}