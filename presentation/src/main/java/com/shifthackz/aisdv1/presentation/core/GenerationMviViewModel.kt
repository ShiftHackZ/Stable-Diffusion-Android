@file:Suppress("UNCHECKED_CAST")

package com.shifthackz.aisdv1.presentation.core

import com.shifthackz.aisdv1.core.common.extensions.EmptyLambda
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.entity.OpenAiSize
import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import com.shifthackz.aisdv1.domain.feature.diffusion.LocalDiffusion
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.caching.SaveLastResultToCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.InterruptGenerationUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveHordeProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveLocalDiffusionProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.SaveGenerationResultUseCase
import com.shifthackz.aisdv1.domain.usecase.sdsampler.GetStableDiffusionSamplersUseCase
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.navigation.Router
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupLaunchSource
import com.shifthackz.aisdv1.presentation.screen.txt2img.mapToUi
import com.shifthackz.android.core.mvi.EmptyEffect
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class GenerationMviViewModel<S : GenerationMviState, I : GenerationMviIntent> :
    MviRxViewModel<S, I, EmptyEffect>(), KoinComponent {

    private val preferenceManager: PreferenceManager by inject()
    private val schedulersProvider: SchedulersProvider by inject()
    private val saveLastResultToCacheUseCase: SaveLastResultToCacheUseCase by inject()
    private val saveGenerationResultUseCase: SaveGenerationResultUseCase by inject()
    private val getStableDiffusionSamplersUseCase: GetStableDiffusionSamplersUseCase by inject()
    private val observeHordeProcessStatusUseCase: ObserveHordeProcessStatusUseCase by inject()
    private val observeLocalDiffusionProcessStatusUseCase: ObserveLocalDiffusionProcessStatusUseCase by inject()
    private val interruptGenerationUseCase: InterruptGenerationUseCase by inject()

    private val router: Router by inject()
    private val dimensionValidator: DimensionValidator by inject()

    private var generationDisposable: Disposable? = null
    private var randomImageDisposable: Disposable? = null

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
                                mode = settings.source,
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

        !observeLocalDiffusionProcessStatusUseCase()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = ::errorLog,
                onNext = ::onReceivedLocalDiffusionStatus,
                onComplete = EmptyLambda,
            )
    }

    abstract fun generate(): Disposable

    open fun onReceivedHordeStatus(status: HordeProcessStatus) {}

    open fun onReceivedLocalDiffusionStatus(status: LocalDiffusion.Status) {}

    override fun processIntent(intent: I) {
        when (intent) {
            is GenerationMviIntent.NewPrompts -> updateGenerationState {
                it.copyState(
                    prompt = intent.positive,
                    negativePrompt = intent.negative,
                )
            }

            is GenerationMviIntent.SetAdvancedOptionsVisibility -> updateGenerationState {
                it.copyState(advancedOptionsVisible = intent.visible)
            }

            is GenerationMviIntent.Update.Prompt -> updateGenerationState {
                it.copyState(prompt = intent.value)
            }

            is GenerationMviIntent.Update.NegativePrompt -> updateGenerationState {
                it.copyState(negativePrompt = intent.value)
            }

            is GenerationMviIntent.Update.Size.Width -> updateGenerationState {
                it.copyState(
                    width = intent.value,
                    widthValidationError = dimensionValidator(intent.value).mapToUi(),
                )
            }

            is GenerationMviIntent.Update.Size.Height -> updateGenerationState {
                it.copyState(
                    height = intent.value,
                    heightValidationError = dimensionValidator(intent.value).mapToUi(),
                )
            }

            is GenerationMviIntent.Update.SamplingSteps -> updateGenerationState {
                it.copyState(samplingSteps = intent.value)
            }

            is GenerationMviIntent.Update.CfgScale -> updateGenerationState {
                it.copyState(cfgScale = intent.value)
            }

            is GenerationMviIntent.Update.RestoreFaces -> updateGenerationState {
                it.copyState(restoreFaces = intent.value)
            }

            is GenerationMviIntent.Update.Seed -> updateGenerationState {
                it.copyState(seed = intent.value)
            }

            is GenerationMviIntent.Update.SubSeed -> updateGenerationState {
                it.copyState(subSeed = intent.value)
            }

            is GenerationMviIntent.Update.SubSeedStrength -> updateGenerationState {
                it.copyState(subSeedStrength = intent.value)
            }

            is GenerationMviIntent.Update.Sampler -> updateGenerationState {
                it.copyState(selectedSampler = intent.value)
            }

            is GenerationMviIntent.Update.Nsfw -> updateGenerationState {
                it.copyState(nsfw = intent.value)
            }

            is GenerationMviIntent.Update.Batch -> updateGenerationState {
                it.copyState(batchCount = intent.value)
            }

            is GenerationMviIntent.Update.OpenAi.Model -> updateGenerationState { state ->
                val size = if (state.openAiSize.supportedModels.contains(intent.value)) {
                    state.openAiSize
                } else {
                    OpenAiSize.entries.first { it.supportedModels.contains(intent.value) }
                }
                state.copyState(openAiModel = intent.value, openAiSize = size)
            }

            is GenerationMviIntent.Update.OpenAi.Size -> updateGenerationState {
                it.copyState(openAiSize = intent.value)
            }

            is GenerationMviIntent.Update.OpenAi.Quality -> updateGenerationState {
                it.copyState(openAiQuality = intent.value)
            }

            is GenerationMviIntent.Update.OpenAi.Style -> updateGenerationState {
                it.copyState(openAiStyle = intent.value)
            }

            is GenerationMviIntent.Result.Save -> !Observable
                .fromIterable(intent.ai)
                .flatMapCompletable(saveGenerationResultUseCase::invoke)
                .subscribeOnMainThread(schedulersProvider)
                .subscribeBy(::errorLog) { setActiveModal(Modal.None) }

            is GenerationMviIntent.Result.View -> !saveLastResultToCacheUseCase(intent.ai)
                .subscribeOnMainThread(schedulersProvider)
                .subscribeBy(::errorLog) { router.navigateToGalleryDetails(it.id) }

            is GenerationMviIntent.SetModal -> setActiveModal(intent.modal)

            GenerationMviIntent.Cancel.Generation -> {
                generationDisposable?.dispose()
                generationDisposable = null
                !interruptGenerationUseCase()
                    .doOnSubscribe { setActiveModal(Modal.None) }
                    .subscribeOnMainThread(schedulersProvider)
                    .subscribeBy(::errorLog)
            }

            GenerationMviIntent.Cancel.FetchRandomImage -> {
                randomImageDisposable?.dispose()
                randomImageDisposable = null
                setActiveModal(Modal.None)
            }

            GenerationMviIntent.Generate -> generate { generate() }

            GenerationMviIntent.Configuration -> router.navigateToServerSetup(
                ServerSetupLaunchSource.SETTINGS
            )

            is GenerationMviIntent.UpdateFromGeneration -> updateFormPreviousAiGeneration(
                intent.ai
            )
        }
    }

    protected open fun updateFormPreviousAiGeneration(ai: AiGenerationResult) =
        updateGenerationState {
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

    protected fun setActiveModal(modal: Modal) = updateGenerationState {
        it.copyState(screenModal = modal)
    }

    protected fun fetchRandomImage(fn: () -> Disposable) {
        randomImageDisposable?.dispose()
        randomImageDisposable = null
        val newDisposable = fn()
        randomImageDisposable = newDisposable
        randomImageDisposable?.addToDisposable()
    }

    private fun generate(fn: () -> Disposable) {
        generationDisposable?.dispose()
        generationDisposable = null
        val newDisposable = fn()
        generationDisposable = newDisposable
        generationDisposable?.addToDisposable()
    }


    private fun updateGenerationState(mutation: (GenerationMviState) -> GenerationMviState) =
        runCatching {
            updateState(mutation as (S) -> S)
        }
}
