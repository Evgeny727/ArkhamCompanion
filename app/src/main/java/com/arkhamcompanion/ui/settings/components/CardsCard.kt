package com.arkhamcompanion.ui.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.SUPPORTED_LANGUAGES
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.domain.model.meta.Pack
import com.arkhamcompanion.domain.model.meta.TabooSet
import com.arkhamcompanion.domain.model.settings.Collection
import com.arkhamcompanion.ui.components.ArkhamCheckboxButton
import com.arkhamcompanion.ui.components.ArkhamDialog
import com.arkhamcompanion.ui.components.ArkhamIconText
import com.arkhamcompanion.ui.components.ArkhamRoundedCardHeader
import com.arkhamcompanion.ui.components.ArkhamRoundedFactionCard
import com.arkhamcompanion.ui.components.ArkhamSquareButton
import com.arkhamcompanion.ui.components.ArkhamSurfaceButton
import com.arkhamcompanion.ui.components.ArkhamSurfaceButtonGroup
import com.arkhamcompanion.ui.components.ArkhamTabooSetButton
import com.arkhamcompanion.ui.components.iconSize
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.theme.LocalLanguage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import java.util.Locale

@Composable
fun CardsCard(
    onLanguageChange: (String) -> Unit,
    allPacks: ImmutableList<Pack>,
    collection: Collection,
    ignoreCollection: Boolean,
    navigateToCollection: () -> Unit,
    rewards: ImmutableSet<String>,
    unlockAllRewards: Boolean,
    navigateToRewards: () -> Unit,
    setTaboo: (Int) -> Unit,
    tabooSetId: Int,
    tabooSetsList: ImmutableList<TabooSet>,
    updateCards: (String) -> Unit,
    loading: Boolean,
    modifier: Modifier = Modifier
) {
    val languageTag = LocalLanguage.current.languageTag

    ArkhamRoundedFactionCard(
        faction = Faction.Neutral,
        header = {
            ArkhamRoundedCardHeader(
                title = stringResource(R.string.cards),
                faction = Faction.Neutral,
            )
        },
    ) {
        ArkhamSurfaceButtonGroup(modifier) {
            ArkhamLanguageSurfaceButton(languageTag, onLanguageChange)

            HorizontalDivider(color = CustomTheme.colors.divider)

            ArkhamCollectionSurfaceButton(
                allPacks = allPacks,
                collection = collection,
                ignoreCollection = ignoreCollection,
                navigateToCollection = navigateToCollection
            )

            HorizontalDivider(color = CustomTheme.colors.divider)

            ArkhamRewardsSurfaceButton(
                rewards = rewards,
                unlockAllRewards = unlockAllRewards,
                navigateToRewards = navigateToRewards
            )

            HorizontalDivider(color = CustomTheme.colors.divider)

            ArkhamTabooSetButton(
                tabooSetId,
                tabooSetsList,
                includeCurrent = true,
                loading = loading,
                onTabooSetChange = setTaboo
            )

            //TODO:Add card pool button
            //HorizontalDivider(color = CustomTheme.colors.divider)

//        ArkhamSurfaceButton(
//            title = stringResource(R.string.card_pool),
//            icon = AppIcon.Deck,
//            valueLabel = languageName,
//            onClick = {}
//        )
        }

        ArkhamSquareButton(
            title = stringResource(R.string.check_for_card_updates),
            onClick = { updateCards(languageTag) },
            icon = { color ->
                ArkhamIconText(
                    iconGlyph = AppIcon.Arkhamdb,
                    color = color,
                    size = iconSize(AppIcon.Arkhamdb)
                )
            }
        )

        //TODO:Add rules button
//    ArkhamSquareButton(
//        title = stringResource(R.string.check_for_card_updates),
//        onClick = { updateCards(languageTag) },
//        icon = {}
//    )
    }
}

@Composable
private fun ArkhamLanguageSurfaceButton(
    languageTag: String,
    onLanguageChange: (String) -> Unit
) {
    val languageName = Locale.forLanguageTag(languageTag).displayLanguage
    var showLanguagePicker by remember { mutableStateOf(false) }

    ArkhamSurfaceButton(
        title = stringResource(R.string.language),
        icon = AppIcon.World,
        valueLabel = languageName,
        onClick = { showLanguagePicker = true }
    )

    if (showLanguagePicker) ArkhamDialog(
        title = stringResource(R.string.language),
        onDismiss = { showLanguagePicker = false },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxHeight(0.6f),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item("description") {
                Text(
                    text = stringResource(R.string.note_not_all_cards_have_translations_available),
                    style = CustomTheme.typography.text,
                )
                HorizontalDivider(color = CustomTheme.colors.divider)
            }

            SUPPORTED_LANGUAGES.forEach { language ->
                item(language) {
                    val resolvedLanguage = Locale.forLanguageTag(when (language) {
                        "zh-cn" -> "zh-Hans"
                        "zh" -> "zh-Hant"
                        else -> language
                    })
                    ArkhamCheckboxButton(
                        title = resolvedLanguage.getDisplayName(resolvedLanguage),
                        isSelected = language == languageTag,
                        enabled = language != languageTag,
                        isRadio = true
                    ) {
                        showLanguagePicker = false
                        onLanguageChange(language)
                    }
                }
                item {
                    HorizontalDivider(color = CustomTheme.colors.divider)
                }
            }
        }
    }
}

@Composable
private fun ArkhamCollectionSurfaceButton(
    allPacks: ImmutableList<Pack>,
    collection: Collection,
    ignoreCollection: Boolean,
    navigateToCollection: () -> Unit
) {
    val (cyclesSize, packsSize) = remember(allPacks, collection) {
        val fullCollection = collection.packs + collection.reprintPacks
        if (fullCollection.isEmpty()) return@remember 0 to 0

        val uniqueCycles = mutableSetOf<Int>()
        var packs = 0

        allPacks.forEach { pack ->
            if (pack.code !in fullCollection) return@forEach

            if (pack.cyclePosition < 50) {
                uniqueCycles += pack.cyclePosition
            } else {
                packs++
            }
        }

        uniqueCycles.size to packs
    }
    val collectionSummarized = pluralStringResource(R.plurals.count_cycle, cyclesSize, cyclesSize) + " + " +
            pluralStringResource(R.plurals.count_pack, packsSize, packsSize)
    val allCollection = stringResource(R.string.all_cycles_and_packs)

    ArkhamSurfaceButton(
        title = stringResource(R.string.card_collection),
        icon = AppIcon.CardOutline,
        valueLabel = if (ignoreCollection) allCollection else collectionSummarized,
        onClick = navigateToCollection
    )
}

@Composable
private fun ArkhamRewardsSurfaceButton(
    rewards: ImmutableSet<String>,
    unlockAllRewards: Boolean,
    navigateToRewards: () -> Unit
) {
    val rewardsSummarized = pluralStringResource(R.plurals.count_reward, rewards.size, rewards.size)
    val allRewards = stringResource(R.string.all_rewards)

    ArkhamSurfaceButton(
        title = stringResource(R.string.unlocked_rewards),
        icon = AppIcon.SpecialCards,
        valueLabel = if (unlockAllRewards) allRewards else rewardsSummarized,
        onClick = navigateToRewards
    )
}