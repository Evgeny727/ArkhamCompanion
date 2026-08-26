package com.arkhamcompanion.ui.cards.filters

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.model.cards.AssetFilter
import com.arkhamcompanion.ui.cards.CardsFiltersSlotsScreen
import com.arkhamcompanion.ui.cards.CardsFiltersUsesScreen
import com.arkhamcompanion.ui.cards.components.filters.ArkhamToggleButtonGroup
import com.arkhamcompanion.ui.cards.components.filters.NavigationFilterButton
import com.arkhamcompanion.ui.components.ArkhamIconText
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.applyScaffoldPaddings
import com.arkhamcompanion.ui.utils.getLocalizedSlot
import com.arkhamcompanion.ui.utils.getLocalizedUse
import kotlinx.collections.immutable.persistentSetOf

@Composable
fun CardsFiltersAssetsScreen(
    assetFilter: AssetFilter,
    onSkillBoostChange: (String) -> Unit,
    navigateTo: (Any) -> Unit,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val resources = LocalResources.current
    val labelSlots = stringResource(R.string.slots)
    val selectedSlots = remember(assetFilter.slots, resources) {
        assetFilter.slots.map { resources.getString(getLocalizedSlot(it)) }
    }

    val labelUses = stringResource(R.string.uses)
    val selectedUses = remember(assetFilter.uses, resources) {
        assetFilter.uses.take(10).map { resources.getString(getLocalizedUse(it)) }
    }

    Column(
        modifier = modifier
            .applyScaffoldPaddings(innerPadding)
            .fillMaxSize(),
    ) {
        NavigationFilterButton(
            label = selectedFilterLabel(labelSlots, selectedSlots),
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            navigateTo(CardsFiltersSlotsScreen)
        }

        HorizontalDivider(color = CustomTheme.colors.divider)

        NavigationFilterButton(
            label = selectedFilterLabel(labelUses, selectedUses),
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            navigateTo(CardsFiltersUsesScreen)
        }

        HorizontalDivider(color = CustomTheme.colors.divider)

        ArkhamToggleButtonGroup(
            values = persistentSetOf(
                "willpower", "intellect", "combat", "agility"
            ),
            selectedValues = assetFilter.skillBoosts,
            onValueToggle = onSkillBoostChange,
            modifier = Modifier.padding(8.dp)
        ) { code, _ ->
            ArkhamIconText(
                iconGlyph = when (code) {
                    "willpower" -> AppIcon.Willpower
                    "intellect" -> AppIcon.Intellect
                    "combat" -> AppIcon.Combat
                    "agility" -> AppIcon.Agility
                    else -> AppIcon.Wild
                },
                size = 28.dp,
                color = CustomTheme.colors.darkText,
            )
        }
    }
}