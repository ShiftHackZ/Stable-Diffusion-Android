@file:Suppress("UNCHECKED_CAST")

package com.shifthackz.aisdv1.presentation.core

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.common.extensions.EmptyLambda
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.ui.MviEffect
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.coin.ObserveCoinsUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveHordeProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.sdsampler.GetStableDiffusionSamplersUseCase
import io.reactivex.rxjava3.kotlin.subscribeBy

abstract class GenerationMviViewModel<S : GenerationMviState, E : MviEffect>(
    buildInfoProvider: BuildInfoProvider,
    preferenceManager: PreferenceManager,
    observeCoinsUseCase: ObserveCoinsUseCase,
    getStableDiffusionSamplersUseCase: GetStableDiffusionSamplersUseCase,
    observeHordeProcessStatusUseCase: ObserveHordeProcessStatusUseCase,
    schedulersProvider: SchedulersProvider,
) : MviRxViewModel<S, E>() {

    init {
        !preferenceManager.observe()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = ::errorLog,
                onComplete = EmptyLambda,
                onNext = { settings ->
                    currentState
                        .copyState(
                            advancedToggleButtonVisible = !settings.formAdvancedOptionsAlwaysShow
                        )
                        .let { state ->
                            if (!settings.formAdvancedOptionsAlwaysShow) state
                            else state.copyState(advancedOptionsVisible = true)
                        }
                        .let(::setGenerationState)
                }
            )

        !getStableDiffusionSamplersUseCase()
            .map { samplers -> samplers.map(StableDiffusionSampler::name) }
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = ::errorLog,
                onSuccess = { samplers ->
                    currentState
                        .copyState(
                            availableSamplers = samplers,
                            selectedSampler = samplers.firstOrNull() ?: "",
                        )
                        .let(::setGenerationState)
                }
            )

        !observeHordeProcessStatusUseCase()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = ::errorLog,
                onNext = ::onReceivedHordeStatus,
                onComplete = EmptyLambda,
            )

        if (buildInfoProvider.buildType == BuildType.GOOGLE_PLAY) {
            !observeCoinsUseCase()
                .subscribeOnMainThread(schedulersProvider)
                .map {result ->
                    when (result) {
                        is ObserveCoinsUseCase.Result.Coins -> {
                            currentState.copyState(generateButtonEnabled = result.value > 0)
                        }
                        else -> currentState
                    }
                }
                .subscribeBy(::errorLog, EmptyLambda, ::setGenerationState)
        }
    }

    open fun updateFormPreviousAiGeneration(ai: AiGenerationResult) = currentState
        .copyState(
            advancedOptionsVisible = true,
            prompt = ai.prompt,
            negativePrompt = ai.negativePrompt,
            width = "${ai.width}",
            height = "${ai.height}",
            seed = ai.seed,
            subSeed = ai.subSeed,
            subSeedStrength = ai.subSeedStrength,
            samplingSteps = ai.samplingSteps,
            cfgScale = ai.cfgScale,
            restoreFaces = ai.restoreFaces,
        )
        .let { state ->
            if (!state.availableSamplers.contains(ai.sampler)) state
            else state.copyState(selectedSampler = ai.sampler)
        }
        .let(::setGenerationState)

    open fun onReceivedHordeStatus(status: HordeProcessStatus) {}

    fun toggleAdvancedOptions(value: Boolean) = currentState
        .copyState(advancedOptionsVisible = value)
        .let(::setGenerationState)

    fun updatePrompt(value: String) = currentState
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

    fun updateSubSeed(value: String) = currentState
        .copyState(subSeed = value)
        .let(::setGenerationState)

    fun updateSubSeedStrength(value: Float) = currentState
        .copyState(subSeedStrength = value)
        .let(::setGenerationState)

    fun updateSampler(value: String) = currentState
        .copyState(selectedSampler = value)
        .let(::setGenerationState)

    private fun setGenerationState(state: GenerationMviState) = runCatching {
        setState(state as? S ?: currentState)
    }
}
