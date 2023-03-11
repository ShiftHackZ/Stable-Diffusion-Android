package com.shifthackz.aisdv1.presentation.screen.img2img

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.imageprocessing.BitmapToBase64Converter
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.generation.ImageToImageUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.SaveGenerationResultUseCase
import com.shifthackz.aisdv1.domain.usecase.sdsampler.GetStableDiffusionSamplersUseCase
import com.shifthackz.aisdv1.presentation.core.GenerationMviViewModel
import com.shifthackz.aisdv1.presentation.screen.txt2img.mapToUi
import com.shz.imagepicker.imagepicker.model.PickedResult
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy

class ImageToImageViewModel(
    getStableDiffusionSamplersUseCase: GetStableDiffusionSamplersUseCase,
    private val imageToImageUseCase: ImageToImageUseCase,
    private val saveGenerationResultUseCase: SaveGenerationResultUseCase,
    private val bitmapToBase64Converter: BitmapToBase64Converter,
    private val dimensionValidator: DimensionValidator,
    private val preferenceManager: PreferenceManager,
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

    fun dismissScreenDialog() = setActiveDialog(ImageToImageState.Dialog.None)

    fun updateDenoisingStrength(value: Float) = currentState
        .copy(denoisingStrength = value)
        .let(::setState)

    fun updateInputImage(value: PickedResult) = when (value) {
        is PickedResult.Single -> currentState
            .copy(imageState = ImageToImageState.ImageState.Image(value.image.bitmap))
            .let(::setState)
        else -> Unit
    }

    fun clearInputImage() = currentState
        .copy(imageState = ImageToImageState.ImageState.None)
        .let(::setState)

    fun generate() = when (currentState.imageState) {
        is ImageToImageState.ImageState.Image -> !Single
            .just((currentState.imageState as ImageToImageState.ImageState.Image).bitmap)
            .doOnSubscribe { setActiveDialog(ImageToImageState.Dialog.Communicating) }
            .map(BitmapToBase64Converter::Input)
            .flatMap(bitmapToBase64Converter::invoke)
            .map(currentState::preProcessed)
            .map(ImageToImageState::mapToPayload)
            .flatMap(imageToImageUseCase::invoke)
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = {
                    setActiveDialog(
                        ImageToImageState.Dialog.Error(
                            UiText.Static(
                                it.localizedMessage ?: "Error"
                            )
                        )
                    )
                },
                onSuccess = { ai ->
                    setActiveDialog(
                        ImageToImageState.Dialog.Image(
                            ai,
                            preferenceManager.autoSaveAiResults
                        )
                    )
                }
            )
        else -> Unit
    }

    fun saveGeneratedResult(ai: AiGenerationResult) = !saveGenerationResultUseCase(ai)
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(Throwable::printStackTrace) { dismissScreenDialog() }

    private fun setActiveDialog(dialog: ImageToImageState.Dialog) = currentState
        .copy(screenDialog = dialog)
        .let(::setState)
}
