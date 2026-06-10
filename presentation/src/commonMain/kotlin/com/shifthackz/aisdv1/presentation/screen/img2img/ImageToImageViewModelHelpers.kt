package com.shifthackz.aisdv1.presentation.screen.img2img

import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.validation.ValidationResult
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.Settings
import com.shifthackz.aisdv1.domain.entity.StabilityAiSampler
import com.shifthackz.aisdv1.presentation.model.GenerationModal

internal const val MIN_STEPS = 1
internal const val MAX_STEPS = 150
internal const val MIN_BATCH_COUNT = 1
internal const val MAX_BATCH_COUNT = 20
internal const val MIN_CFG_SCALE = 1f
internal const val MAX_CFG_SCALE = 30f
internal const val MIN_SUB_SEED_STRENGTH = 0f
internal const val MAX_SUB_SEED_STRENGTH = 1f
internal const val MIN_DENOISING_STRENGTH = 0f
internal const val MAX_DENOISING_STRENGTH = 1f

internal data class StableDiffusionSamplersKey(
    val serverUrl: String,
    val demoMode: Boolean,
)

internal fun ImageToImageState.progressModal(
    canCancelLocalGeneration: Boolean,
): GenerationModal = if (
    mode == ServerSource.LOCAL_MICROSOFT_ONNX || mode == ServerSource.LOCAL_GOOGLE_MEDIA_PIPE
) {
    GenerationModal.Generating(canCancel = canCancelLocalGeneration)
} else {
    GenerationModal.Communicating()
}

internal fun List<AiGenerationResult>.toImageToImageResultModal(
    autoSaveEnabled: Boolean,
    reportEnabled: Boolean,
): GenerationModal =
    takeIf(List<AiGenerationResult>::isNotEmpty)
        ?.let {
            GenerationModal.Image.create(
                list = it,
                autoSaveEnabled = autoSaveEnabled,
                reportEnabled = reportEnabled,
            )
        }
        ?: GenerationModal.Error(Localization.string("error_invalid").asUiText())

internal fun ImageToImageState.validated(
    dimensionValidator: DimensionValidator,
): ImageToImageState {
    val widthResult = dimensionValidator(width)
    val heightResult = dimensionValidator(height)
    val sourceError = when {
        !sourceSupportsImageToImage && mode == ServerSource.OPEN_AI ->
            Localization.string("error_img2img_openai_unsupported")
        !sourceSupportsImageToImage ->
            Localization.string("error_img2img_local_android_only")
        imageBase64.isBlank() ->
            Localization.string("error_img2img_select_input")
        else -> null
    }
    return copy(
        promptValidationError = null,
        widthValidationError = widthResult.errorText(),
        heightValidationError = heightResult.errorText(),
        error = sourceError,
    )
}

internal fun ValidationResult<DimensionValidator.Error>.errorText(): UiText? {
    if (isValid) return null
    return UiText.Static(
        when (val error = validationError) {
            DimensionValidator.Error.Empty -> "Required"
            is DimensionValidator.Error.LessThanMinimum -> "Minimum ${error.min}px"
            is DimensionValidator.Error.BiggerThanMaximum -> "Maximum ${error.max}px"
            DimensionValidator.Error.Unexpected,
            null,
            -> "Invalid value"
        },
    )
}

internal fun ImageToImageState.withSettings(
    settings: Settings,
    stableDiffusionSamplers: List<String>?,
): ImageToImageState =
    withSource(settings.source, stableDiffusionSamplers).copy(
        advancedToggleButtonVisible = !settings.formAdvancedOptionsAlwaysShow,
        advancedOptionsVisible = if (settings.formAdvancedOptionsAlwaysShow) {
            true
        } else {
            advancedOptionsVisible
        },
        formPromptTaggedInput = settings.formPromptTaggedInput,
    )

internal fun ImageToImageState.withSource(
    source: ServerSource,
    stableDiffusionSamplers: List<String>?,
): ImageToImageState {
    val samplers = when (source) {
        ServerSource.STABILITY_AI -> StabilityAiSampler.entries.map { "$it" }
        else -> stableDiffusionSamplers.orEmpty()
    }
    return copy(
        mode = source,
        availableSamplers = samplers,
        selectedSampler = selectedSampler.takeIf { sampler -> samplers.contains(sampler) }
            ?: samplers.firstOrNull()
            ?: selectedSampler,
    )
}

internal fun Throwable.localizedMessageText(): UiText =
    UiText.Static(message ?: Localization.string("error_invalid"))
