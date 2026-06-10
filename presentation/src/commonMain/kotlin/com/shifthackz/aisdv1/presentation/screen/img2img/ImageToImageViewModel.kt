package com.shifthackz.aisdv1.presentation.screen.img2img

import com.shifthackz.aisdv1.core.localization.Localization
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
import com.shifthackz.aisdv1.domain.usecase.generation.GetRandomImageUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ImageToImageUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.InterruptGenerationUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveHordeProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveLocalDiffusionProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.SaveGenerationResultUseCase
import com.shifthackz.aisdv1.domain.usecase.sdsampler.GetStableDiffusionSamplersUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.GetConfigurationUseCase
import com.shifthackz.aisdv1.presentation.core.GenerationFormUpdateEvent
import com.shifthackz.aisdv1.presentation.core.GenerationPlatformServices
import com.shifthackz.aisdv1.presentation.model.GenerationModal
import com.shifthackz.aisdv1.presentation.navigation.router.ImageToImageRouter
import com.shifthackz.aisdv1.presentation.screen.txt2img.ImageSaver
import com.shifthackz.aisdv1.presentation.screen.txt2img.ImageSharer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.withContext

/**
 * Coordinates `ImageToImageViewModel` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
class ImageToImageViewModel(
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
     * Exposes the `getRandomImageUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val getRandomImageUseCase: GetRandomImageUseCase,
    /**
     * Exposes the `imageToImageUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val imageToImageUseCase: ImageToImageUseCase,
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
    private val router: ImageToImageRouter,
    /**
     * Exposes the `platformActions` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val platformActions: ImageToImagePlatformActions,
    /**
     * Exposes the `onError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val onError: (Throwable) -> Unit = {},
) : BaseMviViewModel<ImageToImageState, ImageToImageIntent, EmptyEffect>(
    initialState = ImageToImageState(),
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

    private val actionHandler = ImageToImageActionHandler(
        dispatchersProvider = dispatchersProvider,
        getRandomImageUseCase = getRandomImageUseCase,
        imageToImageUseCase = imageToImageUseCase,
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
        platformActions = platformActions,
        currentState = { currentState },
        emitState = ::emitState,
        updateState = { reducer -> updateState(reducer) },
        launch = { dispatcher, start, block -> launch(dispatcher, start, block) },
        onError = onError,
    )

    private val intentProcessor = ImageToImageIntentProcessor(
        router = router,
        updateState = { reducer -> updateState(reducer) },
        pickImage = actionHandler::pickImage,
        pickRandomImage = actionHandler::pickRandomImage,
        generate = actionHandler::generate,
        cancelGeneration = actionHandler::cancelGeneration,
        saveImage = actionHandler::saveImage,
        shareImage = actionHandler::shareImage,
        saveGenerationResults = actionHandler::saveGenerationResults,
        viewGenerationResult = actionHandler::viewGenerationResult,
        reportGenerationResult = actionHandler::reportGenerationResult,
        applyGenerationResult = ::applyGenerationResult,
    )

    override fun processIntent(intent: ImageToImageIntent) = intentProcessor.process(intent)

    private fun applyGenerationResult(ai: com.shifthackz.aisdv1.domain.entity.AiGenerationResult) {
        applyGenerationResult(ai, imageBase64 = null)
    }

    private fun applyGenerationResult(
        ai: com.shifthackz.aisdv1.domain.entity.AiGenerationResult,
        inputImage: Boolean,
    ) {
        applyGenerationResult(
            ai = ai,
            imageBase64 = if (inputImage) {
                ai.inputImage.ifBlank { ai.image }
            } else {
                null
            },
        )
    }

    private fun applyGenerationResult(
        ai: com.shifthackz.aisdv1.domain.entity.AiGenerationResult,
        imageBase64: String?,
    ) {
        updateState { state ->
            state.copy(
                advancedOptionsVisible = true,
                imageBase64 = imageBase64 ?: state.imageBase64,
                inPaint = if (imageBase64 == null) state.inPaint else ImageInPaintState(),
                results = if (imageBase64 == null) state.results else emptyList(),
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
                denoisingStrength = ai.denoisingStrength,
                selectedSampler = ai.sampler.takeIf(state.availableSamplers::contains)
                    ?: state.selectedSampler,
                error = null,
                message = null,
            )
        }
    }

    private fun observeGenerationFormUpdate() {
        launch(dispatchersProvider.immediate) {
            generationFormUpdateEvent.observeImg2ImgForm().collect { payload ->
                if (payload is GenerationFormUpdateEvent.Payload.I2IForm) {
                    applyGenerationResult(payload)
                }
            }
        }
    }

    private fun consumePendingGenerationFormUpdate() {
        generationFormUpdateEvent.consumeImg2ImgForm()?.let(::applyGenerationResult)
    }

    private fun applyGenerationResult(payload: GenerationFormUpdateEvent.Payload.I2IForm) {
        applyGenerationResult(
            ai = payload.ai,
            imageBase64 = if (payload.inputImage && payload.ai.inputImage.isNotBlank()) {
                payload.ai.inputImage
            } else {
                payload.ai.image
            },
        )
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
                                error = t.message ?: "Unable to load configuration",
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
                    updateState { it.copy(error = t.message ?: Localization.string("error_invalid")) }
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
                        updateState { it.copy(error = t.message ?: Localization.string("error_invalid")) }
                    }
                    onError(t)
                }
        }
    }

}
