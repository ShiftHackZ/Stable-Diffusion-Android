package com.shifthackz.aisdv1.presentation.screen.setup

import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.ui.MviEffect
import com.shifthackz.aisdv1.core.ui.MviState
import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.presentation.utils.Constants

sealed interface ServerSetupEffect : MviEffect {
    object CompleteSetup : ServerSetupEffect
}

data class ServerSetupState(
    val showBackNavArrow: Boolean = false,
    val mode: Mode = Mode.OWN_SERVER,
    val allowedModes: List<Mode> = listOf(Mode.OWN_SERVER),
    val originalMode: Mode = Mode.OWN_SERVER,
    val screenDialog: Dialog = Dialog.None,
    val serverUrl: String = "",
    val originalSeverUrl: String = "",
    val hordeApiKey: String = "",
    val originalHordeApiKey: String = "",
    val hordeDefaultApiKey: Boolean = false,
    val demoMode: Boolean = false,
    val originalDemoMode: Boolean = false,
    val authType: AuthType = AuthType.ANONYMOUS,
    val originalAuthType: AuthType = AuthType.ANONYMOUS,
    val login: String = "",
    val originalLogin: String = "",
    val password: String = "",
    val originalPassword: String = "",
    val passwordVisible: Boolean = false,
    val serverUrlValidationError: UiText? = null,
    val loginValidationError: UiText? = null,
    val passwordValidationError: UiText? = null,
    val hordeApiKeyValidationError: UiText? = null,
) : MviState {

    fun withSource(value: ServerSource) = this.copy(
        originalMode = Mode.fromSource(value),
        mode = Mode.fromSource(value),
    )

    fun withDemoMode(value: Boolean) = this.copy(
        originalDemoMode = value,
        demoMode = value,
    )

    fun withServerUrl(value: String) = this.copy(
        serverUrl = value,
        originalSeverUrl = value,
    )

    fun withHordeApiKey(value: String) = this.copy(
        hordeApiKey = value,
        originalHordeApiKey = value,
        hordeDefaultApiKey = value == Constants.HORDE_DEFAULT_API_KEY,
    )

    fun withAuthType(value: AuthType) = this.copy(
        authType = value,
        originalAuthType = value,
    )

    fun withCredentials(value: AuthorizationCredentials) = when (value) {
        is AuthorizationCredentials.HttpBasic -> this.copy(
            login = value.login,
            originalLogin = value.login,
            password = value.password,
            originalPassword = value.password
        )
        AuthorizationCredentials.None -> this
    }

    sealed interface Dialog {
        object None : Dialog
        object Communicating : Dialog
        data class Error(val error: UiText) : Dialog
    }

    enum class Mode {
        OWN_SERVER,
        SD_AI_CLOUD,
        HORDE;

        fun toSource() = when (this) {
            SD_AI_CLOUD -> ServerSource.SDAI
            OWN_SERVER -> ServerSource.CUSTOM
            HORDE -> ServerSource.HORDE
        }

        companion object {
            fun fromSource(source: ServerSource) = when (source) {
                ServerSource.CUSTOM -> OWN_SERVER
                ServerSource.SDAI -> SD_AI_CLOUD
                ServerSource.HORDE -> HORDE
            }
        }
    }

    enum class AuthType {
        ANONYMOUS,
        HTTP_BASIC;
    }
}

enum class ServerSetupLaunchSource(val key: Int) {
    SPLASH(0),
    SETTINGS(1);

    companion object {
        fun fromKey(key: Int) = values().firstOrNull { it.key == key } ?: SPLASH
    }
}


val BuildType.allowedModes: List<ServerSetupState.Mode>
    get() = when (this) {
        BuildType.FOSS -> listOf(ServerSetupState.Mode.OWN_SERVER, ServerSetupState.Mode.HORDE)
        BuildType.GOOGLE_PLAY -> ServerSetupState.Mode.values().toList()
    }

val Configuration.authType: ServerSetupState.AuthType
    get() {
        val noCredentials = ServerSetupState.AuthType.ANONYMOUS
        if (this.demoMode) return noCredentials
        if (this.source != ServerSource.CUSTOM) return noCredentials
        return when (this.authCredentials.key) {
            AuthorizationCredentials.Key.NONE -> noCredentials
            AuthorizationCredentials.Key.HTTP_BASIC -> ServerSetupState.AuthType.HTTP_BASIC
        }
    }

fun ServerSetupState.credentialsDomain(original: Boolean = false): AuthorizationCredentials {
    return when (this.authType) {
        ServerSetupState.AuthType.ANONYMOUS -> AuthorizationCredentials.None
        ServerSetupState.AuthType.HTTP_BASIC -> {
            if (original) {
                AuthorizationCredentials.HttpBasic(originalLogin, originalPassword)
            } else {
                AuthorizationCredentials.HttpBasic(login, password)
            }
        }
    }
}
