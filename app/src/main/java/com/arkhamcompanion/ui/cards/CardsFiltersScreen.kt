package com.arkhamcompanion.ui.cards

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.enums.CardType
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.domain.model.cards.CardFilters
import com.arkhamcompanion.domain.model.cards.NullableIntRange
import com.arkhamcompanion.ui.cards.components.filters.ArkhamFiltersCheckboxOption
import com.arkhamcompanion.ui.cards.components.filters.ArkhamRangeSlider
import com.arkhamcompanion.ui.cards.components.filters.ArkhamSingleToggleButtonGroup
import com.arkhamcompanion.ui.cards.components.filters.ArkhamToggleButtonGroup
import com.arkhamcompanion.ui.cards.components.filters.CollapsableFiltersSection
import com.arkhamcompanion.ui.cards.components.filters.FiltersPropertiesSectionContent
import com.arkhamcompanion.ui.cards.components.filters.NavigationFilterButton
import com.arkhamcompanion.ui.cards.components.factionIcon
import com.arkhamcompanion.ui.cards.components.filters.FilersSkillIconsSection
import com.arkhamcompanion.ui.components.ArkhamIconText
import com.arkhamcompanion.ui.components.ArkhamTabooDialog
import com.arkhamcompanion.ui.components.factionColor
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.theme.LocalLanguage
import com.arkhamcompanion.ui.utils.applyScaffoldPaddings
import com.arkhamcompanion.ui.utils.getLocalizedAction
import com.arkhamcompanion.ui.utils.getLocalizedSkill
import com.arkhamcompanion.ui.utils.getLocalizedSlot
import com.arkhamcompanion.ui.utils.getLocalizedTrait
import com.arkhamcompanion.ui.utils.getLocalizedUse
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableMap

@Composable
fun CardsFiltersScreen(
    cardsViewModel: CardsViewModel,
    cardsFiltersViewModel: CardsFiltersViewModel,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val resources = LocalResources.current

    val defaultFilters = remember { CardFilters() }
    val filters by cardsViewModel.cardFilters.collectAsState()
    val spoilerState by cardsViewModel.spoilerState.collectAsState()
    val filtersUiState by cardsFiltersViewModel.uiState.collectAsState()
    val allFactions by cardsFiltersViewModel.factions.collectAsState()
    val factions = remember(allFactions, spoilerState) {
        allFactions.filter { faction ->
            spoilerState || faction.key != Faction.Mythos
        }.toImmutableMap()
    }
    val types by cardsFiltersViewModel.types.collectAsState()
    val subtypes by cardsFiltersViewModel.subtypes.collectAsState()
    val encounterSets by cardsFiltersViewModel.encounterSets.collectAsState()
    val packs by cardsFiltersViewModel.packs.collectAsState()
    val taboos by cardsFiltersViewModel.taboos.collectAsState()
    var showTabooDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .applyScaffoldPaddings(innerPadding)
            .fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        item("factions_filter", "segmented_button") {
            ArkhamToggleButtonGroup(
                values = factions.keys,
                selectedValues = filters.factions,
                onValueToggle = cardsViewModel::updateFactions,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 8.dp,
                        end = 8.dp,
                        bottom = 16.dp
                    )
            ) { faction, selected ->
                ArkhamIconText(
                    iconGlyph = factionIcon(faction),
                    size = when (faction) {
                        Faction.Mythos, Faction.Neutral -> 28.dp
                        else -> 32.dp
                    },
                    color = if (selected) factionColor(faction).text else CustomTheme.colors.m,
                    modifier = Modifier.align(
                        alignment = if (faction == Faction.Neutral || faction == Faction.Mythos) Alignment.BottomCenter
                        else Alignment.Center
                    )
                )
            }
        }

        item("level_section", "section") {
            val label = stringResource(R.string.level)
            val colon = LocalLanguage.current.colon
            val isCollapsed = filtersUiState.collapsedSections[FilterSection.Level] ?: true
            val range = filters.levelFilter.forcedRange ?: filters.levelFilter.range
            val isDefaultValues = range == defaultFilters.levelFilter.range
            val nullText = stringResource(R.string.none)

            CollapsableFiltersSection(
                label = if (isDefaultValues) stringResource(R.string.label_all, label)
                    else label + colon + range.format(nullText),
                isNotCollapsed = !isCollapsed,
                onCollapseChange = {
                    cardsFiltersViewModel.toggleSection(FilterSection.Level)
                },
                onSectionClear = cardsViewModel::clearLevelFilter,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .animateContentSize()
            ) {
                ArkhamRangeSlider(
                    range = range,
                    maxRange = defaultFilters.levelFilter.range,
                    onUpdateRange = cardsViewModel::updateLevelRange,
                    nullText = nullText
                )
            }

            if (!isCollapsed) HorizontalDivider(color = CustomTheme.colors.divider)
        }

        if (filtersUiState.collapsedSections[FilterSection.Level] ?: true) {
            item("level_short_filter", "segmented_button") {
                ArkhamSingleToggleButtonGroup(
                    values = persistentListOf(
                        NullableIntRange(0, 0), NullableIntRange(1, 5)
                    ),
                    selectedValue = filters.levelFilter.forcedRange,
                    onValueToggle = cardsViewModel::toggleForcedLevelRange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) { range ->
                    val text = if (range.min == 0)
                        stringResource(R.string.level_start, range.min!!)
                    else stringResource(R.string.level_start_end, range.min!!, range.max!!)
                    Text(
                        text = text,
                        style = CustomTheme.typography.small
                    )
                }

                HorizontalDivider(color = CustomTheme.colors.divider)
            }
        }

        item("type_navigation", "navigation") {
            val label = stringResource(R.string.types)
            val selectedTypes = filters.types.mapNotNull { types[it] }

            NavigationFilterButton(
                label = selectedFilterLabel(label, selectedTypes),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {

            }
        }

        item("type_short_filter", "segmented_button") {
            ArkhamToggleButtonGroup(
                values = persistentSetOf(
                    CardType.Asset, CardType.Event, CardType.Skill
                ),
                selectedValues = filters.types,
                onValueToggle = cardsViewModel::updateTypes,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) { type, _ ->
                Text(
                    text = types[type].toString(),
                    style = CustomTheme.typography.small
                )
            }

            HorizontalDivider(color = CustomTheme.colors.divider)
        }

        item("subtype_navigation", "navigation") {
            val label = stringResource(R.string.subtypes)
            val selectedSubTypes = filters.subTypes.mapNotNull { subtypes[it] }

            NavigationFilterButton(
                label = selectedFilterLabel(label, selectedSubTypes),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {

            }

            HorizontalDivider(color = CustomTheme.colors.divider)
        }

        item("cost_section", "section") {
            val label = stringResource(R.string.cost)
            val colon = LocalLanguage.current.colon
            val isCollapsed = filtersUiState.collapsedSections[FilterSection.Cost] ?: true
            val isDefaultValues = filters.costFilter == defaultFilters.costFilter
            val nullText = stringResource(R.string.none)

            CollapsableFiltersSection(
                label = if (isDefaultValues) stringResource(R.string.label_all, label)
                    else "$label$colon${filters.costFilter.range.format(nullText)}",
                isNotCollapsed = !isCollapsed,
                onCollapseChange = {
                    cardsFiltersViewModel.toggleSection(FilterSection.Cost)
                },
                onSectionClear = cardsViewModel::clearCostFilter,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .animateContentSize()
            ) {
                ArkhamRangeSlider(
                    range = filters.costFilter.range,
                    maxRange = defaultFilters.costFilter.range,
                    onUpdateRange = cardsViewModel::updateCostRange,
                    nullText = nullText
                )

                ArkhamFiltersCheckboxOption(
                    title = stringResource(R.string.even),
                    isSelected = filters.costFilter.evenCost,
                    onValueChange = cardsViewModel::toggleEvenCost
                )

                ArkhamFiltersCheckboxOption(
                    title = stringResource(R.string.odd),
                    isSelected = filters.costFilter.oddCost,
                    onValueChange = cardsViewModel::toggleOddCost
                )
            }

            HorizontalDivider(color = CustomTheme.colors.divider)
        }

        item("skills_section", "section") {
            FilersSkillIconsSection(
                skillsFilter = filters.skillsFilter,
                defaultFilter = defaultFilters.skillsFilter,
                isCollapsed = filtersUiState.collapsedSections[FilterSection.Skills] ?: true,
                onCollapseChange = {
                    cardsFiltersViewModel.toggleSection(FilterSection.Skills)
                },
                onSectionClear = cardsViewModel::clearSkillsFilter,
                onValueToggle = cardsViewModel::updateSkillsFilter
            )
        }

        item("action_navigation", "navigation") {
            val label = stringResource(R.string.actions)
            val selectedActions = filters.actions.map {
                stringResource(getLocalizedAction(it))
            }

            NavigationFilterButton(
                label = selectedFilterLabel(label, selectedActions),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {

            }

            HorizontalDivider(color = CustomTheme.colors.divider)
        }

        item("trait_navigation", "navigation") {
            val label = stringResource(R.string.traits)
            val selectedTraits = filters.traits.take(10).map {
                stringResource(getLocalizedTrait(it))
            }

            NavigationFilterButton(
                label = selectedFilterLabel(label, selectedTraits),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {

            }

            HorizontalDivider(color = CustomTheme.colors.divider)
        }

        item("health_sanity_section", "section") {
            val healthText = stringResource(R.string.health)
            val sanityText = stringResource(R.string.sanity)
            val label = "$healthText / $sanityText"
            val colon = LocalLanguage.current.colon
            val isCollapsed = filtersUiState.collapsedSections[FilterSection.HealthSanity] ?: true
            val isDefaultValues = filters.healthSanityFilter == defaultFilters.healthSanityFilter
            val nullText = stringResource(R.string.none)
            val healthValues = "$healthText (${
                filters.healthSanityFilter.health.format(nullText)
            })"
            val sanityValues = "$sanityText (${
                filters.healthSanityFilter.sanity.format(nullText)
            })"

            CollapsableFiltersSection(
                label = if (isDefaultValues) stringResource(R.string.label_all, label)
                    else "$label$colon$healthValues, $sanityValues",
                isNotCollapsed = !isCollapsed,
                onCollapseChange = {
                    cardsFiltersViewModel.toggleSection(FilterSection.HealthSanity)
                },
                onSectionClear = cardsViewModel::clearHealthSanityFilter,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .animateContentSize()
            ) {
                Text(
                    text = healthText,
                    style = CustomTheme.typography.menuText
                )

                ArkhamRangeSlider(
                    range = filters.healthSanityFilter.health,
                    maxRange = defaultFilters.healthSanityFilter.health,
                    onUpdateRange = cardsViewModel::updateHealthRange,
                    nullText = nullText
                )

                ArkhamFiltersCheckboxOption(
                    title = stringResource(R.string.per_investigator),
                    isSelected = filters.healthSanityFilter.healthPerInvestigator,
                    onValueChange = cardsViewModel::toggleHealthPerInvestigator
                )

                HorizontalDivider(color = CustomTheme.colors.divider)

                Text(
                    text = sanityText,
                    style = CustomTheme.typography.menuText
                )

                ArkhamRangeSlider(
                    range = filters.healthSanityFilter.sanity,
                    maxRange = defaultFilters.healthSanityFilter.sanity,
                    onUpdateRange = cardsViewModel::updateSanityRange,
                    nullText = nullText
                )

                ArkhamFiltersCheckboxOption(
                    title = stringResource(R.string.include_x_health_or_sanity),
                    isSelected = filters.healthSanityFilter.includeXHealthOrSanity,
                    onValueChange = cardsViewModel::toggleIncludeXHealthOrSanity
                )
            }

            HorizontalDivider(color = CustomTheme.colors.divider)
        }

        item("asset_navigation", "navigation") {
            val slotsText = stringResource(R.string.slots)
            val usesText = stringResource(R.string.uses)
            val boostsText = stringResource(R.string.boost)
            val parts = remember(filters.assetFilter) {
                buildList {
                    if (filters.assetFilter.slots.isNotEmpty()) {
                        add(
                            "$slotsText(" + filters.assetFilter.slots
                                .joinToString(", ") {
                                    resources.getString(getLocalizedSlot(it))
                                } + ")"
                        )
                    }
                    if (filters.assetFilter.uses.isNotEmpty()) {
                        add(
                            "$usesText(" + filters.assetFilter.uses
                                .take(10)
                                .joinToString(", ") {
                                    resources.getString(getLocalizedUse(it))
                                } + ")"
                        )
                    }
                    if (filters.assetFilter.skillBoosts.isNotEmpty()) {
                        add(
                            "$boostsText(" + filters.assetFilter.skillBoosts
                                .joinToString(", ") {
                                    resources.getString(getLocalizedSkill(it))
                                } + ")"
                        )
                    }
                }
            }
            val label = stringResource(R.string.assets_parts, parts)
            val textAll = stringResource(R.string.assets_all)
            val text = if (parts.isNotEmpty()) label else textAll

            NavigationFilterButton(
                label = text,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {

            }

            HorizontalDivider(color = CustomTheme.colors.divider)
        }

        item("properties_section", "section") {
            val label = stringResource(R.string.properties)
            val isCollapsed = filtersUiState.collapsedSections[FilterSection.Properties] ?: true

            CollapsableFiltersSection(
                label = label,
                isNotCollapsed = isCollapsed,
                onCollapseChange = {
                    cardsFiltersViewModel.toggleSection(FilterSection.Properties)
                },
                onSectionClear = cardsViewModel::clearPropertiesFilter,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .animateContentSize()
            ) {
                FiltersPropertiesSectionContent(
                    propertiesFilter = filters.propertiesFilter,
                    onValueChange = cardsViewModel::updatePropertiesFilter
                )
            }

            HorizontalDivider(color = CustomTheme.colors.divider)
        }

        item("enemy_navigation", "navigation") {
            val fightText = stringResource(R.string.fight)
            val evadeText = stringResource(R.string.evade)
            val damageText = stringResource(R.string.damage)
            val horrorText = stringResource(R.string.horror)
            val noValue = stringResource(R.string.none)
            val parts = remember(filters.enemyFilter) {
                buildList {
                    if (filters.enemyFilter.fight != defaultFilters.enemyFilter.fight) {
                        add("$fightText(${filters.enemyFilter.fight.format(noValue)})")
                    }
                    if (filters.enemyFilter.evade != defaultFilters.enemyFilter.evade) {
                        add("$evadeText(${filters.enemyFilter.evade.format(noValue)})")
                    }
                    if (filters.enemyFilter.damage != defaultFilters.enemyFilter.damage) {
                        add("$damageText(${filters.enemyFilter.damage.format(noValue)})")
                    }
                    if (filters.enemyFilter.horror != defaultFilters.enemyFilter.horror) {
                        add("$horrorText(${filters.enemyFilter.horror.format(noValue)})")
                    }
                }
            }
            val label = stringResource(R.string.enemies_parts, parts)
            val textAll = stringResource(R.string.enemies_all)
            val text = if (parts.isNotEmpty()) label else textAll

            NavigationFilterButton(
                label = text,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {

            }

            HorizontalDivider(color = CustomTheme.colors.divider)
        }

        item("location_navigation", "navigation") {
            val shroudText = stringResource(R.string.shroud)
            val cluesText = stringResource(R.string.clues)
            val noValue = stringResource(R.string.none)
            val parts = remember(filters.locationFilter) {
                buildList {
                    if (filters.locationFilter.shroud != defaultFilters.locationFilter.shroud) {
                        add("$shroudText(${filters.locationFilter.shroud.format(noValue)})")
                    }
                    if (filters.locationFilter.clues != defaultFilters.locationFilter.clues) {
                        add("$cluesText(${filters.locationFilter.clues.format(noValue)})")
                    }
                }
            }
            val label = stringResource(R.string.locations_parts, parts)
            val textAll = stringResource(R.string.locations_all)
            val text = if (parts.isNotEmpty()) label else textAll

            NavigationFilterButton(
                label = text,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {

            }

            HorizontalDivider(color = CustomTheme.colors.divider)
        }

        item("encounter_navigation", "navigation") {
            val label = stringResource(R.string.encounter_sets)
            val selectedEncounterSets = filters.encounterSets.take(10)
                .mapNotNull { encounterSets[it] }

            NavigationFilterButton(
                label = selectedFilterLabel(label, selectedEncounterSets),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {

            }

            HorizontalDivider(color = CustomTheme.colors.divider)
        }

        item("official_section", "section") {
            val officialOnlyText = stringResource(R.string.official_only)
            val fanmadeOnlyText = stringResource(R.string.fanmade_only)
            val label = stringResource(R.string.fanmade_content)
            val defaultText = stringResource(R.string.default_text)
            val colon = LocalLanguage.current.colon
            val isCollapsed = filtersUiState.collapsedSections[FilterSection.Official] ?: true
            val isDefaultValues = filters.officialFilter == defaultFilters.officialFilter

            CollapsableFiltersSection(
                label = "$label$colon" + if (isDefaultValues) defaultText
                    else if (filters.officialFilter == true) officialOnlyText else fanmadeOnlyText,
                isNotCollapsed = !isCollapsed,
                onCollapseChange = {
                    cardsFiltersViewModel.toggleSection(FilterSection.Official)
                },
                onSectionClear = cardsViewModel::clearOfficialFilter,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .animateContentSize()
            ) {
                ArkhamSingleToggleButtonGroup(
                    values = persistentListOf(false, true),
                    selectedValue = filters.officialFilter,
                    onValueToggle = cardsViewModel::toggleOfficialFilter,
                    modifier = Modifier.fillMaxWidth()
                ) { official ->
                    Text(
                        text = if (official) officialOnlyText else fanmadeOnlyText,
                        style = CustomTheme.typography.small
                    )
                }
            }

            HorizontalDivider(color = CustomTheme.colors.divider)
        }

        item("pack_navigation", "navigation") {
            val textAll = stringResource(R.string.packs_all)
            val selectedPacks = filters.packs.reprintPacks.take(10).mapNotNull { packs[it]?.name } +
                    filters.packs.packs.take(10).mapNotNull { packs[it]?.name }
            val label = stringResource(R.string.packs_value, selectedPacks)
            val text = if (selectedPacks.isNotEmpty()) label else textAll

            NavigationFilterButton(
                label = text,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {

            }

            HorizontalDivider(color = CustomTheme.colors.divider)
        }

        item("taboo_navigation", "navigation") {
            val label = stringResource(R.string.taboo_list)
            val tabooName = if (filters.tabooSetId != null) {
                taboos.find { it.id == filters.tabooSetId }?.name ?: ""
            } else ""

            NavigationFilterButton(
                label = selectedFilterLabel(label, tabooName),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                showTabooDialog = true
            }

            HorizontalDivider(color = CustomTheme.colors.divider)
        }

        item("illustrator_navigation", "navigation") {
            val label = stringResource(R.string.illustrators)
            val selectedIllustrators = filters.illustrators.take(10)

            NavigationFilterButton(
                label = selectedFilterLabel(label, selectedIllustrators),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {

            }

            HorizontalDivider(color = CustomTheme.colors.divider)
        }
    }

    if (showTabooDialog) {
        ArkhamTabooDialog(
            onDismiss = { showTabooDialog = false },
            tabooSetId = filters.tabooSetId ?: 0,
            tabooSetsList = taboos,
            onTabooSetChange = cardsViewModel::updateTabooSet
        )
    }
}

private fun NullableIntRange.format(noValue: String): String =
    if (min == max) {
        min?.toString() ?: noValue
    } else {
        "${min ?: noValue} - ${max ?: noValue}"
    }

@Composable
private fun selectedFilterLabel(
    label: String,
    values: List<String>,
): String {
    val colon = LocalLanguage.current.colon

    return if (values.isNotEmpty()) {
        "$label$colon${values.joinToString(", ")}"
    } else {
        stringResource(R.string.label_all, label)
    }
}

@Composable
private fun selectedFilterLabel(
    label: String,
    value: String,
): String {
    val colon = LocalLanguage.current.colon

    return if (value.isNotBlank()) {
        "$label$colon$value"
    } else {
        label
    }
}