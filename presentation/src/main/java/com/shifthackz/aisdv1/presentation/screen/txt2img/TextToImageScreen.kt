@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.txt2img

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixNormal
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.core.GenerationMviEffect
import com.shifthackz.aisdv1.presentation.core.GenerationMviScreen
import com.shifthackz.aisdv1.presentation.modal.ModalRenderer
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputForm
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputMode
import com.shifthackz.aisdv1.presentation.widget.toolbar.GenerationBottomToolbar

class TextToImageScreen(
    private val viewModel: TextToImageViewModel,
    launchGalleryDetail: (Long) -> Unit,
) : GenerationMviScreen<TextToImageState, GenerationMviEffect>(
    viewModel,
    launchGalleryDetail,
) {

    @Composable
    override fun Content() {
        val state = viewModel.state.collectAsStateWithLifecycle().value
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = state,
            onShowAdvancedOptionsToggle = viewModel::toggleAdvancedOptions,
            onPromptUpdated = viewModel::updatePrompt,
            onNegativePromptUpdated = viewModel::updateNegativePrompt,
            onWidthUpdated = viewModel::updateWidth,
            onHeightUpdated = viewModel::updateHeight,
            onSamplingStepsUpdated = viewModel::updateSamplingSteps,
            onCfgScaleUpdated = viewModel::updateCfgScale,
            onRestoreFacesUpdated = viewModel::updateRestoreFaces,
            onSeedUpdated = viewModel::updateSeed,
            onSubSeedUpdated = viewModel::updateSubSeed,
            onSubSeedStrengthUpdated = viewModel::updateSubSeedStrength,
            onSamplerUpdated = viewModel::updateSampler,
            onNsfwUpdated = viewModel::updateNsfw,
            onBatchCountUpdated = viewModel::updateBatchCount,
            onGenerateClicked = viewModel::generate,
            onSaveGeneratedImages = viewModel::saveGeneratedResults,
            onViewGeneratedImage = viewModel::viewGeneratedResult,
            onOpenPreviousGenerationInput = viewModel::openPreviousGenerationInput,
            onUpdateFromPreviousAiGeneration = viewModel::updateFormPreviousAiGeneration,
            onOpenLoraInput = viewModel::openLoraInput,
            onOpenHyperNetInput = viewModel::openHyperNetInput,
            onOpenEmbedding = viewModel::openEmbeddingInput,
            onProcessNewPrompts = viewModel::processNewPrompts,
            onDismissScreenDialog = viewModel::dismissScreenModal,
        )
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: TextToImageState,
    onShowAdvancedOptionsToggle: (Boolean) -> Unit = {},
    onPromptUpdated: (String) -> Unit = {},
    onNegativePromptUpdated: (String) -> Unit = {},
    onWidthUpdated: (String) -> Unit = {},
    onHeightUpdated: (String) -> Unit = {},
    onSamplingStepsUpdated: (Int) -> Unit = {},
    onCfgScaleUpdated: (Float) -> Unit = {},
    onRestoreFacesUpdated: (Boolean) -> Unit = {},
    onSeedUpdated: (String) -> Unit = {},
    onSubSeedUpdated: (String) -> Unit = {},
    onSubSeedStrengthUpdated: (Float) -> Unit = {},
    onSamplerUpdated: (String) -> Unit = {},
    onNsfwUpdated: (Boolean) -> Unit = {},
    onBatchCountUpdated: (Int) -> Unit = {},
    onGenerateClicked: () -> Unit = {},
    onSaveGeneratedImages: (List<AiGenerationResult>) -> Unit = {},
    onViewGeneratedImage: (AiGenerationResult) -> Unit = {},
    onOpenPreviousGenerationInput: () -> Unit = {},
    onUpdateFromPreviousAiGeneration: (AiGenerationResult) -> Unit = {},
    onOpenLoraInput: () -> Unit = {},
    onOpenHyperNetInput: () -> Unit = {},
    onOpenEmbedding: () -> Unit = {},
    onProcessNewPrompts: (String, String) -> Unit = { _, _ -> },
    onDismissScreenDialog: () -> Unit = {},
) {
    Box(modifier) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.title_text_to_image),
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    },
                    actions = {
                        IconButton(onClick = onOpenPreviousGenerationInput) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                            )
                        }
                    },
                )
            },
            content = { paddingValues ->
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp),
                ) {
                    GenerationInputForm(
                        state = state,
                        onShowAdvancedOptionsToggle = onShowAdvancedOptionsToggle,
                        onPromptUpdated = onPromptUpdated,
                        onNegativePromptUpdated = onNegativePromptUpdated,
                        onWidthUpdated = onWidthUpdated,
                        onHeightUpdated = onHeightUpdated,
                        onSamplingStepsUpdated = onSamplingStepsUpdated,
                        onCfgScaleUpdated = onCfgScaleUpdated,
                        onRestoreFacesUpdated = onRestoreFacesUpdated,
                        onSeedUpdated = onSeedUpdated,
                        onSubSeedUpdated = onSubSeedUpdated,
                        onSubSeedStrengthUpdated = onSubSeedStrengthUpdated,
                        onSamplerUpdated = onSamplerUpdated,
                        onNsfwUpdated = onNsfwUpdated,
                        onBatchCountUpdated = onBatchCountUpdated,
                        widthValidationError = state.widthValidationError,
                        heightValidationError = state.heightValidationError,
                    )
                }
            },
            bottomBar = {
                GenerationBottomToolbar(
                    showToolbar = state.mode == GenerationInputMode.AUTOMATIC1111,
                    strokeAccentState = !state.hasValidationErrors,
                    onOpenLoraInput = onOpenLoraInput,
                    onOpenHyperNetInput = onOpenHyperNetInput,
                    onOpenEmbedding = onOpenEmbedding,
                ) {
                    Button(
                        modifier = Modifier
                            .height(height = 60.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp),
                        onClick = onGenerateClicked,
                        enabled = !state.hasValidationErrors
                    ) {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            imageVector = Icons.Default.AutoFixNormal,
                            contentDescription = "Imagine",
                        )
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = stringResource(id = R.string.action_generate),
                            color = LocalContentColor.current,
                        )
                    }
                }
            }
        )
        ModalRenderer(
            screenModal = state.screenModal,
            onSaveGeneratedImages = onSaveGeneratedImages,
            onViewGeneratedImage = onViewGeneratedImage,
            onUpdateFromPreviousAiGeneration = onUpdateFromPreviousAiGeneration,
            onProcessNewPrompts = onProcessNewPrompts,
            onDismissScreenDialog = onDismissScreenDialog,
        )
    }
}


//region PREVIEWS
@Composable
@Preview(showSystemUi = true, showBackground = true)
fun PreviewStateContent() {
    ScreenContent(
        modifier = Modifier.fillMaxSize(),
        state = TextToImageState(
            prompt = "Opel Astra H OPC",
            negativePrompt = "White background",
            samplingSteps = 55,
            availableSamplers = listOf("Euler a")
        )
    )
}
//endregion
