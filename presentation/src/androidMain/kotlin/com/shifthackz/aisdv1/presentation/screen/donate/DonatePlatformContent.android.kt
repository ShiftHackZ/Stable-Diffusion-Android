package com.shifthackz.aisdv1.presentation.screen.donate

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.presentation.R as PresentationR

@Composable
internal actual fun DonateBrandIcon() {
    Image(
        modifier = Modifier.size(36.dp),
        painter = painterResource(id = PresentationR.drawable.ic_sdai_logo),
        contentDescription = "SDAI Android Branding",
    )
}
