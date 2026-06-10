package com.shifthackz.aisdv1.presentation.widget.engine

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.core.mvi.EmptyEffect
import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.downloadable.ObserveLocalOnnxModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.huggingface.FetchHuggingFaceModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.GetStableDiffusionModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.SelectStableDiffusionModelUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.GetConfigurationUseCase
import com.shifthackz.aisdv1.domain.usecase.stabilityai.FetchAndGetStabilityAiEnginesUseCase
import com.shifthackz.aisdv1.domain.usecase.swarmmodel.FetchAndGetSwarmUiModelsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeoutOrNull

@OptIn(ExperimentalCoroutinesApi::class)
class EngineSelectionViewModel(
    private val dispatchersProvider: DispatchersProvider,
    private val fetchAndGetSwarmUiModelsUseCase: FetchAndGetSwarmUiModelsUseCase,
    private val observeLocalOnnxModelsUseCase: ObserveLocalOnnxModelsUseCase,
    private val fetchAndGetStabilityAiEnginesUseCase: FetchAndGetStabilityAiEnginesUseCase,
    private val getHuggingFaceModelsUseCase: FetchHuggingFaceModelsUseCase,
    private val preferenceManager: PreferenceManager,
    private val getConfigurationUseCase: GetConfigurationUseCase,
    private val selectStableDiffusionModelUseCase: SelectStableDiffusionModelUseCase,
    private val getStableDiffusionModelsUseCase: GetStableDiffusionModelsUseCase,
    private val onError: (Throwable) -> Unit = {},
) : BaseMviViewModel<EngineSelectionState, EngineSelectionIntent, EmptyEffect>(
    initialState = EngineSelectionState(),
    effectDispatcher = dispatchersProvider.immediate,
) {

    init {
        val configuration = preferenceManager
            .observe()
            .map { getConfigurationUseCase() }
            .catch {
                onError(it)
                emit(Configuration())
            }

        val localAiModels = observeLocalOnnxModelsUseCase()
            .map { models ->
                models.filter { it.downloaded || it.id == LocalAiModel.CustomOnnx.id }
            }
            .catch {
                onError(it)
                emit(emptyList())
            }

        launch(dispatchersProvider.io) {
            configuration
                .combine(localAiModels) { config, localModels -> config to localModels }
                .catch { onError(it) }
                .collectLatest { (config, localModels) ->
                    updateState {
                        it.copy(
                            loading = true,
                            mode = config.source,
                            localAiModels = localModels,
                        )
                    }
                    val options = loadOptions(config)
                    updateState { state ->
                        state.copy(
                            loading = false,
                            mode = options.config.source,
                            sdModels = options.sdModels.map { it.first.title },
                            selectedSdModel = options.sdModels
                                .firstOrNull { it.second }
                                ?.first
                                ?.title
                                ?: state.selectedSdModel,
                            swarmModels = options.swarmModels.map { it.name },
                            selectedSwarmModel = options.swarmModels
                                .firstOrNull { it.name == options.config.swarmUiModel }
                                ?.name
                                ?: state.selectedSwarmModel,
                            hfModels = options.hfModels.map { it.alias },
                            selectedHfModel = options.config.huggingFaceModel.safeHuggingFaceModelAlias(),
                            stEngines = options.stEngines.map { it.id },
                            selectedStEngine = options.config.stabilityAiEngineId,
                            localAiModels = localModels,
                            selectedLocalAiModelId = localModels
                                .firstOrNull { it.id == options.config.localOnnxModelId }
                                ?.id
                                ?: state.selectedLocalAiModelId,
                        )
                    }
                }
        }
    }

    private suspend fun loadOptions(config: Configuration): RemoteOptions {
        val remoteOptions = RemoteOptions(config = config)
        return when (config.source) {
            ServerSource.AUTOMATIC1111 -> remoteOptions.copy(
                sdModels = if (config.hasA1111Endpoint()) {
                    loadList { getStableDiffusionModelsUseCase() }
                } else {
                    emptyList()
                },
            )

            ServerSource.SWARM_UI -> remoteOptions.copy(
                swarmModels = if (config.hasSwarmEndpoint()) {
                    loadList { fetchAndGetSwarmUiModelsUseCase() }
                } else {
                    emptyList()
                },
            )

            ServerSource.HUGGING_FACE -> remoteOptions.copy(
                hfModels = loadList { getHuggingFaceModelsUseCase() }
                    .ifEmpty { HuggingFaceModel.supportedHfInferenceTextToImageModels },
            )

            ServerSource.STABILITY_AI -> remoteOptions.copy(
                stEngines = if (config.stabilityAiApiKey.isBlank()) {
                    emptyList()
                } else {
                    loadList { fetchAndGetStabilityAiEnginesUseCase() }
                },
            )

            ServerSource.LOCAL_MICROSOFT_ONNX,
            ServerSource.LOCAL_GOOGLE_MEDIA_PIPE,
            ServerSource.HORDE,
            ServerSource.OPEN_AI,
            -> remoteOptions
        }
    }

    override fun processIntent(intent: EngineSelectionIntent) {
        when (currentState.mode) {
            ServerSource.AUTOMATIC1111 -> selectA1111Model(intent.value)
            ServerSource.SWARM_UI -> preferenceManager.swarmUiModel = intent.value
            ServerSource.HUGGING_FACE -> preferenceManager.huggingFaceModel =
                intent.value.safeHuggingFaceModelAlias()
            ServerSource.STABILITY_AI -> preferenceManager.stabilityAiEngineId = intent.value
            ServerSource.LOCAL_MICROSOFT_ONNX -> preferenceManager.localOnnxModelId = intent.value
            else -> Unit
        }
    }

    private fun selectA1111Model(value: String) {
        updateState {
            it.copy(
                loading = true,
                selectedSdModel = value,
            )
        }

        launch(dispatchersProvider.io) {
            runCatching {
                selectStableDiffusionModelUseCase(value)
                getStableDiffusionModelsUseCase()
            }
                .onFailure { throwable ->
                    onError(throwable)
                    updateState { it.copy(loading = false) }
                }
                .onSuccess { sdModels ->
                    updateState { state ->
                        state.copy(
                            loading = false,
                            sdModels = sdModels.map { it.first.title },
                            selectedSdModel = sdModels
                                .firstOrNull { it.second }
                                ?.first
                                ?.title
                                ?: state.selectedSdModel,
                        )
                    }
                }
        }
    }

    private suspend fun <T> loadList(block: suspend () -> List<T>): List<T> = runCatching {
        withTimeoutOrNull(REMOTE_OPTIONS_TIMEOUT_MILLIS) {
            block()
        } ?: emptyList()
    }
        .onFailure(onError)
        .getOrElse { emptyList() }
}

private const val REMOTE_OPTIONS_TIMEOUT_MILLIS = 5_000L

private fun Configuration.hasA1111Endpoint(): Boolean = serverUrl.isMobileRemoteEndpoint()

private fun Configuration.hasSwarmEndpoint(): Boolean = swarmUiUrl.isMobileRemoteEndpoint()

private fun String.isMobileRemoteEndpoint(): Boolean {
    val value = trim().lowercase()
    return value.isNotBlank() &&
        !value.startsWith("http://localhost") &&
        !value.startsWith("https://localhost") &&
        !value.startsWith("http://127.0.0.1") &&
        !value.startsWith("https://127.0.0.1") &&
        !value.startsWith("http://[::1]") &&
        !value.startsWith("https://[::1]")
}

private fun String.safeHuggingFaceModelAlias(): String =
    takeIf(HuggingFaceModel.supportedHfInferenceTextToImageAliases::contains)
        ?: HuggingFaceModel.default.alias

private data class RemoteOptions(
    val config: Configuration,
    val sdModels: List<Pair<com.shifthackz.aisdv1.domain.entity.StableDiffusionModel, Boolean>> = emptyList(),
    val swarmModels: List<com.shifthackz.aisdv1.domain.entity.SwarmUiModel> = emptyList(),
    val hfModels: List<com.shifthackz.aisdv1.domain.entity.HuggingFaceModel> = emptyList(),
    val stEngines: List<com.shifthackz.aisdv1.domain.entity.StabilityAiEngine> = emptyList(),
)
