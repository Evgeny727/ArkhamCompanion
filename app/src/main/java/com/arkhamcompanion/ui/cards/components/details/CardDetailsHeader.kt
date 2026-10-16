package com.arkhamcompanion.ui.cards.components.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.enums.CardSubType
import com.arkhamcompanion.domain.enums.CardType
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.domain.model.cards.CardDetails
import com.arkhamcompanion.domain.model.cards.CardPack
import com.arkhamcompanion.ui.cards.components.CardCostIcon
import com.arkhamcompanion.ui.cards.components.EncounterIcon
import com.arkhamcompanion.ui.cards.components.factionIcon
import com.arkhamcompanion.ui.cards.components.iconScaleFactor
import com.arkhamcompanion.ui.components.ArkhamScalableIconText
import com.arkhamcompanion.ui.components.factionColor
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.icons.PackIcon
import com.arkhamcompanion.ui.theme.AppIconsFont
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.appSp
import com.arkhamcompanion.ui.utils.iconize
import com.arkhamcompanion.ui.utils.scaledByFont

@Composable
fun CardDetailsHeader(
    cardDetails: CardDetails,
    firstPackInCollection: CardPack?,
    modifier: Modifier = Modifier,
    isBack: Boolean = false,
    isCustomizableSheet: Boolean = false
) {
    val cardFaction = if (cardDetails.faction2 != null) Faction.Dual else cardDetails.faction

    val density = LocalDensity.current
    val minRowHeight = with(density) {
        maxOf(
            a = 36.dp,
            b = (47 * CustomTheme.typography.scaleFactor * fontScale).dp
        )
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        color = factionColor(cardFaction).background,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minRowHeight)
                .paint(
                    painter = painterResource(when (cardFaction) {
                        Faction.Guardian -> R.drawable.guardian
                        Faction.Seeker -> R.drawable.seeker
                        Faction.Rogue -> R.drawable.rogue
                        Faction.Mystic -> R.drawable.mystic
                        Faction.Survivor -> R.drawable.survivor
                        Faction.Neutral -> R.drawable.neutral
                        Faction.Dual -> R.drawable.multiclass
                        else -> R.drawable.mythos
                    }),
                    alpha = 0.2f,
                    contentScale = ContentScale.Crop
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isCustomizableSheet) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.customizations),
                            style = CustomTheme.typography.cardName,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                } else {
                    val showCost = cardDetails.type == CardType.Asset ||
                            cardDetails.type == CardType.Event || cardDetails.type == CardType.Skill
                    val iconScaleFactor = iconScaleFactor(CustomTheme.typography.scaleFactor)

                    //cost
                    if (showCost) {
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CardCostIcon(
                                xp = cardDetails.xp,
                                type = cardDetails.type,
                                subType = cardDetails.subType,
                                faction = cardDetails.faction,
                                faction2 = cardDetails.faction2,
                                factionColor = CustomTheme.colors.faction.neutral,
                                cost = cardDetails.cost,
                                inverted = true,
                                iconScaleFactor = iconScaleFactor
                            )
                        }
                    }

                    //name block
                    val name = if (isBack) {
                        cardDetails.backName ?: stringResource(
                            R.string.card_back_name_fallback,
                            cardDetails.name
                        )
                    } else {
                        cardDetails.name
                    }

                    CardDetailsNameRow(
                        parallel = cardDetails.parallel,
                        isUnique = cardDetails.isUnique,
                        name = name,
                        packCode = firstPackInCollection?.code ?: cardDetails.reprintPackCode ?: cardDetails.packCode,
                        packPosition = firstPackInCollection?.position ?: cardDetails.packPosition,
                        subname = if (isBack) cardDetails.subname else cardDetails.subname
                    )

                    //faction icons
                    CardDetailsFactionIcons(
                        isWithCost = showCost,
                        subType = cardDetails.subType,
                        faction = cardDetails.faction,
                        faction2 = cardDetails.faction2,
                        faction3 = cardDetails.faction3,
                        encounterCode = cardDetails.encounterCode,
                        iconScaleFactor = iconScaleFactor
                    )
                }
            }
        }
    }
}

@Composable
fun CardDetailsFactionIcons(
    isWithCost: Boolean,
    subType: CardSubType?,
    faction: Faction,
    faction2: Faction?,
    faction3: Faction?,
    encounterCode: String?,
    iconScaleFactor: Float
) {
    if (subType == CardSubType.BasicWeakness || subType == CardSubType.Weakness) {
        ArkhamScalableIconText(
            iconGlyph = AppIcon.Weakness,
            size = 28.appSp(iconScaleFactor),
            color = Color.White,
        )
    } else if (encounterCode != null && !isWithCost) {
        EncounterIcon(
            iconCode = encounterCode,
            iconSize = 28.appSp(iconScaleFactor),
            iconColor = Color.White,
        )
    } else {
        val factionIcon = factionIcon(faction)
        ArkhamScalableIconText(
            iconGlyph = factionIcon,
            size = 32.appSp(iconScaleFactor),
            color = Color.White,
            modifier = Modifier.padding(
                bottom = if (faction != Faction.Neutral) 4.dp.scaledByFont(iconScaleFactor)
                    else 0.dp
            )
        )

        faction2?.let {
            val faction2Icon = factionIcon(faction2)
            ArkhamScalableIconText(
                iconGlyph = faction2Icon,
                size = 32.appSp(iconScaleFactor),
                color = Color.White,
                modifier = Modifier.padding(bottom = 4.dp.scaledByFont(iconScaleFactor))
            )
        }

        faction3?.let {
            val faction3Icon = factionIcon(faction3)
            ArkhamScalableIconText(
                iconGlyph = faction3Icon,
                size = 32.appSp(iconScaleFactor),
                color = Color.White,
                modifier = Modifier.padding(bottom = 4.dp.scaledByFont(iconScaleFactor))
            )
        }
    }
}

@Composable
private fun RowScope.CardDetailsNameRow(
    parallel: Boolean,
    isUnique: Boolean,
    name: String,
    packCode: String,
    packPosition: Int,
    subname: String?,
) {
    Column(modifier = Modifier.weight(1f)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val iconSize = 16.appSp(CustomTheme.typography.scaleFactor)
            val nameString = buildAnnotatedString {
                if (parallel) {
                    withStyle(
                        style = SpanStyle(
                            fontFamily = AppIconsFont,
                            fontSize = 18.appSp(CustomTheme.typography.scaleFactor),
                            baselineShift = BaselineShift(-0.1f)
                        )
                    ) {
                        append(AppIcon.Parallel1.glyph)
                    }
                }
                if (isUnique) {
                    withStyle(
                        style = SpanStyle(
                            fontFamily = AppIconsFont,
                            fontSize = 12.appSp(CustomTheme.typography.scaleFactor),
                        )
                    ) {
                        append(AppIcon.Unique.glyph)
                    }
                }

                append(" ")
                append(name.iconize(iconSize = 16.sp, color = Color.White))

                if (packPosition != 1000) {
                    val packIcon = PackIcon.fromPackCode(packCode)!!

                    append(" ")

                    withStyle(
                        style = SpanStyle(
                            fontFamily = packIcon.fontFamily,
                            fontSize = iconSize,
                            baselineShift = BaselineShift(-0.1f)
                        )
                    ) {
                        append(packIcon.glyph)
                    }

                    append('\u00A0')

                    withStyle(
                        style = SpanStyle(
                            fontSize = iconSize,
                            baselineShift = BaselineShift(0.05f)
                        )
                    ) {
                        append(packPosition.toString())
                    }
                }
            }

            Text(
                text = nameString,
                style = CustomTheme.typography.cardName,
                color = Color.White,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }

        subname?.let {
            Text(
                text = subname.iconize(iconSize = 12.sp, color = Color.White),
                style = CustomTheme.typography.cardTraits,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.White,
            )
        }
    }
}