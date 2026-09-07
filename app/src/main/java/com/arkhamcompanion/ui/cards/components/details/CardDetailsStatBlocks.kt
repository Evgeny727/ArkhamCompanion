package com.arkhamcompanion.ui.cards.components.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.arkhamcompanion.ui.components.ArkhamScalableIconText
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.theme.AppIconsFont
import com.arkhamcompanion.ui.theme.CustomTheme
import com.arkhamcompanion.ui.utils.appSp
import com.arkhamcompanion.ui.utils.scaledByFont

@Composable
fun CardDetailsInvestigatorStatLine(
    skillWillpower: Int?,
    skillIntellect: Int?,
    skillCombat: Int?,
    skillAgility: Int?,
) {
    FlowRow(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CardDetailsStatValue(
            skillCode = "willpower",
            skillValue = skillWillpower
        )

        CardDetailsStatValue(
            skillCode = "intellect",
            skillValue = skillIntellect
        )

        CardDetailsStatValue(
            skillCode = "combat",
            skillValue = skillCombat
        )

        CardDetailsStatValue(
            skillCode = "agility",
            skillValue = skillAgility
        )
    }
}

@Composable
fun CardDetailsEnemyStatBlock(
    enemyFight: Int?,
    enemyFightPerInvestigator: Boolean,
    health: Int?,
    healthPerInvestigator: Boolean,
    enemyEvade: Int?,
    enemyEvadePerInvestigator: Boolean ,
    enemyDamage: Int?,
    enemyHorror: Int?,
) {
    val density = LocalDensity.current
    val rowHeight = with(density) {
        (34 * CustomTheme.typography.scaleFactor * fontScale).dp
    }

    FlowRow(
        modifier = Modifier.height(rowHeight),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CardDetailsStatValue(
            skillCode = "combat",
            skillValue = enemyFight,
            perInvestigator = enemyFightPerInvestigator,
            reversed = true,
            modifier = Modifier.height(rowHeight)
        )

        Box(
            modifier = Modifier.fillMaxHeight().background(
                color = CustomTheme.colors.l20,
                shape = CustomTheme.shapes.small
            ),
            contentAlignment = Alignment.Center
        ) {
            val scaleFactor = CustomTheme.typography.scaleFactor
            val healthText = specialNumericValue(health, 18.appSp(scaleFactor))
            val isNumericValue = healthText.isDigitsOnly() || healthText.text == "?"
            val topPadding = if (isNumericValue) 2.dp.scaledByFont(scaleFactor) else 0.dp

            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = healthText,
                    style = CustomTheme.typography.mediumGameFont,
                    color = CustomTheme.colors.darkText,
                    modifier = Modifier.padding(top = topPadding)
                )

                if (healthPerInvestigator) {
                    ArkhamScalableIconText(
                        iconGlyph = AppIcon.PerInvestigator,
                        size = 18.appSp(scaleFactor),
                        color = CustomTheme.colors.darkText,
                        modifier = Modifier.align(Alignment.Top).padding(top = 2.dp)
                    )
                }
            }
        }

        CardDetailsStatValue(
            skillCode = "agility",
            skillValue = enemyEvade,
            perInvestigator = enemyEvadePerInvestigator,
            modifier = Modifier.height(rowHeight)
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    FlowRow(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        enemyDamage?.let {
            repeat(it) {
                HealthSanityIcon(
                    isHealth = true,
                    iconSize = 24.appSp(CustomTheme.typography.scaleFactor)
                )
            }
        }
        enemyHorror?.let {
            repeat(it) {
                HealthSanityIcon(
                    isHealth = false,
                    iconSize = 24.appSp(CustomTheme.typography.scaleFactor)
                )
            }
        }
    }
}

@Composable
fun CardDetailsStatValue(
    skillCode: String,
    skillValue: Int?,
    modifier: Modifier = Modifier,
    perInvestigator: Boolean = false,
    reversed: Boolean = false,
) {
    val scaleFactor = CustomTheme.typography.scaleFactor
    val text = specialNumericValue(skillValue, 18.appSp(scaleFactor))
    val isNumericValue = text.isDigitsOnly() || text.text == "?"
    val topPadding = if (isNumericValue) 2.dp.scaledByFont(scaleFactor) else 0.dp

    Box(
        modifier = modifier.background(
            color = CustomTheme.colors.l20,
            shape = CustomTheme.shapes.small
        ),
        contentAlignment = Alignment.Center
    ) {

        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            if (!reversed) {
                Text(
                    text = text,
                    style = CustomTheme.typography.mediumGameFont,
                    color = CustomTheme.colors.darkText,
                    modifier = Modifier.padding(top = topPadding)
                )
                if (perInvestigator) {
                    ArkhamScalableIconText(
                        iconGlyph = AppIcon.PerInvestigator,
                        size = 18.appSp(scaleFactor),
                        color = CustomTheme.colors.darkText,
                        modifier = Modifier.align(Alignment.Top).padding(top = 2.dp.scaledByFont(scaleFactor))
                    )
                }
            }

            SkillIcon(
                skillCode = skillCode,
                iconSize = 26.appSp(scaleFactor),
            )

            if (reversed) {
                Text(
                    text = text,
                    style = CustomTheme.typography.mediumGameFont,
                    color = CustomTheme.colors.darkText,
                    modifier = Modifier.padding(top = topPadding)
                )
                if (perInvestigator) {
                    ArkhamScalableIconText(
                        iconGlyph = AppIcon.PerInvestigator,
                        size = 18.appSp(scaleFactor),
                        color = CustomTheme.colors.darkText,
                        modifier = Modifier.align(Alignment.Top).padding(top = 2.dp.scaledByFont(scaleFactor))
                    )
                }
            }
        }
    }
}

fun specialNumericValue(value: Int?, size: TextUnit) = buildAnnotatedString {
    when (value) {
        null -> withStyle(
            style = SpanStyle(
                fontFamily = AppIconsFont,
                fontSize = size
            )
        ) {
            append(AppIcon.Numnull.glyph)
        }
        -2 -> withStyle(
            style = SpanStyle(
                fontFamily = AppIconsFont,
                fontSize = size
            )
        ) {
            append(AppIcon.X.glyph)
        }
        -3 -> withStyle(
            style = SpanStyle(
                fontFamily = AppIconsFont,
                fontSize = size
            )
        ) {
            append(AppIcon.Star.glyph)
        }
        -4 -> append("?")
        else -> append(value.toString())
    }
}