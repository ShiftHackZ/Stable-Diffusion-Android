package com.shifthackz.aisdv1.presentation.screen.setup.platform

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shifthackz.aisdv1.domain.entity.ServerSource
import platform.UIKit.UIDevice

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
