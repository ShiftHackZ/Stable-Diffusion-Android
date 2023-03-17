package com.shifthackz.aisdv1.presentation.widget.ad

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.shifthackz.aisdv1.domain.feature.AdFeature

@Composable
fun HomeBannedAd(
    modifier: Modifier = Modifier,
    adFeature: AdFeature,
) {
    val ad = adFeature.getHomeScreenBannerAdView(LocalContext.current)
    if (ad.isEmpty) return
    ad.view?.let { adView ->
        AndroidView(
            modifier = modifier,
            factory = {
                adFeature.loadAd(ad)
                adView
            }
        )
    }
}
