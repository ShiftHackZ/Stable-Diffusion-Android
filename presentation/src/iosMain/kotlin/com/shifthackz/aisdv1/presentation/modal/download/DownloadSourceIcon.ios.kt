package com.shifthackz.aisdv1.presentation.modal.download

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Renders the `DownloadSourceIcon` UI for the SDAI presentation layer.
 *
 * @param host host value consumed by the API.
 * @param modifier Compose modifier applied to the rendered UI.
 * @author Dmitriy Moroz
 */
@Composable
internal actual fun DownloadSourceIcon(
    host: String,
    modifier: Modifier,
) {
    Icon(
        modifier = modifier,
        imageVector = Icons.Default.Link,
        contentDescription = null,
    )
}
