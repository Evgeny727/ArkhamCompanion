package com.arkhamcompanion.ui.cards.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.model.cards.NullableIntRange
import com.arkhamcompanion.ui.components.ArkhamIconText
import com.arkhamcompanion.ui.components.ArkhamToggleButton
import com.arkhamcompanion.ui.components.PlusMinusButtons
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.theme.CustomTheme

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
                modifier = Modifier.weight(1f)
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

        AnimatedVisibility(isNotCollapsed) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun MinMaxFilterButtons(
    range: NullableIntRange,
    maxRange: NullableIntRange,
    onUpdateRange: (NullableIntRange) -> Unit,
    modifier: Modifier = Modifier,
    nullText: String = stringResource(R.string.none),
) {
    val (min, max) = range
    var minValue by remember(range) { mutableStateOf(min) }
    var maxValue by remember(range) { mutableStateOf(max) }

    FlowRow(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.border(
                1.dp,
                CustomTheme.colors.l10,
                CustomTheme.shapes.large
            ).padding(8.dp)
        ) {
            Text(
                text = stringResource(R.string.min),
                style = CustomTheme.typography.counter
            )

            PlusMinusButtons(
                value = minValue,
                range = maxRange,
                onIncrement = {
                    onUpdateRange(NullableIntRange(
                        it,
                        if (it > (maxValue ?: -1)) it else maxValue
                    ))
                },
                onDecrement = {
                    val newValue = if (maxRange.min == null && it < 0) null else it

                    onUpdateRange(NullableIntRange(newValue, maxValue))
                },
                nullText = nullText,
                overrideTextStyle = (if (minValue != null) CustomTheme.typography.counter else null)
                    ?.copy(color = CustomTheme.colors.darkText)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.border(
                1.dp,
                CustomTheme.colors.l10,
                CustomTheme.shapes.large
            ).padding(8.dp)
        ) {
            Text(
                text = stringResource(R.string.max),
                style = CustomTheme.typography.counter
            )

            PlusMinusButtons(
                value = maxValue,
                range = maxRange,
                onIncrement = {
                    onUpdateRange(NullableIntRange(minValue, it))
                },
                onDecrement = {
                    val newValue = if (maxRange.min == null && it < 0) null else it

                    onUpdateRange(NullableIntRange(
                        if ((newValue ?: -1) < (minValue ?: -1)) newValue else minValue,
                        newValue
                    ))
                },
                nullText = nullText,
                overrideTextStyle = (if (maxValue != null) CustomTheme.typography.counter else null)
                    ?.copy(color = CustomTheme.colors.darkText)
            )
        }
    }
}