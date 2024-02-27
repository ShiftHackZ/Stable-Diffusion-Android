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
import com.shifthackz.aisdv1.presentation.model.ExtraType
import com.shifthackz.aisdv1.presentation.utils.ExtrasFormatter
import com.shifthackz.aisdv1.presentation.model.Modal
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
                    updateGenerationState {
                        it
                            .copyState(
                                mode = GenerationInputMode.fromSource(settings.source),
                                advancedToggleButtonVisible = !settings.formAdvancedOptionsAlwaysShow,
                            )
                            .let { state ->
                                if (!settings.formAdvancedOptionsAlwaysShow) state
                                else state.copyState(advancedOptionsVisible = true)
                            }
                    }
                }
            )

        !getStableDiffusionSamplersUseCase()
            .map { samplers -> samplers.map(StableDiffusionSampler::name) }
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = ::errorLog,
                onSuccess = { samplers ->
                    updateGenerationState {
                        it.copyState(
                            availableSamplers = samplers,
                            selectedSampler = samplers.firstOrNull() ?: "",
                        )
                    }
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

    open fun updateFormPreviousAiGeneration(ai: AiGenerationResult) = updateGenerationState {
        it
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
    }

    open fun onReceivedHordeStatus(status: HordeProcessStatus) {}

    open fun onReceivedLocalDiffusionStatus(status: LocalDiffusion.Status) {}

    fun processNewPrompts(positive: String, negative: String) = updateGenerationState {
        it.copyState(
            prompt = positive,
            negativePrompt = negative,
        )
    }

    fun toggleAdvancedOptions(value: Boolean) = updateGenerationState {
        it.copyState(advancedOptionsVisible = value)
    }

    fun updatePrompt(value: String) = updateGenerationState {
        it.copyState(prompt = value)
    }

    fun updateNegativePrompt(value: String) = updateGenerationState {
        it.copyState(negativePrompt = value)
    }

    fun updateWidth(value: String) = updateGenerationState {
        it.copyState(width = value)
    }

    fun updateHeight(value: String) = updateGenerationState {
        it.copyState(height = value)
    }

    fun updateSamplingSteps(value: Int) = updateGenerationState {
        it.copyState(samplingSteps = value)
    }

    fun updateCfgScale(value: Float) = updateGenerationState {
        it.copyState(cfgScale = value)
    }

    fun updateRestoreFaces(value: Boolean) = updateGenerationState {
        it.copyState(restoreFaces = value)
    }

    fun updateSeed(value: String) = updateGenerationState {
        it.copyState(seed = value)
    }

    fun updateSubSeed(value: String) = updateGenerationState {
        it.copyState(subSeed = value)
    }

    fun updateSubSeedStrength(value: Float) = updateGenerationState {
        it.copyState(subSeedStrength = value)
    }

    fun updateSampler(value: String) = updateGenerationState {
        it.copyState(selectedSampler = value)
    }

    fun updateNsfw(value: Boolean) = updateGenerationState {
        it.copyState(nsfw = value)
    }

    fun updateBatchCount(value: Int) = updateGenerationState {
        it.copyState(batchCount = value)
    }

    fun saveGeneratedResults(ai: List<AiGenerationResult>) = !Observable
        .fromIterable(ai)
        .flatMapCompletable(saveGenerationResultUseCase::invoke)
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(::errorLog) { dismissScreenModal() }

    fun viewGeneratedResult(ai: AiGenerationResult) = !saveLastResultToCacheUseCase(ai)
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(::errorLog) { emitEffect(GenerationMviEffect.LaunchGalleryDetail(it.id) as E) }

    fun dismissScreenModal() = setActiveModal(Modal.None)

    fun openPreviousGenerationInput() = setActiveModal(Modal.PromptBottomSheet)

    fun openLoraInput() = setActiveModal(Modal.ExtraBottomSheet(currentState.prompt, currentState.negativePrompt, ExtraType.Lora))

    fun openHyperNetInput() = setActiveModal(Modal.ExtraBottomSheet(currentState.prompt, currentState.negativePrompt, ExtraType.HyperNet))

    fun openEmbeddingInput() = setActiveModal(Modal.Embeddings(currentState.prompt, currentState.negativePrompt))

    protected fun setActiveModal(modal: Modal) = updateGenerationState {
        it.copyState(screenModal = modal)
    }

    private fun updateGenerationState(mutation: (GenerationMviState) -> GenerationMviState) = runCatching {
        updateState(mutation as (S) -> S)
    }
}
