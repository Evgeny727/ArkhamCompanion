package com.arkhamcompanion.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.theme.CustomTheme

@Composable
fun ArkhamButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isOutlined: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    icon: @Composable (Color) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = modifier.clickable(
            interactionSource = interactionSource,
            indication = ripple(color = CustomTheme.colors.l10),
            onClick = onClick
        ),
        color = if (isOutlined) CustomTheme.colors.background else CustomTheme.colors.m,
        shape = CustomTheme.shapes.circle,
        border = if (isOutlined)
            BorderStroke(1.dp, CustomTheme.colors.m)
        else
            null,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box {
                icon(if (isOutlined) CustomTheme.colors.d20 else CustomTheme.colors.l20)
            }

            Text(
                text = title,
                color = if (isOutlined) CustomTheme.colors.d20 else CustomTheme.colors.l30,
                style = CustomTheme.typography.button,
                modifier = Modifier.weight(1f),
                maxLines = maxLines,
                overflow = TextOverflow.MiddleEllipsis
            )
        }
    }
}

@Composable
fun ArkhamButtonSearchIcon(color: Color) {
    ArkhamIconText(
        iconGlyph = AppIcon.Search,
        color = color,
        size = 24.dp
    )
}