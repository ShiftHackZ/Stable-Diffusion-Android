package com.shifthackz.aisdv1.presentation.screen.gallery.list

import androidx.compose.runtime.Composable

/**
 * Renders the `GalleryBackHandler` UI for the SDAI presentation layer.
 *
 * @param enabled enabled value consumed by the API.
 * @param onBack callback invoked by the component.
 * @author Dmitriy Moroz
 */
@Composable
internal expect fun GalleryBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
)
