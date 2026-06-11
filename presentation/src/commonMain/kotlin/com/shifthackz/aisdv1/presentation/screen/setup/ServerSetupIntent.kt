package com.shifthackz.aisdv1.presentation.screen.setup

import com.shifthackz.aisdv1.core.mvi.MviIntent
import com.shifthackz.aisdv1.domain.entity.ServerSource

/**
 * Defines the `ServerSetupIntent` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface ServerSetupIntent : MviIntent {
    /**
     * Carries `UpdateServerMode` data through the SDAI presentation layer.
     *
     * @param mode mode value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateServerMode(val mode: ServerSource) : ServerSetupIntent
    /**
     * Carries `UpdateServerUrl` data through the SDAI presentation layer.
     *
     * @param url remote URL used by the operation.
     * @author Dmitriy Moroz
     */
    data class UpdateServerUrl(val url: String) : ServerSetupIntent
    /**
     * Carries `UpdateSwarmUiUrl` data through the SDAI presentation layer.
     *
     * @param url remote URL used by the operation.
     * @author Dmitriy Moroz
     */
    data class UpdateSwarmUiUrl(val url: String) : ServerSetupIntent
    /**
     * Carries `UpdateAuthType` data through the SDAI presentation layer.
     *
     * @param type type value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateAuthType(val type: ServerSetupState.AuthType) : ServerSetupIntent
    /**
     * Carries `UpdateLogin` data through the SDAI presentation layer.
     *
     * @param login login value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateLogin(val login: String) : ServerSetupIntent
    /**
     * Carries `UpdatePassword` data through the SDAI presentation layer.
     *
     * @param password password value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdatePassword(val password: String) : ServerSetupIntent
    /**
     * Carries `UpdatePasswordVisibility` data through the SDAI presentation layer.
     *
     * @param visible visible value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdatePasswordVisibility(val visible: Boolean) : ServerSetupIntent
    /**
     * Carries `UpdateHordeApiKey` data through the SDAI presentation layer.
     *
     * @param key key value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateHordeApiKey(val key: String) : ServerSetupIntent
    /**
     * Carries `UpdateOpenAiApiKey` data through the SDAI presentation layer.
     *
     * @param key key value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateOpenAiApiKey(val key: String) : ServerSetupIntent
    /**
     * Carries `UpdateStabilityAiApiKey` data through the SDAI presentation layer.
     *
     * @param key key value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateStabilityAiApiKey(val key: String) : ServerSetupIntent
    /**
     * Carries `UpdateFalAiApiKey` data through the SDAI presentation layer.
     *
     * @param key key value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateFalAiApiKey(val key: String) : ServerSetupIntent
    /**
     * Carries `UpdateHuggingFaceApiKey` data through the SDAI presentation layer.
     *
     * @param key key value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateHuggingFaceApiKey(val key: String) : ServerSetupIntent
    /**
     * Carries `UpdateHuggingFaceModel` data through the SDAI presentation layer.
     *
     * @param model model value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateHuggingFaceModel(val model: String) : ServerSetupIntent
    /**
     * Carries `UpdateDemoMode` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateDemoMode(val value: Boolean) : ServerSetupIntent
    /**
     * Carries `UpdateHordeDefaultApiKey` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateHordeDefaultApiKey(val value: Boolean) : ServerSetupIntent
    /**
     * Carries `SelectLocalModelPath` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class SelectLocalModelPath(val value: String) : ServerSetupIntent
    /**
     * Carries `SelectLocalModel` data through the SDAI presentation layer.
     *
     * @param model model value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class SelectLocalModel(val model: ServerSetupState.LocalModel) : ServerSetupIntent
    /**
     * Carries `AllowLocalCustomModel` data through the SDAI presentation layer.
     *
     * @param allow allow value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class AllowLocalCustomModel(val allow: Boolean) : ServerSetupIntent
    /**
     * Carries `LaunchUrl` data through the SDAI presentation layer.
     *
     * @param link link value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class LaunchUrl(val link: ServerSetupLink) : ServerSetupIntent
    /**
     * Provides the `MainButtonClick` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object MainButtonClick : ServerSetupIntent
    /**
     * Provides the `DismissDialog` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object DismissDialog : ServerSetupIntent
    /**
     * Provides the `NavigateBack` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object NavigateBack : ServerSetupIntent
    /**
     * Provides the `LaunchManageStoragePermission` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object LaunchManageStoragePermission : ServerSetupIntent
    /**
     * Provides the `ConnectToLocalHost` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object ConnectToLocalHost : ServerSetupIntent

    /**
     * Defines the `LocalModel` contract for the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    sealed interface LocalModel : ServerSetupIntent {
        /**
         * Carries `ClickReduce` data through the SDAI presentation layer.
         *
         * @param model model value consumed by the API.
         * @author Dmitriy Moroz
         */
        data class ClickReduce(val model: ServerSetupState.LocalModel) : LocalModel
        /**
         * Carries `DownloadConfirm` data through the SDAI presentation layer.
         *
         * @param modelId model id value consumed by the API.
         * @param url remote URL used by the operation.
         * @author Dmitriy Moroz
         */
        data class DownloadConfirm(val modelId: String, val url: String) : LocalModel
        /**
         * Carries `DeleteConfirm` data through the SDAI presentation layer.
         *
         * @param model model value consumed by the API.
         * @author Dmitriy Moroz
         */
        data class DeleteConfirm(val model: ServerSetupState.LocalModel) : LocalModel
    }
}

/**
 * Coordinates `ServerSetupLink` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
enum class ServerSetupLink {
    A1111Instructions,
    SwarmUiInstructions,
    HordeInfo,
    HordeSignUp,
    HuggingFaceInfo,
    OpenAiInfo,
    StabilityAiInfo,
    FalAiInfo,
}
