package com.arkhamcompanion.ui.cards.components.filters

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.model.cards.SkillsFilter
import com.arkhamcompanion.ui.components.ArkhamScalableIconText
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.theme.LocalLanguage
import com.arkhamcompanion.ui.utils.appSp
import kotlinx.collections.immutable.persistentListOf

@Composable
fun FilersSkillIconsSection(
    skillsFilter: SkillsFilter,
    defaultFilter: SkillsFilter,
    isCollapsed: Boolean,
    onCollapseChange: () -> Unit,
    onSectionClear: () -> Unit,
    modifier: Modifier = Modifier,
    onValueToggle: (SkillsFilter) -> Unit
) {
    val label = stringResource(R.string.skill_icons)
    val textAll = stringResource(R.string.skill_icons_all)
    val colon = LocalLanguage.current.colon
    val resources = LocalResources.current
    val isDefaultValues = skillsFilter == defaultFilter
    val skillIcons = remember(skillsFilter) {
        buildList {
            skillsFilter.willpower?.let {
                add(resources.getString(R.string.willpower_count, "$it+"))
            }
            skillsFilter.intellect?.let {
                add(resources.getString(R.string.intellect_count, "$it+"))
            }
            skillsFilter.combat?.let {
                add(resources.getString(R.string.combat_count, "$it+"))
            }
            skillsFilter.agility?.let {
                add(resources.getString(R.string.agility_count, "$it+"))
            }
            skillsFilter.wild?.let {
                add(resources.getString(R.string.wild_count, "$it+"))
            }
            skillsFilter.any?.let {
                add("${resources.getString(R.string.skills_any)}$colon$it+")
            }
        }.joinToString(", ")
    }

    CollapsableFiltersSection(
        label = if (isDefaultValues) textAll else "$label$colon$skillIcons",
        isNotCollapsed = !isCollapsed,
        onCollapseChange = onCollapseChange,
        onSectionClear = onSectionClear,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .animateContentSize()
    ) {
        SkillIconsBlock(skillsFilter, onValueToggle = onValueToggle)
    }

    HorizontalDivider(color = CustomTheme.colors.divider)
}

@Composable
private fun SkillIconsBlock(
    skillsFilter: SkillsFilter,
    modifier: Modifier = Modifier,
    onValueToggle: (SkillsFilter) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            modifier = Modifier.weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            SkillIconRow(
                skillCode = "agility",
                selectedValue = skillsFilter.agility,
            ) {
                onValueToggle(skillsFilter.copy(
                    agility = skillsFilter.agility.toggleCompare(it)
                ))
            }

            SkillIconRow(
                skillCode = "intellect",
                selectedValue = skillsFilter.intellect,
            ) {
                onValueToggle(skillsFilter.copy(
                    intellect = skillsFilter.intellect.toggleCompare(it)
                ))
            }

            SkillIconRow(
                skillCode = "wild",
                selectedValue = skillsFilter.wild,
            ) {
                onValueToggle(skillsFilter.copy(
                    wild = skillsFilter.wild.toggleCompare(it)
                ))
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            SkillIconRow(
                skillCode = "combat",
                selectedValue = skillsFilter.combat,
            ) {
                onValueToggle(skillsFilter.copy(
                    combat = skillsFilter.combat.toggleCompare(it)
                ))
            }

            SkillIconRow(
                skillCode = "willpower",
                selectedValue = skillsFilter.willpower,
            ) {
                onValueToggle(skillsFilter.copy(
                    willpower = skillsFilter.willpower.toggleCompare(it)
                ))
            }

            SkillIconRow(
                skillCode = "any",
                selectedValue = skillsFilter.any,
            ) {
                onValueToggle(skillsFilter.copy(
                    any = skillsFilter.any.toggleCompare(it)
                ))
            }
        }
    }
}

@Composable
private fun SkillIconRow(
    skillCode: String,
    selectedValue: Int?,
    modifier: Modifier = Modifier,
    onValueToggle: (Int) -> Unit
) {
    val icon = AppIcon.fromNameCode(skillCode)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            ArkhamScalableIconText(
                iconGlyph = it,
                size = 18.appSp(CustomTheme.typography.scaleFactor),
                color = CustomTheme.colors.darkText,
            )
        } ?: Text(
            text = stringResource(R.string.skills_any),
            style = CustomTheme.typography.small
        )

        ArkhamSingleToggleButtonGroup(
            values = persistentListOf(1, 2),
            selectedValue = selectedValue,
            onValueToggle = onValueToggle,
            modifier = Modifier.sizeIn(maxWidth = 120.dp),
            minHeight = null
        ) { value ->
            Text(
                text = "$value+",
                style = CustomTheme.typography.small
            )
        }
    }
}

private fun Int?.toggleCompare(other: Int?) =
    if (this == other) null else other