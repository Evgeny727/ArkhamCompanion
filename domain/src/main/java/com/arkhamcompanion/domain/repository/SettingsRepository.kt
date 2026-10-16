package com.arkhamcompanion.domain.repository

import com.arkhamcompanion.domain.model.settings.RulesSections

interface SettingsRepository {
    suspend fun getChapterOneRules(locale: String): RulesSections
}