package com.arkhamcompanion.ui.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.model.cards.CardTabooInfo
import com.arkhamcompanion.ui.cards.components.details.ParsedCardText
import com.arkhamcompanion.ui.cards.components.details.rememberCardTextStyles
import com.arkhamcompanion.ui.components.ArkhamIconText
import com.arkhamcompanion.ui.components.ArkhamSquareButton
import com.arkhamcompanion.ui.components.iconSize
import com.arkhamcompanion.ui.components.toLocalizedDate
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.CardTextStyleResolver
import com.arkhamcompanion.ui.utils.applyScaffoldPaddings
import kotlinx.collections.immutable.persistentListOf
import kotlin.math.absoluteValue

@Composable
fun CardTabooHistoryScreen(
    cardCode: String,
    cardDetailsViewModel: CardDetailsViewModel,
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues
) {
    val tabooHistory by cardDetailsViewModel.getTabooHistory(cardCode)
        .collectAsState(initial = persistentListOf())
    val currentTaboo = tabooHistory.firstOrNull()

    val styles = rememberCardTextStyles(flavorText = false)
    val styleResolver = remember(styles) {
        CardTextStyleResolver(styles)
    }

    var showHistory by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .applyScaffoldPaddings(innerPadding)
            .fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item("current_taboo", "taboo_section") {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(
                        R.string.latest_taboo_list_name_date,
                        currentTaboo?.tabooName ?: "",
                        currentTaboo?.tabooDate?.toLocalizedDate() ?: ""
                    ),
                    style = CustomTheme.typography.large
                )

                if (currentTaboo?.tabooPlaceholder == true) {
                    Text(
                        text = stringResource(R.string.this_card_is_no_longer_on_the_taboo_list),
                        style = CustomTheme.typography.text
                    )
                } else if (currentTaboo != null){
                    TabooTextChange(currentTaboo, styleResolver)
                }
            }
        }

        item("history_header", "text") {
            Text(
                text = stringResource(R.string.taboo_list_history),
                style = CustomTheme.typography.large
            )
        }

        if (showHistory) {
            tabooHistory.drop(1).forEachIndexed { index, tabooInfo ->
                if (tabooInfo.tabooPlaceholder && tabooHistory.getOrNull(index + 1)?.tabooPlaceholder == false) {
                    item(tabooInfo.tabooName, "taboo_section") {
                        TabooSection(tabooInfo, styleResolver, noLongerInTaboo = true)
                    }
                } else if (!tabooInfo.tabooPlaceholder) {
                    item(tabooInfo.tabooName, "taboo_section") {
                        TabooSection(tabooInfo, styleResolver)
                    }
                }
            }
        }

        if (!showHistory) {
            item("show_history_button", "button") {
                ArkhamSquareButton(
                    title = stringResource(R.string.see_taboo_list_history),
                    onClick = { showHistory = true },
                    icon = { color ->
                        ArkhamIconText(
                            iconGlyph = AppIcon.Taboo,
                            size = iconSize(AppIcon.Taboo),
                            color = color,
                        )
                    }
                )
            }
        }

        item("first_text", "text") {
            Text(
                text = stringResource(R.string.taboo_description),
                style = CustomTheme.typography.text
            )
        }
        item("second_text", "text") {
            Text(
                text = stringResource(R.string.taboo_using_description),
                style = CustomTheme.typography.text
            )
        }
        item("third_text", "text") {
            Text(
                text = stringResource(R.string.opt_in_taboos_build_decks_with_them_in_settings),
                style = CustomTheme.typography.text
            )
        }
    }
}

@Composable
fun TabooSection(
    taboo: CardTabooInfo,
    styleResolver: CardTextStyleResolver,
    modifier: Modifier = Modifier,
    noLongerInTaboo: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = (taboo.tabooName ?: "") + " — " + (taboo.tabooDate?.toLocalizedDate() ?: ""),
            style = CustomTheme.typography.large
        )

        if (noLongerInTaboo) {
            Text(
                text = stringResource(R.string.this_card_is_no_longer_on_the_taboo_list),
                style = CustomTheme.typography.text
            )
        } else {
            TabooTextChange(taboo, styleResolver)
        }
    }
}

@Composable
private fun TabooTextChange(
    taboo: CardTabooInfo,
    styleResolver: CardTextStyleResolver,
    modifier: Modifier = Modifier
) {
    when {
        taboo.tabooXp != null -> {
            Text(
                text = if (taboo.tabooXp!! > 0) {
                    stringResource(R.string.additional_taboo_xp, taboo.tabooXp!!)
                } else {
                    stringResource(R.string.xp_discount_taboo, taboo.tabooXp!!.absoluteValue)
                },
                style = CustomTheme.typography.text,
                modifier = modifier
            )
        }

        taboo.tabooTextChange != null -> {
            ParsedCardText(
                text = taboo.tabooTextChange!!,
                styleResolver = styleResolver,
                modifier = modifier
            )
        }
    }
}