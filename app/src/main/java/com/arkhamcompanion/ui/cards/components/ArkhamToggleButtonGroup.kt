package com.arkhamcompanion.ui.cards.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.ui.theme.CustomTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun <T> ArkhamToggleButtonGroup(
    values: ImmutableSet<T>,
    selectedValues: ImmutableSet<T>,
    onValueToggle: (T) -> Unit,
    content: @Composable BoxScope.(T, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(CustomTheme.shapes.circle)
            .border(
                width = 1.dp,
                color = CustomTheme.colors.divider,
                shape = CustomTheme.shapes.circle
            )
            .height(IntrinsicSize.Min),
    ) {
        values.forEach { value ->
            val selected = value in selectedValues

            Segment(
                selected = selected,
                onClick = { onValueToggle(value) },
                modifier = Modifier.weight(1f),
            ) {
                content(value, selected)
            }

            if (value != values.lastOrNull()) {
                VerticalDivider(
                    modifier = Modifier.fillMaxHeight(),
                    color = CustomTheme.colors.divider,
                )
            }
        }
    }
}

@Composable
fun <T> ArkhamSingleToggleButtonGroup(
    values: ImmutableList<T>,
    selectedValue: T?,
    onValueToggle: (T) -> Unit,
    content: @Composable (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(CustomTheme.shapes.circle)
            .border(
                width = 1.dp,
                color = CustomTheme.colors.divider,
                shape = CustomTheme.shapes.circle
            )
            .height(IntrinsicSize.Min),
    ) {
        values.forEach { value ->
            Segment(
                selected = value == selectedValue,
                onClick = { onValueToggle(value) },
                modifier = Modifier.weight(1f),
            ) {
                content(value)
            }

            if (value != values.lastOrNull()) {
                VerticalDivider(
                    modifier = Modifier.fillMaxHeight(),
                    color = CustomTheme.colors.divider,
                )
            }
        }
    }
}

@Composable
private fun Segment(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .heightIn(min = 48.dp)
            .background(color = if (selected) CustomTheme.colors.l15 else Color.Unspecified)
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}