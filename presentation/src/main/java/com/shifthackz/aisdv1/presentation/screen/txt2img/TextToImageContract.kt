package com.shifthackz.aisdv1.presentation.screen.txt2img

import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.MviState
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.core.validation.model.ValidationResult
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.presentation.R

data class TextToImageState(
    val screenDialog: Dialog = Dialog.None,
    val prompt: String = "",
    val negativePrompt: String = "",
    val width: String = 512.toString(),
    val height: String = 512.toString(),
    val samplingSteps: Int = 20,
    val cfgScale: Float = 7f,
    val restoreFaces: Boolean = false,
    val selectedSampler: String = "",
    val availableSamplers: List<String> = emptyList(),
    val widthValidationError: UiText? = null,
    val heightValidationError: UiText? = null,
) : MviState {

    val hasValidationErrors: Boolean
        get() = widthValidationError != null || heightValidationError != null

    sealed interface Dialog {
        object None : Dialog
        object Communicating : Dialog
        data class Image(val image: String) : Dialog
        data class Error(val error: UiText) : Dialog
    }
}

fun TextToImageState.mapToPayload(): TextToImagePayload = with(this) {
    TextToImagePayload(
        prompt = prompt,
        negativePrompt = negativePrompt,
        samplingSteps = samplingSteps,
        cfgScale = cfgScale,
        width = width.toIntOrNull() ?: 64,
        height = height.toIntOrNull() ?: 64,
        restoreFaces = restoreFaces,
        sampler = selectedSampler,
    )
}

fun ValidationResult<DimensionValidator.Error>.mapToUi(): UiText? {
    if (this.isValid) return null
    return when (validationError as DimensionValidator.Error) {
        DimensionValidator.Error.Empty -> R.string.error_empty.asUiText()
        is DimensionValidator.Error.LessThanMinimum -> UiText.Resource(
            R.string.error_min_size,
            (validationError as DimensionValidator.Error.LessThanMinimum).min,
        )
        is DimensionValidator.Error.BiggerThanMaximum -> UiText.Resource(
            R.string.error_max_size,
            (validationError as DimensionValidator.Error.BiggerThanMaximum).max,
        )
        else -> R.string.error_invalid.asUiText()
    }
}
