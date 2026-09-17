package com.arkhamcompanion.ui.cards.components.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.ui.components.ArkhamRoundedCardFooter
import com.arkhamcompanion.ui.components.ArkhamRoundedCardFooterItem
import com.arkhamcompanion.ui.components.ArkhamScalableIconText
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.appSp

@Composable
fun CardDetailsFooter(
    official: Boolean,
    onFaq: () -> Unit,
    tabooSetId: String?,
    tabooPlaceholder: Boolean,
    onTaboo: () -> Unit,
    isFavorite: Boolean,
    onFavorite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scaleFactor = CustomTheme.typography.scaleFactor
    val showTaboo = tabooSetId != null || tabooPlaceholder

    ArkhamRoundedCardFooter(modifier = modifier) {
//        if (official) {
//            ArkhamRoundedCardFooterItem(
//                iconGlyph = AppIcon.Wild,
//                iconSize = 20.appSp(scaleFactor),
//                text = stringResource(R.string.faq),
//                onClick = onFaq
//            )
//
//            VerticalDivider(modifier = Modifier.fillMaxHeight(0.6f), color = CustomTheme.colors.d20)
//        }

        if (showTaboo) {
            ArkhamRoundedCardFooterItem(
                iconGlyph = AppIcon.Taboo,
                iconSize = 24.appSp(scaleFactor),
                text = stringResource(R.string.taboo),
                onClick = onTaboo
            )

            VerticalDivider(modifier = Modifier.fillMaxHeight(0.6f), color = CustomTheme.colors.d20)
        }

        Row(
            modifier = Modifier.weight(1f)
                .clickable(onClick = onFavorite)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ArkhamScalableIconText(
                iconGlyph = if (isFavorite) AppIcon.FavoriteFill else AppIcon.FavoriteOutline,
                color = CustomTheme.colors.d20,
                size = 24.appSp(scaleFactor)
            )
        }
    }
}