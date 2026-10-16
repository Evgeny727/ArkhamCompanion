package com.arkhamcompanion.ui.cards.components.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.domain.model.cards.CardText
import com.arkhamcompanion.domain.model.cards.CardTextLink
import com.arkhamcompanion.domain.model.cards.CardTextParagraph
import com.arkhamcompanion.domain.model.cards.CardTextSegment
import com.arkhamcompanion.domain.model.cards.ParagraphAlignment
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.theme.AppIconsFont
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.CardTextStyleResolver

@Composable
fun ParsedCardText(
    text: CardText,
    styleResolver: CardTextStyleResolver,
    modifier: Modifier = Modifier,
    isFlavor: Boolean = false,
    isCustomizationText: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        if (!isFlavor && !isCustomizationText) {
            VerticalDivider(thickness = 2.dp, color = CustomTheme.colors.m)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = if (isFlavor || isCustomizationText) 0.dp else 8.dp,
                    end = 8.dp,
                    top = 4.dp,
                    bottom = 4.dp
                ),
        ) {
            text.paragraphs.forEach { paragraph ->
                ParsedParagraphText(
                    text = text.text,
                    paragraph = paragraph,
                    styleResolver = styleResolver,
                )
            }

        }
    }
}

@Composable
fun ParsedParagraphText(
    text: String,
    paragraph: CardTextParagraph,
    styleResolver: CardTextStyleResolver,
    modifier: Modifier = Modifier,
    baseTextStyleOverride: TextStyle? = null,
    onLinkClick: (CardTextLink) -> Unit = {},
) {
    val paragraphText = remember(text, paragraph, styleResolver) {
        paragraph.toAnnotatedString(text, styleResolver, onLinkClick)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (paragraph.blockQuote) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                VerticalDivider(thickness = 1.dp, color = CustomTheme.colors.m)
                VerticalDivider(thickness = 1.dp, color = CustomTheme.colors.m)
            }
        }

        val baseTextStyle = baseTextStyleOverride ?: CustomTheme.typography.cardText

        Text(
            text = paragraphText,
            textAlign = when (paragraph.alignment) {
                ParagraphAlignment.Start -> TextAlign.Start
                ParagraphAlignment.Center -> TextAlign.Center
                ParagraphAlignment.End -> TextAlign.End
            },
            style = baseTextStyle,
            modifier = Modifier.padding(vertical = if (paragraph.blockQuote) 4.dp else 0.dp)
        )
    }

    if (paragraph.horizontalRule) {
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(thickness = 1.dp, color = CustomTheme.colors.m)
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
internal fun rememberCardTextStyles(flavorText: Boolean): CardTextStyles {
    val colors = CustomTheme.colors
    val typography = CustomTheme.typography

    return remember(colors, typography) {

        CardTextStyles(
            regular = (if (flavorText) typography.italic else typography.regular).toSpanStyle(),
            bold = typography.bold.toSpanStyle(),
            italic = (if (flavorText) typography.regular else typography.italic).toSpanStyle(),
            boldItalic = typography.boldItalic.toSpanStyle(),
            underline = typography.underline,
            strike = typography.strikethrough,
            red = SpanStyle(color = colors.health),
            blue = SpanStyle(color = colors.sanity),
            game = typography.mediumGameFont.copy(color = Color.Unspecified).toSpanStyle(),
            cite = typography.tiny.copy(color = Color.Unspecified).toSpanStyle(),
            icon = SpanStyle(fontFamily = AppIconsFont)
        )

    }
}

internal fun CardTextParagraph.toAnnotatedString(
    text: String,
    styleResolver: CardTextStyleResolver,
    onLinkClick: (CardTextLink) -> Unit = {}
): AnnotatedString {
    return buildAnnotatedString {
        segments.forEach { segment ->
            when (segment) {
                is CardTextSegment.Text -> {
                    val styles = styleResolver.resolve(segment.styleFlags)

                    withStyle(styles) {
                        segment.link?.let { link ->
                            withLink(
                                LinkAnnotation.Clickable(
                                    tag = when (link) {
                                        is CardTextLink.Card -> "card/${link.code}"
                                        is CardTextLink.Rule -> "rule/${link.id}"
                                    },
                                    styles = TextLinkStyles(style = styles),
                                ) { onLinkClick(link) }
                            ) {
                                append(
                                    text,
                                    segment.start,
                                    segment.end,
                                )
                            }
                        } ?: append(
                            text,
                            segment.start,
                            segment.end,
                        )
                    }
                }

                is CardTextSegment.Icon -> {
                    withStyle(styleResolver.getIconStyle()) {
                        val icon = AppIcon.fromNameCode(
                            when (segment.glyph) {
                                "fast" -> "free"
                                else -> segment.glyph
                            }
                        )
                        append(icon?.glyph ?: segment.glyph)
                    }
                }
            }
        }
    }
}