package com.arkhamcompanion.domain.model.settings

import com.arkhamcompanion.domain.model.cards.CardText
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

typealias RulesSections = ImmutableMap<String, ChapterOneRulesSection>

data class ChapterOneRulesSection(
    val id: String,
    val title: CardText,
    val level: Int,
    val parentId: String?,
    val content: ImmutableList<RuleContent>
)

sealed class RuleContent {

    data class Text(val text: CardText) : RuleContent()

    data class Chart(
        val items: ImmutableList<ChartItem>,
        val title: String,
    ) : RuleContent()
}

data class ChartItem(
    val text: CardText,
    val color: ChartItemColor,
)

enum class ChartItemColor {
    RED, GREEN, GRAY
}

fun RulesSections.search(query: String): List<ChapterOneRulesSection> {
    return values.filter { section ->
        section.title.text.contains(query, ignoreCase = true) ||
                section.content.any { content ->
                    content.search(query)
                }
    }
}

private fun RuleContent.search(query: String): Boolean {
    return when (this) {
        is RuleContent.Text -> text.text.contains(query, ignoreCase = true)
        is RuleContent.Chart -> {
            title.contains(query, ignoreCase = true) || items.any { item ->
                item.text.text.contains(query, ignoreCase = true)
            }
        }
    }
}

fun RulesSections.findTopLevelRule(id: String): ChapterOneRulesSection? {
    var current = get(id)

    while (current != null && current.parentId != null) {
        current = get(current.parentId)
    }

    return current
}