package com.shifthackz.aisdv1.presentation.screen.setup

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.utils.Constants
import com.shifthackz.android.core.mvi.MviState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Immutable
data class ServerSetupState(
    val showBackNavArrow: Boolean = false,
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
    val localModels: List<LocalModel> = emptyList(),
    val localCustomModel: Boolean = false,
    val localCustomModelPath: String = "",
    val passwordVisible: Boolean = false,
    val serverUrlValidationError: UiText? = null,
    val swarmUiUrlValidationError: UiText? = null,
    val loginValidationError: UiText? = null,
    val passwordValidationError: UiText? = null,
    val hordeApiKeyValidationError: UiText? = null,
    val huggingFaceApiKeyValidationError: UiText? = null,
    val openAiApiKeyValidationError: UiText? = null,
    val stabilityAiApiKeyValidationError: UiText? = null,
    val localCustomModelPathValidationError: UiText? = null,
) : MviState, KoinComponent {

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
        is AuthorizationCredentials.HttpBasic -> this.copy(
            login = value.login,
            password = value.password,
        )
        AuthorizationCredentials.None -> this
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
