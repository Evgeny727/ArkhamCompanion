package com.arkhamcompanion.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.arkhamcompanion.R
import com.arkhamcompanion.ui.utils.appSp

@Immutable
data class CustomTypography(
    val scaleFactor: Float = 1f,
    val cursive: TextStyle,
    val searchLabel: TextStyle,
    val smallLabel: TextStyle,
    val smallButtonLabel: TextStyle,
    val small: TextStyle,
    val tiny: TextStyle,
    val menuText: TextStyle,
    val cardName: TextStyle,
    val cardTraits: TextStyle,
    val cardText: TextStyle,
    val counter: TextStyle,
    val large: TextStyle,
    val header: TextStyle,
    val button: TextStyle,
    val subHeaderText: TextStyle,
    val text: TextStyle,
    val regular: TextStyle,
    val bold: TextStyle,
    val boldItalic: TextStyle,
    val italic: TextStyle,
    val simpleTitleFont: TextStyle,
    val gameFont: TextStyle,
    val mediumGameFont: TextStyle,
    val bigGameFont: TextStyle,
    val dialogLabel: TextStyle,
    val strikethrough: SpanStyle,
    val underline: SpanStyle,
)

val Caveat = FontFamily(
    Font(R.font.caveat, FontWeight.Normal),
)

val Alegreya = FontFamily(
    Font(R.font.alegreya_regular, FontWeight.Normal),
    Font(R.font.alegreya_medium, FontWeight.Medium),
    Font(R.font.alegreya_bold, FontWeight.Bold),
    Font(R.font.alegreya_extrabold, FontWeight.ExtraBold),
    Font(R.font.alegreya_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.alegreya_mediumitalic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.alegreya_bolditalic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.alegreya_extrabolditalic, FontWeight.ExtraBold, FontStyle.Italic),
)

val PingFangTC = FontFamily(
    Font(R.font.pingfangtc_light, FontWeight.Normal),
    Font(R.font.pingfangtc_semibold, FontWeight.SemiBold),
)

val TeutonicRU = FontFamily(
    Font(R.font.teutonic_ru, FontWeight.Normal),
)

val Arkhamic = FontFamily(
    Font(R.font.arkhamic, FontWeight.Normal),
)

//icon fonts
val EncounterIconsFont = FontFamily(
    Font(R.font.encounter_icons),
)
val AppIconsFont = FontFamily(
    Font(R.font.app_icons),
)

internal fun typography(
    colorScheme: CustomColors,
    lang: String,
    scaleFactor: Float = 1f,
    usePingFang: Boolean
): CustomTypography {
    val italicFont = if (usePingFang) PingFangTC else Alegreya
    val gameFont = if (lang == "ru") TeutonicRU else Arkhamic
    return typography.copy(
        scaleFactor = scaleFactor,
        cursive = TextStyle(
            fontFamily = Caveat,
            fontSize = 22.appSp(scaleFactor),
            lineHeight = 24.appSp(scaleFactor),
            color = colorScheme.d30
        ),
        searchLabel = TextStyle(
            fontFamily = Alegreya,
            fontSize = 16.appSp(scaleFactor),
            lineHeight = 20.appSp(scaleFactor),
            color = colorScheme.l20
        ),
        smallLabel = TextStyle(
            fontFamily = Alegreya,
            fontSize = 15.appSp(scaleFactor),
            lineHeight = 18.appSp(scaleFactor),
            letterSpacing = 0.3.sp,
            color = colorScheme.lightText
        ),
        smallButtonLabel = TextStyle(
            fontFamily = italicFont,
            fontStyle = FontStyle.Italic,
            fontSize = 14.appSp(scaleFactor),
            lineHeight = (if (lang == "zh" || lang == "zh-CN") 17 else 16).appSp(scaleFactor),
            letterSpacing = 0.3.sp,
            color = colorScheme.lightText
        ),
        small = TextStyle(
            fontFamily = Alegreya,
            fontSize = 16.appSp(scaleFactor),
            lineHeight = 18.appSp(scaleFactor),
            color = colorScheme.darkText
        ),
        tiny = TextStyle(
            fontFamily = Alegreya,
            fontSize = 12.appSp(scaleFactor),
            lineHeight = (if (lang == "zh" || lang == "zh-CN") 16 else 14).appSp(scaleFactor),
            color = colorScheme.darkText
        ),
        menuText = TextStyle(
            fontFamily = Alegreya,
            fontWeight = FontWeight.Medium,
            fontSize = 18.appSp(scaleFactor),
            lineHeight = 20.appSp(scaleFactor),
            color = colorScheme.d30,
        ),
        cardName = TextStyle(
            fontFamily = Alegreya,
            fontWeight = FontWeight.Medium,
            fontSize = 20.appSp(scaleFactor),
            lineHeight = (if (lang == "zh" || lang == "zh-CN") 24 else 22).appSp(scaleFactor),
            color = colorScheme.darkText,
        ),
        cardTraits = TextStyle(
            fontFamily = italicFont,
            fontStyle = FontStyle.Italic,
            fontSize = 16.appSp(scaleFactor),
            lineHeight = 18.appSp(scaleFactor),
            color = colorScheme.lightText
        ),
        cardText = TextStyle(
            fontFamily = Alegreya,
            fontSize = 16.appSp(scaleFactor),
            lineHeight = 20.appSp(scaleFactor),
            color = colorScheme.darkText
        ),
        counter = TextStyle(
            fontFamily = Alegreya,
            fontWeight = FontWeight.Medium,
            fontSize = 24.appSp(scaleFactor),
            lineHeight = (if (lang == "zh" || lang == "zh-CN") 28 else 26).appSp(scaleFactor),
            color = colorScheme.d10,
        ),
        large = TextStyle(
            fontFamily = Alegreya,
            fontWeight = FontWeight.Medium,
            fontSize = 18.appSp(scaleFactor),
            lineHeight = (if (lang == "zh" || lang == "zh-CN") 22 else 20).appSp(scaleFactor),
            color = colorScheme.darkText,
        ),
        header = TextStyle(
            fontFamily = Alegreya,
            fontWeight = FontWeight.Medium,
            fontSize = 22.appSp(scaleFactor),
            lineHeight = (if (lang == "zh" || lang == "zh-CN") 26 else 24).appSp(scaleFactor),
            color = colorScheme.darkText,
        ),
        button = TextStyle(
            fontFamily = Alegreya,
            fontSize = 18.appSp(scaleFactor),
            lineHeight = (if (lang == "zh" || lang == "zh-CN") 22 else 20).appSp(scaleFactor),
            color = colorScheme.l30,
        ),
        subHeaderText = TextStyle(
            fontFamily = Alegreya,
            fontWeight = FontWeight.Medium,
            fontSize = 18.appSp(scaleFactor),
            lineHeight = 22.appSp(scaleFactor),
            color = colorScheme.d10,
        ),
        text = TextStyle(
            fontFamily = Alegreya,
            fontSize = 18.appSp(scaleFactor),
            lineHeight = 22.appSp(scaleFactor),
            color = colorScheme.darkText,
        ),
        regular = TextStyle(
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal
        ),
        bold = TextStyle(
            fontWeight = FontWeight.Bold,
        ),
        boldItalic = TextStyle(
            fontFamily = italicFont,
            fontWeight = if (usePingFang) FontWeight.SemiBold else FontWeight.ExtraBold,
            fontStyle = FontStyle.Italic,
        ),
        italic = TextStyle(
            fontFamily = italicFont,
            fontStyle = FontStyle.Italic,
        ),
        simpleTitleFont = TextStyle(
            fontFamily = Alegreya,
            fontWeight = FontWeight.Medium,
            fontSize = 28.appSp(scaleFactor),
            lineHeight = 36.appSp(scaleFactor),
            color = colorScheme.darkText,
        ),
        gameFont = TextStyle(
            fontFamily = gameFont,
            fontSize = 18.appSp(scaleFactor),
            lineHeight = 26.appSp(scaleFactor),
            color = colorScheme.darkText,
        ),
        mediumGameFont = TextStyle(
            fontFamily = gameFont,
            fontSize = 24.appSp(scaleFactor),
            lineHeight = 30.appSp(scaleFactor),
            color = colorScheme.darkText,
        ),
        bigGameFont = TextStyle(
            fontFamily = gameFont,
            fontSize = 28.appSp(scaleFactor),
            lineHeight = 36.appSp(scaleFactor),
            color = colorScheme.darkText,
        ),
        dialogLabel = TextStyle(
            fontSize = 16.appSp(scaleFactor), //13 for iOS
            color = colorScheme.darkText,
        ),
    )
}

val typography = CustomTypography(
    cursive = TextStyle(),
    searchLabel = TextStyle(),
    smallLabel = TextStyle(),
    smallButtonLabel = TextStyle(),
    small = TextStyle(),
    tiny = TextStyle(),
    menuText = TextStyle(),
    cardName = TextStyle(),
    cardTraits = TextStyle(),
    cardText = TextStyle(),
    counter = TextStyle(),
    large = TextStyle(),
    header = TextStyle(),
    button = TextStyle(),
    subHeaderText = TextStyle(),
    text = TextStyle(),
    regular = TextStyle(),
    bold = TextStyle(),
    boldItalic = TextStyle(),
    italic = TextStyle(),
    simpleTitleFont = TextStyle(),
    gameFont = TextStyle(),
    mediumGameFont = TextStyle(),
    bigGameFont = TextStyle(),
    dialogLabel = TextStyle(),
    strikethrough = SpanStyle(textDecoration = TextDecoration.LineThrough),
    underline = SpanStyle(textDecoration = TextDecoration.Underline),
)

val LocalCustomTypography = compositionLocalOf { typography }