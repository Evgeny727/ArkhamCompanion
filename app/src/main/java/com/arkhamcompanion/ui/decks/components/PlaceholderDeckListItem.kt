package com.arkhamcompanion.ui.decks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.ui.cards.components.factionIcon
import com.arkhamcompanion.ui.components.ArkhamRoundedFactionCard
import com.arkhamcompanion.ui.components.ArkhamScalableIconText
import com.arkhamcompanion.ui.components.factionColor
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.appSp

@Composable
fun PlaceholderDeckListItem(modifier: Modifier = Modifier) {
    val faction = Faction.Neutral

    ArkhamRoundedFactionCard(
        faction = faction,
        modifier = modifier,
        header = {
            val factionColor = factionColor(faction)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                        color = factionColor.background,
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
                        url = null,
                        factionIcon = factionIcon,
                        alignment = Alignment.Center,
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
                        Box(
                            Modifier
                                .height(24.dp)
                                .fillMaxWidth(0.7f)
                                .background(color = CustomTheme.colors.m, shape = CustomTheme.shapes.small)
                        )

                        Box(
                            Modifier
                                .height(20.dp)
                                .fillMaxWidth(0.3f)
                                .background(color = CustomTheme.colors.m, shape = CustomTheme.shapes.small)
                        )
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
                Box(
                    Modifier
                        .height(28.dp)
                        .width(128.dp)
                        .background(color = CustomTheme.colors.m, shape = CustomTheme.shapes.small)
                )
            }
        }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ArkhamScalableIconText(
                iconGlyph = AppIcon.TagBold,
                size = 18.appSp(CustomTheme.typography.scaleFactor),
                color = CustomTheme.colors.m,
            )

            Box(
                Modifier
                    .height(28.dp)
                    .width(72.dp)
                    .background(color = CustomTheme.colors.m, shape = CustomTheme.shapes.small)
            )

            Box(
                Modifier
                    .height(28.dp)
                    .width(48.dp)
                    .background(color = CustomTheme.colors.m, shape = CustomTheme.shapes.small)
            )

            Box(
                Modifier
                    .height(28.dp)
                    .width(72.dp)
                    .background(color = CustomTheme.colors.m, shape = CustomTheme.shapes.small)
            )
        }
    }
}