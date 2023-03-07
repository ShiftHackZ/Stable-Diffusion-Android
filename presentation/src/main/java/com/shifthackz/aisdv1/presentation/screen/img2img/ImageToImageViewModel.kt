package com.shifthackz.aisdv1.presentation.screen.img2img

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.domain.usecase.sdsampler.GetStableDiffusionSamplersUseCase
import com.shifthackz.aisdv1.presentation.core.GenerationMviViewModel
import com.shifthackz.aisdv1.presentation.screen.txt2img.mapToUi
import com.shz.imagepicker.imagepicker.model.PickedResult

class ImageToImageViewModel(
    getStableDiffusionSamplersUseCase: GetStableDiffusionSamplersUseCase,
    private val dimensionValidator: DimensionValidator,
    private val schedulersProvider: SchedulersProvider,
) : GenerationMviViewModel<ImageToImageState, ImageToImageEffect>(
    getStableDiffusionSamplersUseCase,
    schedulersProvider,
) {

    override val emptyState = ImageToImageState()

    override fun setState(state: ImageToImageState) = super.setState(
        state.copy(
            widthValidationError = dimensionValidator(state.width).mapToUi(),
            heightValidationError = dimensionValidator(state.height).mapToUi(),
        )
    )

    fun updateInputImage(value: PickedResult) = when (value) {
        is PickedResult.Single -> currentState
            .copy(imageState = ImageToImageState.ImageState.Image(value.image.bitmap))
            .let(::setState)
        else -> Unit
    }

    fun clearInputImage() = currentState
        .copy(imageState = ImageToImageState.ImageState.None)
        .let(::setState)
}
