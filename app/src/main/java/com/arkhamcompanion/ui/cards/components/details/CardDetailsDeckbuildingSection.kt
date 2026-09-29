package com.arkhamcompanion.ui.cards.components.details

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.ui.components.ArkhamButton
import com.arkhamcompanion.ui.components.ArkhamIconText
import com.arkhamcompanion.ui.icons.AppIcon

fun LazyListScope.cardDetailsDeckbuildingSection(
    onShowBaseInvestigatorCards: () -> Unit,
    onShowParallelInvestigatorCards: (() -> Unit)? = null,
    onShowWhoCanTakeCard: (() -> Unit)? = null,
    isBase: Boolean = true,
    onCreateNewDeck: () -> Unit
) {
    item("deckbuilding_header", contentType = "header") {
        CardDetailsSectionHeader(
            title = stringResource(R.string.deckbuilding),
            normalCase = false
        )
    }

    if (onShowWhoCanTakeCard == null) {
        if (isBase) {
            item("base_investigator_cards_button", contentType = "button") {
                ArkhamButton(
                    title = stringResource(R.string.show_all_available_cards),
                    onClick = onShowBaseInvestigatorCards,
                ) { color ->
                    ArkhamIconText(
                        iconGlyph = AppIcon.Cards,
                        color = color,
                        size = 24.dp
                    )
                }
            }

            onShowParallelInvestigatorCards?.let {
                item("parallel_investigator_cards_button", contentType = "button") {
                    ArkhamButton(
                        title = stringResource(R.string.show_all_available_cards_for_parallel),
                        onClick = it,
                    ) { color ->
                        ArkhamIconText(
                            iconGlyph = AppIcon.Parallel1,
                            color = color,
                            size = 24.dp
                        )
                    }
                }
            }
        } else {
            onShowParallelInvestigatorCards?.let {
                item("parallel_investigator_cards_button", contentType = "button") {
                    ArkhamButton(
                        title = stringResource(R.string.show_all_available_cards),
                        onClick = it,
                    ) { color ->
                        ArkhamIconText(
                            iconGlyph = AppIcon.Parallel1,
                            color = color,
                            size = 24.dp
                        )
                    }
                }
            }

            item("base_investigator_cards_button", contentType = "button") {
                ArkhamButton(
                    title = stringResource(R.string.show_all_available_cards_for_base),
                    onClick = onShowBaseInvestigatorCards,
                ) { color ->
                    ArkhamIconText(
                        iconGlyph = AppIcon.Cards,
                        color = color,
                        size = 24.dp
                    )
                }
            }
        }

        //TODO:Implement deck creation
//    item("create_deck_button", contentType = "button") {
//        ArkhamButton(
//            title = stringResource(R.string.create_new_deck),
//            onClick = onCreateNewDeck,
//        ) { color ->
//            ArkhamIconText(
//                iconGlyph = AppIcon.Deck,
//                color = color,
//                size = 24.dp
//            )
//        }
//    }
    } else {
        item("who_can_use_button", contentType = "button") {
            ArkhamButton(
                title = stringResource(R.string.who_can_take_this_card),
                onClick = onShowWhoCanTakeCard,
            ) { color ->
                ArkhamIconText(
                    iconGlyph = AppIcon.PerInvestigator,
                    color = color,
                    size = 24.dp
                )
            }
        }
    }
}