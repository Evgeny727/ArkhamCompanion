package com.arkhamcompanion.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.arkhamcompanion.R
import com.arkhamcompanion.ui.theme.CustomTheme

@Composable
fun BackUpScreen(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.not_implemented_text),
            style = CustomTheme.typography.large
        )
    }
}