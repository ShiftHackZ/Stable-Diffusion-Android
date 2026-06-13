package com.shifthackz.aisdv1.presentation.screen.setup

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shifthackz.aisdv1.domain.entity.ServerSource
import platform.UIKit.UIDevice

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

/**
 * Executes the `isServerSourceAvailableOnPlatform` step in the SDAI presentation layer.
 *
 * @param source source value consumed by the API.
 * @return Result produced by `isServerSourceAvailableOnPlatform`.
 * @author Dmitriy Moroz
 */
internal actual fun isServerSourceAvailableOnPlatform(source: ServerSource): Boolean = when (source) {
    ServerSource.LOCAL_MICROSOFT_ONNX,
    ServerSource.LOCAL_GOOGLE_MEDIA_PIPE,
    ServerSource.LOCAL_STABLE_DIFFUSION_CPP,
    -> false

    ServerSource.LOCAL_APPLE_CORE_ML -> isCoreMlRuntimeAvailable()

    else -> true
}

private fun isCoreMlRuntimeAvailable(): Boolean =
    UIDevice.currentDevice.systemVersion
        .split(".")
        .mapNotNull(String::toIntOrNull)
        .let { parts ->
            val major = parts.getOrElse(0) { 0 }
            val minor = parts.getOrElse(1) { 0 }
            major > 16 || major == 16 && minor >= 2
        }
