package com.shifthackz.aisdv1.presentation.screen.gallery.list

import androidx.compose.runtime.Composable

@Composable
internal expect fun GalleryBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
)
