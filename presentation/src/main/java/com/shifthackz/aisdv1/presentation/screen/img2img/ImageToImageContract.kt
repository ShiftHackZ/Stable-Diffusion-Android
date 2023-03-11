package com.shifthackz.aisdv1.presentation.screen.img2img

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.imageprocessing.BitmapToBase64Converter
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.ui.MviEffect
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.presentation.core.GenerationMviState

sealed interface ImageToImageEffect : MviEffect

data class ImageToImageState(
    val imageState: ImageState = ImageState.None,
    val imageBase64: String = "",
    val screenDialog: Dialog = Dialog.None,
    val denoisingStrength: Float = 0.75f,
    override val prompt: String = "",
    override val negativePrompt: String = "",
    override val width: String = 512.toString(),
    override val height: String = 512.toString(),
    override val samplingSteps: Int = 20,
    override val cfgScale: Float = 7f,
    override val restoreFaces: Boolean = false,
    override val seed: String = "",
    override val selectedSampler: String = "",
    override val availableSamplers: List<String> = emptyList(),
    override val widthValidationError: UiText? = null,
    override val heightValidationError: UiText? = null,
) : GenerationMviState() {

    sealed interface ImageState {

        val isEmpty: Boolean
            get() = this is None

        object None : ImageState
        data class Image(val bitmap: Bitmap) : ImageState
    }

    sealed interface Dialog {
        object None : Dialog
        object Communicating : Dialog
        data class Image(val result: AiGenerationResult, val autoSaveEnabled: Boolean) : Dialog
        data class Error(val error: UiText) : Dialog
    }

    override fun copyState(
        prompt: String,
        negativePrompt: String,
        width: String,
        height: String,
        samplingSteps: Int,
        cfgScale: Float,
        restoreFaces: Boolean,
        seed: String,
        selectedSampler: String,
        availableSamplers: List<String>,
        widthValidationError: UiText?,
        heightValidationError: UiText?
    ): GenerationMviState = copy(
        prompt = prompt,
        negativePrompt = negativePrompt,
        width = width,
        height = height,
        samplingSteps = samplingSteps,
        cfgScale = cfgScale,
        restoreFaces = restoreFaces,
        seed = seed,
        selectedSampler = selectedSampler,
        availableSamplers = availableSamplers,
        widthValidationError = widthValidationError,
        heightValidationError = heightValidationError,
    )

    fun preProcessed(output: BitmapToBase64Converter.Output): ImageToImageState =
        copy(imageBase64 = output.base64ImageString)
}

enum class ImagePickButton {
    PHOTO, CAMERA
}

fun ImageToImageState.mapToPayload(): ImageToImagePayload = with(this) {
    ImageToImagePayload(
        base64Image = imageBase64,
        denoisingStrength = denoisingStrength,
        prompt = prompt.trim(),
        negativePrompt = negativePrompt.trim(),
        samplingSteps = samplingSteps,
        cfgScale = cfgScale,
        width = width.toIntOrNull() ?: 64,
        height = height.toIntOrNull() ?: 64,
        restoreFaces = restoreFaces,
        seed = seed.trim(),
        sampler = selectedSampler,
    )
}
