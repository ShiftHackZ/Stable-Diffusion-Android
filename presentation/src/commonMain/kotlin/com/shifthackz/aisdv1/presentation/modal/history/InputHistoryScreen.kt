@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.modal.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixNormal
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.mvi.MviComponent
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.di.initKoin
import com.shifthackz.aisdv1.presentation.theme.global.persistentBottomBarWindowInsets
import com.shifthackz.aisdv1.presentation.theme.global.persistentTopAppBarWindowInsets

/**
 * Renders the `InputHistoryBottomSheet` UI for the SDAI presentation layer.
 *
 * @param onClose callback invoked by the component.
 * @param onGenerationSelected callback invoked by the component.
 * @param modifier Compose modifier applied to the rendered UI.
 * @author Dmitriy Moroz
 */
@Composable
fun InputHistoryBottomSheet(
    onClose: () -> Unit,
    onGenerationSelected: (AiGenerationResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { value -> value != SheetValue.PartiallyExpanded },
    )

    ModalBottomSheet(
        modifier = modifier.windowInsetsPadding(persistentTopAppBarWindowInsets()),
        sheetState = sheetState,
        onDismissRequest = onClose,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        InputHistoryScreen(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .windowInsetsPadding(persistentBottomBarWindowInsets()),
            onGenerationSelected = onGenerationSelected,
        )
    }
}

/**
 * Renders the `InputHistoryScreen` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param onGenerationSelected callback invoked by the component.
 * @author Dmitriy Moroz
 */
@Composable
fun InputHistoryScreen(
    modifier: Modifier = Modifier,
    onGenerationSelected: (AiGenerationResult) -> Unit = {},
) {
    val koin = remember { initKoin() }
    val viewModel = remember(koin) {
        koin.get<InputHistoryViewModel>()
    }

    MviComponent(
        viewModel = viewModel,
    ) { state: InputHistoryState, processIntent: (InputHistoryIntent) -> Unit ->
        ScreenContent(
            modifier = modifier.fillMaxSize(),
            state = state,
            processIntent = processIntent,
            onGenerationSelected = onGenerationSelected,
        )
    }
}

/**
 * Renders the `ScreenContent` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param state state rendered or processed by the component.
 * @param processIntent process intent value consumed by the API.
 * @param onGenerationSelected callback invoked by the component.
 * @author Dmitriy Moroz
 */
@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: InputHistoryState,
    processIntent: (InputHistoryIntent) -> Unit = {},
    onGenerationSelected: (AiGenerationResult) -> Unit = {},
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when {
            state.loading -> CircularProgressIndicator(modifier = Modifier.size(48.dp))
            state.error != null -> InputHistoryError(
                message = state.error,
                processIntent = processIntent,
            )
            state.items.isEmpty() -> Text(
                modifier = Modifier.padding(24.dp),
                text = Localization.string("gallery_empty_title"),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(
                    items = state.items,
                    key = { it.generationResult.id },
                ) { itemUi ->
                    InputHistoryItem(
                        item = itemUi,
                        onClick = { onGenerationSelected(it.generationResult) },
                    )
                }
                if (state.canLoadMore) {
                    item(key = "load_next_page") {
                        LaunchedEffect(state.nextPage) {
                            processIntent(InputHistoryIntent.LoadNextPage)
                        }
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .size(32.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Renders the `InputHistoryError` UI for the SDAI presentation layer.
 *
 * @param message message value consumed by the API.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
private fun InputHistoryError(
    message: String,
    processIntent: (InputHistoryIntent) -> Unit,
) {
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Button(onClick = { processIntent(InputHistoryIntent.Retry) }) {
            Text(text = Localization.string("retry"))
        }
    }
}

/**
 * Renders the `InputHistoryItem` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param item item value consumed by the API.
 * @param onClick callback invoked when the user activates the control.
 * @author Dmitriy Moroz
 */
@Composable
private fun InputHistoryItem(
    modifier: Modifier = Modifier,
    item: InputHistoryItemUi,
    onClick: (InputHistoryItemUi) -> Unit = {},
) {
    val (generation, image) = item
    val itemPropertyText: (String, Any?) -> AnnotatedString = { key, value ->
        buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(Localization.string(key))
                append(": ")
            }
            val text = "$value"
            if (text.isNotEmpty()) append(text)
            else withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                append(Localization.string("empty"))
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
        if (image != null) {
            Image(
                modifier = Modifier
                    .fillMaxWidth(0.36f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp)),
                bitmap = image,
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.36f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.Default.AutoFixNormal,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val textStyle = MaterialTheme.typography.bodySmall
            val textModifier = Modifier.padding(horizontal = 4.dp)
            Text(
                modifier = textModifier.padding(top = 2.dp),
                text = itemPropertyText("hint_prompt", generation.prompt),
                style = textStyle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = LocalContentColor.current,
            )
            Text(
                modifier = textModifier,
                text = itemPropertyText("hint_prompt_negative", generation.negativePrompt),
                style = textStyle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = LocalContentColor.current,
            )
            val tags = buildList {
                add(generation.type.key)
                add("${generation.width} X ${generation.height}")
                add(generation.sampler)
                add(Localization.string("tag_steps", generation.samplingSteps))
                add(Localization.string("tag_cfg", "${generation.cfgScale}"))
                if (generation.type == AiGenerationResult.Type.IMAGE_TO_IMAGE) {
                    add(Localization.string("tag_denoising", "${generation.denoisingStrength}"))
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
