package com.shifthackz.aisdv1.presentation.screen.setup

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
internal expect fun ServerSetupLocalPathPickerButton(
    modifier: Modifier,
    text: String,
    onPathSelected: (String) -> Unit,
)

/**
 * Executes the `isLocalGenerationSetupAvailable` step in the SDAI presentation layer.
 *
 * @return Result produced by `isLocalGenerationSetupAvailable`.
 * @author Dmitriy Moroz
 */
internal expect fun isLocalGenerationSetupAvailable(): Boolean
