@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.img2img

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.TextFieldValue
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.modal.history.InputHistoryBottomSheet
import com.shifthackz.aisdv1.presentation.model.ExtraType
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputFormEvent
import kotlin.math.roundToInt


/**
 * Converts SDAI data with `toImageToImageIntent`.
 *
 * @author Dmitriy Moroz
 */
internal fun GenerationInputFormEvent.toImageToImageIntent(): ImageToImageIntent? = when (this) {
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
    GenerationInputFormEvent.SwapDimensions -> ImageToImageIntent.SwapDimensions
    is GenerationInputFormEvent.ApplyAspectRatio -> ImageToImageIntent.ApplyAspectRatio(ratio)
    is GenerationInputFormEvent.UpdateHeight -> ImageToImageIntent.UpdateHeight(value)
    is GenerationInputFormEvent.UpdateSamplingSteps -> ImageToImageIntent.UpdateSamplingSteps(value)
    is GenerationInputFormEvent.UpdateCfgScale -> ImageToImageIntent.UpdateCfgScale(value)
    is GenerationInputFormEvent.UpdateRestoreFaces -> ImageToImageIntent.UpdateRestoreFaces(value)
    is GenerationInputFormEvent.UpdateSeed -> ImageToImageIntent.UpdateSeed(value)
    is GenerationInputFormEvent.UpdateSubSeed -> ImageToImageIntent.UpdateSubSeed(value)
    is GenerationInputFormEvent.UpdateSubSeedStrength -> ImageToImageIntent.UpdateSubSeedStrength(value)
    is GenerationInputFormEvent.UpdateSampler -> ImageToImageIntent.UpdateSampler(value)
    is GenerationInputFormEvent.UpdateScheduler -> ImageToImageIntent.UpdateScheduler(value)
    is GenerationInputFormEvent.UpdateForgeModules -> null
    is GenerationInputFormEvent.UpdateNsfw -> ImageToImageIntent.UpdateNsfw(value)
    is GenerationInputFormEvent.UpdateBatch -> ImageToImageIntent.UpdateBatchCount(value)
    is GenerationInputFormEvent.UpdateOpenAiModel -> ImageToImageIntent.UpdateOpenAiModel(value)
    is GenerationInputFormEvent.UpdateOpenAiSize -> ImageToImageIntent.UpdateOpenAiSize(value)
    is GenerationInputFormEvent.UpdateOpenAiQuality -> ImageToImageIntent.UpdateOpenAiQuality(value)
    is GenerationInputFormEvent.UpdateFalAiModel -> ImageToImageIntent.UpdateFalAiModel(value)
    is GenerationInputFormEvent.UpdateFalAiImageSize -> ImageToImageIntent.UpdateFalAiImageSize(value)
    is GenerationInputFormEvent.UpdateFalAiAcceleration -> ImageToImageIntent.UpdateFalAiAcceleration(value)
    is GenerationInputFormEvent.UpdateSdxlBackend -> null
    is GenerationInputFormEvent.UpdateFalAiSyncMode -> ImageToImageIntent.UpdateFalAiSyncMode(value)
    is GenerationInputFormEvent.UpdateArliAiModel -> ImageToImageIntent.UpdateArliAiModel(value)
    is GenerationInputFormEvent.UpdateStabilityAiStyle -> ImageToImageIntent.UpdateStabilityAiStyle(value)
    is GenerationInputFormEvent.UpdateStabilityAiClipGuidance ->
        ImageToImageIntent.UpdateStabilityAiClipGuidance(value)
    is GenerationInputFormEvent.UpdateHiresConfig -> null
    is GenerationInputFormEvent.UpdateADetailerConfig -> ImageToImageIntent.UpdateADetailerConfig(value)
    GenerationInputFormEvent.RefreshADetailerAvailability -> ImageToImageIntent.RefreshADetailerAvailability
    GenerationInputFormEvent.OpenADetailerInstallInstructions -> ImageToImageIntent.OpenADetailerInstallInstructions
}

/**
 * Executes the `appendPromptTag` step in the SDAI presentation layer.
 *
 * @param tag tag value consumed by the API.
 * @return Result produced by `appendPromptTag`.
 * @author Dmitriy Moroz
 */
internal fun String.appendPromptTag(tag: String): String =
    listOf(this, tag.trim())
        .filter(String::isNotBlank)
        .joinToString(", ")

/**
 * Executes the `flushPendingTaggedText` step in the SDAI presentation layer.
 *
 * @param state state rendered or processed by the component.
 * @param promptChipTextFieldState prompt chip text field state value consumed by the API.
 * @param negativePromptChipTextFieldState negative prompt chip text field state value consumed by the API.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
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

/**
 * Renders the `GenerationHistoryDialog` UI for the SDAI presentation layer.
 *
 * @param onClose callback invoked by the component.
 * @param onGenerationSelected callback invoked by the component.
 * @author Dmitriy Moroz
 */
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

/**
 * Defines the `ImageToImagePanel` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal sealed interface ImageToImagePanel {
    /**
     * Provides the `History` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object History : ImageToImagePanel
    /**
     * Carries `Embeddings` data through the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data class Embeddings(
        /**
         * Exposes the `prompt` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        val prompt: String,
        /**
         * Exposes the `negativePrompt` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        val negativePrompt: String,
    ) : ImageToImagePanel

    /**
     * Carries `Extras` data through the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data class Extras(
        /**
         * Exposes the `prompt` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        val prompt: String,
        /**
         * Exposes the `negativePrompt` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        val negativePrompt: String,
        /**
         * Exposes the `type` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        val type: ExtraType,
    ) : ImageToImagePanel
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
    val rounded = (this * 100f).roundToInt() / 100f
    return rounded.toString()
}
