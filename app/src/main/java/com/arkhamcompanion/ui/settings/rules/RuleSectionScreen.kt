package com.arkhamcompanion.ui.settings.rules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.domain.model.cards.CardTextLink
import com.arkhamcompanion.domain.model.settings.ChapterOneRulesSection
import com.arkhamcompanion.domain.model.settings.ChartItemColor
import com.arkhamcompanion.domain.model.settings.RuleContent
import com.arkhamcompanion.ui.cards.components.details.ParsedParagraphText
import com.arkhamcompanion.ui.cards.components.details.rememberCardTextStyles
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.CardTextStyleResolver
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toPersistentList

@Composable
fun RuleSectionScreen(
    rulesViewModel: RulesViewModel,
    section: ChapterOneRulesSection,
    scrollToId: String?,
    navigateToSection: (String) -> Unit,
    navigateToCard: (String) -> Unit,
) {
    val childSections by rulesViewModel.getChildSections(section.id).collectAsState(persistentListOf())

    val preparedSections = remember(section, childSections) {
        (listOf(section) + childSections).map { currentSection ->
            currentSection to prepareParagraphIndents(currentSection)
        }
    }

    val sectionTitleIndices = remember(preparedSections) {
        buildSectionTitleIndices(preparedSections.map { it.first })
    }

    val listState = rememberLazyListState()

    LaunchedEffect(scrollToId, sectionTitleIndices) {
        val index = scrollToId?.let(sectionTitleIndices::get)

        if (index != null) {
            listState.animateScrollToItem(index)
        }
    }

    val styles = rememberCardTextStyles(flavorText = false)
    val styleResolver = remember(styles) { CardTextStyleResolver(styles) }

    val titleTextStyleOverride = CustomTheme.typography.mediumGameFont.copy(
        fontWeight = FontWeight.Medium
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        preparedSections.forEach { (currentSection, paragraphIndents) ->
            ruleSection(
                section = currentSection,
                paragraphIndents = paragraphIndents,
                styleResolver = styleResolver,
                titleTextStyleOverride = titleTextStyleOverride,
                navigateToSection = navigateToSection,
                navigateToCard = navigateToCard
            )
        }
    }
}

private enum class ParagraphIndent {
    None,
    Bullet,
    NestedBullet
}

private fun prepareParagraphIndents(
    section: ChapterOneRulesSection
): ImmutableList<ImmutableList<ParagraphIndent>?> =
    section.content.map { content ->
        val textContent = content as? RuleContent.Text
            ?: return@map null

        val text = textContent.text.text
        val paragraphs = textContent.text.paragraphs

        paragraphs.mapIndexed { index, paragraph ->
            val isBullet = text.startsWith("[bullet]", paragraph.start)
            val isGuideBullet =
                text.startsWith("[guide_bullet]", paragraph.start)

            val previousIsGuideBullet = index > 0 &&
                    text.startsWith(
                        "[guide_bullet]",
                        paragraphs[index - 1].start
                    )

            when {
                isBullet && previousIsGuideBullet -> ParagraphIndent.NestedBullet
                isBullet || isGuideBullet -> ParagraphIndent.Bullet
                else -> ParagraphIndent.None
            }
        }.toPersistentList()
    }.toPersistentList()

private fun buildSectionTitleIndices(
    sections: List<ChapterOneRulesSection>
): ImmutableMap<String, Int> = buildMap {
    var itemIndex = 0

    sections.forEach { section ->
        // Title item
        put(section.id, itemIndex)
        itemIndex++

        section.content.forEach { content ->
            when (content) {
                is RuleContent.Text -> {
                    // One lazy item per paragraph
                    itemIndex += content.text.paragraphs.size
                }

                is RuleContent.Chart -> {
                    // One lazy item for the entire chart
                    itemIndex++
                }
            }
        }
    }
}.toImmutableMap()

private fun LazyListScope.ruleSection(
    section: ChapterOneRulesSection,
    paragraphIndents: ImmutableList<ImmutableList<ParagraphIndent>?>,
    styleResolver: CardTextStyleResolver,
    titleTextStyleOverride: TextStyle,
    navigateToSection: (String) -> Unit,
    navigateToCard: (String) -> Unit,
) {
    item(key = "${section.id}_title", contentType = "title") {
        ParsedParagraphText(
            text = section.title.text,
            paragraph = section.title.paragraphs.first(), //safe cause text can't contain empty paragraphs
            styleResolver = styleResolver,
            baseTextStyleOverride = titleTextStyleOverride
        )
    }

    section.content.forEachIndexed { contentIndex, content ->
        when (content) {
            is RuleContent.Text -> {
                itemsIndexed(
                    content.text.paragraphs,
                    key = { _, paragraph ->
                        "${section.id}.content_${contentIndex}.paragraph_${paragraph.start}"
                    },
                    contentType = { _, _ -> "paragraph" }
                ) { index, paragraph ->
                    val indent = paragraphIndents[contentIndex]?.get(index)
                        ?: ParagraphIndent.None

                    val modifier = when (indent) {
                        ParagraphIndent.None -> Modifier
                        ParagraphIndent.Bullet -> Modifier.padding(start = 8.dp)
                        ParagraphIndent.NestedBullet -> Modifier.padding(start = 16.dp)
                    }

                    ParsedParagraphText(
                        text = content.text.text,
                        paragraph = paragraph,
                        styleResolver = styleResolver,
                        modifier = modifier
                    ) { link ->
                        when (link) {
                            is CardTextLink.Card -> navigateToCard(link.code)
                            is CardTextLink.Rule -> navigateToSection(link.id)
                        }
                    }
                }
            }

            is RuleContent.Chart -> {
                item(key = "${section.id}.content_${contentIndex}_chart", contentType = "chart") {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Text(
                                text = content.title,
                                textAlign = TextAlign.Center,
                                style = CustomTheme.typography.cardText,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                            )
                        }

                        content.items.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .background(color = when (item.color) {
                                        ChartItemColor.RED -> CustomTheme.colors.faction.survivor.lightBackground
                                        ChartItemColor.GREEN -> CustomTheme.colors.faction.rogue.lightBackground
                                        ChartItemColor.GRAY -> CustomTheme.colors.faction.neutral.lightBackground
                                    })
                                    .padding(vertical = 8.dp)
                            ) {
                                ParsedParagraphText(
                                    text = item.text.text,
                                    paragraph = item.text.paragraphs.first(), //chart items has only one paragraph
                                    styleResolver = styleResolver,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}