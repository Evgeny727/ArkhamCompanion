package com.arkhamcompanion.ui.cards.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.model.cards.NullableIntRange
import com.arkhamcompanion.ui.components.ArkhamCheckCircle
import com.arkhamcompanion.ui.components.ArkhamIconText
import com.arkhamcompanion.ui.components.ArkhamToggleButton
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.theme.CustomTheme
import kotlin.math.roundToInt

@Composable
fun CollapsableFiltersSection(
    label: String,
    isNotCollapsed: Boolean,
    onCollapseChange: () -> Unit,
    onSectionClear: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.animateContentSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .clickable(onClick = onCollapseChange)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = CustomTheme.typography.text,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            AnimatedVisibility(isNotCollapsed) {
                Surface(
                    modifier = Modifier.clip(CustomTheme.shapes.circle)
                        .clickable(onClick = onSectionClear),
                    color = CustomTheme.colors.l10
                ) {
                    ArkhamIconText(
                        iconGlyph = AppIcon.Trash,
                        size = 24.dp,
                        color = CustomTheme.colors.d10,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }

            ArkhamToggleButton(
                checked = isNotCollapsed,
                iconGlyph = AppIcon.ExpandMore,
                size = 28.dp,
            ) {
                onCollapseChange()
            }
        }

        AnimatedVisibility(
            visible = isNotCollapsed,
            enter = fadeIn() + expandVertically(
                animationSpec = tween(300)
            ),
            exit = fadeOut() + shrinkVertically(
                animationSpec = tween(150)
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun NavigationFilterButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = CustomTheme.typography.text,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        ArkhamIconText(
            iconGlyph = AppIcon.RightArrow,
            size = 32.dp,
            color = CustomTheme.colors.d10
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArkhamRangeSlider(
    range: NullableIntRange,
    maxRange: NullableIntRange,
    onUpdateRange: (NullableIntRange) -> Unit,
    modifier: Modifier = Modifier,
    nullText: String = stringResource(R.string.none),
) {
    var sliderRange by remember(range) {
        mutableStateOf(range.min.toSliderValue()..range.max.toSliderValue())
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        RangeSlider(
            value = sliderRange,
            onValueChange = { sliderRange = it },
            valueRange = maxRange.min.toSliderValue()..maxRange.max.toSliderValue(),
            onValueChangeFinished = {
                onUpdateRange(NullableIntRange(
                    sliderRange.start.toIntValue(),
                    sliderRange.endInclusive.toIntValue()
                ))
            },
            modifier = Modifier.fillMaxWidth().height(32.dp),
            steps = maxRange.max ?: 0,
            startThumb = {
                SliderDefaults.Thumb(
                    interactionSource = remember { MutableInteractionSource() },
                    thumbSize = DpSize(32.dp, 32.dp),
                    colors = SliderDefaults.colors().copy(
                        thumbColor = CustomTheme.colors.darkText,
                    ),
                )
            },
            endThumb = {
                SliderDefaults.Thumb(
                    interactionSource = remember { MutableInteractionSource() },
                    thumbSize = DpSize(32.dp, 32.dp),
                    colors = SliderDefaults.colors().copy(
                        thumbColor = CustomTheme.colors.darkText,
                    ),
                )
            },
            track = {
                SliderDefaults.Track(
                    rangeSliderState = it,
                    colors = SliderDefaults.colors().copy(
                        activeTrackColor = CustomTheme.colors.darkText,
                        activeTickColor = CustomTheme.colors.m,
                        inactiveTrackColor = CustomTheme.colors.m,
                        inactiveTickColor = CustomTheme.colors.darkText,
                    ),
                    thumbTrackGapSize = 0.dp
                )
            }
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            val startText =
                if (sliderRange.start == NO_VALUE) nullText else sliderRange.start.roundToInt()
                    .toString()
            val endText =
                if (sliderRange.endInclusive == NO_VALUE) nullText else sliderRange.endInclusive.roundToInt()
                    .toString()

            Text(
                text = startText,
                style = CustomTheme.typography.menuText
            )

            Text(
                text = endText,
                style = CustomTheme.typography.menuText
            )
        }
    }
}

private const val NO_VALUE = -1f

private fun Int?.toSliderValue(): Float =
    this?.toFloat() ?: NO_VALUE

private fun Float.toIntValue(): Int? =
    if (this == NO_VALUE) null else roundToInt()

@Composable
fun ArkhamFiltersCheckboxOption(
    title: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onValueChange: (Boolean) -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(CustomTheme.shapes.medium)
            .toggleable(
                value = isSelected,
                onValueChange = onValueChange
            ),
        shape = CustomTheme.shapes.large,
        color = Color.Transparent,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = CustomTheme.typography.text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            Spacer(modifier = Modifier.width(8.dp))
            ArkhamCheckCircle(
                value = isSelected,
                onValueChange = onValueChange
            )
        }
    }
}

@Composable
fun ArkhamFiltersCheckboxOption(
    title: AnnotatedString,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onValueChange: (Boolean) -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(CustomTheme.shapes.medium)
            .toggleable(
                value = isSelected,
                onValueChange = onValueChange
            ),
        shape = CustomTheme.shapes.large,
        color = Color.Transparent,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = CustomTheme.typography.text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            Spacer(modifier = Modifier.width(8.dp))
            ArkhamCheckCircle(
                value = isSelected,
                onValueChange = onValueChange
            )
        }
    }
}