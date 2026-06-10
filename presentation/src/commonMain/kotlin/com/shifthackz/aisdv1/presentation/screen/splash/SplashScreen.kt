package com.shifthackz.aisdv1.presentation.screen.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.shifthackz.aisdv1.core.mvi.MviComponent
import com.shifthackz.aisdv1.presentation.di.initKoin
import com.shifthackz.aisdv1.presentation.navigation.router.SplashRouter
import org.koin.core.parameter.parametersOf

@Composable
fun SplashScreen() {
    val koin = remember { initKoin() }
    val splashRouter = remember(koin) { koin.get<SplashRouter>() }
    val viewModel = remember(koin, splashRouter) {
        koin.get<SplashViewModel> {
            parametersOf(splashRouter)
        }
    }
    MviComponent(
        viewModel = viewModel,
    ) {
        SplashScreenContent(
            modifier = Modifier,
        )
    }
}
