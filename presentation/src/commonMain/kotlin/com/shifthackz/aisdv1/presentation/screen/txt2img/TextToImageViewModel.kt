package com.shifthackz.aisdv1.presentation.screen.txt2img

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.core.mvi.EmptyEffect
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.Settings
import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import com.shifthackz.aisdv1.domain.feature.work.BackgroundTaskManager
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.caching.SaveLastResultToCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.SaveGenerationResultUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.TextToImageUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.InterruptGenerationUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveHordeProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveLocalDiffusionProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.sdsampler.GetStableDiffusionSamplersUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.GetConfigurationUseCase
import com.shifthackz.aisdv1.presentation.core.GenerationFormUpdateEvent
import com.shifthackz.aisdv1.presentation.core.GenerationPlatformServices
import com.shifthackz.aisdv1.presentation.model.GenerationModal
import com.shifthackz.aisdv1.presentation.navigation.router.TextToImageRouter
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.withContext

class TextToImageViewModel(
    private val dispatchersProvider: DispatchersProvider,
    private val getConfigurationUseCase: GetConfigurationUseCase,
    private val getStableDiffusionSamplersUseCase: GetStableDiffusionSamplersUseCase,
    private val textToImageUseCase: TextToImageUseCase,
    private val saveGenerationResultUseCase: SaveGenerationResultUseCase,
    private val saveLastResultToCacheUseCase: SaveLastResultToCacheUseCase,
    private val interruptGenerationUseCase: InterruptGenerationUseCase,
    private val observeHordeProcessStatusUseCase: ObserveHordeProcessStatusUseCase,
    private val observeLocalDiffusionProcessStatusUseCase: ObserveLocalDiffusionProcessStatusUseCase,
    private val preferenceManager: PreferenceManager,
    private val backgroundTaskManager: BackgroundTaskManager,
    private val backgroundWorkObserver: BackgroundWorkObserver,
    private val platformServices: GenerationPlatformServices,
    private val buildInfoProvider: BuildInfoProvider,
    private val generationFormUpdateEvent: GenerationFormUpdateEvent,
    private val dimensionValidator: DimensionValidator,
    private val imageSaver: ImageSaver,
    private val imageSharer: ImageSharer,
    private val router: TextToImageRouter,
    private val onError: (Throwable) -> Unit = {},
) : BaseMviViewModel<TextToImageState, TextToImageIntent, EmptyEffect>(
    initialState = TextToImageState(),
    effectDispatcher = dispatchersProvider.immediate,
) {

    init {
        loadConfiguration()
        observeSettings()
        consumePendingGenerationFormUpdate()
        observeGenerationFormUpdate()
        observeGenerationProgress()
    }

    private var stableDiffusionSamplers: List<String>? = null
    private var stableDiffusionSamplersKey: StableDiffusionSamplersKey? = null

    private val actionHandler = TextToImageActionHandler(
        dispatchersProvider = dispatchersProvider,
        textToImageUseCase = textToImageUseCase,
        saveGenerationResultUseCase = saveGenerationResultUseCase,
        saveLastResultToCacheUseCase = saveLastResultToCacheUseCase,
        interruptGenerationUseCase = interruptGenerationUseCase,
        preferenceManager = preferenceManager,
        backgroundTaskManager = backgroundTaskManager,
        backgroundWorkObserver = backgroundWorkObserver,
        platformServices = platformServices,
        buildInfoProvider = buildInfoProvider,
        dimensionValidator = dimensionValidator,
        imageSaver = imageSaver,
        imageSharer = imageSharer,
        router = router,
        currentState = { currentState },
        emitState = ::emitState,
        updateState = { reducer -> updateState(reducer) },
        launch = { dispatcher, start, block -> launch(dispatcher, start, block) },
        onError = onError,
    )

    private val intentProcessor = TextToImageIntentProcessor(
        router = router,
        updateState = { reducer -> updateState(reducer) },
        generate = actionHandler::generate,
        cancelGeneration = actionHandler::cancelGeneration,
        saveImage = actionHandler::saveImage,
        shareImage = actionHandler::shareImage,
        saveGenerationResults = actionHandler::saveGenerationResults,
        viewGenerationResult = actionHandler::viewGenerationResult,
        reportGenerationResult = actionHandler::reportGenerationResult,
        applyGenerationResult = ::applyGenerationResult,
    )

    override fun processIntent(intent: TextToImageIntent) = intentProcessor.process(intent)

    private fun applyGenerationResult(ai: com.shifthackz.aisdv1.domain.entity.AiGenerationResult) {
        updateState { state ->
            state.copy(
                advancedOptionsVisible = true,
                prompt = ai.prompt,
                negativePrompt = ai.negativePrompt,
                width = ai.width.toString(),
                height = ai.height.toString(),
                seed = ai.seed,
                subSeed = ai.subSeed,
                subSeedStrength = ai.subSeedStrength,
                samplingSteps = ai.samplingSteps,
                cfgScale = ai.cfgScale,
                restoreFaces = ai.restoreFaces,
                selectedSampler = ai.sampler.takeIf(state.availableSamplers::contains)
                    ?: state.selectedSampler,
                error = null,
                message = null,
            )
        }
    }

    private fun observeGenerationFormUpdate() {
        launch(dispatchersProvider.immediate) {
            generationFormUpdateEvent.observeTxt2ImgForm().collect { payload ->
                if (payload is GenerationFormUpdateEvent.Payload.T2IForm) {
                    applyGenerationResult(payload.ai)
                }
            }
        }
    }

    private fun consumePendingGenerationFormUpdate() {
        generationFormUpdateEvent.consumeTxt2ImgForm()?.let { payload ->
            applyGenerationResult(payload.ai)
        }
    }

    private fun observeGenerationProgress() {
        launch(dispatchersProvider.immediate) {
            observeHordeProcessStatusUseCase().collect { status ->
                updateState { state ->
                    val modal = state.screenModal
                    if (modal is GenerationModal.Communicating) {
                        state.copy(screenModal = modal.copy(hordeProcessStatus = status))
                    } else {
                        state
                    }
                }
            }
        }
        launch(dispatchersProvider.immediate) {
            observeLocalDiffusionProcessStatusUseCase().collect { status ->
                updateState { state ->
                    val modal = state.screenModal
                    if (modal is GenerationModal.Generating) {
                        state.copy(screenModal = modal.copy(status = status))
                    } else {
                        state
                    }
                }
            }
        }
    }

    private fun loadConfiguration() {
        updateState { it.copy(loadingConfiguration = true, error = null) }
        launch(dispatchersProvider.io) {
            runCatching { getConfigurationUseCase() }
                .onSuccess { configuration ->
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.copy(
                                loadingConfiguration = false,
                            ).withSource(configuration.source, stableDiffusionSamplers)
                        }
                    }
                }
                .onFailure { t ->
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.copy(
                                loadingConfiguration = false,
                                error = t.localizedMessageText(),
                            )
                        }
                    }
                    onError(t)
                }
        }
    }

    private fun observeSettings() {
        launch(dispatchersProvider.immediate) {
            preferenceManager
                .observe()
                .catch { t ->
                    updateState { it.copy(error = t.localizedMessageText()) }
                    onError(t)
                }
                .collect { settings ->
                    refreshStableDiffusionSamplersIfNeeded(settings)
                    updateState { state -> state.withSettings(settings, stableDiffusionSamplers) }
                }
        }
    }

    private fun refreshStableDiffusionSamplersIfNeeded(settings: Settings) {
        if (settings.source != ServerSource.AUTOMATIC1111) {
            stableDiffusionSamplersKey = null
            return
        }
        val key = StableDiffusionSamplersKey(
            serverUrl = settings.serverUrl,
            demoMode = settings.demoMode,
        )
        if (stableDiffusionSamplersKey == key) return

        stableDiffusionSamplersKey = key
        stableDiffusionSamplers = emptyList()
        updateState { it.withSource(ServerSource.AUTOMATIC1111, stableDiffusionSamplers) }
        loadSamplers()
    }

    private fun loadSamplers() {
        launch(dispatchersProvider.io) {
            runCatching {
                getStableDiffusionSamplersUseCase().map(StableDiffusionSampler::name)
            }
                .onSuccess { samplers ->
                    stableDiffusionSamplers = samplers
                    withContext(dispatchersProvider.immediate) {
                        updateState { it.withSource(it.mode, stableDiffusionSamplers) }
                    }
                }
                .onFailure { t ->
                    withContext(dispatchersProvider.immediate) {
                        updateState { it.copy(error = t.localizedMessageText()) }
                    }
                    onError(t)
                }
        }
    }

}
