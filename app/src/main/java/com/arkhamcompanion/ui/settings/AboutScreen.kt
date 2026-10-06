package com.arkhamcompanion.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.BuildConfig
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.objects.CardTextParser
import com.arkhamcompanion.ui.cards.components.details.rememberCardTextStyles
import com.arkhamcompanion.ui.cards.components.details.toAnnotatedString
import com.arkhamcompanion.ui.settings.components.supportEmail
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.CardTextStyleResolver

const val VISUAL_DESIGNER = "Eugene Sarnetsky"
const val spanishTranslators = "TengounplanAH, Midraed, Alvaro"
const val frenchTranslators = "Alexandre Carpentier, Fabrice2, Aifé"
const val germanTranslators = "Hauke, tjanu"
const val koreanTranslators = "엘케인(elkeinkrad), 푸른이(derornos)"
const val simplifiedChineseTranslators = "Chris 崔家宁"
const val iconAttribution = "\n• 'crate' by Imogen Oh from the Noun Project\n• 'rail' by Angelo Troiano from Noun Project"

@Composable
fun AboutScreen(modifier: Modifier = Modifier) {
    val aboutText = stringResource(R.string.about_app_text, VISUAL_DESIGNER)
    val annotatedAboutText = buildAnnotatedString {
        val emailIndex = aboutText.indexOf(supportEmail)
        append(aboutText.substring(0, emailIndex))

        withLink(
            LinkAnnotation.Url(
                url = "mailto:$supportEmail",
                styles = TextLinkStyles(
                    style = CustomTheme.typography.underline
                )
            )
        ) {
            append(supportEmail)
        }

        append(aboutText.substring(emailIndex + supportEmail.length))
    }
    val styles = rememberCardTextStyles(flavorText = false)
    val styleResolver = remember(styles) { CardTextStyleResolver(styles) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 8.dp,
            start = 8.dp,
            end = 8.dp,
            bottom = 32.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item("app_version_text", "text") {
            Text(
                text = stringResource(R.string.app_version, BuildConfig.VERSION_NAME),
                style = CustomTheme.typography.header
            )
        }

        item("app_rights_text", "text") {
            Text(
                text = annotatedAboutText,
                style = CustomTheme.typography.text
            )
        }

        item("app_translation_text", "text") {
            val text = stringResource(
                R.string.app_translations_text,
                spanishTranslators,
                frenchTranslators,
                germanTranslators,
                koreanTranslators,
                simplifiedChineseTranslators
            )
            val parsedText = CardTextParser.parse(text)

            Column {
                parsedText.paragraphs.forEach { paragraph ->
                    val paragraphText = paragraph.toAnnotatedString(parsedText.text, styleResolver)

                    Text(
                        text = paragraphText,
                        style = CustomTheme.typography.text,
                    )
                }
            }
        }

        item("app_contributions_text", "text") {
            val text = stringResource(
                R.string.app_contributions_text,
                VISUAL_DESIGNER
            )
            val parsedText = CardTextParser.parse(text)

            Column {
                parsedText.paragraphs.forEach { paragraph ->
                    val paragraphText = paragraph.toAnnotatedString(parsedText.text, styleResolver)

                    Text(
                        text = paragraphText,
                        style = CustomTheme.typography.text,
                    )
                }
            }
        }

        item("app_icons_attribution_text", "text") {
            val text = stringResource(
                R.string.app_icons_attribution_text,
                VISUAL_DESIGNER
            ) + iconAttribution
            val parsedText = CardTextParser.parse(text)

            Column {
                parsedText.paragraphs.forEach { paragraph ->
                    val paragraphText = paragraph.toAnnotatedString(parsedText.text, styleResolver)

                    Text(
                        text = paragraphText,
                        style = CustomTheme.typography.text,
                    )
                }
            }
        }
    }
}