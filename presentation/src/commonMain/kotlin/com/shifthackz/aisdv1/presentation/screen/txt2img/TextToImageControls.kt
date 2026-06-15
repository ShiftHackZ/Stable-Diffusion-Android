@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.txt2img

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixNormal
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ServerSource
import kotlin.math.roundToInt


/**
 * Renders the `NumberField` UI for the SDAI presentation layer.
 *
 * @param value value value consumed by the API.
 * @param label label value consumed by the API.
 * @param error error value consumed by the API.
 * @param onValueChange callback invoked by the component.
 * @param modifier Compose modifier applied to the rendered UI.
 * @author Dmitriy Moroz
 */
@Composable
internal fun NumberField(
    value: String,
    label: String,
    error: UiText?,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        isError = error != null,
        supportingText = error?.let { message ->
            { Text(message.asString()) }
        },
    )
}

/**
 * Renders the `SliderRow` UI for the SDAI presentation layer.
 *
 * @param label label value consumed by the API.
 * @param value value value consumed by the API.
 * @param content content value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
internal fun SliderRow(
    label: String,
    value: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.W600,
            )
        }
        content()
    }
}

/**
 * Renders the `BatchControl` UI for the SDAI presentation layer.
 *
 * @param state state rendered or processed by the component.
 * @param strings strings value consumed by the API.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
internal fun BatchControl(
    state: TextToImageState,
    strings: TextToImageStrings,
    processIntent: (TextToImageIntent) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = strings.batch,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(
                contentPadding = PaddingValues(horizontal = 12.dp),
                onClick = {
                    processIntent(TextToImageIntent.UpdateBatchCount(state.batchCount - 1))
                },
            ) {
                Text("-")
            }
            Text(
                text = state.batchCount.toString(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.W600,
            )
            OutlinedButton(
                contentPadding = PaddingValues(horizontal = 12.dp),
                onClick = {
                    processIntent(TextToImageIntent.UpdateBatchCount(state.batchCount + 1))
                },
            ) {
                Text("+")
            }
        }
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
    strings: TextToImageStrings,
    savingImage: Boolean,
    sharingImage: Boolean,
    processIntent: (TextToImageIntent) -> Unit,
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(result.aspectRatio),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        modifier = Modifier.size(56.dp),
                        imageVector = Icons.Default.AutoFixNormal,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                Text(
                    text = strings.imageUnavailable,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
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
                    onClick = { processIntent(TextToImageIntent.SaveResult(result.image)) },
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
                    onClick = { processIntent(TextToImageIntent.ShareResult(result.image)) },
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
 * Exposes the `AiGenerationResult` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal val AiGenerationResult.aspectRatio: Float
    get() = if (width > 0 && height > 0) width.toFloat() / height.toFloat() else 1f

/**
 * Exposes the `ServerSource` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal val ServerSource.displayName: String
    get() = when (this) {
        ServerSource.AUTOMATIC1111 -> Localization.string("srv_type_own")
        ServerSource.SWARM_UI -> Localization.string("srv_type_swarm_ui")
        ServerSource.HORDE -> Localization.string("srv_type_horde")
        ServerSource.HUGGING_FACE -> Localization.string("srv_type_hugging_face")
        ServerSource.OPEN_AI -> Localization.string("srv_type_open_ai")
        ServerSource.STABILITY_AI -> Localization.string("srv_type_stability_ai")
        ServerSource.FAL_AI -> Localization.string("srv_type_fal_ai")
        ServerSource.ARLI_AI -> Localization.string("srv_type_arli_ai")
        ServerSource.LOCAL_MICROSOFT_ONNX -> Localization.string("srv_type_local_short")
        ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> Localization.string("srv_type_media_pipe_short")
        ServerSource.LOCAL_STABLE_DIFFUSION_CPP -> Localization.string("srv_type_sdxl_short")
        ServerSource.LOCAL_APPLE_CORE_ML -> "Core ML"
        ServerSource.LOCAL_APPLE_BONSAI -> "Silicon Diffusion PrismML Bonsai"
    }

/**
 * Executes the `roundToString` step in the SDAI presentation layer.
 *
 * @return Result produced by `roundToString`.
 * @author Dmitriy Moroz
 */
internal fun Float.roundToString(): String {
    val rounded = (this * 10f).roundToInt() / 10f
    return rounded.toString()
}
