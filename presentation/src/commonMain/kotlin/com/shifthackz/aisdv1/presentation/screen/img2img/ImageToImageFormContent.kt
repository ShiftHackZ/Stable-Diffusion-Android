@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.img2img

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.platform.rememberExternalUrlLauncher
import com.shifthackz.aisdv1.presentation.screen.txt2img.decodeBase64ImageBitmap
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputForm
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputFormEvent


/**
 * Renders the `ImageToImageForm` UI for the SDAI presentation layer.
 *
 * @param state state rendered or processed by the component.
 * @param strings strings value consumed by the API.
 * @param promptChipTextFieldState prompt chip text field state value consumed by the API.
 * @param negativePromptChipTextFieldState negative prompt chip text field state value consumed by the API.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
internal fun ImageToImageForm(
    state: ImageToImageState,
    strings: ImageToImageStrings,
    promptChipTextFieldState: androidx.compose.runtime.MutableState<TextFieldValue>,
    negativePromptChipTextFieldState: androidx.compose.runtime.MutableState<TextFieldValue>,
    processIntent: (ImageToImageIntent) -> Unit,
) {
    val urlLauncher = rememberExternalUrlLauncher()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            GenerationInputForm(
                state = state,
                isImg2Img = true,
                textFieldContainerColor = MaterialTheme.colorScheme.surface,
                promptChipTextFieldState = promptChipTextFieldState,
                negativePromptChipTextFieldState = negativePromptChipTextFieldState,
                onEvent = { event ->
                    if (event == GenerationInputFormEvent.OpenADetailerInstallInstructions) {
                        urlLauncher.openUrl(ADETAILER_INSTALL_URL)
                    } else {
                        event.toImageToImageIntent()?.let(processIntent)
                    }
                },
                afterSlidersSection = {
                    DenoisingStrengthSlider(
                        state = state,
                        strings = strings,
                        processIntent = processIntent,
                    )
                },
            )

            state.promptValidationError?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            if (!state.sourceSupportsImageToImage) {
                Text(
                    text = strings.sourceUnavailable,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            state.error?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            state.message?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            if (state.generating) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

private const val ADETAILER_INSTALL_URL = "https://github.com/Bing-su/adetailer#install"

/**
 * Renders the `DenoisingStrengthSlider` UI for the SDAI presentation layer.
 *
 * @param state state rendered or processed by the component.
 * @param strings strings value consumed by the API.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
internal fun DenoisingStrengthSlider(
    state: ImageToImageState,
    strings: ImageToImageStrings,
    processIntent: (ImageToImageIntent) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = Localization.string(
                    "hint_denoising_strength",
                    state.denoisingStrength.roundToString(),
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Slider(
            value = state.denoisingStrength,
            onValueChange = {
                processIntent(ImageToImageIntent.UpdateDenoisingStrength(it))
            },
            valueRange = 0f..1f,
        )
    }
}

/**
 * Renders the `GeneratedImageItem` UI for the SDAI presentation layer.
 *
 * @param result result value consumed by the API.
 * @param strings strings value consumed by the API.
 * @param savingImage saving image value consumed by the API.
 * @param sharingImage sharing image value consumed by the API.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
internal fun GeneratedImageItem(
    result: AiGenerationResult,
    strings: ImageToImageStrings,
    savingImage: Boolean,
    sharingImage: Boolean,
    processIntent: (ImageToImageIntent) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            val imageBitmap = remember(result.image) {
                result.image.decodeBase64ImageBitmap()
            }
            if (imageBitmap != null) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(result.aspectRatio),
                    bitmap = imageBitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )
            } else {
                EmptyGeneratedImage(strings.imageUnavailable)
            }

            Text(
                text = result.prompt,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = strings.resultMeta(result),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val actionsEnabled = result.image.isNotBlank() && !savingImage && !sharingImage
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    enabled = actionsEnabled,
                    onClick = { processIntent(ImageToImageIntent.SaveResult(result.image)) },
                ) {
                    if (savingImage) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                        )
                    }
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = if (savingImage) strings.savingImage else strings.save,
                    )
                }
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    enabled = actionsEnabled,
                    onClick = { processIntent(ImageToImageIntent.ShareResult(result.image)) },
                ) {
                    if (sharingImage) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                        )
                    }
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = if (sharingImage) strings.sharingImage else strings.share,
                    )
                }
            }
        }
    }
}

/**
 * Renders the `EmptyGeneratedImage` UI for the SDAI presentation layer.
 *
 * @param text text value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
internal fun EmptyGeneratedImage(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
