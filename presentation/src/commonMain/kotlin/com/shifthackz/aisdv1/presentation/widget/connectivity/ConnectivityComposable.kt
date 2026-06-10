package com.shifthackz.aisdv1.presentation.widget.connectivity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.shifthackz.aisdv1.core.mvi.MviComponent
import com.shifthackz.aisdv1.presentation.di.initKoin

@Composable
fun ConnectivityComposable() {
    val koin = remember { initKoin() }
    val viewModel = remember(koin) {
        koin.get<ConnectivityViewModel>()
    }
    MviComponent(
        viewModel = viewModel,
    ) { state ->
        ConnectivityWidget(
            state = state,
        )
    }
}
