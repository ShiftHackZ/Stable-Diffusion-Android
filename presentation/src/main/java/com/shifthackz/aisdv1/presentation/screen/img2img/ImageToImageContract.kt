package com.shifthackz.aisdv1.presentation.screen.img2img

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.imageprocessing.BitmapToBase64Converter
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.presentation.core.GenerationMviState
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputMode

data class ImageToImageState(
    val imageState: ImageState = ImageState.None,
    val imageBase64: String = "",
    val denoisingStrength: Float = 0.75f,
    override val screenModal: Modal = Modal.None,
    override val mode: GenerationInputMode = GenerationInputMode.AUTOMATIC1111,
    override val advancedToggleButtonVisible: Boolean = true,
    override val advancedOptionsVisible: Boolean = false,
    override val prompt: String = "",
    override val negativePrompt: String = "",
    override val width: String = 512.toString(),
    override val height: String = 512.toString(),
    override val samplingSteps: Int = 20,
    override val cfgScale: Float = 7f,
    override val restoreFaces: Boolean = false,
    override val seed: String = "",
    override val subSeed: String = "",
    override val subSeedStrength: Float = 0f,
    override val selectedSampler: String = "",
    override val availableSamplers: List<String> = emptyList(),
    override val widthValidationError: UiText? = null,
    override val heightValidationError: UiText? = null,
    override val nsfw: Boolean = false,
    override val batchCount: Int = 1,
    override val generateButtonEnabled: Boolean = true,
) : GenerationMviState() {

    sealed interface ImageState {

        val isEmpty: Boolean
            get() = this is None

        data object None : ImageState
        data class Image(val bitmap: Bitmap) : ImageState
    }

    override fun copyState(
        screenModal: Modal,
        mode: GenerationInputMode,
        advancedToggleButtonVisible: Boolean,
        advancedOptionsVisible: Boolean,
        prompt: String,
        negativePrompt: String,
        width: String,
        height: String,
        samplingSteps: Int,
        cfgScale: Float,
        restoreFaces: Boolean,
        seed: String,
        subSeed: String,
        subSeedStrength: Float,
        selectedSampler: String,
        availableSamplers: List<String>,
        widthValidationError: UiText?,
        heightValidationError: UiText?,
        nsfw: Boolean,
        batchCount: Int,
        generateButtonEnabled: Boolean
    ): GenerationMviState = copy(
        screenModal = screenModal,
        mode = mode,
        advancedToggleButtonVisible = advancedToggleButtonVisible,
        advancedOptionsVisible = advancedOptionsVisible,
        prompt = prompt,
        negativePrompt = negativePrompt,
        width = width,
        height = height,
        samplingSteps = samplingSteps,
        cfgScale = cfgScale,
        restoreFaces = restoreFaces,
        seed = seed,
        subSeed = subSeed,
        subSeedStrength = subSeedStrength,
        selectedSampler = selectedSampler,
        availableSamplers = availableSamplers,
        widthValidationError = widthValidationError,
        heightValidationError = heightValidationError,
        nsfw = nsfw,
        batchCount = batchCount,
        generateButtonEnabled = generateButtonEnabled,
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
        subSeed = subSeed.trim(),
        subSeedStrength = subSeedStrength,
        sampler = selectedSampler,
        nsfw = if (mode == GenerationInputMode.HORDE) nsfw else false,
        batchCount = batchCount,
    )
}
