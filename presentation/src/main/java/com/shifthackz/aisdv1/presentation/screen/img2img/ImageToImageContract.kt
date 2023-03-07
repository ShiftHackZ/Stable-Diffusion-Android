package com.shifthackz.aisdv1.presentation.screen.img2img

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.ui.MviEffect
import com.shifthackz.aisdv1.presentation.core.GenerationMviState

sealed interface ImageToImageEffect : MviEffect

data class ImageToImageState(
    val imageState: ImageState = ImageState.None,
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
        object None : ImageState
        data class Image(val bitmap: Bitmap) : ImageState
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
}

enum class ImagePickButton {
    PHOTO, CAMERA
}
