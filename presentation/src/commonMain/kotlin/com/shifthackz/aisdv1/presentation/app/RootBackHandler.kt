package com.shifthackz.aisdv1.presentation.app

import androidx.compose.runtime.Composable

@Composable
internal expect fun RootBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
)
