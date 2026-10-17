package com.arkhamcompanion.ui.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.model.cards.CardTextLink
import com.arkhamcompanion.ui.cards.components.details.ParsedParagraphText
import com.arkhamcompanion.ui.cards.components.details.rememberCardTextStyles
import com.arkhamcompanion.ui.components.ArkhamIconText
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.CardTextStyleResolver

@Composable
fun CardFaqScreen(
    cardCode: String,
    cardFaqViewModel: CardFaqViewModel,
    onCardLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val faqEntries by cardFaqViewModel.getCardFaq(cardCode).collectAsState(initial = null)

    val styles = rememberCardTextStyles(flavorText = false)
    val styleResolver = remember(styles) {
        CardTextStyleResolver(styles)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        faqEntries?.let { entries ->
            if (entries.isEmpty()) {
                item("no_results", contentType = "text") {
                    Text(
                        text =  stringResource(R.string.no_entries_at_this_time),
                        style = CustomTheme.typography.text,
                        modifier = Modifier.animateItem()
                    )
                }
            } else {
                entries.forEach { entry ->
                    item("question_${entry.id}", contentType = "text") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ArkhamIconText(
                                iconGlyph = AppIcon.Bullet,
                                size = 24.dp,
                                color = CustomTheme.colors.darkText,
                            )

                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                entry.question.paragraphs.forEach { paragraph ->
                                    ParsedParagraphText(
                                        text = entry.question.text,
                                        paragraph = paragraph,
                                        styleResolver = styleResolver,
                                        onLinkClick = { link ->
                                            if (link is CardTextLink.Card) {
                                                onCardLinkClick(link.code)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    item("ruling_${entry.id}", contentType = "text") {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(start = 32.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            entry.ruling.paragraphs.forEach { paragraph ->
                                ParsedParagraphText(
                                    text = entry.ruling.text,
                                    paragraph = paragraph,
                                    styleResolver = styleResolver,
                                    onLinkClick = { link ->
                                        if (link is CardTextLink.Card) {
                                            onCardLinkClick(link.code)
                                        }
                                    }
                                )
                            }
                        }
                    }

                    item("citation_${entry.id}", contentType = "text") {
                        Row(
                            modifier = Modifier.padding(start = 32.dp)
                                .background(
                                    color = CustomTheme.colors.l15,
                                    shape = CustomTheme.shapes.medium
                                ),
                        ) {
                            Text(
                                text = entry.citation,
                                style = CustomTheme.typography.cardText,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        } ?: item("loading", contentType = "text") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = CustomTheme.colors.m
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.checking_for_faq),
                    style = CustomTheme.typography.text,
                )
            }
        }
    }
}