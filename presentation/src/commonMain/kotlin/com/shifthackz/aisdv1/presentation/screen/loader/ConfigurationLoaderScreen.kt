package com.shifthackz.aisdv1.presentation.screen.loader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.shifthackz.aisdv1.core.mvi.MviComponent
import com.shifthackz.aisdv1.presentation.di.initKoin
import com.shifthackz.aisdv1.presentation.navigation.router.ConfigurationLoaderRouter
import org.koin.core.parameter.parametersOf

@Composable
fun ConfigurationLoaderScreen() {
    val koin = remember { initKoin() }
    val configurationLoaderRouter = remember(koin) { koin.get<ConfigurationLoaderRouter>() }
    val viewModel = remember(koin, configurationLoaderRouter) {
        koin.get<ConfigurationLoaderViewModel> {
            parametersOf(configurationLoaderRouter)
        }
    }
    MviComponent(
        viewModel = viewModel,
    ) { state ->
        ConfigurationLoaderScreenContent(
            modifier = Modifier,
            state = state.toContentState(),
        )
    }
}
