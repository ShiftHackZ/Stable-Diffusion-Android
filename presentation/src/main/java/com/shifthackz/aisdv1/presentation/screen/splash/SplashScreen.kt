package com.shifthackz.aisdv1.presentation.screen.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shifthackz.aisdv1.core.ui.MviComponent
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen() {
    MviComponent(
        viewModel = koinViewModel<SplashViewModel>(),
        navigationBarColor = MaterialTheme.colorScheme.surface,
        applySystemUiColors = true,
    ) { _, _ ->
        Box(
            Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surface),
        )
    }
}
