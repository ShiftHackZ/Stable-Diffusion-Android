package com.shifthackz.aisdv1.presentation.modal.download

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.shifthackz.android.core.mvi.MviComponent
import org.koin.androidx.compose.koinViewModel

@Composable
fun DownloadDialog(
    modifier: Modifier = Modifier,
    modelId: String,
    onDismissRequest: () -> Unit,
    onDownloadSourceSelected: (url: String) -> Unit,
) {
    MviComponent(
        viewModel = koinViewModel<DownloadDialogViewModel>().apply {
            processIntent(DownloadDialogIntent.LoadModelData(modelId))
        },
        processEffect = { effect ->
            when (effect) {
                DownloadDialogEffect.Close -> onDismissRequest()
                is DownloadDialogEffect.Select -> onDownloadSourceSelected(effect.url)
            }
        }
    ) { state, processIntent ->
        ScreenContent(
            modifier = modifier,
            state = state,
            processIntent = processIntent,
        )
    }
}

@Composable
@Preview
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: DownloadDialogState = DownloadDialogState(),
    processIntent: (DownloadDialogIntent) -> Unit = {},
) {
    Dialog(
        onDismissRequest = {},
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
                bottomBar = {
                    Button(
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .fillMaxWidth(),
                        onClick = {

                        },
                    ) {
                        
                    }
                },
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier.padding(paddingValues),
                ) {
                    items(
                        count = state.sources.size,
                        key = { index -> state.sources[index] }
                    ) { index ->
                        val source = state.sources[index]
                        Box(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(color = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.8f))
                                .defaultMinSize(minHeight = 50.dp)
//                                .border(
//                                    width = 2.dp,
//                                    shape = RoundedCornerShape(16.dp),
//                                    color = if (model.selected) MaterialTheme.colorScheme.primary else Color.Transparent,
//                                )
                                .clickable { processIntent(DownloadDialogIntent.SelectSource(source)) },
                        ) {
                            Text(
                                text = source.split("://").last().split("/").first()
                            )
                        }
                    }
                }
            }
        }
    }
}
