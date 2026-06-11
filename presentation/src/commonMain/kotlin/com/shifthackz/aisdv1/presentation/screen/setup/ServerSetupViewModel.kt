package com.shifthackz.aisdv1.presentation.screen.setup

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.core.validation.common.CommonStringValidator
import com.shifthackz.aisdv1.core.validation.path.FilePathValidator
import com.shifthackz.aisdv1.core.validation.url.UrlValidator
import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.downloadable.DeleteModelUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.DownloadModelUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.GetLocalCoreMlModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.GetLocalMediaPipeModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.GetLocalOnnxModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.huggingface.FetchHuggingFaceModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToA1111UseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToCoreMlUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToFalAiUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToHordeUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToHuggingFaceUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToLocalDiffusionUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToMediaPipeUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToOpenAiUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToStabilityAiUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToSwarmUiUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.GetConfigurationUseCase
import com.shifthackz.aisdv1.presentation.model.LaunchSource
import com.shifthackz.aisdv1.presentation.navigation.router.ServerSetupRouter
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.cancellation.CancellationException

/**
 * Coordinates `ServerSetupViewModel` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
class ServerSetupViewModel(
    /**
     * Exposes the `launchSource` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val launchSource: LaunchSource = LaunchSource.SPLASH,
    /**
     * Exposes the `dispatchersProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val dispatchersProvider: DispatchersProvider,
    /**
     * Exposes the `buildInfoProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val buildInfoProvider: BuildInfoProvider,
    /**
     * Exposes the `getConfigurationUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val getConfigurationUseCase: GetConfigurationUseCase,
    /**
     * Exposes the `getLocalOnnxModelsUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val getLocalOnnxModelsUseCase: GetLocalOnnxModelsUseCase,
    /**
     * Exposes the `getLocalMediaPipeModelsUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val getLocalMediaPipeModelsUseCase: GetLocalMediaPipeModelsUseCase,
    /**
     * Exposes the `getLocalCoreMlModelsUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val getLocalCoreMlModelsUseCase: GetLocalCoreMlModelsUseCase,
    /**
     * Exposes the `fetchHuggingFaceModelsUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val fetchHuggingFaceModelsUseCase: FetchHuggingFaceModelsUseCase,
    /**
     * Exposes the `urlValidator` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val urlValidator: UrlValidator,
    /**
     * Exposes the `stringValidator` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val stringValidator: CommonStringValidator,
    /**
     * Exposes the `filePathValidator` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val filePathValidator: FilePathValidator,
    /**
     * Exposes the `connectToA1111UseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val connectToA1111UseCase: ConnectToA1111UseCase,
    /**
     * Exposes the `connectToSwarmUiUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val connectToSwarmUiUseCase: ConnectToSwarmUiUseCase,
    /**
     * Exposes the `connectToHordeUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val connectToHordeUseCase: ConnectToHordeUseCase,
    /**
     * Exposes the `connectToHuggingFaceUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val connectToHuggingFaceUseCase: ConnectToHuggingFaceUseCase,
    /**
     * Exposes the `connectToLocalDiffusionUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val connectToLocalDiffusionUseCase: ConnectToLocalDiffusionUseCase,
    /**
     * Exposes the `connectToMediaPipeUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val connectToMediaPipeUseCase: ConnectToMediaPipeUseCase,
    /**
     * Exposes the `connectToCoreMlUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val connectToCoreMlUseCase: ConnectToCoreMlUseCase,
    /**
     * Exposes the `connectToOpenAiUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val connectToOpenAiUseCase: ConnectToOpenAiUseCase,
    /**
     * Exposes the `connectToStabilityAiUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val connectToStabilityAiUseCase: ConnectToStabilityAiUseCase,
    /**
     * Exposes the `connectToFalAiUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val connectToFalAiUseCase: ConnectToFalAiUseCase,
    /**
     * Exposes the `downloadModelUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val downloadModelUseCase: DownloadModelUseCase,
    /**
     * Exposes the `deleteModelUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val deleteModelUseCase: DeleteModelUseCase,
    /**
     * Exposes the `downloadGuard` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val downloadGuard: ServerSetupDownloadGuard,
    /**
     * Exposes the `linksProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val linksProvider: LinksProvider,
    /**
     * Exposes the `preferenceManager` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
    /**
     * Exposes the `router` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val router: ServerSetupRouter,
    /**
     * Exposes the `onError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val onError: (Throwable) -> Unit = {},
) : BaseMviViewModel<ServerSetupState, ServerSetupIntent, ServerSetupEffect>(
    initialState = ServerSetupState(
        showBackNavArrow = launchSource == LaunchSource.SETTINGS,
        allowedModes = buildInfoProvider.setupAllowedModes(),
        demoModeUrl = linksProvider.demoModeUrl,
    ),
    effectDispatcher = dispatchersProvider.immediate,
) {

    private val downloadJobs: MutableMap<String, Job> = mutableMapOf()

    init {
        launch(dispatchersProvider.io) {
            runCatching {
                val configuration = getConfigurationUseCase()
                val allowedModes = buildInfoProvider.setupAllowedModes()
                val onnxModels = if (ServerSource.LOCAL_MICROSOFT_ONNX in allowedModes) {
                    getLocalOnnxModelsUseCase()
                } else {
                    emptyList()
                }
                val mediaPipeModels = if (ServerSource.LOCAL_GOOGLE_MEDIA_PIPE in allowedModes) {
                    getLocalMediaPipeModelsUseCase()
                } else {
                    emptyList()
                }
                val coreMlModels = if (ServerSource.LOCAL_APPLE_CORE_ML in allowedModes) {
                    getLocalCoreMlModelsUseCase()
                } else {
                    emptyList()
                }
                val models = runCatching {
                    withTimeout(HUGGING_FACE_MODELS_TIMEOUT_MILLIS) {
                        fetchHuggingFaceModelsUseCase()
                    }
                }
                    .onFailure(onError)
                    .getOrElse { HuggingFaceModel.supportedHfInferenceTextToImageModels }
                    .ifEmpty { HuggingFaceModel.supportedHfInferenceTextToImageModels }
                    .map(HuggingFaceModel::alias)
                configuration.toServerSetupState(
                    allowedModes = allowedModes,
                    huggingFaceModels = models,
                    localOnnxModels = onnxModels,
                    localMediaPipeModels = mediaPipeModels,
                    localCoreMlModels = coreMlModels,
                    allowLocalCustomModels = buildInfoProvider.type != BuildType.PLAY,
                    demoModeUrl = linksProvider.demoModeUrl,
                    showBackNavArrow = launchSource == LaunchSource.SETTINGS,
                )
            }
                .onSuccess { state ->
                    withContext(dispatchersProvider.immediate) {
                        emitState(state)
                    }
                }
                .onFailure { t ->
                    withContext(dispatchersProvider.immediate) {
                        updateState { state ->
                            state.copy(
                                loadingConfiguration = false,
                                modal = ServerSetupState.Modal.Error(
                                    t.message ?: "Unable to load configuration",
                                ),
                            )
                        }
                    }
                    onError(t)
                }
        }
    }

    private val intentProcessor = ServerSetupIntentProcessor(
        router = router,
        linksProvider = linksProvider,
        currentState = { currentState },
        updateState = { reducer -> updateState(reducer) },
        emitEffect = ::emitEffect,
        localModelDownloadClickReducer = ::localModelDownloadClickReducer,
        deleteLocalModel = ::deleteLocalModel,
        download = ::download,
        validateAndConnectToServer = ::validateAndConnectToServer,
        connectToServer = ::connectToServer,
    )

    override fun processIntent(intent: ServerSetupIntent) = intentProcessor.process(intent)

    private fun validateAndConnectToServer() {
        if (!validate()) return
        connectToServer()
    }

    private fun validate(): Boolean {
        val result = currentState.validateServerSetup(
            urlValidator = urlValidator,
            stringValidator = stringValidator,
            filePathValidator = filePathValidator,
        )
        emitState(result.state)
        return result.isValid
    }

    private fun connectToServer() {
        emitEffect(ServerSetupEffect.HideKeyboard)
        updateState { it.copy(modal = ServerSetupState.Modal.Communicating) }
        launch(dispatchersProvider.io) {
            val result = try {
                when (currentState.mode) {
                    ServerSource.AUTOMATIC1111 -> connectToAutomaticInstance()
                    ServerSource.SWARM_UI -> connectToSwarmUi()
                    ServerSource.HORDE -> connectToHorde()
                    ServerSource.HUGGING_FACE -> connectToHuggingFace()
                    ServerSource.OPEN_AI -> connectToOpenAi()
                    ServerSource.STABILITY_AI -> connectToStabilityAi()
                    ServerSource.FAL_AI -> connectToFalAi()
                    ServerSource.LOCAL_MICROSOFT_ONNX -> connectToLocalDiffusion()
                    ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> connectToMediaPipe()
                    ServerSource.LOCAL_APPLE_CORE_ML -> connectToCoreMl()
                }
            } catch (t: Throwable) {
                if (t is CancellationException) throw t
                Result.failure(t)
            }
            withContext(dispatchersProvider.immediate) {
                result.fold(
                    onSuccess = {
                        preferenceManager.forceSetupAfterUpdate = false
                        updateState { state -> state.copy(modal = ServerSetupState.Modal.None) }
                        router.navigateToPostSetupConfigLoader()
                    },
                    onFailure = { t ->
                        updateState { state ->
                            state.copy(
                                modal = ServerSetupState.Modal.Error(
                                    t.message ?: "Connection failed",
                                ),
                            )
                        }
                        onError(t)
                    },
                )
            }
        }
    }

    private suspend fun connectToAutomaticInstance(): Result<Unit> {
        val isDemo = currentState.demoMode
        val url = if (isDemo) linksProvider.demoModeUrl else currentState.serverUrl
        return connectToA1111UseCase(
            url = url,
            isDemo = isDemo,
            credentials = if (isDemo) AuthorizationCredentials.None else currentState.credentialsDomain(),
        )
    }

    private suspend fun connectToSwarmUi(): Result<Unit> = connectToSwarmUiUseCase(
        url = currentState.swarmUiUrl,
        credentials = currentState.credentialsDomain(),
    )

    private suspend fun connectToHorde(): Result<Unit> = connectToHordeUseCase(
        apiKey = if (currentState.hordeDefaultApiKey) {
            HORDE_DEFAULT_API_KEY
        } else {
            currentState.hordeApiKey
        },
    )

    private suspend fun connectToHuggingFace(): Result<Unit> = connectToHuggingFaceUseCase(
        apiKey = currentState.huggingFaceApiKey,
        model = currentState.huggingFaceModel,
    )

    private suspend fun connectToOpenAi(): Result<Unit> = connectToOpenAiUseCase(
        apiKey = currentState.openAiApiKey,
    )

    private suspend fun connectToStabilityAi(): Result<Unit> = connectToStabilityAiUseCase(
        apiKey = currentState.stabilityAiApiKey,
    )

    private suspend fun connectToFalAi(): Result<Unit> = connectToFalAiUseCase(
        apiKey = currentState.falAiApiKey,
    )

    private suspend fun connectToLocalDiffusion(): Result<Unit> = connectToLocalDiffusionUseCase(
        modelId = currentState.localOnnxModels.find { it.selected }?.id.orEmpty(),
        modelPath = currentState.localOnnxCustomModelPath,
    )

    private suspend fun connectToMediaPipe(): Result<Unit> = connectToMediaPipeUseCase(
        modelId = currentState.localMediaPipeModels.find { it.selected }?.id.orEmpty(),
        modelPath = currentState.localMediaPipeCustomModelPath,
    )

    private suspend fun connectToCoreMl(): Result<Unit> = connectToCoreMlUseCase(
        modelId = currentState.localCoreMlModels.find { it.selected }?.id.orEmpty(),
        modelPath = currentState.localCoreMlCustomModelPath,
    )

    private fun localModelDownloadClickReducer(value: ServerSetupState.LocalModel) {
        fun localModel(): ServerSetupState.LocalModel =
            currentState.localModels.firstOrNull { it.id == value.id }
                ?.let { value.copy(selected = it.selected) }
                ?: value

        when {
            localModel().downloadState is DownloadState.Downloading -> {
                downloadJobs.remove(localModel().id)?.cancel()
                deleteLocalModel(localModel().id)
                updateState { state ->
                    state.withUpdatedLocalModel(
                        value = localModel().copy(downloadState = DownloadState.Unknown),
                    )
                }
            }

            localModel().downloaded -> updateState {
                it.copy(modal = ServerSetupState.Modal.DeleteLocalModelConfirm(localModel()))
            }

            else -> updateState {
                it.copy(modal = ServerSetupState.Modal.SelectDownloadSource(localModel().id))
            }
        }
    }

    private fun download(modelId: String, url: String) {
        val localModel = currentState.localModels.firstOrNull { it.id == modelId } ?: return

        updateState { state ->
            state
                .copy(modal = ServerSetupState.Modal.None)
                .withUpdatedLocalModel(
                    localModel.copy(downloadState = DownloadState.Downloading()),
                )
        }

        val job = scopeLaunchDownload(localModel, url)
        downloadJobs[localModel.id] = job
        job.start()
    }

    private fun scopeLaunchDownload(
        localModel: ServerSetupState.LocalModel,
        url: String,
    ): Job =
        launch(dispatchersProvider.io, start = CoroutineStart.LAZY) {
            downloadGuard.withDownload {
                try {
                    downloadModelUseCase(localModel.id, url)
                        .distinctUntilChanged()
                        .catch { t ->
                            if (t is CancellationException) throw t
                            onDownloadFailure(localModel, t)
                        }
                        .collect { downloadState ->
                            updateState { state ->
                                state.withUpdatedLocalModel(
                                    localModel.copy(
                                        downloadState = downloadState,
                                        downloaded = downloadState is DownloadState.Complete,
                                    ),
                                )
                            }
                        }
                } finally {
                    downloadJobs.remove(localModel.id)
                }
            }
        }

    private fun onDownloadFailure(localModel: ServerSetupState.LocalModel, t: Throwable) {
        updateState { state ->
            state
                .withUpdatedLocalModel(
                    localModel.copy(downloadState = DownloadState.Error(t)),
                )
                .copy(
                    modal = ServerSetupState.Modal.Error(
                        t.message ?: Localization.string("error_title"),
                    ),
                )
        }
        onError(t)
    }

    private fun deleteLocalModel(id: String) {
        launch(dispatchersProvider.io) {
            runCatching { deleteModelUseCase(id) }
                .onFailure(onError)
        }
    }

}
