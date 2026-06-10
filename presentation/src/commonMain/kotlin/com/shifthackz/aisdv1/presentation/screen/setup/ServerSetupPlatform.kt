package com.shifthackz.aisdv1.presentation.screen.setup

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal expect fun ServerSetupLocalPathPickerButton(
    modifier: Modifier,
    text: String,
    onPathSelected: (String) -> Unit,
)

internal expect fun isLocalGenerationSetupAvailable(): Boolean
