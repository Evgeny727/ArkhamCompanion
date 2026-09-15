package com.arkhamcompanion.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.ui.icons.IconGlyph
import com.arkhamcompanion.ui.theme.CustomTheme

@Composable
fun ArkhamRoundedCardFooter(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
        color = CustomTheme.colors.l10,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .height(IntrinsicSize.Max),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
        }
    }
}

@Composable
fun RowScope.ArkhamRoundedCardFooterItem(
    iconGlyph: IconGlyph,
    iconSize: TextUnit,
    text: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.weight(1f)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ArkhamScalableIconText(
            iconGlyph = iconGlyph,
            color = CustomTheme.colors.d20,
            size = iconSize
        )

        Text(
            text = text,
            color = CustomTheme.colors.d20,
            style = CustomTheme.typography.button,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}