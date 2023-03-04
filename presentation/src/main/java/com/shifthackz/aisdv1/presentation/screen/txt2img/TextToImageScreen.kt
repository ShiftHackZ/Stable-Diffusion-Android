@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.txt2img

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.presentation.utils.base64ToImage
import kotlin.math.roundToInt

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
            onSamplingStepsUpdated = viewModel::updateSamplingSteps,
            onGenerateClicked = viewModel::generate,
            onSelectedSdModel = viewModel::selectStableDiffusionModel,
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
    onSamplingStepsUpdated: (Int) -> Unit = { _ -> },
    onGenerateClicked: () -> Unit = {},
    onSelectedSdModel: (String) -> Unit = { _ -> },
) {
    when (state) {
        is TextToImageState.Content -> TextToImageGenerationForm(
            modifier = modifier,
            state = state,
            onPromptUpdated = onPromptUpdated,
            onNegativePromptUpdated = onNegativePromptUpdated,
            onSamplingStepsUpdated = onSamplingStepsUpdated,
            onGenerateClicked = onGenerateClicked,
            onSelectedSdModel = onSelectedSdModel,
        )
//        is TextToImageState.Image -> {
//            val bmp = base64ToImage(state.image)
//            Image(bitmap = bmp.asImageBitmap(), contentDescription = "ai")
//        }
        TextToImageState.Uninitialized -> {
            Text("Uninitialized")
        }
    }
}

@Composable
private fun TextToImageGenerationForm(
    modifier: Modifier = Modifier,
    state: TextToImageState.Content,
    onPromptUpdated: (String) -> Unit = { _ -> },
    onNegativePromptUpdated: (String) -> Unit = { _ -> },
    onSamplingStepsUpdated: (Int) -> Unit = { _ -> },
    onGenerateClicked: () -> Unit = {},
    onSelectedSdModel: (String) -> Unit = { _ -> },
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
//        var sdModelsItems by remember { mutableStateOf(listOf<StableDiffusionModelUi>()) }
        var sdModelsExpanded by remember { mutableStateOf(false) }
//        var sdModelsSelection by remember { mutableStateOf("") }

//        sdModelsItems = state.models
//        sdModelsSelection = state.models.first { it.isSelected }.title

        ExposedDropdownMenuBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            expanded = sdModelsExpanded,
            onExpandedChange = { sdModelsExpanded = !sdModelsExpanded },
        ) {
            val selectedModel = state.selectedModel.asString()
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                value = selectedModel,
                onValueChange = {},
                readOnly = true,
                label = { Text("SD Model") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = sdModelsExpanded)
                }
            )

            ExposedDropdownMenu(
                expanded = sdModelsExpanded,
                onDismissRequest = { sdModelsExpanded = false },
            ) {
                state.models.forEach { title ->
                    DropdownMenuItem(
                        text = { Text(title) },
                        onClick = {
                            sdModelsExpanded = false
                            if (selectedModel == title) return@DropdownMenuItem
                            onSelectedSdModel(title)
                            //sdModelsSelection = model.title
                            //selectSdModel(model)
                        },
                    )
                }
            }
        }

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            value = state.prompt,
            onValueChange = onPromptUpdated,
            label = { Text("Prompt") },
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            value = state.negativePrompt,
            onValueChange = onNegativePromptUpdated,
            label = { Text("Negative prompt") },
        )

        Text(
            text = "Sampling steps: ${state.samplingSteps}",
        )
        Slider(
            value = state.samplingSteps * 1f,
            valueRange = 0f..150f,
            steps = 149,
            onValueChange = {
                onSamplingStepsUpdated(it.roundToInt())
            },
        )

        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.6f),
            onClick = onGenerateClicked,
        ) {
            Text(
                text = "Txt 2 img"
            )
        }
    }
}

//region PREVIEWS
@Composable
@Preview(showSystemUi = true, showBackground = true)
private fun PreviewStateUninitialized() {
    ScreenContent(
        modifier = Modifier.fillMaxSize(),
        state = TextToImageState.Uninitialized,
    )
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
private fun PreviewStateContent() {
    ScreenContent(
        modifier = Modifier.fillMaxSize(),
        state = TextToImageState.Content(
            models = listOf("Stable Diffusion v1.5"),
            selectedModel = "Stable Diffusion v1.5".asUiText(),
            prompt = "Opel Astra H OPC",
            negativePrompt = "White background",
            samplingSteps = 55,
        )
    )
}
//endregion
