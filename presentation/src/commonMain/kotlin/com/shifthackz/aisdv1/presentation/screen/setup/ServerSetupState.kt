package com.shifthackz.aisdv1.presentation.screen.setup

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

@Immutable
data class ServerSetupState(
    val showBackNavArrow: Boolean = false,
    val step: Step = Step.SOURCE,
    val mode: ServerSource = ServerSource.AUTOMATIC1111,
    val allowedModes: List<ServerSource> = remoteSetupSources,
    val allowLocalCustomModels: Boolean = true,
    val modal: Modal = Modal.None,
    val loadingConfiguration: Boolean = true,
    val serverUrl: String = "",
    val swarmUiUrl: String = "",
    val hordeApiKey: String = "",
    val huggingFaceApiKey: String = "",
    val openAiApiKey: String = "",
    val stabilityAiApiKey: String = "",
    val hordeDefaultApiKey: Boolean = false,
    val demoMode: Boolean = false,
    val demoModeUrl: String = "",
    val authType: AuthType = AuthType.ANONYMOUS,
    val login: String = "",
    val password: String = "",
    val huggingFaceModels: List<String> = emptyList(),
    val huggingFaceModel: String = HuggingFaceModel.default.alias,
    val localOnnxModels: List<LocalModel> = emptyList(),
    val localOnnxCustomModel: Boolean = false,
    val localOnnxCustomModelPath: String = "",
    val localMediaPipeModels: List<LocalModel> = emptyList(),
    val localMediaPipeCustomModel: Boolean = false,
    val localMediaPipeCustomModelPath: String = "",
    val passwordVisible: Boolean = false,
    val serverUrlValidationError: ValidationError? = null,
    val swarmUiUrlValidationError: ValidationError? = null,
    val loginValidationError: ValidationError? = null,
    val passwordValidationError: ValidationError? = null,
    val hordeApiKeyValidationError: ValidationError? = null,
    val huggingFaceApiKeyValidationError: ValidationError? = null,
    val openAiApiKeyValidationError: ValidationError? = null,
    val stabilityAiApiKeyValidationError: ValidationError? = null,
    val localCustomOnnxPathValidationError: ValidationError? = null,
    val localCustomMediaPipePathValidationError: ValidationError? = null,
) : MviState {

    val localCustomModel: Boolean
        get() = if (mode == ServerSource.LOCAL_MICROSOFT_ONNX) {
            localOnnxCustomModel
        } else {
            localMediaPipeCustomModel
        }

    val localCustomModelPath: String
        get() = if (mode == ServerSource.LOCAL_MICROSOFT_ONNX) {
            localOnnxCustomModelPath
        } else {
            localMediaPipeCustomModelPath
        }

    val localModels: List<LocalModel>
        get() = if (mode == ServerSource.LOCAL_MICROSOFT_ONNX) {
            localOnnxModels
        } else {
            localMediaPipeModels
        }

    val localCustomModelPathValidationError: ValidationError?
        get() = if (mode == ServerSource.LOCAL_MICROSOFT_ONNX) {
            localCustomOnnxPathValidationError
        } else {
            localCustomMediaPipePathValidationError
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

        else -> this
    }

    fun withUpdatedLocalModel(value: LocalModel): ServerSetupState = when (mode) {
        ServerSource.LOCAL_MICROSOFT_ONNX -> copy(
            localOnnxModels = localOnnxModels.withCommonNewState(value),
        )

        ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> copy(
            localMediaPipeModels = localMediaPipeModels.withCommonNewState(value),
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

        else -> copy(modal = Modal.None)
    }

    fun withSelectedLocalModel(value: LocalModel): ServerSetupState = when (mode) {
        ServerSource.LOCAL_MICROSOFT_ONNX -> copy(
            localOnnxModels = localOnnxModels.withCommonNewState(value.copy(selected = true)),
        )

        ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> copy(
            localMediaPipeModels = localMediaPipeModels.withCommonNewState(value.copy(selected = true)),
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

fun Configuration.toServerSetupState(
    allowedModes: List<ServerSource>,
    huggingFaceModels: List<String>,
    localOnnxModels: List<LocalAiModel> = emptyList(),
    localMediaPipeModels: List<LocalAiModel> = emptyList(),
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
        openAiApiKey = openAiApiKey,
        stabilityAiApiKey = stabilityAiApiKey,
        demoMode = demoMode,
        demoModeUrl = demoModeUrl,
    ).withCredentials(
        if (demoMode) AuthorizationCredentials.None else authCredentials,
    )
}

const val HORDE_DEFAULT_API_KEY = "0000000000"

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

private fun List<LocalAiModel>.hasCommonSelectedCustomModel(id: String): Boolean =
    any { model -> model.selected && model.id == id }

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
