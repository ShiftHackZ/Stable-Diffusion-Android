package com.shifthackz.aisdv1.presentation.platform

import androidx.compose.runtime.Composable

fun interface ExternalUrlLauncher {
    fun openUrl(url: String)
}

@Composable
expect fun rememberExternalUrlLauncher(): ExternalUrlLauncher
