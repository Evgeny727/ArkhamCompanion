package com.arkhamcompanion.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.ui.theme.CustomTheme

@ReadOnlyComposable
@Composable
fun getCardRowHeight(): Dp {
    val density = LocalDensity.current
    return with(density) {
        maxOf(
            a = 36.dp,
            b = (47 * CustomTheme.typography.scaleFactor * fontScale).dp
        )
    }
}