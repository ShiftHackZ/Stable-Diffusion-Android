package com.shifthackz.aisdv1.presentation.screen.setup

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

/**
 * Carries `ServerSetupState` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class ServerSetupState(
    /**
     * Exposes the `showBackNavArrow` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val showBackNavArrow: Boolean = false,
    /**
     * Exposes the `step` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val step: Step = Step.SOURCE,
    /**
     * Exposes the `mode` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val mode: ServerSource = ServerSource.AUTOMATIC1111,
    /**
     * Exposes the `allowedModes` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val allowedModes: List<ServerSource> = remoteSetupSources,
    /**
     * Exposes the `allowLocalCustomModels` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val allowLocalCustomModels: Boolean = true,
    /**
     * Exposes the `modal` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val modal: Modal = Modal.None,
    /**
     * Exposes the `loadingConfiguration` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val loadingConfiguration: Boolean = true,
    /**
     * Exposes the `serverUrl` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val serverUrl: String = "",
    /**
     * Exposes the `swarmUiUrl` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val swarmUiUrl: String = "",
    /**
     * Exposes the `hordeApiKey` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val hordeApiKey: String = "",
    /**
     * Exposes the `huggingFaceApiKey` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val huggingFaceApiKey: String = "",
    /**
     * Exposes the `openAiApiKey` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val openAiApiKey: String = "",
    /**
     * Exposes the `stabilityAiApiKey` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val stabilityAiApiKey: String = "",
    /**
     * Exposes the `hordeDefaultApiKey` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val hordeDefaultApiKey: Boolean = false,
    /**
     * Exposes the `demoMode` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val demoMode: Boolean = false,
    /**
     * Exposes the `demoModeUrl` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val demoModeUrl: String = "",
    /**
     * Exposes the `authType` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val authType: AuthType = AuthType.ANONYMOUS,
    /**
     * Exposes the `login` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val login: String = "",
    /**
     * Exposes the `password` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val password: String = "",
    /**
     * Exposes the `huggingFaceModels` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val huggingFaceModels: List<String> = emptyList(),
    /**
     * Exposes the `huggingFaceModel` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val huggingFaceModel: String = HuggingFaceModel.default.alias,
    /**
     * Exposes the `localOnnxModels` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localOnnxModels: List<LocalModel> = emptyList(),
    /**
     * Exposes the `localOnnxCustomModel` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localOnnxCustomModel: Boolean = false,
    /**
     * Exposes the `localOnnxCustomModelPath` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localOnnxCustomModelPath: String = "",
    /**
     * Exposes the `localMediaPipeModels` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localMediaPipeModels: List<LocalModel> = emptyList(),
    /**
     * Exposes the `localMediaPipeCustomModel` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localMediaPipeCustomModel: Boolean = false,
    /**
     * Exposes the `localMediaPipeCustomModelPath` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localMediaPipeCustomModelPath: String = "",
    /**
     * Exposes the `localCoreMlModels` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localCoreMlModels: List<LocalModel> = emptyList(),
    /**
     * Exposes the `localCoreMlCustomModelPath` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localCoreMlCustomModelPath: String = "",
    /**
     * Exposes the `passwordVisible` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val passwordVisible: Boolean = false,
    /**
     * Exposes the `serverUrlValidationError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val serverUrlValidationError: ValidationError? = null,
    /**
     * Exposes the `swarmUiUrlValidationError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val swarmUiUrlValidationError: ValidationError? = null,
    /**
     * Exposes the `loginValidationError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val loginValidationError: ValidationError? = null,
    /**
     * Exposes the `passwordValidationError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val passwordValidationError: ValidationError? = null,
    /**
     * Exposes the `hordeApiKeyValidationError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val hordeApiKeyValidationError: ValidationError? = null,
    /**
     * Exposes the `huggingFaceApiKeyValidationError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val huggingFaceApiKeyValidationError: ValidationError? = null,
    /**
     * Exposes the `openAiApiKeyValidationError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val openAiApiKeyValidationError: ValidationError? = null,
    /**
     * Exposes the `stabilityAiApiKeyValidationError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val stabilityAiApiKeyValidationError: ValidationError? = null,
    /**
     * Exposes the `localCustomOnnxPathValidationError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localCustomOnnxPathValidationError: ValidationError? = null,
    /**
     * Exposes the `localCustomMediaPipePathValidationError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localCustomMediaPipePathValidationError: ValidationError? = null,
) : MviState {

    val localCustomModel: Boolean
        get() = when (mode) {
            ServerSource.LOCAL_MICROSOFT_ONNX -> localOnnxCustomModel
            ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> localMediaPipeCustomModel
            else -> false
        }

    val localCustomModelPath: String
        get() = when (mode) {
            ServerSource.LOCAL_MICROSOFT_ONNX -> localOnnxCustomModelPath
            ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> localMediaPipeCustomModelPath
            ServerSource.LOCAL_APPLE_CORE_ML -> localCoreMlCustomModelPath
            else -> ""
        }

    val localModels: List<LocalModel>
        get() = when (mode) {
            ServerSource.LOCAL_MICROSOFT_ONNX -> localOnnxModels
            ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> localMediaPipeModels
            ServerSource.LOCAL_APPLE_CORE_ML -> localCoreMlModels
            else -> emptyList()
        }

    val localCustomModelPathValidationError: ValidationError?
        get() = when (mode) {
            ServerSource.LOCAL_MICROSOFT_ONNX -> localCustomOnnxPathValidationError
            ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> localCustomMediaPipePathValidationError
            else -> null
        }

    fun withCredentials(value: AuthorizationCredentials): ServerSetupState = when (value) {
        is AuthorizationCredentials.HttpBasic -> copy(
            authType = AuthType.HTTP_BASIC,
            login = value.login,
            password = value.password,
        )

        AuthorizationCredentials.None -> copy(authType = AuthType.ANONYMOUS)
    }

    fun credentialsDomain(): AuthorizationCredentials = when (authType) {
        AuthType.ANONYMOUS -> AuthorizationCredentials.None
        AuthType.HTTP_BASIC -> AuthorizationCredentials.HttpBasic(login, password)
    }

    fun withLocalCustomModelPath(value: String): ServerSetupState = when (mode) {
        ServerSource.LOCAL_MICROSOFT_ONNX -> copy(
            localOnnxCustomModelPath = value,
            localCustomOnnxPathValidationError = null,
        )

        ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> copy(
            localMediaPipeCustomModelPath = value,
            localCustomMediaPipePathValidationError = null,
        )

        ServerSource.LOCAL_APPLE_CORE_ML -> copy(localCoreMlCustomModelPath = value)

        else -> this
    }

    fun withUpdatedLocalModel(value: LocalModel): ServerSetupState = when (mode) {
        ServerSource.LOCAL_MICROSOFT_ONNX -> copy(
            localOnnxModels = localOnnxModels.withCommonNewState(value),
        )

        ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> copy(
            localMediaPipeModels = localMediaPipeModels.withCommonNewState(value),
        )

        ServerSource.LOCAL_APPLE_CORE_ML -> copy(
            localCoreMlModels = localCoreMlModels.withCommonNewState(value),
        )

        else -> this
    }

    fun withDeletedLocalModel(value: LocalModel): ServerSetupState = when (mode) {
        ServerSource.LOCAL_MICROSOFT_ONNX -> copy(
            modal = Modal.None,
            localOnnxModels = localOnnxModels.withCommonNewState(
                value.copy(
                    downloadState = DownloadState.Unknown,
                    downloaded = false,
                ),
            ),
        )

        ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> copy(
            modal = Modal.None,
            localMediaPipeModels = localMediaPipeModels.withCommonNewState(
                value.copy(
                    downloadState = DownloadState.Unknown,
                    downloaded = false,
                ),
            ),
        )

        ServerSource.LOCAL_APPLE_CORE_ML -> copy(
            modal = Modal.None,
            localCoreMlModels = localCoreMlModels.withCommonNewState(
                value.copy(
                    downloadState = DownloadState.Unknown,
                    downloaded = false,
                ),
            ),
        )

        else -> copy(modal = Modal.None)
    }

    fun withSelectedLocalModel(value: LocalModel): ServerSetupState = when (mode) {
        ServerSource.LOCAL_MICROSOFT_ONNX -> copy(
            localOnnxModels = localOnnxModels.withCommonNewState(value.copy(selected = true)),
        )

        ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> copy(
            localMediaPipeModels = localMediaPipeModels.withCommonNewState(value.copy(selected = true)),
        )

        ServerSource.LOCAL_APPLE_CORE_ML -> copy(
            localCoreMlModels = localCoreMlModels.withCommonNewState(value.copy(selected = true)),
        )

        else -> this
    }

    fun withAllowCustomModel(value: Boolean): ServerSetupState {
        fun List<LocalModel>.updateCustomModelSelection(id: String) = withCommonNewState(
            find { model -> model.id == id }?.copy(selected = value),
        )
        return when (mode) {
            ServerSource.LOCAL_MICROSOFT_ONNX -> copy(
                localOnnxCustomModel = value,
                localOnnxModels = localOnnxModels.updateCustomModelSelection(
                    id = LocalAiModel.CustomOnnx.id,
                ),
            )

            ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> copy(
                localMediaPipeCustomModel = value,
                localMediaPipeModels = localMediaPipeModels.updateCustomModelSelection(
                    id = LocalAiModel.CustomMediaPipe.id,
                ),
            )

            else -> this
        }
    }

    enum class Step {
        SOURCE,
        CONFIGURE,
    }

    enum class AuthType {
        ANONYMOUS,
        HTTP_BASIC,
    }

    enum class ValidationError {
        EmptyField,
        EmptyUrl,
        InvalidScheme,
        InvalidPort,
        InvalidUrl,
    }

    sealed interface Modal {
        data object None : Modal
        data object Communicating : Modal
        data object ConnectLocalHost : Modal
        data class SelectDownloadSource(val modelId: String) : Modal
        data class DeleteLocalModelConfirm(val model: LocalModel) : Modal
        data class Error(val message: String) : Modal
    }

    data class LocalModel(
        val id: String,
        val name: String,
        val size: String,
        val sources: List<String> = emptyList(),
        val downloaded: Boolean = false,
        val downloadState: DownloadState = DownloadState.Unknown,
        val selected: Boolean = false,
    )

    companion object {
        val remoteSetupSources = listOf(
            ServerSource.AUTOMATIC1111,
            ServerSource.SWARM_UI,
            ServerSource.HORDE,
            ServerSource.HUGGING_FACE,
            ServerSource.OPEN_AI,
            ServerSource.STABILITY_AI,
        )
    }
}

/**
 * Converts SDAI data with `toServerSetupState`.
 *
 * @param allowedModes allowed modes value consumed by the API.
 * @param huggingFaceModels hugging face models value consumed by the API.
 * @param localOnnxModels local onnx models value consumed by the API.
 * @param localMediaPipeModels local media pipe models value consumed by the API.
 * @param localCoreMlModels local core ml models value consumed by the API.
 * @param allowLocalCustomModels allow local custom models value consumed by the API.
 * @param demoModeUrl demo mode url value consumed by the API.
 * @param showBackNavArrow show back nav arrow value consumed by the API.
 * @return Result produced by `toServerSetupState`.
 * @author Dmitriy Moroz
 */
fun Configuration.toServerSetupState(
    allowedModes: List<ServerSource>,
    huggingFaceModels: List<String>,
    localOnnxModels: List<LocalAiModel> = emptyList(),
    localMediaPipeModels: List<LocalAiModel> = emptyList(),
    localCoreMlModels: List<LocalAiModel> = emptyList(),
    allowLocalCustomModels: Boolean = true,
    demoModeUrl: String = "",
    showBackNavArrow: Boolean = false,
): ServerSetupState {
    val safeMode = source.takeIf(allowedModes::contains) ?: ServerSource.AUTOMATIC1111
    val supportedHuggingFaceAliases = HuggingFaceModel.supportedHfInferenceTextToImageAliases
    val safeHuggingFaceModels = (
        huggingFaceModels
            .filter(supportedHuggingFaceAliases::contains)
            .ifEmpty { HuggingFaceModel.supportedHfInferenceTextToImageModels.map(HuggingFaceModel::alias) }
            + HuggingFaceModel.default.alias
    )
        .distinct()
    val safeHuggingFaceModel = huggingFaceModel
        .takeIf(safeHuggingFaceModels::contains)
        ?: HuggingFaceModel.default.alias
    return ServerSetupState(
        loadingConfiguration = false,
        showBackNavArrow = showBackNavArrow,
        mode = safeMode,
        allowedModes = allowedModes,
        allowLocalCustomModels = allowLocalCustomModels,
        serverUrl = serverUrl,
        swarmUiUrl = swarmUiUrl,
        hordeApiKey = hordeApiKey,
        hordeDefaultApiKey = hordeApiKey == HORDE_DEFAULT_API_KEY,
        huggingFaceApiKey = huggingFaceApiKey,
        huggingFaceModel = safeHuggingFaceModel,
        huggingFaceModels = safeHuggingFaceModels,
        localOnnxModels = localOnnxModels.mapToCommonSetupModels(),
        localOnnxCustomModel = localOnnxModels.hasCommonSelectedCustomModel(LocalAiModel.CustomOnnx.id),
        localOnnxCustomModelPath = localOnnxModelPath,
        localMediaPipeModels = localMediaPipeModels.mapToCommonSetupModels(),
        localMediaPipeCustomModel = localMediaPipeModels.hasCommonSelectedCustomModel(LocalAiModel.CustomMediaPipe.id),
        localMediaPipeCustomModelPath = localMediaPipeModelPath,
        localCoreMlModels = localCoreMlModels.mapToCommonSetupModels(),
        localCoreMlCustomModelPath = localCoreMlModelPath,
        openAiApiKey = openAiApiKey,
        stabilityAiApiKey = stabilityAiApiKey,
        demoMode = demoMode,
        demoModeUrl = demoModeUrl,
    ).withCredentials(
        if (demoMode) AuthorizationCredentials.None else authCredentials,
    )
}

/**
 * Exposes the `HORDE_DEFAULT_API_KEY` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
const val HORDE_DEFAULT_API_KEY = "0000000000"

/**
 * Converts SDAI data with `mapToCommonSetupModels`.
 *
 * @return Result produced by `mapToCommonSetupModels`.
 * @author Dmitriy Moroz
 */
private fun List<LocalAiModel>.mapToCommonSetupModels(): List<ServerSetupState.LocalModel> =
    map { model ->
        ServerSetupState.LocalModel(
            id = model.id,
            name = model.name,
            size = model.size,
            sources = model.sources,
            downloaded = model.downloaded,
            selected = model.selected,
        )
    }

/**
 * Executes the `hasCommonSelectedCustomModel` step in the SDAI presentation layer.
 *
 * @param id identifier of the target entity.
 * @return Result produced by `hasCommonSelectedCustomModel`.
 * @author Dmitriy Moroz
 */
private fun List<LocalAiModel>.hasCommonSelectedCustomModel(id: String): Boolean =
    any { model -> model.selected && model.id == id }

/**
 * Executes the `function` step in the SDAI presentation layer.
 *
 * @param model model value consumed by the API.
 * @return Result produced by `function`.
 * @author Dmitriy Moroz
 */
private fun List<ServerSetupState.LocalModel>.withCommonNewState(
    model: ServerSetupState.LocalModel?,
): List<ServerSetupState.LocalModel> =
    map { item ->
        if (model == null) {
            item
        } else if (item.id == model.id) {
            model
        } else if (model.selected) {
            item.copy(selected = false)
        } else {
            item
        }
    }
