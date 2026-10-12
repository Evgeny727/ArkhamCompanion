package com.arkhamcompanion.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.model.cards.CardListItem
import com.arkhamcompanion.ui.cards.components.CardListItem
import com.arkhamcompanion.ui.components.ArkhamCheckCircle
import com.arkhamcompanion.ui.components.ArkhamCheckboxButton
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.theme.CustomTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun RewardsScreen(
    rewards: ImmutableSet<String>,
    unlockAllRewards: Boolean,
    rewardCards: ImmutableList<CardListItem>,
    onUnlockAllChange: (Boolean) -> Unit,
    onRewardsChange: (Set<String>) -> Unit,
    onCardClick: (String, Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val rowHeight = with(density) {
        maxOf(
            a = 36.dp,
            b = (47 * CustomTheme.typography.scaleFactor * fontScale).dp
        )
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        item("text", contentType = "text") {
            Text(
                text = stringResource(R.string.set_rewards_text),
                style = CustomTheme.typography.small,
                modifier = Modifier.padding(8.dp)
            )
        }
        item("unlock_all_button", contentType = "button") {
            ArkhamCheckboxButton(
                title = stringResource(R.string.unlock_all_rewards),
                description = stringResource(
                    if (unlockAllRewards) R.string.disable_to_choose_individual_rewards_to_show
                    else R.string.all_rewards_are_shown_throughout_the_decks
                ),
                isSelected = unlockAllRewards,
                iconGlyph = AppIcon.Show,
                onValueChange = onUnlockAllChange,
                modifier = Modifier.padding(8.dp)
            )
        }
        if (!unlockAllRewards) {
            items(rewardCards, key = { it.id }) { card ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    CardListItem(
                        cardListItem = card,
                        rowHeight = rowHeight,
                        isFavorite = false,
                        onClick = { onCardClick(card.code, card.tabooSetId) },
                        modifier = Modifier.weight(1f)
                    )

                    ArkhamCheckCircle(
                        value = card.code in rewards,
                        isRadio = false,
                        onValueChange = { newValue ->
                            onRewardsChange(
                                if (newValue) {
                                    rewards + card.code
                                } else {
                                    rewards - card.code
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}