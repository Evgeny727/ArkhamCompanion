package com.arkhamcompanion.ui.cards.components.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.model.cards.CardText
import com.arkhamcompanion.ui.components.ArkhamToggleButton
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.theme.AppIconsFont
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.CardTextStyleResolver
import com.arkhamcompanion.ui.utils.appSp
import kotlin.math.absoluteValue

@Composable
fun CardDetailsTabooBlock(
    tabooXp: Int?,
    tabooOriginalText: CardText?,
    tabooOriginalBackText: CardText?,
    styleResolver: CardTextStyleResolver,
    modifier: Modifier = Modifier,
) {
    var showTabooTextChange by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth().height(IntrinsicSize.Min).animateContentSize(),
    ) {
        VerticalDivider(thickness = 2.dp, color = CustomTheme.colors.taboo)

        Column(
            modifier = modifier.weight(1f).padding(horizontal = 8.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = AppIcon.Tablet.glyph,
                    fontFamily = AppIconsFont,
                    fontSize = 16.appSp(CustomTheme.typography.scaleFactor),
                    color = CustomTheme.colors.taboo
                )
                Text(
                    text = stringResource(R.string.taboo_list),
                    style = CustomTheme.typography.small
                )
            }

            if (tabooXp != null) {
                val state = stringResource(if (tabooXp > 0) R.string.chained else R.string.unchained)
                val value = stringResource(
                    if (tabooXp > 0) R.string.additional_taboo_xp else R.string.xp_discount_taboo,
                    tabooXp.absoluteValue
                )

                Text(
                    text = "$state $value",
                    style = CustomTheme.typography.small
                )
            } else {
                Text(
                    text = stringResource(R.string.mutated),
                    style = CustomTheme.typography.small
                )

                if (tabooOriginalText != null || tabooOriginalBackText != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.clickable { showTabooTextChange = !showTabooTextChange }
                    ) {
                        Text(
                            text = stringResource(R.string.original_card_text),
                            style = CustomTheme.typography.menuText,
                            modifier = Modifier.weight(1f, false)
                        )
                        ArkhamToggleButton(
                            checked = showTabooTextChange,
                            iconGlyph = AppIcon.ExpandMore,
                            size = 22.dp,
                            modifier = Modifier.padding(2.dp)
                        ) { newValue -> showTabooTextChange = newValue }
                    }
                }

                AnimatedVisibility(showTabooTextChange) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        tabooOriginalText?.let {
                            ParsedCardText(it, styleResolver)
                        }

                        tabooOriginalBackText?.let {
                            ParsedCardText(it, styleResolver)
                        }
                    }
                }
            }
        }
    }
}