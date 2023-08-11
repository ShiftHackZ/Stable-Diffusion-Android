package com.shifthackz.aisdv1.presentation.widget.ad

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.shifthackz.aisdv1.domain.feature.ad.AdFeature

@Composable
fun AdBanner(
    modifier: Modifier = Modifier,
    adFactory: (Context) -> AdFeature.Ad,
) {
    val ad = adFactory(LocalContext.current)
    if (ad.isEmpty) return
    ad.view?.let { adView ->
        AndroidView(
            modifier = modifier,
            factory = { adView }
        )
    }
}
