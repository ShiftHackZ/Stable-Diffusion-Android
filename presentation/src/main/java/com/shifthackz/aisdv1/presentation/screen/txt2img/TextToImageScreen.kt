@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.txt2img

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.widget.GenerationInputForm
import com.shifthackz.aisdv1.presentation.widget.coins.AvailableCoinsComposable
import com.shifthackz.aisdv1.presentation.widget.dialog.ErrorDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.GenerationImageResultDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.NoSdAiCoinsDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialog
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
            onPromptUpdated = viewModel::updatePrompt,
            onNegativePromptUpdated = viewModel::updateNegativePrompt,
            onWidthUpdated = viewModel::updateWidth,
            onHeightUpdated = viewModel::updateHeight,
            onSamplingStepsUpdated = viewModel::updateSamplingSteps,
            onCfgScaleUpdated = viewModel::updateCfgScale,
            onRestoreFacesUpdated = viewModel::updateRestoreFaces,
            onSeedUpdated = viewModel::updateSeed,
            onSamplerUpdated = viewModel::updateSampler,
            onGenerateClicked = viewModel::generate,
            onSaveGeneratedImage = viewModel::saveGeneratedResult,
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
    onPromptUpdated: (String) -> Unit = {},
    onNegativePromptUpdated: (String) -> Unit = {},
    onWidthUpdated: (String) -> Unit = {},
    onHeightUpdated: (String) -> Unit = {},
    onSamplingStepsUpdated: (Int) -> Unit = {},
    onCfgScaleUpdated: (Float) -> Unit = {},
    onRestoreFacesUpdated: (Boolean) -> Unit = {},
    onSeedUpdated: (String) -> Unit = {},
    onSamplerUpdated: (String) -> Unit = {},
    onGenerateClicked: () -> Unit = {},
    onSaveGeneratedImage: (AiGenerationResult) -> Unit = {},
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
                        onPromptUpdated = onPromptUpdated,
                        onNegativePromptUpdated = onNegativePromptUpdated,
                        onWidthUpdated = onWidthUpdated,
                        onHeightUpdated = onHeightUpdated,
                        onSamplingStepsUpdated = onSamplingStepsUpdated,
                        onCfgScaleUpdated = onCfgScaleUpdated,
                        onRestoreFacesUpdated = onRestoreFacesUpdated,
                        onSeedUpdated = onSeedUpdated,
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
                        Text(
                            text = stringResource(id = R.string.action_generate)
                        )
                    }
                }
            }
        )
        when (state.screenDialog) {
            TextToImageState.Dialog.Communicating -> ProgressDialog(
                canDismiss = false,
            )
            TextToImageState.Dialog.NoSdAiCoins -> NoSdAiCoinsDialog(
                onDismissRequest = onDismissScreenDialog,
                launchRewarded = onLaunchRewarded,
            )
            is TextToImageState.Dialog.Image -> GenerationImageResultDialog(
                imageBase64 = state.screenDialog.result.image,
                showSaveButton = !state.screenDialog.autoSaveEnabled,
                onDismissRequest = onDismissScreenDialog,
                onSaveRequest = { onSaveGeneratedImage(state.screenDialog.result) },
            )
            is TextToImageState.Dialog.Error -> ErrorDialog(
                text = state.screenDialog.error,
                onDismissScreenDialog,
            )
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
