package com.shifthackz.aisdv1.presentation.screen.txt2img

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.usecase.generation.TextToImageUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.GetStableDiffusionModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.SelectStableDiffusionModelUseCase
import io.reactivex.rxjava3.kotlin.subscribeBy

class TextToImageViewModel(
    getStableDiffusionModelsUseCase: GetStableDiffusionModelsUseCase,
    private val selectStableDiffusionModelUseCase: SelectStableDiffusionModelUseCase,
    private val textToImageUseCase: TextToImageUseCase,
    private val schedulersProvider: SchedulersProvider,
) : MviRxViewModel<TextToImageState, TextToImageEffect>() {

    override val emptyState = TextToImageState.Uninitialized

    init {
        getStableDiffusionModelsUseCase()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = { t ->
                    t.printStackTrace()
                },
                onSuccess = { data ->
                    setState(
                        TextToImageState.Content(
                            models = data.map { (model, _) -> model.title },
                            selectedModel = UiText.Static(
                                data.firstOrNull { it.second }?.first?.title ?: "",
                            ),
                        )
                    )
                },
            )
            .addToDisposable()
    }

    fun updatePrompt(value: String) = (currentState as? TextToImageState.Content)
        ?.copy(prompt = value)
        ?.let(::setState)

    fun updateNegativePrompt(value: String) = (currentState as? TextToImageState.Content)
        ?.copy(negativePrompt = value)
        ?.let(::setState)

    fun updateSamplingSteps(value: Int) = (currentState as? TextToImageState.Content)
        ?.copy(samplingSteps = value)
        ?.let(::setState)

    fun generate() = (currentState as? TextToImageState.Content)
        ?.let(TextToImageState.Content::mapToPayload)
        ?.let(textToImageUseCase::invoke)
        ?.subscribeOnMainThread(schedulersProvider)
        ?.subscribeBy(
            onError = {
                it.printStackTrace()
            },
            onSuccess = {
                println("VM : $it")
                //setState(TextToImageState.Image(it.image))
            }
        )
        ?.addToDisposable()

    fun selectStableDiffusionModel(value: String) = (currentState as? TextToImageState.Content)
        ?.copy(selectedModel = value.asUiText())
        ?.also(::setState)
        ?.let(TextToImageState.Content::selectedModel)
        ?.let(UiText::toString)
        ?.let(selectStableDiffusionModelUseCase::invoke)
        ?.subscribeOnMainThread(schedulersProvider)
        ?.subscribeBy(
            onError = {

            },
            onComplete = {

            },
        )
        ?.addToDisposable()
}
