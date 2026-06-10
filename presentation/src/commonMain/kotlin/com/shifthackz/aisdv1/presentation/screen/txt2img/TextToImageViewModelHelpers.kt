package com.shifthackz.aisdv1.presentation.screen.txt2img

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

internal data class StableDiffusionSamplersKey(
    val serverUrl: String,
    val demoMode: Boolean,
)

internal fun TextToImageState.progressModal(
    canCancelLocalGeneration: Boolean,
): GenerationModal = if (localSourceSelected) {
    GenerationModal.Generating(canCancel = canCancelLocalGeneration)
} else {
    GenerationModal.Communicating()
}

internal fun List<AiGenerationResult>.toTextToImageResultModal(
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

internal fun TextToImageState.validated(
    dimensionValidator: DimensionValidator,
): TextToImageState {
    val validateDimensions = mode != ServerSource.OPEN_AI
    val widthResult = dimensionValidator(width).takeIf { validateDimensions }
    val heightResult = dimensionValidator(height).takeIf { validateDimensions }
    return copy(
        promptValidationError = null,
        widthValidationError = widthResult?.errorMessage(),
        heightValidationError = heightResult?.errorMessage(),
        error = null,
    )
}

internal fun TextToImageState.canGenerate(): Boolean =
    promptValidationError == null &&
        widthValidationError == null &&
        heightValidationError == null &&
        error == null

internal fun ValidationResult<DimensionValidator.Error>.errorMessage(): UiText? {
    if (isValid) return null
    return UiText.Static(
        when (val error = validationError) {
            DimensionValidator.Error.Empty -> Localization.string("error_empty")
            is DimensionValidator.Error.LessThanMinimum -> Localization.string("error_min_size", error.min)
            is DimensionValidator.Error.BiggerThanMaximum -> Localization.string("error_max_size", error.max)
            DimensionValidator.Error.Unexpected,
            null,
            -> Localization.string("error_invalid")
        },
    )
}

internal fun TextToImageState.withSettings(
    settings: Settings,
    stableDiffusionSamplers: List<String>?,
): TextToImageState =
    withSource(settings.source, stableDiffusionSamplers).copy(
        advancedToggleButtonVisible = !settings.formAdvancedOptionsAlwaysShow,
        advancedOptionsVisible = if (settings.formAdvancedOptionsAlwaysShow) {
            true
        } else {
            advancedOptionsVisible
        },
        formPromptTaggedInput = settings.formPromptTaggedInput,
    )

internal fun TextToImageState.withSource(
    source: ServerSource,
    stableDiffusionSamplers: List<String>?,
): TextToImageState {
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
