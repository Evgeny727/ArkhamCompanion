package com.arkhamcompanion.ui.settings.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.LocalThemeIsDark
import com.arkhamcompanion.R
import com.arkhamcompanion.ui.components.ArkhamCheckboxButton
import com.arkhamcompanion.ui.components.ArkhamDialog
import com.arkhamcompanion.ui.components.ArkhamRoundedCardHeader
import com.arkhamcompanion.ui.components.ArkhamRoundedFactionCard
import com.arkhamcompanion.ui.components.ArkhamSurfaceButton
import com.arkhamcompanion.ui.components.ArkhamSurfaceButtonGroup
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.theme.LocalLanguage

@Composable
fun SettingsCard(
    themeInt: Int,
    onThemeChange: (Int) -> Unit,
    scaleFactor: Float,
    onScaleChange: (Float) -> Unit,
    showFanmadeCards: Boolean,
    onFanmadeCardsChange: (Boolean) -> Unit,
    showPreviewCards: Boolean,
    onPreviewCardsChange: (Boolean) -> Unit,
    includeEnglishSearchResults: Boolean,
    onIncludeEnglishResultsChange: (Boolean) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val languageTag = LocalLanguage.current.languageTag

    ArkhamRoundedFactionCard(
        faction = Faction.Neutral,
        header = {
            ArkhamRoundedCardHeader(
                title = stringResource(R.string.settings),
                faction = Faction.Neutral,
            )
        },
    ) {
        ArkhamSurfaceButtonGroup(modifier) {
            ArkhamThemeSurfaceButton(themeInt, onThemeChange)

            HorizontalDivider(color = CustomTheme.colors.divider)

            ArkhamScaleFactorSurfaceButton(scaleFactor, onScaleChange)
        }

        Column(
            modifier = Modifier.padding(end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ArkhamCheckboxButton(
                title = stringResource(R.string.show_fanmade_cards),
                iconGlyph = AppIcon.SpecialCards,
                loading = isLoading,
                isSelected = showFanmadeCards,
                onValueChange = onFanmadeCardsChange
            )

            HorizontalDivider(color = CustomTheme.colors.divider)

            ArkhamCheckboxButton(
                title = stringResource(R.string.show_preview_cards),
                iconGlyph = AppIcon.Show,
                description = stringResource(R.string.grants_access_to_previewed_player_cards),
                loading = isLoading,
                isSelected = showPreviewCards,
                onValueChange = onPreviewCardsChange
            )

            if (languageTag != "en") {
                HorizontalDivider(color = CustomTheme.colors.divider)

                ArkhamCheckboxButton(
                    title = stringResource(R.string.search_english_card_names),
                    iconGlyph = AppIcon.Search,
                    description = stringResource(R.string.include_english_results_when_searching),
                    loading = isLoading,
                    isSelected = includeEnglishSearchResults,
                    onValueChange = onIncludeEnglishResultsChange
                )
            }
        }
    }
}

@Composable
private fun ArkhamThemeSurfaceButton(
    themeInt: Int,
    onThemeChange: (Int) -> Unit
) {
    val isDarkTheme = LocalThemeIsDark.current
    val lightThemeText = stringResource(R.string.light)
    val darkThemeText = stringResource(R.string.dark)
    val themeValue = when (isDarkTheme) {
        false -> lightThemeText
        true -> darkThemeText
    }
    val systemValue = stringResource(R.string.default_language, when (isSystemInDarkTheme()) {
        false -> lightThemeText
        true -> darkThemeText
    })
    var showThemePicker by remember { mutableStateOf(false) }

    ArkhamSurfaceButton(
        title = stringResource(R.string.theme),
        icon = AppIcon.Theme,
        valueLabel = themeValue,
        onClick = { showThemePicker = true }
    )

    if (showThemePicker) ArkhamDialog(
        title = stringResource(R.string.theme),
        onDismiss = { showThemePicker = false },
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ArkhamCheckboxButton(
                title = systemValue,
                isSelected = themeInt == 2,
                enabled = themeInt != 2,
                isRadio = true
            ) {
                showThemePicker = false
                onThemeChange(2)
            }

            HorizontalDivider(color = CustomTheme.colors.divider)

            ArkhamCheckboxButton(
                title = lightThemeText,
                isSelected = themeInt == 0,
                enabled = themeInt != 0,
                isRadio = true
            ) {
                showThemePicker = false
                onThemeChange(0)
            }

            HorizontalDivider(color = CustomTheme.colors.divider)

            ArkhamCheckboxButton(
                title = darkThemeText,
                isSelected = themeInt == 1,
                enabled = themeInt != 1,
                isRadio = true
            ) {
                showThemePicker = false
                onThemeChange(1)
            }
        }
    }
}

@Composable
private fun ArkhamScaleFactorSurfaceButton(
    scaleFactor: Float,
    onScaleChange: (Float) -> Unit
) {
    val scaleValue = when (scaleFactor) {
        0.9f -> stringResource(R.string.smaller)
        1.1f -> stringResource(R.string.medium)
        1.25f -> stringResource(R.string.large)
        1.4f -> stringResource(R.string.xlarge)
        1.6f -> stringResource(R.string.xxlarge)
        else -> stringResource(R.string.default_text)
    }
    var showScalePicker by remember { mutableStateOf(false) }

    ArkhamSurfaceButton(
        title = stringResource(R.string.font_size),
        icon = AppIcon.FontSize,
        valueLabel = scaleValue,
        onClick = { showScalePicker = true }
    )

    if (showScalePicker) ArkhamDialog(
        title = stringResource(R.string.font_size),
        onDismiss = { showScalePicker = false },
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.font_size_warning_text),
                style = CustomTheme.typography.text,
            )

            HorizontalDivider(color = CustomTheme.colors.divider)

            ArkhamCheckboxButton(
                title = stringResource(R.string.smaller),
                isSelected = scaleFactor == 0.9f,
                enabled = scaleFactor != 0.9f,
                isRadio = true
            ) {
                showScalePicker = false
                onScaleChange(0.9f)
            }

            HorizontalDivider(color = CustomTheme.colors.divider)

            ArkhamCheckboxButton(
                title = stringResource(R.string.default_text),
                isSelected = scaleFactor == 1f,
                enabled = scaleFactor != 1f,
                isRadio = true
            ) {
                showScalePicker = false
                onScaleChange(1f)
            }

            HorizontalDivider(color = CustomTheme.colors.divider)

            ArkhamCheckboxButton(
                title = stringResource(R.string.medium),
                isSelected = scaleFactor == 1.1f,
                enabled = scaleFactor != 1.1f,
                isRadio = true
            ) {
                showScalePicker = false
                onScaleChange(1.1f)
            }

            HorizontalDivider(color = CustomTheme.colors.divider)

            ArkhamCheckboxButton(
                title = stringResource(R.string.large),
                isSelected = scaleFactor == 1.25f,
                enabled = scaleFactor != 1.25f,
                isRadio = true
            ) {
                showScalePicker = false
                onScaleChange(1.25f)
            }

            HorizontalDivider(color = CustomTheme.colors.divider)

            ArkhamCheckboxButton(
                title = stringResource(R.string.xlarge),
                isSelected = scaleFactor == 1.4f,
                enabled = scaleFactor != 1.4f,
                isRadio = true
            ) {
                showScalePicker = false
                onScaleChange(1.4f)
            }

            HorizontalDivider(color = CustomTheme.colors.divider)

            ArkhamCheckboxButton(
                title = stringResource(R.string.xxlarge),
                isSelected = scaleFactor == 1.6f,
                enabled = scaleFactor != 1.6f,
                isRadio = true
            ) {
                showScalePicker = false
                onScaleChange(1.6f)
            }
        }
    }
}