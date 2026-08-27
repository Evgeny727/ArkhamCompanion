package com.arkhamcompanion.ui.cards.components.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.model.cards.PropertiesFilter
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.theme.AppIconsFont
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.appSp

@Composable
fun FiltersPropertiesSectionContent(
    propertiesFilter: PropertiesFilter,
    modifier: Modifier = Modifier,
    onValueChange: (PropertiesFilter) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            modifier = Modifier.width(IntrinsicSize.Max).weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ArkhamFiltersCheckboxOption(
                title = stringResource(R.string.customizable),
                isSelected = propertiesFilter.customizable,
                onValueChange = {
                    onValueChange(propertiesFilter.copy(customizable = it))
                }
            )

            ArkhamFiltersCheckboxOption(
                title = stringResource(R.string.exceptional),
                isSelected = propertiesFilter.exceptional,
                onValueChange = {
                    onValueChange(propertiesFilter.copy(exceptional = it))
                }
            )

            ArkhamFiltersCheckboxOption(
                title = stringResource(R.string.heals_damage),
                isSelected = propertiesFilter.healsDamage,
                onValueChange = {
                    onValueChange(propertiesFilter.copy(healsDamage = it))
                }
            )

            ArkhamFiltersCheckboxOption(
                title = buildAnnotatedString {
                    append(stringResource(R.string.multiclass))
                    append(" (")
                    withStyle(
                        style = SpanStyle(
                            fontSize = 24.appSp(CustomTheme.typography.scaleFactor),
                            fontFamily = AppIconsFont,
                            baselineShift = BaselineShift(-0.25f)
                        )
                    ) {
                        append(AppIcon.Multiclass.glyph)
                    }
                    append(")")
                },
                isSelected = propertiesFilter.multiclass,
                onValueChange = {
                    onValueChange(propertiesFilter.copy(multiclass = it))
                }
            )

            ArkhamFiltersCheckboxOption(
                title = stringResource(R.string.permanent),
                isSelected = propertiesFilter.permanent,
                onValueChange = {
                    onValueChange(propertiesFilter.copy(permanent = it))
                }
            )

            ArkhamFiltersCheckboxOption(
                title = stringResource(R.string.specialist),
                isSelected = propertiesFilter.specialist,
                onValueChange = {
                    onValueChange(propertiesFilter.copy(specialist = it))
                }
            )

            ArkhamFiltersCheckboxOption(
                title = buildAnnotatedString {
                    append(stringResource(R.string.unique))
                    append(" (")
                    withStyle(
                        style = SpanStyle(
                            fontSize = 16.appSp(CustomTheme.typography.scaleFactor),
                            fontFamily = AppIconsFont,
                            baselineShift = BaselineShift(-0.15f)
                        )
                    ) {
                        append(AppIcon.Unique.glyph)
                    }
                    append(")")
                },
                isSelected = propertiesFilter.unique,
                onValueChange = {
                    onValueChange(propertiesFilter.copy(unique = it))
                }
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.width(IntrinsicSize.Max).weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ArkhamFiltersCheckboxOption(
                title = stringResource(R.string.exile),
                isSelected = propertiesFilter.exile,
                onValueChange = {
                    onValueChange(propertiesFilter.copy(exile = it))
                }
            )

            ArkhamFiltersCheckboxOption(
                title = stringResource(R.string.fast),
                isSelected = propertiesFilter.fast,
                onValueChange = {
                    onValueChange(propertiesFilter.copy(fast = it))
                }
            )

            ArkhamFiltersCheckboxOption(
                title = stringResource(R.string.heals_horror),
                isSelected = propertiesFilter.healsHorror,
                onValueChange = {
                    onValueChange(propertiesFilter.copy(healsHorror = it))
                }
            )

            ArkhamFiltersCheckboxOption(
                title = stringResource(R.string.myriad),
                isSelected = propertiesFilter.myriad,
                onValueChange = {
                    onValueChange(propertiesFilter.copy(myriad = it))
                }
            )

            ArkhamFiltersCheckboxOption(
                title = stringResource(R.string.seal),
                isSelected = propertiesFilter.seal,
                onValueChange = {
                    onValueChange(propertiesFilter.copy(seal = it))
                }
            )

            ArkhamFiltersCheckboxOption(
                title = stringResource(R.string.succeed_by),
                isSelected = propertiesFilter.succeedBy,
                onValueChange = {
                    onValueChange(propertiesFilter.copy(succeedBy = it))
                }
            )

            ArkhamFiltersCheckboxOption(
                title = stringResource(R.string.victory),
                isSelected = propertiesFilter.victory,
                onValueChange = {
                    onValueChange(propertiesFilter.copy(victory = it))
                }
            )
        }
    }
}