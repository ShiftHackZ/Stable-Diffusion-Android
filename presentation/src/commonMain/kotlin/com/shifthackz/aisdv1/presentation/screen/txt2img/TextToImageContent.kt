@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.txt2img

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoFixNormal
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.modal.GenerationModalRenderer
import com.shifthackz.aisdv1.presentation.modal.embedding.EmbeddingScreen
import com.shifthackz.aisdv1.presentation.modal.extras.ExtrasScreen
import com.shifthackz.aisdv1.presentation.modal.history.InputHistoryBottomSheet
import com.shifthackz.aisdv1.presentation.modal.tag.EditTagDialog
import com.shifthackz.aisdv1.presentation.model.ExtraType
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputFormEvent
import com.shifthackz.aisdv1.presentation.widget.toolbar.GenerationBottomToolbar
import com.shifthackz.aisdv1.presentation.widget.work.BackgroundWorkWidget

/**
 * Carries `TextToImageStrings` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
data class TextToImageStrings(
    /**
     * Exposes the `title` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val title: String = Localization.string("title_text_to_image"),
    /**
     * Exposes the `prompt` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val prompt: String = Localization.string("hint_prompt"),
    /**
     * Exposes the `negativePrompt` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val negativePrompt: String = Localization.string("hint_prompt_negative"),
    /**
     * Exposes the `width` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val width: String = Localization.string("width"),
    /**
     * Exposes the `height` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val height: String = Localization.string("height"),
    /**
     * Exposes the `steps` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val steps: String = Localization.string("gallery_info_field_sampling_steps"),
    /**
     * Exposes the `cfgScale` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val cfgScale: String = Localization.string("gallery_info_field_cfg"),
    /**
     * Exposes the `batch` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val batch: String = Localization.string("hint_batch_tag"),
    /**
     * Exposes the `generate` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val generate: String = Localization.string("action_generate"),
    /**
     * Exposes the `generating` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val generating: String = Localization.string("notification_running_title"),
    /**
     * Exposes the `save` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val save: String = Localization.string("action_save"),
    /**
     * Exposes the `savingImage` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val savingImage: String = Localization.string("message_image_saving"),
    /**
     * Exposes the `share` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val share: String = Localization.string("action_share_prompt"),
    /**
     * Exposes the `sharingImage` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val sharingImage: String = Localization.string("message_image_sharing"),
    /**
     * Exposes the `configureProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val configureProvider: String = Localization.string("settings_item_config"),
    /**
     * Exposes the `sourceUnavailable` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val sourceUnavailable: String = Localization.string("error_source_android_only"),
    /**
     * Exposes the `results` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val results: String = Localization.string("title_generation_results"),
    /**
     * Exposes the `imageUnavailable` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val imageUnavailable: String = Localization.string("message_image_data_received"),
    /**
     * Exposes the `resultMeta` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val resultMeta: (AiGenerationResult) -> String = { result ->
        Localization.string(
            "generation_result_meta",
            result.width,
            result.height,
            result.samplingSteps,
        )
    },
)

/**
 * Renders the `TextToImageContent` UI for the SDAI presentation layer.
 *
 * @param state state rendered or processed by the component.
 * @param processIntent process intent value consumed by the API.
 * @param modifier Compose modifier applied to the rendered UI.
 * @param strings strings value consumed by the API.
 * @param useDrawerNavigation use drawer navigation value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
fun TextToImageContent(
    state: TextToImageState,
    processIntent: (TextToImageIntent) -> Unit,
    modifier: Modifier = Modifier,
    strings: TextToImageStrings = TextToImageStrings(),
    useDrawerNavigation: Boolean = false,
) {
    val promptChipTextFieldState = remember { mutableStateOf(TextFieldValue()) }
    val negativePromptChipTextFieldState = remember { mutableStateOf(TextFieldValue()) }
    var activeModal by remember { mutableStateOf<TextToImagePanel?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    windowInsets = WindowInsets(0, 0, 0, 0),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                processIntent(
                                    if (useDrawerNavigation) {
                                        TextToImageIntent.OpenDrawer
                                    } else {
                                        TextToImageIntent.NavigateBack
                                    },
                                )
                            },
                        ) {
                            Icon(
                                imageVector = if (useDrawerNavigation) {
                                    Icons.Default.Menu
                                } else {
                                    Icons.AutoMirrored.Filled.ArrowBack
                                },
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
                        IconButton(
                            onClick = { activeModal = TextToImagePanel.History },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                            )
                        }
                    },
                )
                BackgroundWorkWidget(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(vertical = 4.dp),
                )
            }
        },
        bottomBar = {
            GenerationBottomToolbar(
                strokeAccentState = !state.hasValidationErrors,
                mode = state.mode,
                prompt = state.prompt,
                negativePrompt = state.negativePrompt,
                onExtraSelected = { prompt, negativePrompt, type ->
                    activeModal = TextToImagePanel.Extras(prompt, negativePrompt, type)
                },
                onEmbeddingsSelected = { prompt, negativePrompt ->
                    activeModal = TextToImagePanel.Embeddings(prompt, negativePrompt)
                },
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    enabled = !state.generating && !state.hasValidationErrors,
                    onClick = {
                        flushPendingTaggedText(
                            state = state,
                            promptChipTextFieldState = promptChipTextFieldState,
                            negativePromptChipTextFieldState = negativePromptChipTextFieldState,
                            processIntent = processIntent,
                        )
                        processIntent(TextToImageIntent.Generate)
                    },
                ) {
                    if (state.generating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AutoFixNormal,
                            contentDescription = null,
                        )
                    }
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = if (state.generating) strings.generating else strings.generate,
                    )
                }
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            if (state.loadingConfiguration) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(56.dp),
                )
            } else {
                TextToImageBody(
                    state = state,
                    strings = strings,
                    promptChipTextFieldState = promptChipTextFieldState,
                    negativePromptChipTextFieldState = negativePromptChipTextFieldState,
                    processIntent = processIntent,
                )
            }
        }
    }
    state.editTag?.let { editTag ->
        EditTagDialog(
            prompt = editTag.prompt,
            negativePrompt = editTag.negativePrompt,
            tag = editTag.tag,
            isNegative = editTag.isNegative,
            onDismissRequest = { processIntent(TextToImageIntent.DismissEditTag) },
            onNewPrompts = { prompt, negativePrompt ->
                processIntent(TextToImageIntent.ApplyPrompts(prompt, negativePrompt))
            },
        )
    }
    when (val modal = activeModal) {
        null -> Unit
        is TextToImagePanel.Embeddings -> EmbeddingScreen(
            prompt = modal.prompt,
            negativePrompt = modal.negativePrompt,
            onNewPrompts = { prompt, negativePrompt ->
                processIntent(TextToImageIntent.ApplyPrompts(prompt, negativePrompt))
            },
            onClose = { activeModal = null },
        )
        is TextToImagePanel.Extras -> ExtrasScreen(
            prompt = modal.prompt,
            negativePrompt = modal.negativePrompt,
            type = modal.type,
            onNewPrompts = { prompt, negativePrompt ->
                processIntent(TextToImageIntent.ApplyPrompts(prompt, negativePrompt))
            },
            onClose = { activeModal = null },
        )
        TextToImagePanel.History -> GenerationHistoryDialog(
            onClose = { activeModal = null },
            onGenerationSelected = { ai ->
                processIntent(TextToImageIntent.ApplyGenerationResult(ai))
                activeModal = null
            },
        )
    }
    GenerationModalRenderer(
        screenModal = state.screenModal,
        onDismissRequest = { processIntent(TextToImageIntent.DismissModal) },
        onCancelGeneration = { processIntent(TextToImageIntent.CancelGeneration) },
        onSaveRequest = { processIntent(TextToImageIntent.SaveGenerationResults(it)) },
        onReportRequest = { processIntent(TextToImageIntent.ReportGenerationResult(it)) },
        onViewDetailRequest = { processIntent(TextToImageIntent.ViewGenerationResult(it)) },
    )
}

/**
 * Converts SDAI data with `toTextToImageIntent`.
 *
 * @author Dmitriy Moroz
 */
internal fun GenerationInputFormEvent.toTextToImageIntent(): TextToImageIntent? = when (this) {
    is GenerationInputFormEvent.EditTag -> TextToImageIntent.ShowEditTag(
        prompt = prompt,
        negativePrompt = negativePrompt,
        tag = tag,
        isNegative = isNegative,
    )
    is GenerationInputFormEvent.UpdateAdvancedOptionsVisibility ->
        TextToImageIntent.UpdateAdvancedOptionsVisibility(visible)
    is GenerationInputFormEvent.UpdateBatch -> TextToImageIntent.UpdateBatchCount(value)
    is GenerationInputFormEvent.UpdateCfgScale -> TextToImageIntent.UpdateCfgScale(value)
    is GenerationInputFormEvent.ApplyAspectRatio -> TextToImageIntent.ApplyAspectRatio(ratio)
    GenerationInputFormEvent.SwapDimensions -> TextToImageIntent.SwapDimensions
    is GenerationInputFormEvent.UpdateHeight -> TextToImageIntent.UpdateHeight(value)
    is GenerationInputFormEvent.UpdateNegativePrompt -> TextToImageIntent.UpdateNegativePrompt(value)
    is GenerationInputFormEvent.UpdateNsfw -> TextToImageIntent.UpdateNsfw(value)
    is GenerationInputFormEvent.UpdateOpenAiModel -> TextToImageIntent.UpdateOpenAiModel(value)
    is GenerationInputFormEvent.UpdateOpenAiQuality -> TextToImageIntent.UpdateOpenAiQuality(value)
    is GenerationInputFormEvent.UpdateOpenAiSize -> TextToImageIntent.UpdateOpenAiSize(value)
    is GenerationInputFormEvent.UpdateFalAiModel -> TextToImageIntent.UpdateFalAiModel(value)
    is GenerationInputFormEvent.UpdateFalAiImageSize -> TextToImageIntent.UpdateFalAiImageSize(value)
    is GenerationInputFormEvent.UpdateFalAiAcceleration -> TextToImageIntent.UpdateFalAiAcceleration(value)
    is GenerationInputFormEvent.UpdateSdxlBackend -> TextToImageIntent.UpdateSdxlBackend(value)
    is GenerationInputFormEvent.UpdateFalAiSyncMode -> TextToImageIntent.UpdateFalAiSyncMode(value)
    is GenerationInputFormEvent.UpdatePrompt -> TextToImageIntent.UpdatePrompt(value)
    is GenerationInputFormEvent.UpdateRestoreFaces -> TextToImageIntent.UpdateRestoreFaces(value)
    is GenerationInputFormEvent.UpdateSampler -> TextToImageIntent.UpdateSampler(value)
    is GenerationInputFormEvent.UpdateScheduler -> TextToImageIntent.UpdateScheduler(value)
    is GenerationInputFormEvent.UpdateForgeModules -> TextToImageIntent.UpdateForgeModules(value)
    is GenerationInputFormEvent.UpdateSamplingSteps -> TextToImageIntent.UpdateSamplingSteps(value)
    is GenerationInputFormEvent.UpdateSeed -> TextToImageIntent.UpdateSeed(value)
    is GenerationInputFormEvent.UpdateStabilityAiClipGuidance ->
        TextToImageIntent.UpdateStabilityAiClipGuidance(value)
    is GenerationInputFormEvent.UpdateStabilityAiStyle -> TextToImageIntent.UpdateStabilityAiStyle(value)
    is GenerationInputFormEvent.UpdateSubSeed -> TextToImageIntent.UpdateSubSeed(value)
    is GenerationInputFormEvent.UpdateSubSeedStrength -> TextToImageIntent.UpdateSubSeedStrength(value)
    is GenerationInputFormEvent.UpdateWidth -> TextToImageIntent.UpdateWidth(value)
    is GenerationInputFormEvent.UpdateHiresConfig -> TextToImageIntent.UpdateHiresConfig(value)
    is GenerationInputFormEvent.UpdateADetailerConfig -> TextToImageIntent.UpdateADetailerConfig(value)
    GenerationInputFormEvent.RefreshADetailerAvailability -> TextToImageIntent.RefreshADetailerAvailability
    GenerationInputFormEvent.OpenADetailerInstallInstructions -> TextToImageIntent.OpenADetailerInstallInstructions
}

/**
 * Executes the `appendPromptTag` step in the SDAI presentation layer.
 *
 * @param tag tag value consumed by the API.
 * @return Result produced by `appendPromptTag`.
 * @author Dmitriy Moroz
 */
private fun String.appendPromptTag(tag: String): String =
    listOf(this, tag.trim())
        .filter(String::isNotBlank)
        .joinToString(", ")

/**
 * Executes the `flushPendingTaggedText` step in the SDAI presentation layer.
 *
 * @param state state rendered or processed by the component.
 * @param promptChipTextFieldState prompt chip text field state value consumed by the API.
 * @param negativePromptChipTextFieldState negative prompt chip text field state value consumed by the API.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
private fun flushPendingTaggedText(
    state: TextToImageState,
    promptChipTextFieldState: androidx.compose.runtime.MutableState<TextFieldValue>,
    negativePromptChipTextFieldState: androidx.compose.runtime.MutableState<TextFieldValue>,
    processIntent: (TextToImageIntent) -> Unit,
) {
    promptChipTextFieldState.value.text
        .takeIf(String::isNotBlank)
        ?.let { state.prompt.appendPromptTag(it) }
        ?.let(TextToImageIntent::UpdatePrompt)
        ?.let(processIntent)
        ?.also { promptChipTextFieldState.value = TextFieldValue() }

    negativePromptChipTextFieldState.value.text
        .takeIf(String::isNotBlank)
        ?.let { state.negativePrompt.appendPromptTag(it) }
        ?.let(TextToImageIntent::UpdateNegativePrompt)
        ?.let(processIntent)
        ?.also { negativePromptChipTextFieldState.value = TextFieldValue() }
}

/**
 * Renders the `GenerationHistoryDialog` UI for the SDAI presentation layer.
 *
 * @param onClose callback invoked by the component.
 * @param onGenerationSelected callback invoked by the component.
 * @author Dmitriy Moroz
 */
@Composable
private fun GenerationHistoryDialog(
    onClose: () -> Unit,
    onGenerationSelected: (AiGenerationResult) -> Unit,
) {
    InputHistoryBottomSheet(
        onClose = onClose,
        onGenerationSelected = onGenerationSelected,
    )
}

/**
 * Defines the `TextToImagePanel` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
private sealed interface TextToImagePanel {
    /**
     * Provides the `History` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object History : TextToImagePanel
    /**
     * Carries `Embeddings` data through the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data class Embeddings(
        /**
         * Exposes the `prompt` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        val prompt: String,
        /**
         * Exposes the `negativePrompt` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        val negativePrompt: String,
    ) : TextToImagePanel
    /**
     * Carries `Extras` data through the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data class Extras(
        /**
         * Exposes the `prompt` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        val prompt: String,
        /**
         * Exposes the `negativePrompt` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        val negativePrompt: String,
        /**
         * Exposes the `type` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        val type: ExtraType,
    ) : TextToImagePanel
}
