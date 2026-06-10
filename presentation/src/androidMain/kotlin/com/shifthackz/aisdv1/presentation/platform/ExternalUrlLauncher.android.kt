package com.shifthackz.aisdv1.presentation.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.shifthackz.aisdv1.core.common.extensions.openUrl

@Composable
actual fun rememberExternalUrlLauncher(): ExternalUrlLauncher {
    val context = LocalContext.current
    return remember(context) {
        ExternalUrlLauncher { url -> context.openUrl(url) }
    }
}
