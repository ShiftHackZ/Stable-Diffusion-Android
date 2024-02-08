@file:Suppress("UNCHECKED_CAST")

package com.shifthackz.aisdv1.presentation.core

import com.shifthackz.aisdv1.core.common.extensions.EmptyLambda
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import com.shifthackz.aisdv1.domain.feature.diffusion.LocalDiffusion
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.caching.SaveLastResultToCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveHordeProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveLocalDiffusionProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.SaveGenerationResultUseCase
import com.shifthackz.aisdv1.domain.usecase.sdsampler.GetStableDiffusionSamplersUseCase
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputMode
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy

abstract class GenerationMviViewModel<S : GenerationMviState, E : GenerationMviEffect>(
    private val schedulersProvider: SchedulersProvider,
    private val saveLastResultToCacheUseCase: SaveLastResultToCacheUseCase,
    private val saveGenerationResultUseCase: SaveGenerationResultUseCase,
    preferenceManager: PreferenceManager,
    getStableDiffusionSamplersUseCase: GetStableDiffusionSamplersUseCase,
    observeHordeProcessStatusUseCase: ObserveHordeProcessStatusUseCase,
    observeLocalDiffusionProcessStatusUseCase: ObserveLocalDiffusionProcessStatusUseCase? = null,
) : MviRxViewModel<S, E>() {

    init {
        !preferenceManager
            .observe()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = ::errorLog,
                onComplete = EmptyLambda,
                onNext = { settings ->
                    currentState
                        .copyState(
                            mode = GenerationInputMode.fromSource(settings.source),
                            advancedToggleButtonVisible = !settings.formAdvancedOptionsAlwaysShow,
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

        observeLocalDiffusionProcessStatusUseCase
            ?.let { lambda -> lambda()}
            ?.subscribeOnMainThread(schedulersProvider)
            ?.subscribeBy(
                onError = ::errorLog,
                onNext = ::onReceivedLocalDiffusionStatus,
                onComplete = EmptyLambda,
            )
            ?.apply { addToDisposable() }
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

    open fun onReceivedLocalDiffusionStatus(status: LocalDiffusion.Status) {}

    open fun dismissScreenModal() {}

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

    fun updateNsfw(value: Boolean) = currentState
        .copyState(nsfw = value)
        .let(::setGenerationState)

    fun updateBatchCount(value: Int) = currentState
        .copyState(batchCount = value)
        .let(::setGenerationState)

    fun saveGeneratedResults(ai: List<AiGenerationResult>) = !Observable
        .fromIterable(ai)
        .flatMapCompletable(saveGenerationResultUseCase::invoke)
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(::errorLog) { dismissScreenModal() }

    fun viewGeneratedResult(ai: AiGenerationResult) = !saveLastResultToCacheUseCase(ai)
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(::errorLog) { emitEffect(GenerationMviEffect.LaunchGalleryDetail(it.id) as E) }

    private fun setGenerationState(state: GenerationMviState) = runCatching {
        setState(state as? S ?: currentState)
    }
}
