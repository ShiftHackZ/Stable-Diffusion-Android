package com.shifthackz.aisdv1.presentation.screen.setup

import com.shifthackz.aisdv1.core.mvi.MviIntent
import com.shifthackz.aisdv1.domain.entity.ServerSource

sealed interface ServerSetupIntent : MviIntent {
    data class UpdateServerMode(val mode: ServerSource) : ServerSetupIntent
    data class UpdateServerUrl(val url: String) : ServerSetupIntent
    data class UpdateSwarmUiUrl(val url: String) : ServerSetupIntent
    data class UpdateAuthType(val type: ServerSetupState.AuthType) : ServerSetupIntent
    data class UpdateLogin(val login: String) : ServerSetupIntent
    data class UpdatePassword(val password: String) : ServerSetupIntent
    data class UpdatePasswordVisibility(val visible: Boolean) : ServerSetupIntent
    data class UpdateHordeApiKey(val key: String) : ServerSetupIntent
    data class UpdateOpenAiApiKey(val key: String) : ServerSetupIntent
    data class UpdateStabilityAiApiKey(val key: String) : ServerSetupIntent
    data class UpdateHuggingFaceApiKey(val key: String) : ServerSetupIntent
    data class UpdateHuggingFaceModel(val model: String) : ServerSetupIntent
    data class UpdateDemoMode(val value: Boolean) : ServerSetupIntent
    data class UpdateHordeDefaultApiKey(val value: Boolean) : ServerSetupIntent
    data class SelectLocalModelPath(val value: String) : ServerSetupIntent
    data class SelectLocalModel(val model: ServerSetupState.LocalModel) : ServerSetupIntent
    data class AllowLocalCustomModel(val allow: Boolean) : ServerSetupIntent
    data class LaunchUrl(val link: ServerSetupLink) : ServerSetupIntent
    data object MainButtonClick : ServerSetupIntent
    data object DismissDialog : ServerSetupIntent
    data object NavigateBack : ServerSetupIntent
    data object LaunchManageStoragePermission : ServerSetupIntent
    data object ConnectToLocalHost : ServerSetupIntent

    sealed interface LocalModel : ServerSetupIntent {
        data class ClickReduce(val model: ServerSetupState.LocalModel) : LocalModel
        data class DownloadConfirm(val modelId: String, val url: String) : LocalModel
        data class DeleteConfirm(val model: ServerSetupState.LocalModel) : LocalModel
    }
}

enum class ServerSetupLink {
    A1111Instructions,
    SwarmUiInstructions,
    HordeInfo,
    HordeSignUp,
    HuggingFaceInfo,
    OpenAiInfo,
    StabilityAiInfo,
}
