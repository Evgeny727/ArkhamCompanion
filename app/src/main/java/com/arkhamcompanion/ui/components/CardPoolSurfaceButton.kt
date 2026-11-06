package com.arkhamcompanion.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.enums.DeckEnvironment
import com.arkhamcompanion.domain.model.decks.ResolvedEnvironment
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.icons.PackIcon
import com.arkhamcompanion.ui.theme.LocalLanguage

@Composable
fun CardPoolSurfaceButton(
    resolvedEnvironment: ResolvedEnvironment,
    onClick: () -> Unit
) {
    val languageTag = LocalLanguage.current.languageTag

    ArkhamSurfaceButton(
        title = stringResource(R.string.card_pool),
        icon = AppIcon.Deck,
        valueLabel = when (resolvedEnvironment.type) {
            DeckEnvironment.Current -> {
                AnnotatedString(
                    stringResource(R.string.current) + " среда".takeIf { languageTag == "ru" }.orEmpty()
                )
            }
            DeckEnvironment.Limited -> buildAnnotatedString {
                append(
                    stringResource(R.string.limited) + " среда".takeIf { languageTag == "ru" }.orEmpty()
                )
                append(" ")

                resolvedEnvironment.expansionCodes.forEach { code ->
                    val icon = PackIcon.fromPackCode(code) ?: return@forEach
                    withStyle(
                        style = SpanStyle(
                            fontFamily = icon.fontFamily,
                            letterSpacing = 2.sp
                        )
                    ) {
                        append(icon.glyph)
                    }
                }
            }

            DeckEnvironment.Legacy -> {
                AnnotatedString(
                    stringResource(R.string.legacy) + " среда".takeIf { languageTag == "ru" }.orEmpty()
                )
            }
            DeckEnvironment.Custom -> AnnotatedString(stringResource(R.string.custom))
        },
        onClick = onClick,
    )
}