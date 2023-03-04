package com.shifthackz.aisdv1.presentation.screen.txt2img

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.interactor.StableDiffusionModelSelectionInteractor
import com.shifthackz.aisdv1.domain.usecase.TextToImageUseCase
import com.shifthackz.aisdv1.presentation.screen.txt2img.contract.TextToImageEffect
import com.shifthackz.aisdv1.presentation.screen.txt2img.contract.TextToImageState
import com.shifthackz.aisdv1.presentation.screen.txt2img.model.StableDiffusionModelUi
import com.shifthackz.aisdv1.presentation.screen.txt2img.model.TextToImagePayloadUi
import io.reactivex.rxjava3.kotlin.subscribeBy

class TextToImageViewModel(
    private val textToImageUseCase: TextToImageUseCase,
    private val sdModelSelectionInteractor: StableDiffusionModelSelectionInteractor,
    private val schedulersProvider: SchedulersProvider,
) : MviRxViewModel<TextToImageState, TextToImageEffect>() {

    override val emptyState = TextToImageState.Idle

    init {
        sdModelSelectionInteractor
            .getData()
            .subscribeOnMainThread(schedulersProvider)
            .map { data ->
                data.map { (model, selection) ->
                    StableDiffusionModelUi(model.title, selection)
                }
            }
            .subscribeBy(
                onError = { t ->
                    t.printStackTrace()
                },
                onSuccess = { sdModelsUi ->
                    setState(TextToImageState.Content(sdModelsUi))
                },
            )
            .addToDisposable()
    }

    fun generate(payload: TextToImagePayloadUi) = textToImageUseCase
        .generate(payload.mapToDomain())
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(
            onError = {
                it.printStackTrace()
            },
            onSuccess = {
                println("VM : $it")
                setState(TextToImageState.Image(it.image))
            }
        )
        .addToDisposable()

    fun selectStableDiffusionModel(sdModel: StableDiffusionModelUi) = sdModelSelectionInteractor
        .selectModelByName(sdModel.title)
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(
            onError = {

            },
            onComplete = {

            },
        )
        .addToDisposable()
}
