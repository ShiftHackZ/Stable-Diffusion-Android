@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.txt2img

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixNormal
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.modal.history.InputHistoryScreen
import com.shifthackz.aisdv1.presentation.widget.coins.AvailableCoinsComposable
import com.shifthackz.aisdv1.presentation.widget.dialog.ErrorDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.GenerationImageResultDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.NoSdAiCoinsDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialog
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputForm
import org.koin.androidx.compose.koinViewModel

class TextToImageScreen(
    private val viewModel: TextToImageViewModel,
    private val launchRewarded: () -> Unit,
) : MviScreen<TextToImageState, EmptyEffect>(viewModel) {

    @Composable
    override fun Content() {
        val state = viewModel.state.collectAsState().value
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
            onOpenPreviousGenerationInput = viewModel::openPreviousGenerationInput,
            onUpdateFromPreviousAiGeneration = viewModel::updateFormPreviousAiGeneration,
            onDismissScreenDialog = viewModel::dismissScreenDialog,
            onLaunchRewarded = launchRewarded,
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
    onOpenPreviousGenerationInput: () -> Unit = {},
    onUpdateFromPreviousAiGeneration: (AiGenerationResult) -> Unit = {},
    onDismissScreenDialog: () -> Unit = {},
    onLaunchRewarded: () -> Unit = {},
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
                    AvailableCoinsComposable(
                        modifier = Modifier.fillMaxWidth(),
                        viewModel = koinViewModel()
                    ).Build()
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
                            text = stringResource(id = R.string.action_generate)
                        )
                    }
                }
            }
        )
        when (state.screenModal) {
            TextToImageState.Modal.Communicating -> ProgressDialog(
                canDismiss = false,
            )
            TextToImageState.Modal.NoSdAiCoins -> NoSdAiCoinsDialog(
                onDismissRequest = onDismissScreenDialog,
                launchRewarded = onLaunchRewarded,
            )
            is TextToImageState.Modal.Image -> GenerationImageResultDialog(
                imageBase64 = state.screenModal.result.image,
                showSaveButton = !state.screenModal.autoSaveEnabled,
                onDismissRequest = onDismissScreenDialog,
                onSaveRequest = { onSaveGeneratedImage(state.screenModal.result) },
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
