package com.arkhamcompanion.ui.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.model.cards.CardText
import com.arkhamcompanion.ui.cards.components.details.ParsedParagraphText
import com.arkhamcompanion.ui.cards.components.details.rememberCardTextStyles
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.CardTextStyleResolver
import kotlinx.collections.immutable.persistentListOf

@Composable
fun CardFaqScreen(
    cardCode: String,
    cardFaqViewModel: CardFaqViewModel,
    onCardLinkClick: (String) -> Unit,
    emitError: (Throwable) -> Unit,
    modifier: Modifier = Modifier,
) {
    val faqText by cardFaqViewModel.getCardFaq(cardCode)
        .collectAsState(initial = CardText("", persistentListOf()))

    val styles = rememberCardTextStyles(flavorText = false)
    val styleResolver = remember(styles) {
        CardTextStyleResolver(styles)
    }

    LaunchedEffect(Unit) {
        cardFaqViewModel.errors.collect {
            emitError(it.exception)
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (faqText?.text?.isBlank() == true) {
            item("loading", contentType = "text") {
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
        } else {
            if (faqText == null) {
                item("no_results", contentType = "text") {
                    Text(
                        text =  stringResource(R.string.no_entries_at_this_time),
                        style = CustomTheme.typography.text,
                        modifier = Modifier.animateItem()
                    )
                }
            } else {
                itemsIndexed(
                    items = faqText!!.paragraphs,
                    key = { index, _ -> index },
                    contentType = { _, _ -> "paragraph" }
                ) { _, item ->
                    ParsedParagraphText(
                        text = faqText!!.text,
                        paragraph = item,
                        styleResolver = styleResolver,
                        onCardLink = onCardLinkClick
                    )
                }
            }
        }
    }
}