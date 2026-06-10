package com.shifthackz.aisdv1.presentation.modal.download

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.shifthackz.aisdv1.presentation.R

@Composable
internal actual fun DownloadSourceIcon(
    host: String,
    modifier: Modifier,
) {
    when (host) {
        GITHUB_WEB_RESOURCE -> Icon(
            modifier = modifier,
            painter = painterResource(R.drawable.ic_github),
            contentDescription = null,
        )

        SDAI_WEB_RESOURCE -> Image(
            modifier = modifier,
            painter = painterResource(R.drawable.ic_sdai_logo),
            contentDescription = null,
        )

        else -> Icon(
            modifier = modifier,
            imageVector = Icons.Default.Link,
            contentDescription = null,
        )
    }
}
