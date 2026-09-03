package com.arkhamcompanion.ui.settings.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.ui.components.ArkhamButtonColor
import com.arkhamcompanion.ui.components.ArkhamIconText
import com.arkhamcompanion.ui.components.ArkhamRoundedCardHeader
import com.arkhamcompanion.ui.components.ArkhamRoundedFactionCard
import com.arkhamcompanion.ui.components.ArkhamSquareButton
import com.arkhamcompanion.ui.components.iconSize
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.utils.openEmail

const val patreonLink = "https://patreon.com/rangerscards"
const val supportEmail = "arkhamcompanion@gmail.com"

@Composable
fun SupportCard(
    navigateToAbout: () -> Unit,
    navigateToBackUp: () -> Unit,
    navigateToDiagnostics: () -> Unit,
) {
    val context = LocalContext.current

    ArkhamRoundedFactionCard(
        faction = Faction.Neutral,
        header = {
            ArkhamRoundedCardHeader(
                title = stringResource(R.string.support),
                faction = Faction.Neutral,
            )
        },
    ) {
        //TODO:add button after creating separate patreon account
//        ArkhamSquareButton(
//            title = stringResource(R.string.patreon_button),
//            onClick = remember { {
//                context.openLink(patreonLink)
//            } },
//        ) { color ->
//            Icon(
//                painter = painterResource(R.drawable.patreon_logo),
//                contentDescription = null,
//                tint = color,
//                modifier = Modifier.size(24.dp)
//            )
//        }
        ArkhamSquareButton(
            title = stringResource(R.string.about_arkham_companion),
            onClick = navigateToAbout,
        ) { color ->
            ArkhamIconText(
                iconGlyph = AppIcon.Logo,
                color = color,
                size = iconSize(AppIcon.Logo)
            )
        }
        //TODO: add button after implementing backup
//        ArkhamSquareButton(
//            title = stringResource(R.string.backup_data),
//            onClick = navigateToBackUp,
//        ) { color ->
//            ArkhamIconText(
//                iconGlyph = AppIcon.Book,
//                color = color,
//                size = iconSize(AppIcon.Book)
//            )
//        }
        ArkhamSquareButton(
            title = stringResource(R.string.diagnostics),
            onClick = navigateToDiagnostics,
        ) { color ->
            ArkhamIconText(
                iconGlyph = AppIcon.Wrench,
                color = color,
                size = iconSize(AppIcon.Wrench)
            )
        }

        ArkhamSquareButton(
            title = stringResource(R.string.contact_us),
            onClick = remember {
                {
                    context.openEmail(supportEmail)
                }
            },
            colors = ArkhamButtonColor.Gold
        ) { color ->
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}