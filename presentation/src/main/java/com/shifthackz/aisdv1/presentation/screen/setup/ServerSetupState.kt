package com.shifthackz.aisdv1.presentation.screen.setup

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.screen.setup.mappers.withNewState
import com.shifthackz.aisdv1.presentation.utils.Constants
import com.shifthackz.android.core.mvi.MviState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Immutable
data class ServerSetupState(
    val showBackNavArrow: Boolean = false,
    val onBoardingDemo: Boolean = false,
    val step: Step = Step.SOURCE,
    val mode: ServerSource = ServerSource.AUTOMATIC1111,
    val allowedModes: List<ServerSource> = ServerSource.entries,
    val screenModal: Modal = Modal.None,
    val serverUrl: String = "",
    val swarmUiUrl: String = "",
    val hordeApiKey: String = "",
    val huggingFaceApiKey: String = "",
    val openAiApiKey: String = "",
    val stabilityAiApiKey: String = "",
    val hordeDefaultApiKey: Boolean = false,
    val demoMode: Boolean = false,
    val authType: AuthType = AuthType.ANONYMOUS,
    val login: String = "",
    val password: String = "",
    val huggingFaceModels: List<String> = emptyList(),
    val huggingFaceModel: String = "",
    val localOnnxModels: List<LocalModel> = emptyList(),
    val localOnnxCustomModel: Boolean = false,
    val localOnnxCustomModelPath: String = "",
    val localMediaPipeModels: List<LocalModel> = emptyList(),
    val localMediaPipeCustomModel: Boolean = false,
    val localMediaPipeCustomModelPath: String = "",
    val passwordVisible: Boolean = false,
    val serverUrlValidationError: UiText? = null,
    val swarmUiUrlValidationError: UiText? = null,
    val loginValidationError: UiText? = null,
    val passwordValidationError: UiText? = null,
    val hordeApiKeyValidationError: UiText? = null,
    val huggingFaceApiKeyValidationError: UiText? = null,
    val openAiApiKeyValidationError: UiText? = null,
    val stabilityAiApiKeyValidationError: UiText? = null,
    val localCustomOnnxPathValidationError: UiText? = null,
    val localCustomMediaPipePathValidationError: UiText? = null,
) : MviState, KoinComponent {

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

    val localCustomModelPathValidationError: UiText?
        get() = if (mode == ServerSource.LOCAL_MICROSOFT_ONNX) {
            localCustomOnnxPathValidationError
        } else {
            localCustomMediaPipePathValidationError
        }

    val demoModeUrl: String
        get() {
            val linksProvider: LinksProvider by inject()
            return linksProvider.demoModeUrl
        }

    fun withHordeApiKey(value: String) = this.copy(
        hordeApiKey = value,
        hordeDefaultApiKey = value == Constants.HORDE_DEFAULT_API_KEY,
    )

    fun withCredentials(value: AuthorizationCredentials) = when (value) {
        is AuthorizationCredentials.HttpBasic -> copy(
            login = value.login,
            password = value.password,
        )

        AuthorizationCredentials.None -> this
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
            localOnnxModels = localOnnxModels.withNewState(value)
        )
        ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> copy(
            localMediaPipeModels = localMediaPipeModels.withNewState(value)
        )
        else -> this
    }

    fun withDeletedLocalModel(value: LocalModel): ServerSetupState = when (mode) {
        ServerSource.LOCAL_MICROSOFT_ONNX -> copy(
            screenModal = Modal.None,
            localOnnxModels = localOnnxModels.withNewState(
                value.copy(
                    downloadState = DownloadState.Unknown,
                    downloaded = false,
                ),
            )
        )

        ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> copy(
            screenModal = Modal.None,
            localMediaPipeModels = localMediaPipeModels.withNewState(
                value.copy(
                    downloadState = DownloadState.Unknown,
                    downloaded = false,
                ),
            )
        )

        else -> copy(screenModal = Modal.None)
    }

    fun withSelectedLocalModel(value: LocalModel): ServerSetupState = when (mode) {
        ServerSource.LOCAL_MICROSOFT_ONNX -> copy(
            localOnnxModels = localOnnxModels.withNewState(
                value.copy(selected = true),
            ),
        )

        ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> copy(
            localMediaPipeModels = localMediaPipeModels.withNewState(
                value.copy(selected = true),
            ),
        )

        else -> this
    }

    fun withAllowCustomModel(value: Boolean): ServerSetupState {
        fun List<LocalModel>.updateCustomModelSelection(id: String) = withNewState(
            find { m -> m.id == id }?.copy(selected = value)
        )
        return when (mode) {
            ServerSource.LOCAL_MICROSOFT_ONNX -> this.copy(
                localOnnxCustomModel = value,
                localOnnxModels = localOnnxModels.updateCustomModelSelection(
                    id = LocalAiModel.CustomOnnx.id,
                ),
            )

            ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> this.copy(
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
        CONFIGURE;
    }

    enum class AuthType {
        ANONYMOUS,
        HTTP_BASIC;
    }

    data class LocalModel(
        val id: String,
        val name: String,
        val size: String,
        val downloaded: Boolean = false,
        val downloadState: DownloadState = DownloadState.Unknown,
        val selected: Boolean = false,
    )
}

val Configuration.authType: ServerSetupState.AuthType
    get() {
        val noCredentials = ServerSetupState.AuthType.ANONYMOUS
        if (this.demoMode) return noCredentials
        if (this.source != ServerSource.AUTOMATIC1111) return noCredentials
        return when (this.authCredentials.key) {
            AuthorizationCredentials.Key.NONE -> noCredentials
            AuthorizationCredentials.Key.HTTP_BASIC -> ServerSetupState.AuthType.HTTP_BASIC
        }
    }

fun ServerSetupState.credentialsDomain(): AuthorizationCredentials {
    return when (this.authType) {
        ServerSetupState.AuthType.ANONYMOUS -> AuthorizationCredentials.None
        ServerSetupState.AuthType.HTTP_BASIC -> AuthorizationCredentials.HttpBasic(login, password)
    }
}
