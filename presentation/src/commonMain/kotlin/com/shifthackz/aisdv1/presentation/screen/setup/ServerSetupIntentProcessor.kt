package com.shifthackz.aisdv1.presentation.screen.setup

import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.presentation.navigation.router.ServerSetupRouter

/**
 * Coordinates `ServerSetupIntentProcessor` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal class ServerSetupIntentProcessor(
    /**
     * Exposes the `router` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val router: ServerSetupRouter,
    /**
     * Exposes the `linksProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val linksProvider: LinksProvider,
    /**
     * Exposes the `currentState` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val currentState: () -> ServerSetupState,
    /**
     * Exposes the `updateState` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val updateState: (((ServerSetupState) -> ServerSetupState) -> Unit),
    /**
     * Exposes the `emitEffect` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val emitEffect: (ServerSetupEffect) -> Unit,
    /**
     * Exposes the `localModelDownloadClickReducer` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val localModelDownloadClickReducer: (ServerSetupState.LocalModel) -> Unit,
    /**
     * Exposes the `deleteLocalModel` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val deleteLocalModel: (String) -> Unit,
    /**
     * Exposes the `download` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val download: (String, String) -> Unit,
    /**
     * Exposes the `validateAndConnectToServer` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val validateAndConnectToServer: () -> Unit,
    /**
     * Exposes the `connectToServer` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val connectToServer: () -> Unit,
) {

    /**
     * Executes the `process` step in the SDAI presentation layer.
     *
     * @param intent intent to process in the MVI workflow.
     * @author Dmitriy Moroz
     */
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

            is ServerSetupIntent.UpdateFalAiApiKey -> updateState {
                it.copy(falAiApiKey = intent.key, falAiApiKeyValidationError = null)
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
