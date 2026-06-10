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

/**
 * Exposes the `MIN_STEPS` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val MIN_STEPS = 1
/**
 * Exposes the `MAX_STEPS` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val MAX_STEPS = 150
/**
 * Exposes the `MIN_BATCH_COUNT` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val MIN_BATCH_COUNT = 1
/**
 * Exposes the `MAX_BATCH_COUNT` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val MAX_BATCH_COUNT = 20
/**
 * Exposes the `MIN_CFG_SCALE` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val MIN_CFG_SCALE = 1f
/**
 * Exposes the `MAX_CFG_SCALE` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val MAX_CFG_SCALE = 30f
/**
 * Exposes the `MIN_SUB_SEED_STRENGTH` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val MIN_SUB_SEED_STRENGTH = 0f
/**
 * Exposes the `MAX_SUB_SEED_STRENGTH` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val MAX_SUB_SEED_STRENGTH = 1f

/**
 * Carries `StableDiffusionSamplersKey` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal data class StableDiffusionSamplersKey(
    /**
     * Exposes the `serverUrl` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val serverUrl: String,
    /**
     * Exposes the `demoMode` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val demoMode: Boolean,
)

/**
 * Executes the `progressModal` step in the SDAI presentation layer.
 *
 * @param canCancelLocalGeneration can cancel local generation value consumed by the API.
 * @author Dmitriy Moroz
 */
internal fun TextToImageState.progressModal(
    canCancelLocalGeneration: Boolean,
): GenerationModal = if (localSourceSelected) {
    GenerationModal.Generating(canCancel = canCancelLocalGeneration)
} else {
    GenerationModal.Communicating()
}

/**
 * Converts SDAI data with `toTextToImageResultModal`.
 *
 * @param autoSaveEnabled auto save enabled value consumed by the API.
 * @param reportEnabled report enabled value consumed by the API.
 * @return Result produced by `toTextToImageResultModal`.
 * @author Dmitriy Moroz
 */
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

/**
 * Executes the `validated` step in the SDAI presentation layer.
 *
 * @param dimensionValidator dimension validator value consumed by the API.
 * @return Result produced by `validated`.
 * @author Dmitriy Moroz
 */
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

/**
 * Executes the `canGenerate` step in the SDAI presentation layer.
 *
 * @return Result produced by `canGenerate`.
 * @author Dmitriy Moroz
 */
internal fun TextToImageState.canGenerate(): Boolean =
    promptValidationError == null &&
        widthValidationError == null &&
        heightValidationError == null &&
        error == null

/**
 * Executes the `function` step in the SDAI presentation layer.
 *
 * @return Result produced by `function`.
 * @author Dmitriy Moroz
 */
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

/**
 * Executes the `withSettings` step in the SDAI presentation layer.
 *
 * @param settings settings value consumed by the API.
 * @param stableDiffusionSamplers stable diffusion samplers value consumed by the API.
 * @return Result produced by `withSettings`.
 * @author Dmitriy Moroz
 */
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

/**
 * Executes the `withSource` step in the SDAI presentation layer.
 *
 * @param source source value consumed by the API.
 * @param stableDiffusionSamplers stable diffusion samplers value consumed by the API.
 * @return Result produced by `withSource`.
 * @author Dmitriy Moroz
 */
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

/**
 * Executes the `localizedMessageText` step in the SDAI presentation layer.
 *
 * @return Result produced by `localizedMessageText`.
 * @author Dmitriy Moroz
 */
internal fun Throwable.localizedMessageText(): UiText =
    UiText.Static(message ?: Localization.string("error_invalid"))
