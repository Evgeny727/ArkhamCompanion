package com.arkhamcompanion.data.local.rules

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
data class ChapterOneRulesDto(
    val schemaVersion: Int,
    val sections: List<RuleSectionDto>,
)

@Serializable
data class RuleSectionDto(
    val id: String,
    val title: String,
    val level: Int,
    val parentId: String? = null,
    val content: List<RuleContentDto> = emptyList(),
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
sealed class RuleContentDto {

    @Serializable
    @SerialName("text")
    data class Text(
        val text: String,
    ) : RuleContentDto()

    @Serializable
    @SerialName("chart")
    data class Chart(
        val items: List<ChartItemDto>,
        val title: String,
    ) : RuleContentDto()
}

@Serializable
data class ChartItemDto(
    val text: String,
    val color: String,
)