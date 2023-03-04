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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.presentation.screen.txt2img.contract.TextToImageEffect
import com.shifthackz.aisdv1.presentation.screen.txt2img.contract.TextToImageState
import com.shifthackz.aisdv1.presentation.screen.txt2img.model.StableDiffusionModelUi
import com.shifthackz.aisdv1.presentation.screen.txt2img.model.TextToImagePayloadUi
import com.shifthackz.aisdv1.presentation.utils.base64ToImage
import kotlin.math.roundToInt

class TextToImageScreen(
    private val viewModel: TextToImageViewModel,
) : MviScreen<TextToImageState, TextToImageEffect>(viewModel) {

    override val statusBarColor: Color = Color.Cyan

    @Composable
    override fun Content() {
        val state = viewModel.state.collectAsState().value
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = state,
            generate = viewModel::generate,
            selectSdModel = viewModel::selectStableDiffusionModel,
        )
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: TextToImageState,
    generate: (TextToImagePayloadUi) -> Unit = { _ -> },
    selectSdModel: (StableDiffusionModelUi) -> Unit = { _ -> },
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
        var sdModelsItems by remember { mutableStateOf(listOf<StableDiffusionModelUi>()) }
        var sdModelsExpanded by remember { mutableStateOf(false) }
        var sdModelsSelection by remember { mutableStateOf("") }

        var prompt by remember { mutableStateOf(TextFieldValue("")) }
        var negativePrompt by remember { mutableStateOf(TextFieldValue("")) }
        var samplingSteps by remember { mutableStateOf(20f) }

        (state as? TextToImageState.Content)?.let { content ->
            sdModelsItems = content.models
            sdModelsSelection = content.models.first { it.isSelected }.title
        }

        ExposedDropdownMenuBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            expanded = sdModelsExpanded,
            onExpandedChange = { sdModelsExpanded = !sdModelsExpanded },
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                value = sdModelsSelection,
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
                sdModelsItems.forEach { model ->
                    DropdownMenuItem(
                        text = { Text(model.title) },
                        onClick = {
                            sdModelsExpanded = false
                            if (sdModelsSelection == model.title) return@DropdownMenuItem
                            sdModelsSelection = model.title
                            selectSdModel(model)
                        },
                    )
                }
            }
        }

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            value = prompt,
            onValueChange = { prompt = it },
            label = { Text("Prompt") },
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            value = negativePrompt,
            onValueChange = { negativePrompt = it },
            label = { Text("Negative prompt") },
        )

        Text(
            text = "Sampling steps: ${samplingSteps.roundToInt()}",
        )
        Slider(
            value = samplingSteps,
            valueRange = 0f..150f,
            steps = 149,
            onValueChange = {
                println("DBG0, samplingSteps = $it")
                samplingSteps = it
            },
        )

        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.6f),
            onClick = {
                generate(
                    TextToImagePayloadUi(
                        prompt = prompt.text,
                        negativePrompt = negativePrompt.text,
                        samplingSteps = samplingSteps.roundToInt(),
                    ),
                )
            },
        ) {
            Text(
                text = "Txt 2 img"
            )
        }

        if (state is TextToImageState.Image) {
            val bmp = base64ToImage(state.image)
            Image(bitmap = bmp.asImageBitmap(), contentDescription = "ai")
        }
    }
}


@Composable
@Preview(name = "STATE -> Idle", showSystemUi = true, showBackground = true)
private fun PreviewStateIdle() {
    ScreenContent(
        modifier = Modifier.fillMaxSize(),
        state = TextToImageState.Idle,
    )
}
