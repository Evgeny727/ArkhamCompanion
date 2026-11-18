package com.arkhamcompanion.ui.decks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.ui.components.ArkhamButtonColor
import com.arkhamcompanion.ui.components.ArkhamDialog
import com.arkhamcompanion.ui.components.ArkhamIconText
import com.arkhamcompanion.ui.components.ArkhamSquareButton
import com.arkhamcompanion.ui.components.iconSize
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.theme.CustomTheme

@Composable
fun EditDeckNameDialog(
    initialName: String,
    onDismiss: () -> Unit,
    onApply: (String) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf(initialName) }

    ArkhamDialog(
        title = stringResource(R.string.name),
        onDismiss = onDismiss,
        allowDismiss = false
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(
                    color = CustomTheme.colors.l20,
                    shape = CustomTheme.shapes.medium
                )
                .border(
                    width = 1.dp,
                    color = CustomTheme.colors.m,
                    shape = CustomTheme.shapes.medium
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                BasicTextField(
                    value = name,
                    onValueChange = { name = it },
                    textStyle = CustomTheme.typography.text,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                    cursorBrush = SolidColor(CustomTheme.colors.d15),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                if (name.isEmpty()) {
                    Text(
                        text = stringResource(R.string.enter_name_for_this_deck),
                        style = CustomTheme.typography.text,
                        color = CustomTheme.colors.d20,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ArkhamSquareButton(
                title = stringResource(R.string.cancel),
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                colors = ArkhamButtonColor.RedOutline,
            ) { color ->
                ArkhamIconText(
                    iconGlyph = AppIcon.Dismiss,
                    color = color,
                    size = iconSize(AppIcon.Dismiss)
                )
            }

            ArkhamSquareButton(
                title = stringResource(R.string.done),
                onClick = { onApply(name) },
                modifier = Modifier.weight(1f),
            ) { color ->
                ArkhamIconText(
                    iconGlyph = AppIcon.CheckThin,
                    color = color,
                    size = iconSize(AppIcon.CheckThin)
                )
            }
        }
    }
}