package com.shifthackz.aisdv1.presentation.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.shifthackz.aisdv1.core.common.extensions.openUrl

/**
 * Renders the `rememberExternalUrlLauncher` UI for the SDAI presentation layer.
 *
 * @return Result produced by `rememberExternalUrlLauncher`.
 * @author Dmitriy Moroz
 */
@Composable
actual fun rememberExternalUrlLauncher(): ExternalUrlLauncher {
    val context = LocalContext.current
    return remember(context) {
        ExternalUrlLauncher { url -> context.openUrl(url) }
    }
}
