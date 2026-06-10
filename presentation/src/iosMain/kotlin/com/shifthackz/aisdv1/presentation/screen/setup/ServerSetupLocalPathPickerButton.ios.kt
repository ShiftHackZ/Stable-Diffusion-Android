package com.shifthackz.aisdv1.presentation.screen.setup

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Renders the `ServerSetupLocalPathPickerButton` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param text text value consumed by the API.
 * @param onPathSelected callback invoked by the component.
 * @author Dmitriy Moroz
 */
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

/**
 * Executes the `isLocalGenerationSetupAvailable` step in the SDAI presentation layer.
 *
 * @return Result produced by `isLocalGenerationSetupAvailable`.
 * @author Dmitriy Moroz
 */
internal actual fun isLocalGenerationSetupAvailable(): Boolean = false
