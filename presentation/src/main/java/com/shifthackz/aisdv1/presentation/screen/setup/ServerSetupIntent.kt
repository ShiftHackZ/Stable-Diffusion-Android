package com.shifthackz.aisdv1.presentation.screen.setup

import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.android.core.mvi.MviIntent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed interface ServerSetupIntent : MviIntent {

    data class UpdateServerMode(val mode: ServerSource) : ServerSetupIntent

    data class UpdateServerUrl(val url: String) : ServerSetupIntent

    data class UpdateSwarmUiUrl(val url: String) : ServerSetupIntent

    data class UpdateAuthType(val type: ServerSetupState.AuthType) : ServerSetupIntent

    data class UpdateLogin(val login: String) : ServerSetupIntent

    data class UpdatePassword(val password: String) : ServerSetupIntent

    data class UpdatePasswordVisibility(val visible: Boolean): ServerSetupIntent

    data class UpdateHordeApiKey(val key: String) : ServerSetupIntent

    data class UpdateOpenAiApiKey(val key: String) : ServerSetupIntent

    data class UpdateStabilityAiApiKey(val key: String) : ServerSetupIntent

    data class UpdateHuggingFaceApiKey(val key: String) : ServerSetupIntent

    data class UpdateHuggingFaceModel(val model: String): ServerSetupIntent

    data class UpdateDemoMode(val value: Boolean) : ServerSetupIntent

    data class UpdateHordeDefaultApiKey(val value: Boolean) : ServerSetupIntent

    data class SelectLocalModelPath(val value: String) : ServerSetupIntent

    data class SelectLocalModel(val model: ServerSetupState.LocalModel) : ServerSetupIntent

    data class AllowLocalCustomModel(val allow: Boolean) : ServerSetupIntent

    data object MainButtonClick : ServerSetupIntent

    data object DismissDialog : ServerSetupIntent

    data object NavigateBack : ServerSetupIntent

    data object LaunchManageStoragePermission : ServerSetupIntent

    data object ConnectToLocalHost : ServerSetupIntent

    sealed class LaunchUrl : ServerSetupIntent, KoinComponent {

        protected val linksProvider: LinksProvider by inject()
        abstract val url: String

        data object A1111Instructions : LaunchUrl() {
            override val url: String
                get() = linksProvider.setupInstructionsUrl
        }

        data object SwarmUiInstructions : LaunchUrl() {
            override val url: String
                get() = linksProvider.swarmUiInfoUrl
        }

        data object HordeInfo : LaunchUrl() {
            override val url: String
                get() = linksProvider.hordeUrl
        }

        data object HordeSignUp : LaunchUrl() {
            override val url: String
                get() = linksProvider.hordeSignUpUrl
        }

        data object HuggingFaceInfo : LaunchUrl() {
            override val url: String
                get() = linksProvider.huggingFaceUrl
        }

        data object OpenAiInfo : LaunchUrl() {
            override val url: String
                get() = linksProvider.openAiInfoUrl
        }

        data object StabilityAiInfo : LaunchUrl() {
            override val url: String
                get() = linksProvider.stabilityAiInfoUrl
        }
    }

    sealed interface LocalModel : ServerSetupIntent {

        val model: ServerSetupState.LocalModel

        data class ClickReduce(override val model: ServerSetupState.LocalModel) : LocalModel

        data class DeleteConfirm(override val model: ServerSetupState.LocalModel) : LocalModel
    }
}
