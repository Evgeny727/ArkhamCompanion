package com.arkhamcompanion.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.ui.icons.IconGlyph
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.appSp

@Composable
fun ArkhamCheckboxButton(
    title: String,
    modifier: Modifier = Modifier,
    iconGlyph: IconGlyph? = null,
    description: String? = null,
    loading: Boolean = false,
    isSelected: Boolean = false,
    enabled: Boolean = true,
    isRadio: Boolean = false,
    isPackRow: Boolean = false,
    isRegularText: Boolean = false,
    onValueChange: (Boolean) -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(CustomTheme.shapes.medium)
            .toggleable(
                value = isSelected,
                enabled = enabled && !loading,
                onValueChange = onValueChange
            ),
        shape = CustomTheme.shapes.large,
        color = Color.Transparent,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (iconGlyph != null) Crossfade(loading) {
                Box(
                    modifier = Modifier.size(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (it) CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = CustomTheme.colors.lightText
                    )
                    else ArkhamIconText(
                        iconGlyph = iconGlyph,
                        color = if (isPackRow) CustomTheme.colors.darkText else CustomTheme.colors.m,
                        size = iconSize(iconGlyph)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = CustomTheme.typography.large,
                    fontWeight = if (isRegularText) FontWeight.Normal else null,
                    fontSize = if (isPackRow) 16.appSp(CustomTheme.typography.scaleFactor) else TextUnit.Unspecified,
                )

                description?.let {
                    Text(
                        text = it,
                        style = CustomTheme.typography.run { smallLabel + italic },
                        color = CustomTheme.colors.darkText
                    )
                }
            }
            ArkhamCheckCircle(
                value = isSelected,
                enabled = enabled,
                isRadio = isRadio,
                onValueChange = onValueChange
            )
        }
    }
}