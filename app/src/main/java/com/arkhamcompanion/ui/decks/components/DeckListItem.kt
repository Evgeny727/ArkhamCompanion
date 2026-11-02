package com.arkhamcompanion.ui.decks.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.enums.CardType
import com.arkhamcompanion.domain.enums.DeckSource
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.domain.model.decks.DeckListItem
import com.arkhamcompanion.ui.cards.components.details.toAlignment
import com.arkhamcompanion.ui.cards.components.factionIcon
import com.arkhamcompanion.ui.components.ArkhamIconText
import com.arkhamcompanion.ui.components.ArkhamRoundedFactionCard
import com.arkhamcompanion.ui.components.ArkhamScalableIconText
import com.arkhamcompanion.ui.components.factionColor
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.icons.IconGlyph
import com.arkhamcompanion.ui.theme.AppIconsFont
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.appSp

@Composable
fun DeckListItem(
    deckListItem: DeckListItem,
    resolveFactionTag: (String) -> String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val faction = when {
        deckListItem.investigator?.faction2 != null -> Faction.Dual

        deckListItem.investigator?.faction == Faction.Mythos ||
                deckListItem.investigator == null -> Faction.Neutral

        else -> deckListItem.investigator!!.faction
    }

    ArkhamRoundedFactionCard(
        faction = faction,
        modifier = modifier.clickable(onClick = onClick),
        header = {
            val factionColor = factionColor(faction)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                        color = factionColor.background,
                    )
                    .paint(
                        painter = painterResource(
                            when (faction) {
                                Faction.Guardian -> R.drawable.guardian
                                Faction.Seeker -> R.drawable.seeker
                                Faction.Rogue -> R.drawable.rogue
                                Faction.Mystic -> R.drawable.mystic
                                Faction.Survivor -> R.drawable.survivor
                                Faction.Neutral -> R.drawable.neutral
                                Faction.Dual -> R.drawable.multiclass
                                else -> R.drawable.mythos
                            }
                        ),
                        alpha = 0.2f,
                        contentScale = ContentScale.Crop
                    ),
                contentAlignment = Alignment.Center
            ) {
                val factionIcon = factionIcon(faction)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InvestigatorImage(
                        url = deckListItem.investigator?.thumbnailUrl ?: deckListItem.investigator?.imageUrl,
                        factionIcon = factionIcon,
                        alignment = if (deckListItem.investigator != null &&
                            deckListItem.investigator?.thumbnailUrl == null) {
                                CardType.Investigator.imageOffset.toAlignment()
                        } else {
                            Alignment.Center
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CustomTheme.shapes.large)
                            .border(
                                width = 2.dp,
                                color = factionColor.border,
                                shape = CustomTheme.shapes.large
                            )
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = deckListItem.name,
                            style = CustomTheme.typography.large,
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            deckListItem.investigator?.name?.let { name ->
                                Text(
                                    text = buildAnnotatedString {
                                        if (deckListItem.investigator?.parallel == true) {
                                            withStyle(
                                                style = SpanStyle(
                                                    fontSize = 18.appSp(CustomTheme.typography.scaleFactor),
                                                    baselineShift = BaselineShift(-0.1f),
                                                    fontFamily = AppIconsFont,
                                                    fontStyle = FontStyle.Normal,
                                                    fontWeight = FontWeight.Normal
                                                )
                                            ) {
                                                append(AppIcon.Parallel1.glyph)
                                            }
                                        }

                                        append(name)
                                    },
                                    style = CustomTheme.typography.run { smallLabel + italic },
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ArkhamScalableIconText(
                                    iconGlyph = AppIcon.XpBold,
                                    size = 16.sp,
                                    color = Color.White,
                                )

                                Spacer(Modifier.width(4.dp))

                                Text(
                                    text = pluralStringResource(
                                        R.plurals.value_xp,
                                        deckListItem.totalXp,
                                        deckListItem.totalXp
                                    ),
                                    style = CustomTheme.typography.run { smallLabel + bold },
                                    color = Color.White,
                                )

                                if ((deckListItem.xp ?: 0) > 0) {
                                    Spacer(Modifier.width(8.dp))

                                    ArkhamScalableIconText(
                                        iconGlyph = AppIcon.Upgrade,
                                        size = 16.sp,
                                        color = Color.White,
                                    )

                                    Spacer(Modifier.width(4.dp))

                                    Text(
                                        text = pluralStringResource(
                                            R.plurals.value_xp,
                                            deckListItem.xp ?: 0,
                                            deckListItem.xp ?: 0
                                        ),
                                        style = CustomTheme.typography.run { smallLabel + bold },
                                        color = Color.White,
                                    )
                                }

                                Spacer(Modifier.width(8.dp))

                                ArkhamScalableIconText(
                                    iconGlyph = AppIcon.CardOutlineBold,
                                    size = 18.sp,
                                    color = Color.White,
                                )

                                Spacer(Modifier.width(2.dp))

                                Text(
                                    text = "×${deckListItem.totalCards}",
                                    style = CustomTheme.typography.run { smallLabel + bold },
                                    color = Color.White,
                                )

                                deckListItem.problem?.let {
                                    Spacer(Modifier.width(8.dp))

                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = if (faction == Faction.Survivor) Color.White
                                                else CustomTheme.colors.warn,
                                                shape = CustomTheme.shapes.circle
                                            )
                                            .padding(2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        ArkhamIconText(
                                            iconGlyph = AppIcon.WarningBold,
                                            size = 18.dp,
                                            color = CustomTheme.colors.run {
                                                if (faction == Faction.Survivor) warn
                                                else d30
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        footer = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 8.dp,
                        end = 8.dp,
                        bottom = 4.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.no_campaign),
                    style = CustomTheme.typography.run { text + italic },
                    color = CustomTheme.colors.d15,
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(8.dp))

                if (deckListItem.xp != null && deckListItem.xpSpent != null) {
                    ArkhamScalableIconText(
                        iconGlyph = AppIcon.XpBold,
                        size = 24.appSp(CustomTheme.typography.scaleFactor),
                        color = CustomTheme.colors.faction.dual.background,
                    )

                    Spacer(Modifier.width(2.dp))

                    Text(
                        text = (deckListItem.xp!! - deckListItem.xpSpent!!).toString(),
                        style = CustomTheme.typography.run { text + boldItalic }
                    )

                    if (deckListItem.xp!! > 0 && deckListItem.xpSpent == 0) {
                        Spacer(Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .background(
                                    color = CustomTheme.colors.upgrade,
                                    shape = CustomTheme.shapes.circle
                                )
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            ArkhamIconText(
                                iconGlyph = AppIcon.Upgrade,
                                size = 24.dp,
                                color = CustomTheme.colors.d30,
                            )
                        }
                    }
                }
            }
        }
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ArkhamScalableIconText(
                iconGlyph = AppIcon.TagBold,
                size = 18.appSp(CustomTheme.typography.scaleFactor),
                color = CustomTheme.colors.m,
                modifier = Modifier.padding(top = 8.dp)
            )

            when (deckListItem.source) {
                DeckSource.Account -> {
                    DeckListTagItem(
                        iconGlyph = AppIcon.BuildLogo,
                        text = "arkham.build"
                    )
                }

                DeckSource.Arkhamdb -> {
                    DeckListTagItem(
                        iconGlyph = AppIcon.Arkhamdb,
                        text = "ArkhamDB"
                    )
                }

                DeckSource.Device -> {
                    DeckListTagItem(text = stringResource(R.string.device))
                }
            }

            deckListItem.tags.forEach { tag ->
                DeckListTagItem(text = resolveFactionTag(tag))
            }
        }
    }
}

@Composable
private fun DeckListTagItem(
    iconGlyph: IconGlyph? = null,
    text: String,
) {
    Surface(
        shape = CustomTheme.shapes.small,
        color = CustomTheme.colors.l20,
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            iconGlyph?.let {
                ArkhamIconText(
                    iconGlyph = iconGlyph,
                    size = 16.dp,
                    color = CustomTheme.colors.darkText,
                )
            }

            Text(
                text = text,
                style = CustomTheme.typography.small,
            )
        }
    }
}

@Composable
fun InvestigatorImage(
    url: String?,
    factionIcon: IconGlyph,
    alignment: Alignment,
    modifier: Modifier = Modifier,
) {
    val painter = rememberAsyncImagePainter(model = url)
    val state by painter.state.collectAsState()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            is AsyncImagePainter.State.Success -> {
                Image(
                    painter = painter,
                    contentDescription = null,
                    alignment = alignment,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = CustomTheme.colors.background,
                            shape = CustomTheme.shapes.large
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    ArkhamIconText(
                        iconGlyph = factionIcon,
                        size = 36.dp,
                        color = CustomTheme.colors.lightText,
                    )
                }
            }
        }
    }
}