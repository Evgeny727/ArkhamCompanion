package com.arkhamcompanion.ui.cards.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.arkhamcompanion.domain.enums.CardType
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.domain.model.cards.CardListItem
import com.arkhamcompanion.ui.components.ArkhamScalableIconText
import com.arkhamcompanion.ui.components.factionColor
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.icons.IconGlyph
import com.arkhamcompanion.ui.icons.PackIcon
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.theme.FactionColors
import com.arkhamcompanion.ui.utils.appSp
import com.arkhamcompanion.ui.utils.iconize
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlin.math.absoluteValue

@Composable
fun CardListItem(
    cardListItem: CardListItem,
    rowHeight: Dp,
    isFavorite: Boolean,
    modifier: Modifier = Modifier,
    invalid: Boolean = false,
    onClick: () -> Unit,
) {
    val factionColor = factionColor(
        if (cardListItem.faction2 != null) Faction.Dual
        else cardListItem.faction
    )

    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .weight(1f)
                        .height(rowHeight),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    cardListItem.thumbnailUrl?.let { thumbnail ->
                        CardListItemThumbnail(
                            thumbnailUrl = thumbnail,
                            factionColor = factionColor,
                        )
                    }

                    with(cardListItem) {
                        CardIcon(
                            xp,
                            type,
                            subType,
                            faction,
                            faction2,
                            factionColor,
                            realCost,
                            encounterCode
                        )
                    }

                    CardListItemName(cardListItem, factionColor, invalid)
                }

                if (isFavorite) {
                    Box(
                        Modifier
                            .padding(end = 4.dp)
                            .width(2.dp)
                            .height(rowHeight)
                            .background(
                                color = factionColor.text,
                                shape = CustomTheme.shapes.circle
                            )
                    )
                }
            }

            HorizontalDivider(color = CustomTheme.colors.divider)
        }
    }
}

@Composable
fun CardListItemThumbnail(
    thumbnailUrl: String,
    factionColor: FactionColors,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(thumbnailUrl)
            .build(),
        modifier = modifier
            .fillMaxHeight(0.8f)
            .aspectRatio(1f)
            .clip(CustomTheme.shapes.small)
            .border(1.dp, factionColor.text, CustomTheme.shapes.small),
        contentDescription = null,
        contentScale = ContentScale.Crop,
    )
}

@Composable
fun RowScope.CardListItemName(
    cardListItem: CardListItem,
    factionColor: FactionColors,
    invalid: Boolean
) {
    Column(modifier = Modifier.weight(1f)) {
        CardListItemNameRow(
            name = cardListItem.name,
            isUnique = cardListItem.isUnique,
            packCode = cardListItem.packCode,
            packPosition = cardListItem.packPosition,
            reprintPackCode = cardListItem.reprintPackCode,
            factionColor = factionColor,
            invalid = invalid
        )
        CardListItemSubname(cardListItem)
    }
}

@Composable
fun CardListItemNameRow(
    name: String,
    isUnique: Boolean,
    packCode: String,
    packPosition: Int,
    reprintPackCode: String?,
    factionColor: FactionColors,
    invalid: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (isUnique) {
            ArkhamScalableIconText(
                iconGlyph = AppIcon.Unique,
                size = 12.appSp(CustomTheme.typography.scaleFactor),
                color = factionColor.text,
                textDecoration = if (invalid) TextDecoration.LineThrough else null
            )
        }

        Text(
            text = name.iconize(iconSize = 16.sp, color = CustomTheme.colors.darkText),
            style = CustomTheme.typography.cardName,
            color = factionColor.text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textDecoration = if (invalid) TextDecoration.LineThrough else null,
            modifier = Modifier.weight(1f, fill = false)
        )

        //Exclude pack info for random basic weakness
        if (packPosition != 1000) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val packIcon = PackIcon.fromPackCode(reprintPackCode ?: packCode)
                ArkhamScalableIconText(
                    iconGlyph = packIcon,
                    size = 16.appSp(CustomTheme.typography.scaleFactor),
                    color = CustomTheme.colors.lightText,
                    textDecoration = if (invalid) TextDecoration.LineThrough else null,
                )
                Text(
                    text = packPosition.toString(),
                    style = CustomTheme.typography.run { cardTraits + regular },
                    textDecoration = if (invalid) TextDecoration.LineThrough else null,
                )
            }
        }
    }
}

@Composable
fun CardListItemSubname(cardListItem: CardListItem) {
    val subname = cardListItem.subname ?:
        if (cardListItem.type == CardType.Act || cardListItem.type == CardType.Agenda
            || cardListItem.type == CardType.Scenario) {
                cardListItem.typeName + if (cardListItem.stage != null) " ${cardListItem.stage}" else ""
        } else {
            ""
        }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (cardListItem.faction != Faction.Mythos && cardListItem.type != CardType.Investigator
            && cardListItem.type != CardType.Skill) {
                val factionIcon = factionIcon(cardListItem.faction)
                ArkhamScalableIconText(
                    iconGlyph = factionIcon,
                    size = 16.appSp(CustomTheme.typography.scaleFactor),
                    color = factionColor(cardListItem.faction).text
                )
                cardListItem.faction2?.let { faction ->
                    val factionIcon = factionIcon(faction)
                    ArkhamScalableIconText(
                        iconGlyph = factionIcon,
                        size = 16.appSp(CustomTheme.typography.scaleFactor),
                        color = factionColor(faction).text
                    )
                }
                cardListItem.faction3?.let { faction ->
                    val factionIcon = factionIcon(faction)
                    ArkhamScalableIconText(
                        iconGlyph = factionIcon,
                        size = 16.appSp(CustomTheme.typography.scaleFactor),
                        color = factionColor(faction).text
                    )
                }
        }

        if (cardListItem.parallel) ArkhamScalableIconText(
            iconGlyph = AppIcon.Parallel1,
            size = 16.appSp(CustomTheme.typography.scaleFactor),
            color = CustomTheme.colors.lightText
        )

        if (cardListItem.type != CardType.Investigator) {
            val skills = with(cardListItem) {
                buildSkillTextRow(skillWillpower, skillIntellect, skillCombat, skillAgility, skillWild)
            }
            if (skills.isNotEmpty()) Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                skills.forEach { skill ->
                    ArkhamScalableIconText(
                        iconGlyph = skill,
                        size = 16.appSp(CustomTheme.typography.scaleFactor),
                        color = CustomTheme.colors.lightText,
                    )
                }
            }
        }

        if (!cardListItem.tabooPlaceholder) cardListItem.tabooSetId?.let {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                ArkhamScalableIconText(
                    iconGlyph = AppIcon.Tablet,
                    size = 14.appSp(CustomTheme.typography.scaleFactor),
                    color = CustomTheme.colors.taboo,
                )
                cardListItem.tabooXp?.let { xp ->
                    val taboo = buildTabooXpRow(xp)
                    if (taboo.isNotEmpty()) taboo.forEach { symbol ->
                        Text(
                            text = symbol,
                            style = CustomTheme.typography.small,
                            color = CustomTheme.colors.taboo,
                        )
                    }
                }
            }
        }

        if (subname.isNotBlank()) Text(
            text = subname.iconize(iconSize = 12.sp, color = CustomTheme.colors.d10),
            style = CustomTheme.typography.cardTraits,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
        )
    }
}

@Stable
private fun buildSkillTextRow(
    willpower: Int?,
    intellect: Int?,
    combat: Int?,
    agility: Int?,
    wild: Int?
): ImmutableList<IconGlyph> {
    return buildList {
        repeat(willpower ?: 0) {
            add(AppIcon.Willpower)
        }
        repeat(intellect ?: 0) {
            add(AppIcon.Intellect)
        }
        repeat(combat ?: 0) {
            add(AppIcon.Combat)
        }
        repeat(agility ?: 0) {
            add(AppIcon.Agility)
        }
        repeat(wild ?: 0) {
            add(AppIcon.Wild)
        }
    }.toImmutableList()
}

@Stable
private fun buildTabooXpRow(xp: Int): ImmutableList<String> {
    return buildList {
        repeat(xp.absoluteValue) {
            if (xp > 0) add("•")
            else add("-")
        }
    }.toImmutableList()
}