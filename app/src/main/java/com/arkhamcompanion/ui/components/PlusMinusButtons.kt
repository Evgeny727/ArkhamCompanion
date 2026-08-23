package com.arkhamcompanion.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.domain.model.cards.NullableIntRange
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.theme.CustomTheme

@Composable
fun PlusMinusButtons(
    value: Int?,
    range: NullableIntRange,
    onIncrement: (Int) -> Unit,
    onDecrement: (Int) -> Unit,
    modifier: Modifier = Modifier,
    color: PlusMinusButtonsColor = PlusMinusButtonsColor.Default,
    rounded: Boolean = false,
    disabled: Boolean = false,
    disablePlus: Boolean = false,
    hideDisabledMinus: Boolean = false,
    showMin: Boolean = true,
    showMax: Boolean = true,
    withSymbol: Boolean = false,
    isLarge: Boolean = false,
    showCounter: Boolean = true,
    nullText: String? = null,
    overrideTextStyle: TextStyle? = null,
    content: @Composable () -> Unit = {}
) {
    val (min, max) = range
    val palette = buttonColors(color, dialogStyle = true, rounded)
    val incrementEnabled = !(disabled || disablePlus || max == null) && value != max
    val decrementEnabled = !disabled && value != min
    val showValue = (showMin || value != min) && (showMax || value != max) && showCounter

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (decrementEnabled
            || (color != PlusMinusButtonsColor.Light && !hideDisabledMinus)
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        if (rounded) CustomTheme.shapes.circle
                        else CustomTheme.shapes.small
                    )
                    .background(
                        if (rounded && decrementEnabled) palette.rounded else Color.Unspecified
                    )
                    .alpha(if (decrementEnabled) 1f else 0.3f)
                    .clickable(enabled = decrementEnabled) {
                        onDecrement((value ?: 0) - 1)
                    },
                contentAlignment = Alignment.Center
            ) {
                ArkhamIconText(
                    iconGlyph = AppIcon.MinusButton,
                    size = if (rounded || isLarge) 36.dp else 28.dp,
                    color = if (decrementEnabled) palette.enabled else CustomTheme.colors.m,
                    modifier = Modifier.padding(2.dp)
                )
            }
        }

        if (showValue) {
            Text(
                text = when {
                    value == null -> nullText.toString()
                    withSymbol && value >= 0 -> "+$value"
                    else -> value.toString()
                },
                style = overrideTextStyle ?:
                    if (rounded) CustomTheme.typography.counter else CustomTheme.typography.menuText
            )

            content()
        }

        if (incrementEnabled
            || (color != PlusMinusButtonsColor.Light && color != PlusMinusButtonsColor.White)
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        if (rounded) CustomTheme.shapes.circle
                        else CustomTheme.shapes.small
                    )
                    .background(
                        if (rounded && incrementEnabled) palette.rounded else Color.Unspecified
                    )
                    .alpha(if (incrementEnabled) 1f else 0.3f)
                    .clickable(enabled = incrementEnabled) {
                        onIncrement((value ?: -1) + 1)
                    },
                contentAlignment = Alignment.Center
            ) {
                ArkhamIconText(
                    iconGlyph = AppIcon.PlusButton,
                    size = if (rounded || isLarge) 36.dp else 28.dp,
                    color = if (incrementEnabled) palette.enabled else CustomTheme.colors.m,
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
    }
}

@Composable
fun PlusMinusButtonsBoxed(
    value: Int?,
    range: NullableIntRange,
    onIncrement: (Int?) -> Unit,
    onDecrement: (Int?) -> Unit,
    modifier: Modifier = Modifier,
    color: PlusMinusButtonsColor = PlusMinusButtonsColor.Default,
    disabled: Boolean = false,
    disablePlus: Boolean = false,
    hideDisabledMinus: Boolean = false,
) {
    val (min, max) = range
    val palette = buttonColors(color, dialogStyle = false, rounded = false)
    val incrementEnabled = !(disabled || disablePlus || max == null) && value != max
    val decrementEnabled = !disabled && value != min

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (decrementEnabled
            || (color != PlusMinusButtonsColor.Light && !hideDisabledMinus)
        ) {
            Box(
                modifier = Modifier.clip(CustomTheme.shapes.small)
                    .clickable(enabled = decrementEnabled) {
                        onDecrement((value ?: 0) - 1)
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier.size(36.dp)
                        .background(palette.rounded)
                        .border(
                            if (!decrementEnabled) 2.dp else 0.dp,
                            palette.disabled
                        )
                        .background(
                            if (decrementEnabled) palette.enabled else Color.Unspecified
                        )
                )

                ArkhamIconText(
                    iconGlyph = AppIcon.MinusButton,
                    size = 28.dp,
                    color = if (decrementEnabled) CustomTheme.colors.background else palette.disabled,
                )
            }
        }

        if (incrementEnabled
            || (color != PlusMinusButtonsColor.Light && color != PlusMinusButtonsColor.White)
        ) {
            Box(
                modifier = Modifier.clip(CustomTheme.shapes.small)
                    .clickable(enabled = incrementEnabled) {
                        onIncrement((value ?: -1) + 1)
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier.size(36.dp)
                        .background(palette.rounded)
                        .border(
                            if (!incrementEnabled) 2.dp else 0.dp,
                            palette.disabled
                        )
                        .background(
                            if (incrementEnabled) palette.enabled else Color.Unspecified
                        )
                )

                ArkhamIconText(
                    iconGlyph = AppIcon.PlusButton,
                    size = 28.dp,
                    color = if (incrementEnabled) CustomTheme.colors.background else palette.disabled,
                )
            }
        }
    }
}

enum class PlusMinusButtonsColor {
    Light, Dark, White, Default
}

@Immutable
private data class ArkhamPlusMinusButtonColors(
    val enabled: Color,
    val rounded: Color,
    val disabled: Color,
)

@Composable
private fun buttonColors(
    type: PlusMinusButtonsColor,
    dialogStyle: Boolean = false,
    rounded: Boolean = false
): ArkhamPlusMinusButtonColors =
    when (type) {
        PlusMinusButtonsColor.Default -> ArkhamPlusMinusButtonColors(
            enabled = if (dialogStyle && rounded) CustomTheme.colors.d10
                else if (dialogStyle) CustomTheme.colors.m
                else CustomTheme.colors.lightText,
            rounded = CustomTheme.colors.l15,
            disabled = CustomTheme.colors.m
        )
        PlusMinusButtonsColor.Light -> ArkhamPlusMinusButtonColors(
            enabled = if (dialogStyle && rounded) CustomTheme.colors.l30
                else if (dialogStyle) CustomTheme.colors.m
                else CustomTheme.colors.background,
            rounded = Color(39485240),
            disabled = CustomTheme.colors.lightText
        )
        PlusMinusButtonsColor.Dark -> ArkhamPlusMinusButtonColors(
            enabled = if (dialogStyle && rounded) CustomTheme.colors.d10
                else if (dialogStyle) CustomTheme.colors.m
                else CustomTheme.colors.darkText,
            rounded = CustomTheme.colors.l15,
            disabled = CustomTheme.colors.lightText
        )
        PlusMinusButtonsColor.White -> ArkhamPlusMinusButtonColors(
            enabled = if (dialogStyle && rounded) CustomTheme.colors.red
                else if (dialogStyle) CustomTheme.colors.m
                else Color.White,
            rounded = CustomTheme.colors.l15,
            disabled = Color.White
        )
    }