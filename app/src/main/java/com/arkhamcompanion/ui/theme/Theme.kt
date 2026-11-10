package com.arkhamcompanion.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat

object CustomTheme {
    val typography: CustomTypography
        @ReadOnlyComposable
        @Composable
        get() = LocalCustomTypography.current
    val colors: CustomColors
        @ReadOnlyComposable
        @Composable
        get() = LocalCustomColors.current
    val shapes: CustomShape
        @ReadOnlyComposable
        @Composable
        get() = LocalCustomShapes.current
    val language: Language
        @ReadOnlyComposable
        @Composable
        get() = LocalLanguage.current
}

@Composable
fun ArkhamCompanionTheme(
    darkTheme: Boolean,
    lang: String,
    scaleFactor: Float = 1f,
    topAppBarColor: Color?,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val typography = typography(colorScheme, lang, scaleFactor, usePingFang = false)
    val language = language(lang)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowInsetsControllerCompat(window, window.decorView)

            controller.isAppearanceLightStatusBars =
                (topAppBarColor ?: colorScheme.background).luminance() > 0.5f
            controller.isAppearanceLightNavigationBars =
                colorScheme.background.luminance() > 0.5f

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }
        }
    }
    CompositionLocalProvider(
        LocalCustomColors provides colorScheme,
        LocalCustomTypography provides typography,
        LocalCustomShapes provides CustomTheme.shapes,
        LocalLanguage provides language,
        content = content
    )
}