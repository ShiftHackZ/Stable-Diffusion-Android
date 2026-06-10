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
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.localization.Localization

/**
 * Carries `ConfigurationLoaderScreenContentState` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
data class ConfigurationLoaderScreenContentState(
    /**
     * Exposes the `statusText` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val statusText: String = "",
)

/**
 * Converts SDAI data with `toContentState`.
 *
 * @author Dmitriy Moroz
 */
fun ConfigurationLoaderState.toContentState() = ConfigurationLoaderScreenContentState(
    statusText = when (status) {
        ConfigurationLoaderState.Status.Initializing -> Localization.string("splash_status_initializing")
        ConfigurationLoaderState.Status.Fetching -> Localization.string("splash_status_fetching")
        ConfigurationLoaderState.Status.Failed -> Localization.string("error_title")
        ConfigurationLoaderState.Status.Launching -> Localization.string("splash_status_launching")
    },
)

/**
 * Renders the `ConfigurationLoaderScreenContent` UI for the SDAI presentation layer.
 *
 * @param state state rendered or processed by the component.
 * @param modifier Compose modifier applied to the rendered UI.
 * @author Dmitriy Moroz
 */
@Composable
fun ConfigurationLoaderScreenContent(
    state: ConfigurationLoaderScreenContentState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
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
                text = state.statusText,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}
