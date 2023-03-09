@file:Suppress("UNCHECKED_CAST")

package com.shifthackz.aisdv1.presentation.core

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.ui.MviEffect
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import com.shifthackz.aisdv1.domain.usecase.sdsampler.GetStableDiffusionSamplersUseCase
import io.reactivex.rxjava3.kotlin.subscribeBy

abstract class GenerationMviViewModel<S : GenerationMviState, E : MviEffect>(
    getStableDiffusionSamplersUseCase: GetStableDiffusionSamplersUseCase,
    schedulersProvider: SchedulersProvider,
) : MviRxViewModel<S, E>() {

    init {
        !getStableDiffusionSamplersUseCase()
            .map { samplers -> samplers.map(StableDiffusionSampler::name) }
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = { t -> t.printStackTrace() },
                onSuccess = { samplers ->
                    currentState
                        .copyState(
                            availableSamplers = samplers,
                            selectedSampler = samplers.firstOrNull() ?: "",
                        )
                        .let(::setGenerationState)
                }
            )
    }

    fun updatePrompt(value: String) = (currentState)
        .copyState(prompt = value)
        .let(::setGenerationState)

    fun updateNegativePrompt(value: String) = currentState
        .copyState(negativePrompt = value)
        .let(::setGenerationState)

    fun updateWidth(value: String) = currentState
        .copyState(width = value)
        .let(::setGenerationState)

    fun updateHeight(value: String) = currentState
        .copyState(height = value)
        .let(::setGenerationState)

    fun updateSamplingSteps(value: Int) = currentState
        .copyState(samplingSteps = value)
        .let(::setGenerationState)

    fun updateCfgScale(value: Float) = currentState
        .copyState(cfgScale = value)
        .let(::setGenerationState)

    fun updateRestoreFaces(value: Boolean) = currentState
        .copyState(restoreFaces = value)
        .let(::setGenerationState)

    fun updateSeed(value: String) = currentState
        .copyState(seed = value)
        .let(::setGenerationState)

    fun updateSampler(value: String) = currentState
        .copyState(selectedSampler = value)
        .let(::setGenerationState)

    private fun setGenerationState(state: GenerationMviState) = runCatching {
        setState(state as? S ?: currentState)
    }
}
