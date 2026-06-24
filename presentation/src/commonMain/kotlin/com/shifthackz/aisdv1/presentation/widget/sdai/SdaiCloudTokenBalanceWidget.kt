package com.shifthackz.aisdv1.presentation.widget.sdai

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.shifthackz.aisdv1.core.sdai.NoOpSdaiCloudUi
import com.shifthackz.aisdv1.core.sdai.SdaiCloudUi
import com.shifthackz.aisdv1.presentation.di.initKoin

@Composable
fun SdaiCloudTokenBalanceWidget(
    onBuyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    rememberSdaiCloudUi().TokenBalance(
        onBuyClick = onBuyClick,
        modifier = modifier,
    )
}

@Composable
internal fun rememberSdaiCloudUi(): SdaiCloudUi {
    val koin = remember { initKoin() }
    return remember(koin) {
        runCatching { koin.get<SdaiCloudUi>() }.getOrElse { NoOpSdaiCloudUi }
    }
}
