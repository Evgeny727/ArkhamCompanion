package com.arkhamcompanion.data.repository

import android.content.Context
import com.arkhamcompanion.data.local.rules.ChapterOneRulesDto
import com.arkhamcompanion.data.mapper.domain.settings.toDomain
import com.arkhamcompanion.domain.model.settings.RulesSections
import com.arkhamcompanion.domain.repository.AnalyticsRepository
import com.arkhamcompanion.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.IOException
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val analyticsRepository: AnalyticsRepository
) : SettingsRepository {
    private val rulesJson = Json {
        ignoreUnknownKeys = true
    }

    @OptIn(ExperimentalSerializationApi::class)
    private suspend fun parseRules(languageTag: String): ChapterOneRulesDto = withContext(Dispatchers.IO) {
        val file = context.assets.open("rules/rules-$languageTag.json")

        val data: ChapterOneRulesDto =
            file.buffered().use {
                rulesJson.decodeFromStream<ChapterOneRulesDto>(it)
            }

        return@withContext data
    }

    override suspend fun getChapterOneRules(locale: String): RulesSections {
        val rules = try {
            parseRules(locale)
        } catch (e: IOException) {
            analyticsRepository.logMessage("Failed to parse chapter one rules for locale $locale")
            analyticsRepository.logError(e)

            parseRules("en")
        }

        return rules.sections.map { it.toDomain() }.toImmutableList()
            .associateBy { it.id }.toImmutableMap()
    }
}