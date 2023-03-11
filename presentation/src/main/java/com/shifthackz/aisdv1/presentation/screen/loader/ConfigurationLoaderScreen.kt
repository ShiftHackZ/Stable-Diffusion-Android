package com.shifthackz.aisdv1.presentation.screen.loader

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(modifier = Modifier.fillMaxHeight(0.1f))
            CircularProgressIndicator(
                modifier = Modifier
                    .size(60.dp)
                    .aspectRatio(1f),
            )
            Spacer(modifier = Modifier.fillMaxHeight(0.2f))
            Text(
                text = (state as? ConfigurationLoaderState.StatusNotification)?.statusNotification?.asString()
                    ?: "",
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

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
