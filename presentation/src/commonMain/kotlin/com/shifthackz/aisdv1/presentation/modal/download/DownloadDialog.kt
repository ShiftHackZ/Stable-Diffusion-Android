package com.shifthackz.aisdv1.presentation.modal.download

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.mvi.MviComponent
import com.shifthackz.aisdv1.presentation.di.initKoin
import org.koin.core.parameter.parametersOf

/**
 * Exposes the `GITHUB_WEB_RESOURCE` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val GITHUB_WEB_RESOURCE = "github.com"
/**
 * Exposes the `SDAI_WEB_RESOURCE` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val SDAI_WEB_RESOURCE = "share.moroz.cc"

/**
 * Renders the `DownloadDialog` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param modelId model id value consumed by the API.
 * @param onDismissRequest callback invoked by the component.
 * @param onDownloadSourceSelected callback invoked by the component.
 * @author Dmitriy Moroz
 */
@Composable
fun DownloadDialog(
    modifier: Modifier = Modifier,
    modelId: String,
    onDismissRequest: () -> Unit,
    onDownloadSourceSelected: (url: String) -> Unit,
) {
    val koin = remember { initKoin() }
    val viewModel = remember(koin, modelId) {
        koin.get<DownloadDialogViewModel> {
            parametersOf(modelId)
        }
    }

    MviComponent(
        viewModel = viewModel,
        processEffect = { effect ->
            when (effect) {
                DownloadDialogEffect.Close -> onDismissRequest()
                is DownloadDialogEffect.StartDownload -> onDownloadSourceSelected(effect.url)
            }
        },
    ) { state: DownloadDialogState, processIntent: (DownloadDialogIntent) -> Unit ->
        DownloadDialogContent(
            modifier = modifier,
            state = state,
            processIntent = processIntent,
        )
    }
}

/**
 * Renders the `DownloadDialogContent` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param state state rendered or processed by the component.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
private fun DownloadDialogContent(
    modifier: Modifier = Modifier,
    state: DownloadDialogState = DownloadDialogState(),
    processIntent: (DownloadDialogIntent) -> Unit = {},
) {
    Dialog(
        onDismissRequest = { processIntent(DownloadDialogIntent.Close) },
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false,
        ),
    ) {
        Surface(
            modifier = modifier.fillMaxHeight(0.38f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background,
        ) {
            Scaffold(
                topBar = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(40.dp))
                        Text(
                            text = Localization.string("title_select_download_source"),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = { processIntent(DownloadDialogIntent.Close) },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                            )
                        }
                    }
                },
                bottomBar = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Button(
                            modifier = Modifier
                                .padding(bottom = 12.dp)
                                .fillMaxWidth(0.65f),
                            onClick = { processIntent(DownloadDialogIntent.StartDownload) },
                        ) {
                            Icon(
                                modifier = Modifier.padding(end = 8.dp),
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                            )
                            Text(
                                text = Localization.string("download"),
                                color = LocalContentColor.current,
                            )
                        }
                    }
                },
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier.padding(paddingValues),
                ) {
                    items(
                        count = state.sources.size,
                        key = { index -> state.sources[index] },
                    ) { index ->
                        val (url, selected) = state.sources[index]
                        Row(
                            modifier = Modifier
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(color = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.8f))
                                .defaultMinSize(minHeight = 50.dp)
                                .border(
                                    width = 2.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (selected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        Color.Transparent
                                    },
                                )
                                .clickable { processIntent(DownloadDialogIntent.SelectSource(url)) },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            val webResource = remember(url) {
                                runCatching {
                                    url.split("://")[1].split("/").first()
                                }.getOrElse { "" }
                            }

                            val iconModifier = Modifier
                                .size(42.dp)
                                .padding(horizontal = 8.dp)

                            DownloadSourceIcon(
                                host = webResource,
                                modifier = iconModifier,
                            )

                            Text(
                                text = webResource,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Renders the `DownloadSourceIcon` UI for the SDAI presentation layer.
 *
 * @param host host value consumed by the API.
 * @param modifier Compose modifier applied to the rendered UI.
 * @author Dmitriy Moroz
 */
@Composable
internal expect fun DownloadSourceIcon(
    host: String,
    modifier: Modifier = Modifier,
)
