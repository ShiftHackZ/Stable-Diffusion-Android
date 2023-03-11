package com.shifthackz.aisdv1.presentation.screen.loader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.MviScreen

class ConfigurationLoaderScreen(
    private val viewModel: ConfigurationLoaderViewModel,
    private val onNavigateNextScreen: () -> Unit = {},
) : MviScreen<ConfigurationLoaderState, ConfigurationLoaderEffect>(viewModel) {

    @Composable
    override fun Content() {
        val state = viewModel.state.collectAsState().value
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = state,
        )
    }

    override fun processEffect(effect: ConfigurationLoaderEffect) = when (effect) {
        ConfigurationLoaderEffect.ProceedNavigation -> onNavigateNextScreen()
        else -> Unit
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: ConfigurationLoaderState,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = (state as? ConfigurationLoaderState.StatusNotification)?.statusNotification?.asString()
                ?: ""
        )
    }
}

@Composable
@Preview(name = "STATE -> StatusNotification", showSystemUi = true, showBackground = true)
private fun PreviewStateStatusNotification() {
    ScreenContent(
        modifier = Modifier.fillMaxSize(),
        state = ConfigurationLoaderState.StatusNotification("asd".asUiText())
    )
}
