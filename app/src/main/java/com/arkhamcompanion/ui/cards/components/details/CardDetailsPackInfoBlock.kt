package com.arkhamcompanion.ui.cards.components.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.domain.model.cards.CardDetailsWithPackInfo
import com.arkhamcompanion.ui.components.ArkhamScalableIconText
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.icons.PackIcon
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.appSp

@Composable
fun CardDetailsPackInfoBlock(
    cardDetailsWithPackInfo: CardDetailsWithPackInfo,
    modifier: Modifier = Modifier,
    firstPackInCollection: String? = null,
    onlyIllustrator: Boolean = false
) {
    HorizontalDivider(color = CustomTheme.colors.l10)

    cardDetailsWithPackInfo.cardDetails.run {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            illustrator?.let { illustrator ->
                Row(
                    modifier = Modifier.weight(0.5f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ArkhamScalableIconText(
                        iconGlyph = AppIcon.Paintbrush,
                        size = 14.appSp(CustomTheme.typography.scaleFactor),
                        color = CustomTheme.colors.d20
                    )

                    Text(
                        text = illustrator,
                        style = CustomTheme.typography.tiny
                    )
                }
            }

            if (!onlyIllustrator) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    encounterCode?.let {
                        val icon = PackIcon.fromPackCode(it)!!
                        val quantityText = encounterPosition?.let { position ->
                            if (quantity > 1) "$encounterPosition - ${position + quantity - 1}"
                            else position.toString()
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = encounterName!!,
                                style = CustomTheme.typography.tiny,
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(1f, fill = false)
                            )

                            ArkhamScalableIconText(
                                iconGlyph = icon,
                                size = 14.appSp(CustomTheme.typography.scaleFactor),
                                color = CustomTheme.colors.darkText
                            )

                            quantityText?.let {
                                Text(
                                    text = quantityText,
                                    style = CustomTheme.typography.tiny
                                )
                            }
                        }
                    }

                    cardDetailsWithPackInfo.allPacks.forEach { pack ->
                        val icon = PackIcon.fromPackCode(pack.code)!!
                        val firstPack = firstPackInCollection == pack.code

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = pack.name,
                                style = CustomTheme.typography.tiny.copy(
                                    fontWeight = if (firstPack) FontWeight.SemiBold else FontWeight.Normal
                                ),
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(1f, fill = false)
                            )

                            Spacer(modifier = Modifier.width(0.dp))

                            ArkhamScalableIconText(
                                iconGlyph = icon,
                                size = 14.appSp(CustomTheme.typography.scaleFactor),
                                color = CustomTheme.colors.darkText
                            )

                            Spacer(modifier = Modifier.width(0.dp))

                            Text(
                                text = pack.position.toString(),
                                style = CustomTheme.typography.tiny.copy(
                                    fontWeight = if (firstPack) FontWeight.SemiBold else FontWeight.Normal
                                )
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            ArkhamScalableIconText(
                                iconGlyph = AppIcon.CardOutline,
                                size = 14.appSp(CustomTheme.typography.scaleFactor),
                                color = CustomTheme.colors.darkText
                            )

                            Text(
                                text = "×${pack.quantity}",
                                style = CustomTheme.typography.tiny.copy(
                                    fontWeight = if (firstPack) FontWeight.SemiBold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}