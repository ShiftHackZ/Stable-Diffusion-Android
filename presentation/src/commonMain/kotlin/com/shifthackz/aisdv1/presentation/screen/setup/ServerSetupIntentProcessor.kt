package com.shifthackz.aisdv1.presentation.screen.setup

import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.presentation.navigation.router.ServerSetupRouter

internal class ServerSetupIntentProcessor(
    private val router: ServerSetupRouter,
    private val linksProvider: LinksProvider,
    private val currentState: () -> ServerSetupState,
    private val updateState: (((ServerSetupState) -> ServerSetupState) -> Unit),
    private val emitEffect: (ServerSetupEffect) -> Unit,
    private val localModelDownloadClickReducer: (ServerSetupState.LocalModel) -> Unit,
    private val deleteLocalModel: (String) -> Unit,
    private val download: (String, String) -> Unit,
    private val validateAndConnectToServer: () -> Unit,
    private val connectToServer: () -> Unit,
) {

    fun process(intent: ServerSetupIntent) {
        when (intent) {
            is ServerSetupIntent.AllowLocalCustomModel -> updateState { state ->
                state.withAllowCustomModel(intent.allow)
            }

            is ServerSetupIntent.SelectLocalModel -> updateState { state ->
                state.withSelectedLocalModel(intent.model)
            }

            is ServerSetupIntent.SelectLocalModelPath -> updateState { state ->
                state.withLocalCustomModelPath(intent.value)
            }

            ServerSetupIntent.LaunchManageStoragePermission -> {
                emitEffect(ServerSetupEffect.LaunchManageStoragePermission)
            }

            is ServerSetupIntent.LocalModel.ClickReduce -> localModelDownloadClickReducer(intent.model)

            is ServerSetupIntent.LocalModel.DeleteConfirm -> updateState {
                deleteLocalModel(intent.model.id)
                it.withDeletedLocalModel(intent.model)
            }

            is ServerSetupIntent.LocalModel.DownloadConfirm -> with(intent) {
                download(modelId, url)
            }

            is ServerSetupIntent.UpdateServerMode -> updateState {
                it.copy(mode = intent.mode)
            }

            is ServerSetupIntent.UpdateServerUrl -> updateState {
                it.copy(serverUrl = intent.url, serverUrlValidationError = null)
            }

            is ServerSetupIntent.UpdateSwarmUiUrl -> updateState {
                it.copy(swarmUiUrl = intent.url, swarmUiUrlValidationError = null)
            }

            is ServerSetupIntent.UpdateAuthType -> updateState {
                it.copy(authType = intent.type)
            }

            is ServerSetupIntent.UpdateLogin -> updateState {
                it.copy(login = intent.login, loginValidationError = null)
            }

            is ServerSetupIntent.UpdatePassword -> updateState {
                it.copy(password = intent.password, passwordValidationError = null)
            }

            is ServerSetupIntent.UpdatePasswordVisibility -> updateState {
                it.copy(passwordVisible = !intent.visible)
            }

            is ServerSetupIntent.UpdateHordeApiKey -> updateState {
                it.copy(
                    hordeApiKey = intent.key,
                    hordeDefaultApiKey = intent.key == HORDE_DEFAULT_API_KEY,
                    hordeApiKeyValidationError = null,
                )
            }

            is ServerSetupIntent.UpdateOpenAiApiKey -> updateState {
                it.copy(openAiApiKey = intent.key, openAiApiKeyValidationError = null)
            }

            is ServerSetupIntent.UpdateStabilityAiApiKey -> updateState {
                it.copy(stabilityAiApiKey = intent.key, stabilityAiApiKeyValidationError = null)
            }

            is ServerSetupIntent.UpdateHuggingFaceApiKey -> updateState {
                it.copy(huggingFaceApiKey = intent.key, huggingFaceApiKeyValidationError = null)
            }

            is ServerSetupIntent.UpdateHuggingFaceModel -> updateState {
                it.copy(huggingFaceModel = intent.model)
            }

            is ServerSetupIntent.UpdateDemoMode -> updateState {
                it.copy(
                    demoMode = intent.value,
                    authType = if (intent.value) {
                        ServerSetupState.AuthType.ANONYMOUS
                    } else {
                        it.authType
                    },
                    serverUrlValidationError = null,
                )
            }

            is ServerSetupIntent.UpdateHordeDefaultApiKey -> updateState {
                it.copy(
                    hordeDefaultApiKey = intent.value,
                    hordeApiKey = if (intent.value) HORDE_DEFAULT_API_KEY else "",
                    hordeApiKeyValidationError = null,
                )
            }

            is ServerSetupIntent.LaunchUrl -> {
                emitEffect(ServerSetupEffect.OpenUrl(intent.link.url(linksProvider)))
            }

            ServerSetupIntent.MainButtonClick -> when (currentState().step) {
                ServerSetupState.Step.SOURCE -> updateState {
                    it.copy(step = ServerSetupState.Step.CONFIGURE)
                }

                ServerSetupState.Step.CONFIGURE -> validateAndConnectToServer()
            }

            ServerSetupIntent.DismissDialog -> updateState {
                it.copy(modal = ServerSetupState.Modal.None)
            }

            ServerSetupIntent.NavigateBack -> if (currentState().step == ServerSetupState.Step.SOURCE) {
                router.navigateBack()
            } else {
                emitEffect(ServerSetupEffect.HideKeyboard)
                updateState {
                    it.copy(step = ServerSetupState.Step.SOURCE)
                }
            }

            ServerSetupIntent.ConnectToLocalHost -> connectToServer()
        }
    }
}
