package com.shifthackz.aisdv1.presentation.screen.setup

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal actual fun ServerSetupLocalPathPickerButton(
    modifier: Modifier,
    text: String,
    onPathSelected: (String) -> Unit,
) {
    OutlinedButton(
        modifier = modifier,
        enabled = false,
        onClick = {},
    ) {
        Text(text = text)
    }
}

internal actual fun isLocalGenerationSetupAvailable(): Boolean = false
