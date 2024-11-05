@file:Suppress("UNCHECKED_CAST")

package com.shifthackz.aisdv1.presentation.core

import com.shifthackz.aisdv1.core.common.extensions.EmptyLambda
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus
import com.shifthackz.aisdv1.domain.entity.OpenAiSize
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.StabilityAiSampler
import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import com.shifthackz.aisdv1.domain.feature.diffusion.LocalDiffusion
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.caching.SaveLastResultToCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.InterruptGenerationUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveHordeProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveLocalDiffusionProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.SaveGenerationResultUseCase
import com.shifthackz.aisdv1.domain.usecase.sdsampler.GetStableDiffusionSamplersUseCase
import com.shifthackz.aisdv1.presentation.model.LaunchSource
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.navigation.router.drawer.DrawerRouter
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.aisdv1.presentation.screen.drawer.DrawerIntent
import com.shifthackz.aisdv1.presentation.screen.txt2img.mapToUi
import com.shifthackz.android.core.mvi.MviEffect
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.util.concurrent.TimeUnit

abstract class GenerationMviViewModel<S : GenerationMviState, I : GenerationMviIntent, E : MviEffect>(
    private val preferenceManager: PreferenceManager,
    getStableDiffusionSamplersUseCase: GetStableDiffusionSamplersUseCase,
    observeHordeProcessStatusUseCase: ObserveHordeProcessStatusUseCase,
    observeLocalDiffusionProcessStatusUseCase: ObserveLocalDiffusionProcessStatusUseCase,
    private val saveLastResultToCacheUseCase: SaveLastResultToCacheUseCase,
    private val saveGenerationResultUseCase: SaveGenerationResultUseCase,
    private val interruptGenerationUseCase: InterruptGenerationUseCase,
    private val mainRouter: MainRouter,
    private val drawerRouter: DrawerRouter,
    private val dimensionValidator: DimensionValidator,
    private val schedulersProvider: SchedulersProvider,
    private val backgroundWorkObserver: BackgroundWorkObserver,
) : MviRxViewModel<S, I, E>() {

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
                                formPromptTaggedInput = settings.formPromptTaggedInput,
                            )
                            .let { state ->
                                if (!settings.formAdvancedOptionsAlwaysShow) state
                                else state.copyState(advancedOptionsVisible = true)
                            }
                    }
                }
            )

        !getStableDiffusionSamplersUseCase()
            .delay(500L, TimeUnit.MILLISECONDS)
            .map { samplers -> samplers.map(StableDiffusionSampler::name) }
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = ::errorLog,
                onSuccess = { samplers ->
                    updateGenerationState { state ->
                        val allSamplers = when (state.mode) {
                            ServerSource.STABILITY_AI -> StabilityAiSampler.entries.map { "$it" }
                            else -> samplers
                        }
                        state.copyState(
                            availableSamplers = allSamplers,
                            selectedSampler = allSamplers.firstOrNull() ?: "",
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

    abstract fun generateDisposable(): Disposable

    abstract fun generateBackground()

    open fun onReceivedHordeStatus(status: HordeProcessStatus) {}

    open fun onReceivedLocalDiffusionStatus(status: LocalDiffusionStatus) {}

    override fun onCleared() {
        super.onCleared()
        generationDisposable?.dispose()
        generationDisposable = null
        randomImageDisposable?.dispose()
        randomImageDisposable = null
    }

    override fun processIntent(intent: I) {
        when (intent) {
            is GenerationMviIntent.NewPrompts -> updateGenerationState {
                it.copyState(
                    prompt = intent.positive.trim(),
                    negativePrompt = intent.negative.trim(),
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
                .subscribeBy(::errorLog) { mainRouter.navigateToGalleryDetails(it.id) }

            is GenerationMviIntent.Result.Report -> mainRouter.navigateToReportImage(intent.ai.id)

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

            GenerationMviIntent.Generate -> {
                if (backgroundWorkObserver.hasActiveTasks()) {
                    setActiveModal(Modal.Background.Running)
                } else {
                    if (preferenceManager.backgroundGeneration) {
                        generateBackground()
                        backgroundWorkObserver.refreshStatus()
                        setActiveModal(Modal.Background.Scheduled)
                    } else {
                        generateOnUi { generateDisposable() }
                    }
                }
            }

            GenerationMviIntent.Configuration -> mainRouter.navigateToServerSetup(
                LaunchSource.SETTINGS,
            )

            is GenerationMviIntent.UpdateFromGeneration -> {
                updateFormPreviousAiGeneration(intent.payload)
            }

            is GenerationMviIntent.Drawer -> when (intent.intent) {
                DrawerIntent.Close -> drawerRouter.closeDrawer()
                DrawerIntent.Open -> drawerRouter.openDrawer()
            }
        }
    }

    protected open fun updateFormPreviousAiGeneration(payload: GenerationFormUpdateEvent.Payload) {
        val ai = when (payload) {
            is GenerationFormUpdateEvent.Payload.I2IForm -> payload.ai
            is GenerationFormUpdateEvent.Payload.T2IForm -> payload.ai
            else -> return
        }
        updateGenerationState { oldState ->
            oldState
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

    private fun generateOnUi(fn: () -> Disposable) {
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
