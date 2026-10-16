package com.arkhamcompanion.data.mapper.domain.settings

import com.arkhamcompanion.data.local.rules.ChartItemDto
import com.arkhamcompanion.data.local.rules.RuleContentDto
import com.arkhamcompanion.data.local.rules.RuleSectionDto
import com.arkhamcompanion.domain.model.settings.ChapterOneRulesSection
import com.arkhamcompanion.domain.model.settings.ChartItem
import com.arkhamcompanion.domain.model.settings.ChartItemColor
import com.arkhamcompanion.domain.model.settings.RuleContent
import com.arkhamcompanion.domain.objects.CardTextParser
import kotlinx.collections.immutable.toImmutableList

internal fun RuleSectionDto.toDomain(): ChapterOneRulesSection = ChapterOneRulesSection(
    id = id,
    title = CardTextParser.parse(title),
    level = level,
    parentId = parentId,
    content = content.map { it.toDomain() }.toImmutableList()
)

private fun RuleContentDto.toDomain(): RuleContent = when (this) {
    is RuleContentDto.Text -> RuleContent.Text(CardTextParser.parse(text))
    is RuleContentDto.Chart -> RuleContent.Chart(
        items = items.map { it.toDomain() }.toImmutableList(),
        title = title
    )
}

private fun ChartItemDto.toDomain(): ChartItem = ChartItem(
    text = CardTextParser.parse(text),
    color = when (color) {
        "red" -> ChartItemColor.RED
        "green" -> ChartItemColor.GREEN
        else -> ChartItemColor.GRAY
    }
)