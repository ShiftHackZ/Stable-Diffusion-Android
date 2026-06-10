package com.shifthackz.aisdv1.presentation.modal.download

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
