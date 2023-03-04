package com.shifthackz.aisdv1.presentation.screen.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.MviScreen

class SplashLoaderScreen(
    private val viewModel: SplashLoaderViewModel,
    private val onNavigateNextScreen: () -> Unit = {},
) : MviScreen<SplashLoaderState, SplashLoaderEffect>(viewModel) {

    override val statusBarColor: Color = Color.Cyan

    @Composable
    override fun Content() {
        val state = viewModel.state.collectAsState().value
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = state,
        )
    }

    override fun processEffect(effect: SplashLoaderEffect) = when (effect) {
        SplashLoaderEffect.ProceedNavigation -> onNavigateNextScreen()
        else -> Unit
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: SplashLoaderState,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = (state as? SplashLoaderState.StatusNotification)?.statusNotification?.asString()
                ?: ""
        )
    }
}

@Composable
@Preview(name = "STATE -> StatusNotification", showSystemUi = true, showBackground = true)
private fun PreviewStateStatusNotification() {
    ScreenContent(
        modifier = Modifier.fillMaxSize(),
        state = SplashLoaderState.StatusNotification("asd".asUiText())
    )
}
