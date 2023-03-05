@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.txt2img

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.widget.CommunicationProgressDialog
import com.shifthackz.aisdv1.presentation.widget.ErrorDialog
import com.shifthackz.aisdv1.presentation.widget.GenerationImageResultDialog
import com.shifthackz.aisdv1.presentation.widget.GenerationInputForm

class TextToImageScreen(
    private val viewModel: TextToImageViewModel,
) : MviScreen<TextToImageState, TextToImageEffect>(viewModel) {

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
            onGenerateClicked = viewModel::generate,
            onDismissScreenDialog = viewModel::dismissScreenDialog,
        )
    }

    @Composable
    override fun ApplySystemUiColors() = Unit
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: TextToImageState,
    onPromptUpdated: (String) -> Unit = { _ -> },
    onNegativePromptUpdated: (String) -> Unit = { _ -> },
    onWidthUpdated: (String) -> Unit = { _ -> },
    onHeightUpdated: (String) -> Unit = { _ -> },
    onSamplingStepsUpdated: (Int) -> Unit = { _ -> },
    onCfgScaleUpdated: (Float) -> Unit = { _ -> },
    onGenerateClicked: () -> Unit = {},
    onDismissScreenDialog: () -> Unit = {},
) {
    Box(modifier) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(stringResource(id = R.string.title_text_to_image))
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                //ToDo implement info bottom sheet
                            },
                            content = {
                                Icon(
                                    imageVector = Icons.Rounded.Info,
                                    contentDescription = "Info",
                                )
                            },
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
                        prompt = state.prompt,
                        negativePrompt = state.negativePrompt,
                        width = state.width,
                        height = state.height,
                        samplingSteps = state.samplingSteps,
                        cfgScale = state.cfgScale,
                        onPromptUpdated = onPromptUpdated,
                        onNegativePromptUpdated = onNegativePromptUpdated,
                        onWidthUpdated = onWidthUpdated,
                        onHeightUpdated = onHeightUpdated,
                        onSamplingStepsUpdated = onSamplingStepsUpdated,
                        onCfgScaleUpdated = onCfgScaleUpdated,
                        widthValidationError = state.widthValidationError,
                        heightValidationError = state.heightValidationError,
                    )
                }
            },
            bottomBar = {
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
        )
        when (state.screenDialog) {
            TextToImageState.Dialog.Communicating -> CommunicationProgressDialog(
                canDismiss = false,
            )
            is TextToImageState.Dialog.Image -> GenerationImageResultDialog(
                state.screenDialog.image,
                onDismissScreenDialog,
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
        )
    )
}
//endregion
