package com.arkhamcompanion.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.arkhamcompanion.R
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.theme.CustomTheme

@Composable
fun ArkhamSearchBox(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    searchPlaceholder: String,
    modifier: Modifier = Modifier,
    searchOptions: @Composable (ColumnScope.() -> Unit)? = null,
) {
    var showOptions by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = CustomTheme.colors.l20
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(8.dp)
                .border(
                    width = 1.dp,
                    color = CustomTheme.colors.l10,
                    shape = CustomTheme.shapes.circle
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ArkhamIconText(
                    iconGlyph = AppIcon.Search,
                    size = 28.dp,
                    color = CustomTheme.colors.m,
                    modifier = Modifier.padding(vertical = 2.dp)
                )

                Box(modifier = Modifier.weight(1f)) {
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onQueryChange,
                        textStyle = CustomTheme.typography.text,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Search
                        ),
                        cursorBrush = SolidColor(CustomTheme.colors.d15),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = searchPlaceholder,
                            style = CustomTheme.typography.text,
                            color = CustomTheme.colors.d20
                        )
                    }
                }

                AnimatedVisibility(searchQuery.isNotEmpty()) {
                    Box(
                        modifier = Modifier.clip(CustomTheme.shapes.circle)
                            .clickable(onClick = onClearQuery)
                            .padding(end = if (searchOptions == null) 8.dp else 0.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        ArkhamIconText(
                            iconGlyph = AppIcon.Dismiss,
                            size = 28.dp,
                            color = CustomTheme.colors.d20
                        )
                    }
                }
            }

            searchOptions?.let {
                ArkhamToggleButton(
                    checked = showOptions,
                    iconGlyph = AppIcon.Dots,
                    size = 28.dp,
                    modifier = Modifier.padding(2.dp)
                ) { newValue -> showOptions = newValue }
            }
        }

        DropdownMenu(
            expanded = showOptions,
            onDismissRequest = {},
            properties = PopupProperties(
                focusable = false,
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            ),
            shape = CustomTheme.shapes.large.copy(topStart = CornerSize(0.dp), topEnd = CornerSize(0.dp)),
            containerColor = CustomTheme.colors.l20,
            shadowElevation = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            searchOptions?.invoke(this)
        }
    }
}

@Composable
fun CardsSearchOptions(
    searchGame: Boolean,
    onSearchGameChange: (Boolean) -> Unit,
    searchFlavor: Boolean,
    onSearchFlavorChange: (Boolean) -> Unit,
    searchBack: Boolean,
    onSearchBackChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 16.dp),
    ) {
        Text(
            text = stringResource(R.string.search_in),
            style = CustomTheme.typography.run { large + bold },
            color = CustomTheme.colors.m,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(0.7f)
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ArkhamSearchCheckboxOption(
                title = stringResource(R.string.game_text),
                isSelected = searchGame,
                onValueChange = onSearchGameChange
            )
            ArkhamSearchCheckboxOption(
                title = stringResource(R.string.flavor_text),
                isSelected = searchFlavor,
                onValueChange = onSearchFlavorChange
            )
            ArkhamSearchCheckboxOption(
                title = stringResource(R.string.card_backs),
                isSelected = searchBack,
                onValueChange = onSearchBackChange
            )
        }
    }
}

@Composable
fun ArkhamSearchCheckboxOption(
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
                style = CustomTheme.typography.searchLabel,
                color = CustomTheme.colors.darkText,
            )
            Spacer(modifier = Modifier.width(8.dp))
            ArkhamCheckCircle(
                value = isSelected,
                onValueChange = onValueChange
            )
        }
    }
}