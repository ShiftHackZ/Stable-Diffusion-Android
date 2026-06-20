package com.shifthackz.aisdv1.presentation.screen.setup.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shifthackz.aisdv1.domain.entity.ServerSource

/**
 * Opens a platform directory picker for custom local model folders.
 */
@Composable
internal expect fun ServerSetupLocalPathPickerButton(
    modifier: Modifier,
    text: String,
    onPathSelected: (String) -> Unit,
)

/**
 * True when the current target can configure custom local model folders.
 *
 * Android depends on storage access state; iOS currently returns false for local runtimes that
 * cannot use arbitrary filesystem paths.
 */
internal expect fun isLocalGenerationSetupAvailable(): Boolean

/**
 * Filters providers that are impossible on the current target, such as a missing runtime ABI.
 *
 * Device capability and performance checks belong to benchmark/setup validation rather than the
 * first provider list, so users can still discover providers supported in principle.
 */
internal expect fun isServerSourceAvailableOnPlatform(source: ServerSource): Boolean
