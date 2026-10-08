package com.arkhamcompanion.ui.cards.components.details

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAbsoluteAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.arkhamcompanion.R
import com.arkhamcompanion.domain.enums.CardBackType
import com.arkhamcompanion.domain.enums.CardSubType
import com.arkhamcompanion.domain.enums.CardType
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.ui.cards.components.factionIcon
import com.arkhamcompanion.ui.components.ArkhamButtonColor
import com.arkhamcompanion.ui.components.ArkhamDialog
import com.arkhamcompanion.ui.components.ArkhamIconText
import com.arkhamcompanion.ui.components.ArkhamSquareButton
import com.arkhamcompanion.ui.components.iconSize
import com.arkhamcompanion.ui.icons.AppIcon
import com.arkhamcompanion.ui.icons.PackIcon
import com.arkhamcompanion.ui.theme.CustomTheme

@Composable
fun CardDetailsClickableThumbnail(
    thumbnailUrl: String?,
    imageUrl: String?,
    backImageUrl: String?,
    taboSetId: Int?,
    backTaboSetId: Int?,
    code: String,
    backCode: String?,
    type: CardType,
    backCardType: CardType?,
    backType: CardBackType,
    encounterCode: String?,
    subType: CardSubType?,
    faction: Faction,
    faction2: Faction?,
    modifier: Modifier = Modifier,
    isBackFirst: Boolean = false,
) {
    var hidePlaceholder by remember { mutableStateOf(false) }
    var showFullImageDialog by remember { mutableStateOf(false) }

    Box(contentAlignment = Alignment.Center) {
        if (!hidePlaceholder) {
            Box(
                modifier = modifier
                    .size(108.dp)
                    .clip(CustomTheme.shapes.medium)
                    .background(CustomTheme.colors.divider)
                    .border(1.dp, CustomTheme.colors.darkText, CustomTheme.shapes.medium)
                    .clickable(enabled = imageUrl != null) { showFullImageDialog = true },
                contentAlignment = Alignment.Center
            ) {
                val icon = if (encounterCode != null) {
                    PackIcon.fromPackCode(encounterCode)
                } else {
                    factionIcon(faction, faction2, subType)
                }

                Text(
                    text = icon.glyph,
                    fontFamily = icon.fontFamily,
                    fontSize = 48.sp,
                    color = CustomTheme.colors.lightText
                )
            }
        }

        if (thumbnailUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(thumbnailUrl)
                    .build(),
                modifier = modifier
                    .size(108.dp)
                    .clip(CustomTheme.shapes.medium)
                    .clickable(enabled = imageUrl != null) { showFullImageDialog = true },
                onSuccess = { hidePlaceholder = true },
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
        } else if (imageUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .build(),
                modifier = modifier
                    .size(108.dp)
                    .clip(CustomTheme.shapes.medium)
                    .clickable { showFullImageDialog = true },
                onSuccess = { hidePlaceholder = true },
                contentDescription = null,
                alignment = type.imageOffset.toAlignment(),
                contentScale = ContentScale.Crop,
            )
        }
    }

    if (showFullImageDialog) {
        CardDetailsFullImageDialog(
            code = code,
            backCode = backCode,
            cardType = type,
            backCardType = backCardType,
            taboSetId = taboSetId,
            backTaboSetId = backTaboSetId,
            imageUrl = imageUrl,
            backImageUrl = backImageUrl,
            isDoubleSided = backCode != null || backType == CardBackType.Card,
            isBackFirst = isBackFirst,
            onDismiss = { showFullImageDialog = false },
        )
    }
}

private fun Pair<Float, Float>.toAlignment(): Alignment =
    BiasAbsoluteAlignment(first, second)

@Composable
private fun CardDetailsFullImageDialog(
    code: String,
    backCode: String?,
    cardType: CardType,
    backCardType: CardType?,
    taboSetId: Int?,
    backTaboSetId: Int?,
    imageUrl: String?,
    backImageUrl: String?,
    isDoubleSided: Boolean,
    isBackFirst: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var hidePlaceholder by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    var showWithoutTaboo by remember { mutableStateOf(false) }
    var showBack by remember { mutableStateOf(isBackFirst) }

    val isFrontSideways = isSideways(cardType, code)
    val isBackSideways = when {
        isDoubleSided && backCode == null -> isFrontSideways
        isDoubleSided -> isSideways(backCardType, backCode)
        else -> false
    }
    val sideWayWidth = 310.dp
    val sideWayHeight = 220.dp

    val backTabooId = if (backCode != null) backTaboSetId else taboSetId
    var imageUrl by remember { mutableStateOf(imageUrl) }
    var backImageUrl by remember { mutableStateOf(backImageUrl) }


    ArkhamDialog(
        title = stringResource(R.string.card_scan),
        modifier = modifier,
        onDismiss = onDismiss,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item(key = "image", contentType = "image") {
                Box(
                    modifier = modifier
                        .size(310.dp)
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    if (!hidePlaceholder) {
                        Box(
                            modifier = Modifier
                                .size(310.dp)
                                .clip(CustomTheme.shapes.medium)
                                .background(CustomTheme.colors.divider)
                                .border(
                                    1.dp,
                                    CustomTheme.colors.darkText,
                                    CustomTheme.shapes.medium
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            ArkhamIconText(
                                iconGlyph = AppIcon.Logo,
                                size = 72.dp,
                                color = CustomTheme.colors.lightText
                            )
                        }
                    }

                    if (isLoading) CircularProgressIndicator(
                        color = CustomTheme.colors.darkText,
                        modifier = Modifier
                            .size(56.dp)
                            .align(Alignment.Center)
                    )

                    FlippableCard(
                        frontUrl = imageUrl,
                        backUrl = backImageUrl,
                        frontWidth = if (isFrontSideways) sideWayWidth else sideWayHeight,
                        backWidth = if (isBackSideways) sideWayWidth else sideWayHeight,
                        frontHeight = if (isFrontSideways) sideWayHeight else sideWayWidth,
                        backHeight = if (isBackSideways) sideWayHeight else sideWayWidth,
                        isFlipped = showBack,
                        onSuccess = { hidePlaceholder = true; isLoading = false },
                        onError = { hidePlaceholder = false; isLoading = false },
                        onLoading = { isLoading = true },
                    )
                }
            }

            item(key = "flip_button", contentType = "button") {
                ArkhamSquareButton(
                    title = stringResource(R.string.flip_card),
                    onClick = { showBack = !showBack },
                    colors = ArkhamButtonColor.Default,
                    icon = { color ->
                        ArkhamIconText(
                            iconGlyph = AppIcon.FlipCard,
                            size = iconSize(AppIcon.FlipCard),
                            color = color
                        )
                    }
                )
            }

            if (taboSetId != null) item(key = "taboo_button", contentType = "button") {
                ArkhamSquareButton(
                    title = stringResource(
                        if (showWithoutTaboo) R.string.card_scan_with_taboo
                        else R.string.card_scan_without_taboo
                    ),
                    onClick = {
                        val newShowWithoutTaboo = !showWithoutTaboo
                        showWithoutTaboo = newShowWithoutTaboo

                        if (newShowWithoutTaboo) {
                            imageUrl = imageUrl.removeTaboo(taboSetId)
                            backImageUrl = backImageUrl.removeTaboo(backTabooId)
                        } else {
                            imageUrl = imageUrl.applyTaboo(taboSetId)
                            backImageUrl = backImageUrl.applyTaboo(backTabooId)
                        }
                    },
                    colors = ArkhamButtonColor.Default,
                    icon = { color ->
                        ArkhamIconText(
                            iconGlyph = AppIcon.Taboo,
                            size = iconSize(AppIcon.Taboo),
                            color = color
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun FlippableCard(
    frontUrl: String?,
    backUrl: String?,
    frontWidth: Dp,
    backWidth: Dp,
    frontHeight: Dp,
    backHeight: Dp,
    isFlipped: Boolean,
    onSuccess: () -> Unit,
    onLoading: () -> Unit,
    onError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "cardRotation"
    )

    val showBack = rotation >= 90f

    val width = if (showBack) backWidth else frontWidth
    val height = if (showBack) backHeight else frontHeight

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12 * density
            }
    ) {
        AsyncImage(
            model = if (showBack) backUrl else frontUrl,
            contentDescription = null,
            onSuccess = { onSuccess() },
            onLoading = { onLoading() },
            onError = { onError() },
            modifier = Modifier
                .fillMaxSize()
                .clip(CustomTheme.shapes.large)
                .graphicsLayer {
                    rotationY = if (showBack) 180f else 0f
                },
            contentScale = ContentScale.Crop,
        )
    }
}

private fun String?.applyTaboo(tabooSetId: Int?): String? {
    if (this == null) return null
    if (tabooSetId == null) return this

    return replaceFirst(".webp", "-$tabooSetId.webp")
}

private fun String?.removeTaboo(tabooSetId: Int?): String? {
    if (this == null) return null
    if (tabooSetId == null) return this

    return replaceFirst("-$tabooSetId", "")
}

private fun isSideways(cardType: CardType?, code: String?): Boolean {
    val result = cardType in SIDEWAYS_TYPE_CODES
    return if (code in ORIENTATION_CHANGED_CARDS) !result else result
}

private val SIDEWAYS_TYPE_CODES = arrayOf(CardType.Act, CardType.Agenda, CardType.Investigator)
private val ORIENTATION_CHANGED_CARDS = arrayOf("85037", "85038")