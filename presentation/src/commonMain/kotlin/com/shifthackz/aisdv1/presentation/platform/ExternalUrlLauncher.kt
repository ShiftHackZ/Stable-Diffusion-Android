package com.shifthackz.aisdv1.presentation.platform

import androidx.compose.runtime.Composable

/**
 * Executes the `function` step in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
fun interface ExternalUrlLauncher {
    fun openUrl(url: String)
}

/**
 * Renders the `rememberExternalUrlLauncher` UI for the SDAI presentation layer.
 *
 * @return Result produced by `rememberExternalUrlLauncher`.
 * @author Dmitriy Moroz
 */
@Composable
expect fun rememberExternalUrlLauncher(): ExternalUrlLauncher
