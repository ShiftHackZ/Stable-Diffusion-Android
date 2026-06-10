@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoFixNormal
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.screen.txt2img.decodeBase64ImageBitmap
import com.shifthackz.aisdv1.presentation.widget.scrollbar.verticalScrollbar

data class HistoryStrings(
    val title: String = Localization.string("title_gallery"),
    val empty: String = Localization.string("gallery_empty_title"),
    val refresh: String = Localization.string("action_update"),
    val imageUnavailable: String = Localization.string("message_image_unavailable"),
    val resultMeta: (AiGenerationResult) -> String = { item ->
        Localization.string(
            "history_result_meta",
            item.width,
            item.height,
            item.samplingSteps,
            item.cfgScale,
        )
    },
)

@Composable
fun HistoryScreenContent(
    state: HistoryState,
    processIntent: (HistoryIntent) -> Unit,
    modifier: Modifier = Modifier,
    strings: HistoryStrings = HistoryStrings(),
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.statusBarsPadding(),
                navigationIcon = {
                    IconButton(onClick = { processIntent(HistoryIntent.NavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                title = {
                    Text(
                        text = strings.title,
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                actions = {
                    IconButton(onClick = { processIntent(HistoryIntent.Refresh) }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                        )
                    }
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
                state.loading -> CircularProgressIndicator(modifier = Modifier.size(48.dp))
                state.error != null -> HistoryError(
                    message = state.error,
                    strings = strings,
                    processIntent = processIntent,
                )
                state.items.isEmpty() -> Text(
                    modifier = Modifier.padding(24.dp),
                    text = strings.empty,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                else -> {
                    val listState = rememberLazyListState()
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScrollbar(listState),
                        state = listState,
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(
                            items = state.items,
                            key = AiGenerationResult::id,
                        ) { item ->
                            HistoryItem(
                                item = item,
                                strings = strings,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryError(
    message: String,
    strings: HistoryStrings,
    processIntent: (HistoryIntent) -> Unit,
) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge,
        )
        OutlinedButton(onClick = { processIntent(HistoryIntent.Refresh) }) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = strings.refresh,
            )
        }
    }
}

@Composable
private fun HistoryItem(
    item: AiGenerationResult,
    strings: HistoryStrings,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val imageBitmap = remember(item.image) {
                item.image.decodeBase64ImageBitmap()
            }
            if (imageBitmap != null) {
                Image(
                    modifier = Modifier
                        .weight(0.38f)
                        .aspectRatio(item.aspectRatio),
                    bitmap = imageBitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )
            } else {
                Box(
                    modifier = Modifier
                        .weight(0.38f)
                        .aspectRatio(item.aspectRatio),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        modifier = Modifier.size(36.dp),
                        imageVector = Icons.Default.AutoFixNormal,
                        contentDescription = strings.imageUnavailable,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Column(
                modifier = Modifier.weight(0.62f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = item.prompt.ifBlank { item.type.key },
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = strings.resultMeta(item),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private val AiGenerationResult.aspectRatio: Float
    get() = if (width > 0 && height > 0) width.toFloat() / height.toFloat() else 1f
