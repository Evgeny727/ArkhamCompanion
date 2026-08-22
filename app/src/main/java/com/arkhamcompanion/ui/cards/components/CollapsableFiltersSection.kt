package com.arkhamcompanion.ui.cards.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.ui.theme.CustomTheme

@Composable
fun CollapsableFiltersSection(
    label: String,
    isCollapsed: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.animateContentSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = label,
                style = CustomTheme.typography.text,
                modifier = Modifier.weight(1f)
            )
        }

        AnimatedVisibility(!isCollapsed) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                content()
            }
        }
    }
}