@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixNormal
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.domain.entity.ServerSource

/**
 * Carries `HomeStrings` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
data class HomeStrings(
    /**
     * Exposes the `title` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val title: String = "SDAI",
    /**
     * Exposes the `providerTitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val providerTitle: String = Localization.string("title_provider"),
    /**
     * Exposes the `endpointTitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val endpointTitle: String = Localization.string("hint_server_url"),
    /**
     * Exposes the `startTextToImage` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val startTextToImage: String = Localization.string("title_text_to_image"),
    /**
     * Exposes the `startImageToImage` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val startImageToImage: String = Localization.string("title_image_to_image"),
    /**
     * Exposes the `gallery` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val gallery: String = Localization.string("title_gallery"),
    /**
     * Exposes the `history` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val history: String = Localization.string("title_history"),
    /**
     * Exposes the `settings` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val settings: String = Localization.string("title_settings"),
    /**
     * Exposes the `configureProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val configureProvider: String = Localization.string("settings_item_config"),
    /**
     * Exposes the `retry` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val retry: String = Localization.string("retry"),
)

/**
 * Renders the `HomeScreenContent` UI for the SDAI presentation layer.
 *
 * @param state state rendered or processed by the component.
 * @param processIntent process intent value consumed by the API.
 * @param modifier Compose modifier applied to the rendered UI.
 * @param strings strings value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
fun HomeScreenContent(
    state: HomeState,
    processIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier,
    strings: HomeStrings = HomeStrings(),
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = {
                    Text(
                        text = strings.title,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                },
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            when {
                state.loading -> CircularProgressIndicator(modifier = Modifier.size(56.dp))
                state.error != null -> HomeError(
                    message = state.error,
                    strings = strings,
                    processIntent = processIntent,
                )

                else -> HomeBody(
                    state = state,
                    strings = strings,
                    processIntent = processIntent,
                )
            }
        }
    }
}

/**
 * Renders the `HomeBody` UI for the SDAI presentation layer.
 *
 * @param state state rendered or processed by the component.
 * @param strings strings value consumed by the API.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
private fun HomeBody(
    state: HomeState,
    strings: HomeStrings,
    processIntent: (HomeIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                HomeInfoRow(
                    label = strings.providerTitle,
                    value = state.source.displayName,
                )
                HomeInfoRow(
                    label = strings.endpointTitle,
                    value = state.endpoint.ifBlank { state.source.displayName },
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { processIntent(HomeIntent.StartTextToImage) },
        ) {
            Icon(
                imageVector = Icons.Default.AutoFixNormal,
                contentDescription = null,
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = strings.startTextToImage,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { processIntent(HomeIntent.StartImageToImage) },
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = strings.startImageToImage,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { processIntent(HomeIntent.OpenHistory) },
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = strings.history,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { processIntent(HomeIntent.OpenGallery) },
        ) {
            Icon(
                imageVector = Icons.Default.PhotoLibrary,
                contentDescription = null,
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = strings.gallery,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { processIntent(HomeIntent.OpenSettings) },
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = strings.settings,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { processIntent(HomeIntent.ConfigureProvider) },
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = strings.configureProvider,
            )
        }
    }
}

/**
 * Renders the `HomeError` UI for the SDAI presentation layer.
 *
 * @param message message value consumed by the API.
 * @param strings strings value consumed by the API.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
private fun HomeError(
    message: String,
    strings: HomeStrings,
    processIntent: (HomeIntent) -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { processIntent(HomeIntent.RefreshConfiguration) },
            ) {
                Text(text = strings.retry)
            }
            Button(
                onClick = { processIntent(HomeIntent.ConfigureProvider) },
            ) {
                Text(text = strings.configureProvider)
            }
        }
    }
}

/**
 * Renders the `HomeInfoRow` UI for the SDAI presentation layer.
 *
 * @param label label value consumed by the API.
 * @param value value value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
private fun HomeInfoRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            modifier = Modifier.weight(0.4f),
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            modifier = Modifier.weight(0.6f),
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.W600,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * Exposes the `ServerSource` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
private val ServerSource.displayName: String
    get() = when (this) {
        ServerSource.AUTOMATIC1111 -> Localization.string("srv_type_own")
        ServerSource.SWARM_UI -> Localization.string("srv_type_swarm_ui")
        ServerSource.HORDE -> Localization.string("srv_type_horde")
        ServerSource.HUGGING_FACE -> Localization.string("srv_type_hugging_face")
        ServerSource.OPEN_AI -> Localization.string("srv_type_open_ai")
        ServerSource.STABILITY_AI -> Localization.string("srv_type_stability_ai")
        ServerSource.LOCAL_MICROSOFT_ONNX -> Localization.string("srv_type_local_short")
        ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> Localization.string("srv_type_media_pipe_short")
    }
