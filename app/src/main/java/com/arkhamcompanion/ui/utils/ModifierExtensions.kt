package com.arkhamcompanion.ui.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

internal fun Modifier.applyScaffoldPaddings(paddingValues: PaddingValues) =
    padding(
        top = paddingValues.calculateTopPadding(),
        bottom = paddingValues.calculateBottomPadding()
    )

@Composable
internal fun Modifier.applyNavigationBarInsets() =
    windowInsetsPadding(WindowInsets.navigationBars)