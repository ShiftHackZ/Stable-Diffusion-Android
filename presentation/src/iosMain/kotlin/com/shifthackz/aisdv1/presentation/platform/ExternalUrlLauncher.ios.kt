package com.shifthackz.aisdv1.presentation.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Renders the `rememberExternalUrlLauncher` UI for the SDAI presentation layer.
 *
 * @return Result produced by `rememberExternalUrlLauncher`.
 * @author Dmitriy Moroz
 */
@Composable
actual fun rememberExternalUrlLauncher(): ExternalUrlLauncher = remember {
    ExternalUrlLauncher { url -> IosUrlOpener.openUrl(url) }
}
