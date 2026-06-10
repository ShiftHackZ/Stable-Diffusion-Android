package com.shifthackz.aisdv1.presentation.screen.home

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.core.mvi.EmptyEffect
import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.usecase.settings.GetConfigurationUseCase
import com.shifthackz.aisdv1.presentation.navigation.router.HomeRouter
import kotlinx.coroutines.withContext

/**
 * Coordinates `HomeViewModel` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
class HomeViewModel(
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
     * Exposes the `router` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val router: HomeRouter,
    /**
     * Exposes the `onError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val onError: (Throwable) -> Unit = {},
) : BaseMviViewModel<HomeState, HomeIntent, EmptyEffect>(
    initialState = HomeState(),
    effectDispatcher = dispatchersProvider.immediate,
) {

    init {
        loadConfiguration()
    }

    override fun processIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.ConfigureProvider -> router.navigateToServerSetup()
            HomeIntent.StartTextToImage -> router.navigateToTextToImage()
            HomeIntent.StartImageToImage -> router.navigateToImageToImage()
            HomeIntent.OpenGallery -> router.navigateToGallery()
            HomeIntent.OpenSettings -> router.navigateToSettings()
            HomeIntent.OpenHistory -> router.navigateToHistory()
            HomeIntent.RefreshConfiguration -> loadConfiguration()
        }
    }

    private fun loadConfiguration() {
        updateState { it.copy(loading = true, error = null) }
        launch(dispatchersProvider.io) {
            runCatching { getConfigurationUseCase() }
                .onSuccess { configuration ->
                    withContext(dispatchersProvider.immediate) {
                        emitState(configuration.toHomeState())
                    }
                }
                .onFailure { t ->
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.copy(
                                loading = false,
                                error = t.message ?: "Unable to load configuration",
                            )
                        }
                    }
                    onError(t)
                }
        }
    }
}

/**
 * Converts SDAI data with `toHomeState`.
 *
 * @return Result produced by `toHomeState`.
 * @author Dmitriy Moroz
 */
private fun Configuration.toHomeState(): HomeState =
    HomeState(
        loading = false,
        source = source,
        endpoint = when (source) {
            ServerSource.AUTOMATIC1111 -> if (demoMode) "Demo endpoint" else serverUrl
            ServerSource.SWARM_UI -> swarmUiUrl
            ServerSource.HORDE -> "AI Horde"
            ServerSource.HUGGING_FACE -> huggingFaceModel
                .takeIf(HuggingFaceModel.supportedHfInferenceTextToImageAliases::contains)
                ?: HuggingFaceModel.default.alias
            ServerSource.OPEN_AI -> "OpenAI"
            ServerSource.STABILITY_AI -> "Stability AI"
            ServerSource.LOCAL_MICROSOFT_ONNX,
            ServerSource.LOCAL_GOOGLE_MEDIA_PIPE,
            -> ""
        },
    )
