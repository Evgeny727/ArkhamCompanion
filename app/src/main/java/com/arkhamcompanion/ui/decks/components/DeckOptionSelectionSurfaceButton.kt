package com.arkhamcompanion.ui.decks.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.domain.model.cards.DeckOption
import com.arkhamcompanion.domain.model.decks.Selection
import com.arkhamcompanion.domain.model.decks.Selections
import com.arkhamcompanion.ui.cards.components.factionIcon
import com.arkhamcompanion.ui.components.ArkhamCheckboxButton
import com.arkhamcompanion.ui.components.ArkhamDialog
import com.arkhamcompanion.ui.components.ArkhamSurfaceButton
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.getLocalizedFaction
import com.arkhamcompanion.ui.utils.getLocalizedOptionSelection
import com.arkhamcompanion.ui.utils.getLocalizedOptionSelectionValue

@Composable
fun DeckOptionSelectionSurfaceButton(
    option: DeckOption,
    selections: Selections,
    editable: Boolean = true,
    onClick: (String, Selection) -> Unit
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    ArkhamSurfaceButton(
        title = valueByOptionType(
            option = option,
            ifDeckSize = { R.string.deck_size },
            ifFaction = { R.string.secondary_class },
            ifOption = { getLocalizedOptionSelection(option.name) }
        )?.let { stringResource(it) } ?: option.name ?: stringResource(R.string.unknown),
        icon = valueByOptionType(
            option = option,
            ifDeckSize = { AppIcon.Deck },
            ifFaction = {
                val selection = selections[option.id ?: "faction_selected"]
                if (selection is Selection.FactionSelection) {
                    factionIcon(Faction.byFaction(selection.value))
                } else {
                    null
                }
            },
            ifOption = { AppIcon.SpecialCards }
        ),
        valueLabel = valueByOptionType(
            option = option,
            ifDeckSize = { selections[option.id ?: "deck_size_selected"] },
            ifFaction = { selections[option.id ?: "faction_selected"] },
            ifOption = { selections[option.id ?: "option_selected"] }
        )?.let { selection ->
            when (selection) {
                is Selection.DeckSizeSelection -> selection.value.toString()
                is Selection.FactionSelection -> stringResource(getLocalizedFaction(selection.value))
                is Selection.OptionSelection -> getLocalizedOptionSelectionValue(selection.value.id)?.let { value ->
                    stringResource(value)
                }
            }
        } ?: stringResource(R.string.none),
        editable = editable
    ) { showDialog = true }

    if (showDialog) {
        ArkhamDialog(
            title = valueByOptionType(
                option = option,
                ifDeckSize = { R.string.select_deck_size },
                ifFaction = { R.string.select_faction },
                ifOption = { R.string.select_option }
            )?.let { stringResource(it) } ?: stringResource(R.string.select_option),
            onDismiss = { showDialog = false },
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when {
                    option.deckSizeSelect.isNotEmpty() -> {
                        items(items = option.deckSizeSelect, key = { it }) { deckSize ->
                            val selection = selections[option.id ?: "deck_size_selected"]
                            val selected =
                                (selection as? Selection.DeckSizeSelection)?.value == deckSize.toIntOrNull()

                            ArkhamCheckboxButton(
                                title = deckSize,
                                isSelected = selected,
                                enabled = !selected,
                                isRadio = true,
                            ) {
                                onClick(
                                    option.id ?: "deck_size_selected",
                                    Selection.DeckSizeSelection(
                                        value = deckSize.toIntOrNull() ?: 30,
                                        options = option.deckSizeSelect,
                                        name = option.name.orEmpty()
                                    )
                                )

                                showDialog = false
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(top = 8.dp),
                                color = CustomTheme.colors.divider
                            )
                        }
                    }

                    option.factionSelect.isNotEmpty() -> {
                        items(items = option.factionSelect, key = { it.name }) { faction ->
                            val factionCode = faction.name.lowercase()
                            val selection = selections[option.id ?: "faction_selected"]
                            val selected =
                                (selection as? Selection.FactionSelection)?.value == factionCode

                            ArkhamCheckboxButton(
                                title = stringResource(
                                    getLocalizedFaction(factionCode)
                                ),
                                isSelected = selected,
                                enabled = !selected,
                                isRadio = true,
                            ) {
                                onClick(
                                    option.id ?: "faction_selected",
                                    Selection.FactionSelection(
                                        value = factionCode,
                                        options = option.factionSelect.map { it.name.lowercase() },
                                        name = option.name.orEmpty()
                                    )
                                )

                                showDialog = false
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(top = 8.dp),
                                color = CustomTheme.colors.divider
                            )
                        }
                    }

                    else -> {
                        items(items = option.optionSelect, key = { it.id }) { optionSelect ->
                            val selection = selections[option.id ?: "option_selected"]
                            val selected =
                                (selection as? Selection.OptionSelection)?.value?.id == optionSelect.id

                            ArkhamCheckboxButton(
                                title = getLocalizedOptionSelectionValue(optionSelect.id)?.let {
                                    stringResource(it)
                                } ?: "${stringResource(R.string.unknown)} (${optionSelect.id})",
                                isSelected = selected,
                                enabled = !selected,
                                isRadio = true,
                            ) {
                                onClick(
                                    option.id ?: "option_selected",
                                    Selection.OptionSelection(
                                        value = optionSelect,
                                        options = option.optionSelect,
                                        name = option.name.orEmpty()
                                    )
                                )

                                showDialog = false
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(top = 8.dp),
                                color = CustomTheme.colors.divider
                            )
                        }
                    }
                }
            }
        }
    }
}

private inline fun <T> valueByOptionType(
    option: DeckOption,
    ifDeckSize: () -> T?,
    ifFaction: () -> T?,
    ifOption: () -> T?,
): T? = when {
    option.deckSizeSelect.isNotEmpty() -> ifDeckSize()
    option.factionSelect.isNotEmpty() -> ifFaction()
    else -> ifOption()
}