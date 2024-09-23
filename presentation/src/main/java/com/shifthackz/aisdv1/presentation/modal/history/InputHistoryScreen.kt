@file:OptIn(ExperimentalLayoutApi::class)

package com.shifthackz.aisdv1.presentation.modal.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.shifthackz.android.core.mvi.MviComponent
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import kotlinx.coroutines.flow.Flow
import org.koin.androidx.compose.koinViewModel
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun InputHistoryScreen(
    onGenerationSelected: (AiGenerationResult) -> Unit = {},
) {
    val viewModel = koinViewModel<InputHistoryViewModel>()
    MviComponent(viewModel = viewModel) { _, _ ->
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            pagingFlow = viewModel.pagingFlow,
            onGenerationSelected = onGenerationSelected,
        )
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    pagingFlow: Flow<PagingData<InputHistoryItemUi>>,
    onGenerationSelected: (AiGenerationResult) -> Unit = {},
) {
    val listState = rememberLazyListState()
    val lazyInputItems = pagingFlow.collectAsLazyPagingItems()
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        state = listState,
    ) {
        items(lazyInputItems) { inputHistoryUi ->
            inputHistoryUi?.let { itemUi ->
                InputHistoryItem(
                    item = itemUi,
                    onClick = { onGenerationSelected(it.generationResult) },
                )
            }
        }
    }
}

@Composable
private fun InputHistoryItem(
    modifier: Modifier = Modifier,
    item: InputHistoryItemUi,
    onClick: (InputHistoryItemUi) -> Unit = {},
) {
    val (generation, bitmap) = item
    val itemPropertyText: @Composable (Int, Any) -> AnnotatedString = { id, value ->
        buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(stringResource(id = id))
                append(": ")
            }
            if ("$value".isNotEmpty()) append("$value")
            else withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                append(stringResource(id = LocalizationR.string.empty))
            }
        }
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(color = MaterialTheme.colorScheme.surface)
            .defaultMinSize(minHeight = 50.dp)
            .clickable { onClick(item) },
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth(0.36f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp)),
            bitmap = bitmap.asImageBitmap(),
            contentScale = ContentScale.Crop,
            contentDescription = "gallery_item",
        )
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            val textStyle = MaterialTheme.typography.bodySmall
            val textModifier = Modifier.padding(horizontal = 4.dp)
            Text(
                modifier = textModifier.padding(top = 2.dp),
                text = itemPropertyText(LocalizationR.string.hint_prompt, generation.prompt),
                style = textStyle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = LocalContentColor.current,
            )
            Text(
                modifier = textModifier,
                text = itemPropertyText(LocalizationR.string.hint_prompt_negative, generation.negativePrompt),
                style = textStyle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = LocalContentColor.current,
            )
            val tags = buildList {
                add(generation.type.key)
                add("${generation.width} X ${generation.height}")
                add(generation.sampler)
                add(stringResource(id = LocalizationR.string.tag_steps, generation.samplingSteps))
                add(stringResource(id = LocalizationR.string.tag_cfg, "${generation.cfgScale}"))
                if (generation.type == AiGenerationResult.Type.IMAGE_TO_IMAGE) {
                    add(stringResource(id = LocalizationR.string.tag_denoising, "${generation.denoisingStrength}"))
                }
                add(generation.seed)
                add(generation.subSeed)
                add("${generation.subSeedStrength}")
            }.filter(String::isNotEmpty)
            FlowRow(
                modifier = Modifier.padding(horizontal = 2.dp),
            ) {
                tags.forEach { tag ->
                    Text(
                        modifier = Modifier
                            .padding(2.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(4.dp),
                            )
                            .padding(1.dp),
                        text = tag,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        }
    }
}
