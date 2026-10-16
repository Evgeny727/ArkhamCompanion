package com.arkhamcompanion.ui.settings.rules

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.objects.CardTextParser
import com.arkhamcompanion.ui.cards.components.details.ParsedParagraphText
import com.arkhamcompanion.ui.cards.components.details.rememberCardTextStyles
import com.arkhamcompanion.ui.components.ArkhamSearchBox
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.CardTextStyleResolver

@Composable
fun RulesScreen(
    rulesViewModel: RulesViewModel,
    navigateToSection: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sections by rulesViewModel.sections.collectAsState()
    val rulesUiState by rulesViewModel.rulesUiState.collectAsState()
    val query by rulesViewModel.searchQuery.collectAsState()

    val styles = rememberCardTextStyles(flavorText = false)
    val styleResolver = remember(styles) { CardTextStyleResolver(styles) }

    val introText = stringResource(R.string.introduction_to_rules)
    val parsedIntroduction = remember(introText) {
        CardTextParser.parse(introText)
    }

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CustomTheme.colors.l20)
                .animateContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ArkhamSearchBox(
                searchQuery = query,
                onQueryChange = rulesViewModel::updateSearchQuery,
                onClearQuery = rulesViewModel::clearSearchQuery,
                searchPlaceholder = stringResource(R.string.search_rules)
            )

            AnimatedVisibility(rulesUiState is RulesUiState.Loading) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CustomTheme.shapes.circle)
                        .background(CustomTheme.colors.darkText),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        color = CustomTheme.colors.background
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            if (query.isBlank()) {
                item("introduction_to_rules", "text") {
                    ParsedParagraphText(
                        text = parsedIntroduction.text,
                        paragraph = parsedIntroduction.paragraphs.first(), //safe cause text can't contain empty paragraphs
                        styleResolver = styleResolver,
                        modifier = Modifier.padding(8.dp),
                    )

                    HorizontalDivider(color = CustomTheme.colors.divider)
                }
            }

            items(sections, key = { it.id }, contentType = { "section" }) { section ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navigateToSection(section.id) }
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ParsedParagraphText(
                        text = section.title.text,
                        paragraph = section.title.paragraphs.first(), //safe cause text can't contain empty paragraphs
                        styleResolver = styleResolver,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        baseTextStyleOverride = CustomTheme.typography.mediumGameFont
                    )

                    HorizontalDivider(color = CustomTheme.colors.divider)
                }
            }
        }
    }
}