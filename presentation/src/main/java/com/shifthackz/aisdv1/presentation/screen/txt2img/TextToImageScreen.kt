@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.txt2img

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.modal.history.InputHistoryScreen
import com.shifthackz.aisdv1.presentation.widget.dialog.ErrorDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.GenerationImageResultDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialog
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputForm
import org.koin.androidx.compose.koinViewModel

class TextToImageScreen(
    private val viewModel: TextToImageViewModel,
    private val launchGalleryDetail: (Long) -> Unit,
) : MviScreen<TextToImageState, EmptyEffect>(viewModel) {

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
            onGenerateClicked = viewModel::generate,
            onSaveGeneratedImage = viewModel::saveGeneratedResult,
            onViewGeneratedImage = launchGalleryDetail,
            onOpenPreviousGenerationInput = viewModel::openPreviousGenerationInput,
            onUpdateFromPreviousAiGeneration = viewModel::updateFormPreviousAiGeneration,
            onDismissScreenDialog = viewModel::dismissScreenModal,
        )
    }

    @Composable
    override fun ApplySystemUiColors() = Unit
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
    onGenerateClicked: () -> Unit = {},
    onSaveGeneratedImage: (AiGenerationResult) -> Unit = {},
    onViewGeneratedImage: (Long) -> Unit = {},
    onOpenPreviousGenerationInput: () -> Unit = {},
    onUpdateFromPreviousAiGeneration: (AiGenerationResult) -> Unit = {},
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
                        widthValidationError = state.widthValidationError,
                        heightValidationError = state.heightValidationError,
                    )
                }
            },
            bottomBar = {
                Column(Modifier.fillMaxWidth()) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
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
        when (state.screenModal) {
            is TextToImageState.Modal.Communicating -> ProgressDialog(
                canDismiss = false,
                waitTimeSeconds = state.screenModal.hordeProcessStatus?.waitTimeSeconds,
                positionInQueue = state.screenModal.hordeProcessStatus?.queuePosition,
            )
            is TextToImageState.Modal.Generating -> ProgressDialog(
                titleResId = R.string.communicating_local_title,
                canDismiss = false,
                step = state.screenModal.pair,
            )
            is TextToImageState.Modal.Image -> GenerationImageResultDialog(
                imageBase64 = state.screenModal.result.image,
                showSaveButton = !state.screenModal.autoSaveEnabled,
                onDismissRequest = onDismissScreenDialog,
                onSaveRequest = { onSaveGeneratedImage(state.screenModal.result) },
                onViewDetailRequest = { onViewGeneratedImage(state.screenModal.result.id) },
            )
            is TextToImageState.Modal.Error -> ErrorDialog(
                text = state.screenModal.error,
                onDismissScreenDialog,
            )
            is TextToImageState.Modal.PromptBottomSheet -> ModalBottomSheet(
                onDismissRequest = onDismissScreenDialog,
                shape = RectangleShape,
            ) {
                InputHistoryScreen(
                    viewModel = koinViewModel(),
                    onGenerationSelected = { ai ->
                        onUpdateFromPreviousAiGeneration(ai)
                        onDismissScreenDialog()
                    },
                ).Build()
            }
            else -> Unit
        }
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
