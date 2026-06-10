@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.img2img

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArtTrack
import androidx.compose.material.icons.filled.AutoFixNormal
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeviceUnknown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.modal.GenerationModalRenderer
import com.shifthackz.aisdv1.presentation.modal.embedding.EmbeddingScreen
import com.shifthackz.aisdv1.presentation.modal.extras.ExtrasScreen
import com.shifthackz.aisdv1.presentation.modal.history.InputHistoryBottomSheet
import com.shifthackz.aisdv1.presentation.modal.tag.EditTagDialog
import com.shifthackz.aisdv1.presentation.model.ExtraType
import com.shifthackz.aisdv1.presentation.screen.txt2img.decodeBase64ImageBitmap
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputForm
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputFormEvent
import com.shifthackz.aisdv1.presentation.widget.scrollbar.verticalScrollbar
import com.shifthackz.aisdv1.presentation.widget.toolbar.GenerationBottomToolbar
import com.shifthackz.aisdv1.presentation.widget.work.BackgroundWorkWidget
import kotlin.math.roundToInt


internal fun GenerationInputFormEvent.toImageToImageIntent(): ImageToImageIntent = when (this) {
    is GenerationInputFormEvent.EditTag -> ImageToImageIntent.ShowEditTag(
        prompt = prompt,
        negativePrompt = negativePrompt,
        tag = tag,
        isNegative = isNegative,
    )
    is GenerationInputFormEvent.UpdateAdvancedOptionsVisibility ->
        ImageToImageIntent.UpdateAdvancedOptionsVisibility(visible)
    is GenerationInputFormEvent.UpdatePrompt -> ImageToImageIntent.UpdatePrompt(value)
    is GenerationInputFormEvent.UpdateNegativePrompt -> ImageToImageIntent.UpdateNegativePrompt(value)
    is GenerationInputFormEvent.UpdateWidth -> ImageToImageIntent.UpdateWidth(value)
    is GenerationInputFormEvent.UpdateHeight -> ImageToImageIntent.UpdateHeight(value)
    is GenerationInputFormEvent.UpdateSamplingSteps -> ImageToImageIntent.UpdateSamplingSteps(value)
    is GenerationInputFormEvent.UpdateCfgScale -> ImageToImageIntent.UpdateCfgScale(value)
    is GenerationInputFormEvent.UpdateRestoreFaces -> ImageToImageIntent.UpdateRestoreFaces(value)
    is GenerationInputFormEvent.UpdateSeed -> ImageToImageIntent.UpdateSeed(value)
    is GenerationInputFormEvent.UpdateSubSeed -> ImageToImageIntent.UpdateSubSeed(value)
    is GenerationInputFormEvent.UpdateSubSeedStrength -> ImageToImageIntent.UpdateSubSeedStrength(value)
    is GenerationInputFormEvent.UpdateSampler -> ImageToImageIntent.UpdateSampler(value)
    is GenerationInputFormEvent.UpdateNsfw -> ImageToImageIntent.UpdateNsfw(value)
    is GenerationInputFormEvent.UpdateBatch -> ImageToImageIntent.UpdateBatchCount(value)
    is GenerationInputFormEvent.UpdateOpenAiModel -> ImageToImageIntent.UpdateOpenAiModel(value)
    is GenerationInputFormEvent.UpdateOpenAiSize -> ImageToImageIntent.UpdateOpenAiSize(value)
    is GenerationInputFormEvent.UpdateOpenAiQuality -> ImageToImageIntent.UpdateOpenAiQuality(value)
    is GenerationInputFormEvent.UpdateStabilityAiStyle -> ImageToImageIntent.UpdateStabilityAiStyle(value)
    is GenerationInputFormEvent.UpdateStabilityAiClipGuidance ->
        ImageToImageIntent.UpdateStabilityAiClipGuidance(value)
}

internal fun String.appendPromptTag(tag: String): String =
    listOf(this, tag.trim())
        .filter(String::isNotBlank)
        .joinToString(", ")

internal fun flushPendingTaggedText(
    state: ImageToImageState,
    promptChipTextFieldState: androidx.compose.runtime.MutableState<TextFieldValue>,
    negativePromptChipTextFieldState: androidx.compose.runtime.MutableState<TextFieldValue>,
    processIntent: (ImageToImageIntent) -> Unit,
) {
    promptChipTextFieldState.value.text
        .takeIf(String::isNotBlank)
        ?.let { state.prompt.appendPromptTag(it) }
        ?.let(ImageToImageIntent::UpdatePrompt)
        ?.let(processIntent)
        ?.also { promptChipTextFieldState.value = TextFieldValue("") }

    negativePromptChipTextFieldState.value.text
        .takeIf(String::isNotBlank)
        ?.let { state.negativePrompt.appendPromptTag(it) }
        ?.let(ImageToImageIntent::UpdateNegativePrompt)
        ?.let(processIntent)
        ?.also { negativePromptChipTextFieldState.value = TextFieldValue("") }
}

@Composable
internal fun GenerationHistoryDialog(
    onClose: () -> Unit,
    onGenerationSelected: (AiGenerationResult) -> Unit,
) {
    InputHistoryBottomSheet(
        onClose = onClose,
        onGenerationSelected = onGenerationSelected,
    )
}

internal sealed interface ImageToImagePanel {
    data object History : ImageToImagePanel
    data class Embeddings(
        val prompt: String,
        val negativePrompt: String,
    ) : ImageToImagePanel

    data class Extras(
        val prompt: String,
        val negativePrompt: String,
        val type: ExtraType,
    ) : ImageToImagePanel
}

internal val AiGenerationResult.aspectRatio: Float
    get() = if (width > 0 && height > 0) width.toFloat() / height.toFloat() else 1f

internal val ImageBitmap.safeAspectRatio: Float
    get() = if (width > 0 && height > 0) width.toFloat() / height.toFloat() else 1f

internal val ServerSource.displayName: String
    get() = when (this) {
        ServerSource.AUTOMATIC1111 -> Localization.string("srv_type_own")
        ServerSource.SWARM_UI -> Localization.string("srv_type_swarm_ui")
        ServerSource.HORDE -> Localization.string("srv_type_horde")
        ServerSource.HUGGING_FACE -> Localization.string("srv_type_hugging_face")
        ServerSource.OPEN_AI -> Localization.string("srv_type_open_ai")
        ServerSource.STABILITY_AI -> Localization.string("srv_type_stability_ai")
        ServerSource.LOCAL_MICROSOFT_ONNX -> Localization.string("srv_type_local_short")
        ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> Localization.string("srv_type_media_pipe_short")
    }

internal fun Float.roundToString(): String {
    val rounded = (this * 100f).roundToInt() / 100f
    return rounded.toString()
}
