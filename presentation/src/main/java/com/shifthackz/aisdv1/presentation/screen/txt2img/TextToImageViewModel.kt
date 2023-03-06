package com.shifthackz.aisdv1.presentation.screen.txt2img

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import com.shifthackz.aisdv1.domain.usecase.generation.TextToImageUseCase
import com.shifthackz.aisdv1.domain.usecase.sdsampler.GetStableDiffusionSamplersUseCase
import io.reactivex.rxjava3.kotlin.subscribeBy

class TextToImageViewModel(
    getStableDiffusionSamplersUseCase: GetStableDiffusionSamplersUseCase,
    private val textToImageUseCase: TextToImageUseCase,
    private val schedulersProvider: SchedulersProvider,
    private val dimensionValidator: DimensionValidator,
) : MviRxViewModel<TextToImageState, EmptyEffect>() {

    override val emptyState = TextToImageState()

    init {
        !getStableDiffusionSamplersUseCase()
            .map { samplers -> samplers.map(StableDiffusionSampler::name) }
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = {
                    it.printStackTrace()
                },
                onSuccess = { samplers ->
                    println("DBG0: samplers - $samplers")
                    currentState
                        .copy(availableSamplers = samplers, selectedSampler = samplers.first())
                        .let(::setState)
                }
            )
    }

    override fun setState(state: TextToImageState) = super.setState(
        state.copy(
            widthValidationError = dimensionValidator(state.width).mapToUi(),
            heightValidationError = dimensionValidator(state.height).mapToUi(),
        )
    )

    fun updatePrompt(value: String) = currentState
        .copy(prompt = value)
        .let(::setState)

    fun updateNegativePrompt(value: String) = currentState
        .copy(negativePrompt = value)
        .let(::setState)

    fun updateWidth(value: String) = currentState
        .copy(width = value)
        .let(::setState)

    fun updateHeight(value: String) = currentState
        .copy(height = value)
        .let(::setState)

    fun updateSamplingSteps(value: Int) = currentState
        .copy(samplingSteps = value)
        .let(::setState)

    fun updateCfgScale(value: Float) = currentState
        .copy(cfgScale = value)
        .let(::setState)

    fun updateRestoreFaces(value: Boolean) = currentState
        .copy(restoreFaces = value)
        .let(::setState)

    fun updateSampler(value: String) = currentState
        .copy(selectedSampler = value)
        .let(::setState)

    fun dismissScreenDialog() = setActiveDialog(TextToImageState.Dialog.None)

    fun generate() = !currentState
        .mapToPayload()
        .let(textToImageUseCase::invoke)
        .doOnSubscribe {
            setActiveDialog(TextToImageState.Dialog.Communicating)
        }
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(
            onError = {
                it.printStackTrace()
                setActiveDialog(
                    TextToImageState.Dialog.Error(
                        (it.localizedMessage ?: "Something went wrong").asUiText()
                    )
                )
            },
            onSuccess = {
                setActiveDialog(TextToImageState.Dialog.Image(it.image))
            }
        )

    private fun setActiveDialog(dialog: TextToImageState.Dialog) = currentState
        .copy(screenDialog = dialog)
        .let(::setState)
}
