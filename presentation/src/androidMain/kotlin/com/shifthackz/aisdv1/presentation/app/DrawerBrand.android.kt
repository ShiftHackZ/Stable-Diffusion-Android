package com.shifthackz.aisdv1.presentation.app

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.shifthackz.aisdv1.presentation.R

@Composable
internal actual fun DrawerBrandLogo(modifier: Modifier) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_sdai_logo),
        contentDescription = "SDAI Android Branding",
    )
}

@Composable
internal actual fun DrawerPlatformIcon(modifier: Modifier) {
    Icon(
        modifier = modifier,
        imageVector = Icons.Default.Android,
        contentDescription = "Android",
    )
}

