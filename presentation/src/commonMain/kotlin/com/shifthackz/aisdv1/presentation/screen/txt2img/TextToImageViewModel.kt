package com.shifthackz.aisdv1.presentation.screen.txt2img

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.core.mvi.EmptyEffect
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.domain.entity.ForgeModule
import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.Settings
import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import com.shifthackz.aisdv1.domain.feature.work.BackgroundTaskManager
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.interactor.wakelock.WakeLockInterActor
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.arliai.FetchAndGetArliAiModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.caching.SaveLastResultToCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.forgemodule.GetForgeModulesUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.InterruptGenerationUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveCoreMlProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveHordeProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveLocalDiffusionProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveStableDiffusionCppProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.SaveGenerationResultUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.TextToImageUseCase
import com.shifthackz.aisdv1.domain.usecase.sdscript.IsADetailerAvailableUseCase
import com.shifthackz.aisdv1.domain.usecase.sdsampler.GetStableDiffusionSamplersUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.GetConfigurationUseCase
import com.shifthackz.aisdv1.feature.benchmark.LocalGenerationBenchmarkGate
import com.shifthackz.aisdv1.presentation.core.GenerationFormUpdateEvent
import com.shifthackz.aisdv1.presentation.core.GenerationPlatformServices
import com.shifthackz.aisdv1.presentation.model.GenerationModal
import com.shifthackz.aisdv1.presentation.navigation.router.TextToImageRouter
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.withContext

/**
 * Coordinates `TextToImageViewModel` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
class TextToImageViewModel(
    /**
     * Exposes the `dispatchersProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val dispatchersProvider: DispatchersProvider,
    /**
     * Exposes the `getConfigurationUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val getConfigurationUseCase: GetConfigurationUseCase,
    /**
     * Exposes the `getStableDiffusionSamplersUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val getStableDiffusionSamplersUseCase: GetStableDiffusionSamplersUseCase,
    /**
     * Exposes the `getForgeModulesUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val getForgeModulesUseCase: GetForgeModulesUseCase,
    /**
     * Exposes the `fetchAndGetArliAiModelsUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val fetchAndGetArliAiModelsUseCase: FetchAndGetArliAiModelsUseCase,
    /**
     * Exposes the `isADetailerAvailableUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val isADetailerAvailableUseCase: IsADetailerAvailableUseCase,
    /**
     * Exposes the `textToImageUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val textToImageUseCase: TextToImageUseCase,
    /**
     * Exposes the `saveGenerationResultUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val saveGenerationResultUseCase: SaveGenerationResultUseCase,
    /**
     * Exposes the `saveLastResultToCacheUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val saveLastResultToCacheUseCase: SaveLastResultToCacheUseCase,
    /**
     * Exposes the `interruptGenerationUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val interruptGenerationUseCase: InterruptGenerationUseCase,
    /**
     * Exposes the `observeHordeProcessStatusUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val observeHordeProcessStatusUseCase: ObserveHordeProcessStatusUseCase,
    /**
     * Exposes the `observeLocalDiffusionProcessStatusUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val observeLocalDiffusionProcessStatusUseCase: ObserveLocalDiffusionProcessStatusUseCase,
    /**
     * Exposes the `observeStableDiffusionCppProcessStatusUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val observeStableDiffusionCppProcessStatusUseCase: ObserveStableDiffusionCppProcessStatusUseCase,
    /**
     * Exposes the `observeCoreMlProcessStatusUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val observeCoreMlProcessStatusUseCase: ObserveCoreMlProcessStatusUseCase,
    /**
     * Exposes the `preferenceManager` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
    /**
     * Exposes the `backgroundTaskManager` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val backgroundTaskManager: BackgroundTaskManager,
    /**
     * Exposes the `backgroundWorkObserver` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val backgroundWorkObserver: BackgroundWorkObserver,
    /**
     * Exposes the `wakeLockInterActor` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val wakeLockInterActor: WakeLockInterActor,
    /**
     * Exposes the `platformServices` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val platformServices: GenerationPlatformServices,
    /**
     * Exposes the `buildInfoProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val buildInfoProvider: BuildInfoProvider,
    /**
     * Exposes the `generationFormUpdateEvent` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val generationFormUpdateEvent: GenerationFormUpdateEvent,
    /**
     * Exposes the `dimensionValidator` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val dimensionValidator: DimensionValidator,
    /**
     * Exposes the `localGenerationBenchmarkGateProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val localGenerationBenchmarkGateProvider: () -> LocalGenerationBenchmarkGate,
    /**
     * Exposes the `imageSaver` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val imageSaver: ImageSaver,
    /**
     * Exposes the `imageSharer` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val imageSharer: ImageSharer,
    /**
     * Exposes the `router` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val router: TextToImageRouter,
    /**
     * Exposes the `onError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
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
    private var forgeModules: List<ForgeModule>? = null
    private var forgeModulesKey: StableDiffusionSamplersKey? = null
    private var aDetailerAvailable: Boolean? = null
    private var aDetailerAvailabilityKey: StableDiffusionSamplersKey? = null
    private var arliAiModels: List<String>? = null
    private var arliAiModelsKey: String? = null

    private val actionHandler = TextToImageActionHandler(
        dispatchersProvider = dispatchersProvider,
        textToImageUseCase = textToImageUseCase,
        saveGenerationResultUseCase = saveGenerationResultUseCase,
        saveLastResultToCacheUseCase = saveLastResultToCacheUseCase,
        interruptGenerationUseCase = interruptGenerationUseCase,
        preferenceManager = preferenceManager,
        backgroundTaskManager = backgroundTaskManager,
        backgroundWorkObserver = backgroundWorkObserver,
        wakeLockInterActor = wakeLockInterActor,
        platformServices = platformServices,
        buildInfoProvider = buildInfoProvider,
        dimensionValidator = dimensionValidator,
        localGenerationBenchmarkGateProvider = localGenerationBenchmarkGateProvider,
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

    override fun processIntent(intent: TextToImageIntent) {
        when (intent) {
            TextToImageIntent.RefreshADetailerAvailability -> loadADetailerAvailability(force = true)
            TextToImageIntent.OpenADetailerInstallInstructions -> Unit
            TextToImageIntent.RunBenchmarkFromPrompt -> actionHandler.runBenchmarkFromPrompt()
            TextToImageIntent.SkipBenchmarkPrompt -> actionHandler.skipBenchmarkPrompt()
            TextToImageIntent.ContinueAfterBenchmarkWarning ->
                actionHandler.continueAfterBenchmarkWarning(suppressFutureWarnings = false)
            TextToImageIntent.SuppressBenchmarkWarningAndContinue ->
                actionHandler.continueAfterBenchmarkWarning(suppressFutureWarnings = true)
            TextToImageIntent.DismissModal -> actionHandler.dismissBenchmarkDialog()
            is TextToImageIntent.UpdateArliAiModel -> {
                preferenceManager.arliAiModel = intent.value
                intentProcessor.process(intent)
            }
            else -> intentProcessor.process(intent)
        }
    }

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
                updateLocalGenerationStatus(status)
            }
        }
        launch(dispatchersProvider.immediate) {
            observeStableDiffusionCppProcessStatusUseCase().collect { status ->
                updateLocalGenerationStatus(status)
            }
        }
        launch(dispatchersProvider.immediate) {
            observeCoreMlProcessStatusUseCase().collect { status ->
                updateLocalGenerationStatus(status)
            }
        }
    }

    private fun updateLocalGenerationStatus(status: LocalDiffusionStatus) {
        updateState { state ->
            val modal = state.screenModal
            if (modal is GenerationModal.Generating) {
                state.copy(screenModal = modal.copy(status = status))
            } else {
                state
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
                            ).withSource(
                                source = configuration.source,
                                stableDiffusionSamplers = stableDiffusionSamplers,
                                forgeModules = forgeModules,
                                aDetailerAvailable = aDetailerAvailable,
                                arliAiModels = arliAiModels,
                            )
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
                    refreshStableDiffusionMetadataIfNeeded(settings)
                    refreshArliAiModelsIfNeeded(settings)
                    updateState { state ->
                        state.withSettings(
                            settings = settings,
                            stableDiffusionSamplers = stableDiffusionSamplers,
                            forgeModules = forgeModules,
                            aDetailerAvailable = aDetailerAvailable,
                            arliAiModels = arliAiModels,
                        )
                    }
                }
        }
    }

    private fun refreshStableDiffusionMetadataIfNeeded(settings: Settings) {
        if (settings.source != ServerSource.AUTOMATIC1111) {
            stableDiffusionSamplersKey = null
            forgeModulesKey = null
            forgeModules = null
            aDetailerAvailabilityKey = null
            aDetailerAvailable = null
            return
        }
        val key = StableDiffusionSamplersKey(
            serverUrl = settings.serverUrl,
            demoMode = settings.demoMode,
        )
        if (
            stableDiffusionSamplersKey == key &&
            forgeModulesKey == key &&
            aDetailerAvailabilityKey == key
        ) return

        stableDiffusionSamplersKey = key
        forgeModulesKey = key
        aDetailerAvailabilityKey = key
        stableDiffusionSamplers = emptyList()
        forgeModules = emptyList()
        aDetailerAvailable = null
        updateState {
            it.withSource(
                source = ServerSource.AUTOMATIC1111,
                stableDiffusionSamplers = stableDiffusionSamplers,
                forgeModules = forgeModules,
                aDetailerAvailable = aDetailerAvailable,
                arliAiModels = arliAiModels,
            )
        }
        loadSamplers()
        loadForgeModules()
        loadADetailerAvailability()
    }

    private fun loadSamplers() {
        launch(dispatchersProvider.io) {
            runCatching {
                getStableDiffusionSamplersUseCase().map(StableDiffusionSampler::name)
            }
                .onSuccess { samplers ->
                    stableDiffusionSamplers = samplers
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.withSource(
                                source = it.mode,
                                stableDiffusionSamplers = stableDiffusionSamplers,
                                forgeModules = forgeModules,
                                aDetailerAvailable = aDetailerAvailable,
                                arliAiModels = arliAiModels,
                            )
                        }
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

    private fun loadForgeModules() {
        launch(dispatchersProvider.io) {
            runCatching { getForgeModulesUseCase() }
                .onSuccess { modules ->
                    forgeModules = modules
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.withSource(
                                source = it.mode,
                                stableDiffusionSamplers = stableDiffusionSamplers,
                                forgeModules = forgeModules,
                                aDetailerAvailable = aDetailerAvailable,
                                arliAiModels = arliAiModels,
                            )
                        }
                    }
                }
                .onFailure { t ->
                    forgeModules = emptyList()
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.withSource(
                                source = it.mode,
                                stableDiffusionSamplers = stableDiffusionSamplers,
                                forgeModules = forgeModules,
                                aDetailerAvailable = aDetailerAvailable,
                                arliAiModels = arliAiModels,
                            )
                        }
                    }
                    onError(t)
                }
        }
    }

    private fun loadADetailerAvailability(force: Boolean = false) {
        if (!force && aDetailerAvailable != null) return
        updateState { it.copy(aDetailerRefreshing = true) }
        launch(dispatchersProvider.io) {
            runCatching { isADetailerAvailableUseCase() }
                .onSuccess { available ->
                    aDetailerAvailable = available
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.copy(
                                aDetailerAvailable = available,
                                aDetailerRefreshing = false,
                                error = null,
                            )
                        }
                    }
                }
                .onFailure { t ->
                    aDetailerAvailable = false
                    withContext(dispatchersProvider.immediate) {
                        updateState { state ->
                            state.copy(
                                aDetailerAvailable = false,
                                aDetailerRefreshing = false,
                            )
                        }
                    }
                    onError(t)
                }
        }
    }

    private fun refreshArliAiModelsIfNeeded(settings: Settings) {
        if (settings.source != ServerSource.ARLI_AI) {
            arliAiModelsKey = null
            return
        }
        val key = preferenceManager.arliAiApiKey
        if (arliAiModelsKey == key) return

        arliAiModelsKey = key
        arliAiModels = emptyList()
        updateState {
            it.withSource(
                source = ServerSource.ARLI_AI,
                stableDiffusionSamplers = stableDiffusionSamplers,
                forgeModules = forgeModules,
                aDetailerAvailable = aDetailerAvailable,
                arliAiModels = arliAiModels,
            )
        }
        loadArliAiModels()
    }

    private fun loadArliAiModels() {
        launch(dispatchersProvider.io) {
            runCatching {
                fetchAndGetArliAiModelsUseCase()
                    .map(StableDiffusionModel::arliAiCheckpointName)
                    .filter(String::isNotBlank)
                    .distinct()
            }
                .onSuccess { models ->
                    arliAiModels = models
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.copy(arliAiModel = preferenceManager.arliAiModel)
                                .withSource(
                                    source = it.mode,
                                    stableDiffusionSamplers = stableDiffusionSamplers,
                                    forgeModules = forgeModules,
                                    aDetailerAvailable = aDetailerAvailable,
                                    arliAiModels = arliAiModels,
                                )
                        }
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

private val StableDiffusionModel.arliAiCheckpointName: String
    get() = title.ifBlank { modelName }.ifBlank { filename }
