package com.shifthackz.aisdv1.presentation.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberExternalUrlLauncher(): ExternalUrlLauncher = remember {
    ExternalUrlLauncher { url -> IosUrlOpener.openUrl(url) }
}
