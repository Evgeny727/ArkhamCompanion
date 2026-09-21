package com.arkhamcompanion.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.ui.components.ArkhamIconText
import com.arkhamcompanion.ui.icons.IconGlyph
import com.arkhamcompanion.ui.theme.CustomTheme

@Composable
fun ArkhamTopAppBar(
    title: String,
    subtitle: String?,
    color: Color,
    contentColor: Color,
    leftAction: @Composable ((Color) -> Unit)?,
    rightActions: @Composable (RowScope.(Color) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp),
        color = color,
        shadowElevation = 4.dp
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            leftAction?.invoke(contentColor)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = contentColor,
                    style = CustomTheme.typography.header,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                subtitle?.let {
                    Text(
                        text = subtitle,
                        color = contentColor,
                        style = CustomTheme.typography.run { small + italic },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (rightActions != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    rightActions(contentColor)
                }
            }
        }
    }
}

@Composable
fun ArkhamAppBarAction(
    contentColor: Color,
    onClick: () -> Unit,
    iconGlyph: IconGlyph? = null,
    text: String? = null,
    isActive: Boolean = false,
) {
    if (iconGlyph != null) {
        Box {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CustomTheme.shapes.circle)
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                ArkhamIconText(
                    iconGlyph = iconGlyph,
                    size = 28.dp,
                    color = contentColor
                )
            }

            if (isActive) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .align(Alignment.TopEnd)
                        .background(
                            color = CustomTheme.colors.faction.rogue.border,
                            shape = CustomTheme.shapes.circle
                        )
                )
            }
        }
    } else if (text != null) {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .clip(CustomTheme.shapes.circle)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = CustomTheme.typography.text,
                color = contentColor,
            )
        }
    }
}