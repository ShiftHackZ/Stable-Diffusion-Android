package com.shifthackz.aisdv1.presentation.screen.loader

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.android.core.mvi.MviComponent
import org.koin.androidx.compose.koinViewModel

@Composable
fun ConfigurationLoaderScreen() {
    MviComponent(
        viewModel = koinViewModel<ConfigurationLoaderViewModel>(),
    ) { state, _ ->
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = state,
        )
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: ConfigurationLoaderState,
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
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
