package com.shifthackz.aisdv1.presentation.screen.txt2img

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.generation.SaveGenerationResultUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.TextToImageUseCase
import com.shifthackz.aisdv1.domain.usecase.sdsampler.GetStableDiffusionSamplersUseCase
import com.shifthackz.aisdv1.presentation.core.GenerationMviViewModel
import io.reactivex.rxjava3.kotlin.subscribeBy

class TextToImageViewModel(
    getStableDiffusionSamplersUseCase: GetStableDiffusionSamplersUseCase,
    private val textToImageUseCase: TextToImageUseCase,
    private val saveGenerationResultUseCase: SaveGenerationResultUseCase,
    private val schedulersProvider: SchedulersProvider,
    private val dimensionValidator: DimensionValidator,
    private val preferenceManager: PreferenceManager,
) : GenerationMviViewModel<TextToImageState, EmptyEffect>(
    getStableDiffusionSamplersUseCase,
    schedulersProvider
) {

    override val emptyState = TextToImageState()

    override fun setState(state: TextToImageState) = super.setState(
        state.copy(
            widthValidationError = dimensionValidator(state.width).mapToUi(),
            heightValidationError = dimensionValidator(state.height).mapToUi(),
        )
    )

    fun dismissScreenDialog() = setActiveDialog(TextToImageState.Dialog.None)

    fun generate() = !currentState
        .mapToPayload()
        .let(textToImageUseCase::invoke)
        .doOnSubscribe { setActiveDialog(TextToImageState.Dialog.Communicating) }
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
            onSuccess = { ai ->
                setActiveDialog(
                    TextToImageState.Dialog.Image(ai, preferenceManager.autoSaveAiResults)
                )
            },
        )

    fun saveGeneratedResult(ai: AiGenerationResult) = !saveGenerationResultUseCase(ai)
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(Throwable::printStackTrace) { dismissScreenDialog() }

    private fun setActiveDialog(dialog: TextToImageState.Dialog) = currentState
        .copy(screenDialog = dialog)
        .let(::setState)
}
