package com.shifthackz.aisdv1.presentation.screen.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shifthackz.aisdv1.core.ui.EmptyState
import com.shifthackz.aisdv1.core.ui.MviScreen

class SplashScreen(
    viewModel: SplashViewModel,
    private val navigateOnBoarding: () -> Unit,
    private val navigateServerSetup: () -> Unit,
    private val navigateHome: () -> Unit,
) : MviScreen<EmptyState, SplashEffect>(viewModel) {

    @Composable
    override fun Content() {
        Box(Modifier.fillMaxSize())
    }

    override fun processEffect(effect: SplashEffect) = when (effect) {
        SplashEffect.LaunchHome -> navigateHome()
        SplashEffect.LaunchOnBoarding -> navigateOnBoarding()
        SplashEffect.LaunchServerSetup -> navigateServerSetup()
    }
}
