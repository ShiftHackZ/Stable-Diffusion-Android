package com.shifthackz.aisdv1.presentation.screen.setup

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.validation.common.CommonStringValidator
import com.shifthackz.aisdv1.core.validation.path.FilePathValidator
import com.shifthackz.aisdv1.core.validation.url.UrlValidator
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.interactor.settings.SetupConnectionInterActor
import com.shifthackz.aisdv1.domain.interactor.wakelock.WakeLockInterActor
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.downloadable.DeleteModelUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.DownloadModelUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.GetLocalAiModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.huggingface.FetchAndGetHuggingFaceModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.GetConfigurationUseCase
import com.shifthackz.aisdv1.presentation.model.LaunchSource
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.aisdv1.presentation.screen.setup.mappers.mapLocalCustomModelSwitchState
import com.shifthackz.aisdv1.presentation.screen.setup.mappers.mapToUi
import com.shifthackz.aisdv1.presentation.screen.setup.mappers.withNewState
import com.shifthackz.aisdv1.presentation.utils.Constants
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy

class ServerSetupViewModel(
    launchSource: LaunchSource,
    getConfigurationUseCase: GetConfigurationUseCase,
    getLocalAiModelsUseCase: GetLocalAiModelsUseCase,
    fetchAndGetHuggingFaceModelsUseCase: FetchAndGetHuggingFaceModelsUseCase,
    private val urlValidator: UrlValidator,
    private val stringValidator: CommonStringValidator,
    private val filePathValidator: FilePathValidator,
    private val setupConnectionInterActor: SetupConnectionInterActor,
    private val downloadModelUseCase: DownloadModelUseCase,
    private val deleteModelUseCase: DeleteModelUseCase,
    private val schedulersProvider: SchedulersProvider,
    private val preferenceManager: PreferenceManager,
    private val wakeLockInterActor: WakeLockInterActor,
    private val mainRouter: MainRouter,
) : MviRxViewModel<ServerSetupState, ServerSetupIntent, ServerSetupEffect>() {

    override val initialState = ServerSetupState(
        showBackNavArrow = launchSource == LaunchSource.SETTINGS,
    )

    private val credentials: AuthorizationCredentials
        get() = when (currentState.mode) {
            ServerSource.AUTOMATIC1111 -> {
                if (!currentState.demoMode) currentState.credentialsDomain()
                else AuthorizationCredentials.None
            }

            ServerSource.SWARM_UI -> currentState.credentialsDomain()

            else -> AuthorizationCredentials.None
        }

    private val downloadDisposables: MutableList<Pair<String, Disposable>> = mutableListOf()

    init {
        !Single.zip(
            getConfigurationUseCase(),
            getLocalAiModelsUseCase(),
            fetchAndGetHuggingFaceModelsUseCase(),
            ::Triple,
        )
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog) { (configuration, localModels, hfModels) ->
                updateState { state ->
                    state.copy(
                        huggingFaceModels = hfModels.map(HuggingFaceModel::alias),
                        huggingFaceModel = configuration.huggingFaceModel,
                        huggingFaceApiKey = configuration.huggingFaceApiKey,
                        openAiApiKey = configuration.openAiApiKey,
                        stabilityAiApiKey = configuration.stabilityAiApiKey,
                        localModels = localModels.mapToUi(),
                        localCustomModel = localModels.mapLocalCustomModelSwitchState(),
                        localCustomModelPath = configuration.localModelPath,
                        mode = configuration.source,
                        demoMode = configuration.demoMode,
                        serverUrl = configuration.serverUrl,
                        swarmUiUrl = configuration.swarmUiUrl,
                        authType = configuration.authType,
                    )
                        .withCredentials(configuration.authCredentials)
                        .withHordeApiKey(configuration.hordeApiKey)
                }
            }
    }

    override fun onCleared() {
        downloadDisposables.forEach { (_, disposable) ->
            disposable.dispose()
        }
        super.onCleared()
    }

    override fun processIntent(intent: ServerSetupIntent) = when (intent) {
        is ServerSetupIntent.AllowLocalCustomModel -> updateState {
            it.copy(
                localCustomModel = intent.allow,
                localModels = currentState.localModels.withNewState(
                    currentState.localModels.find { m -> m.id == LocalAiModel.CUSTOM.id }?.copy(
                        selected = intent.allow,
                    ),
                ),
            )
        }

        ServerSetupIntent.DismissDialog -> setScreenModal(Modal.None)

        is ServerSetupIntent.LocalModel.ClickReduce -> localModelDownloadClickReducer(intent.model)

        is ServerSetupIntent.LocalModel.DeleteConfirm -> updateState {
            !deleteModelUseCase(intent.model.id)
                .subscribeOnMainThread(schedulersProvider)
                .subscribeBy(::errorLog)
            it.copy(
                screenModal = Modal.None,
                localModels = currentState.localModels.withNewState(
                    intent.model.copy(
                        downloadState = DownloadState.Unknown,
                        downloaded = false,
                    ),
                ),
            )
        }

        is ServerSetupIntent.SelectLocalModel -> {
            if (currentState.localModels.any { it.downloadState is DownloadState.Downloading }) {
                Unit
            }
            updateState {
                it.copy(
                    localModels = currentState.localModels.withNewState(
                        intent.model.copy(selected = true),
                    ),
                )
            }
        }

        ServerSetupIntent.MainButtonClick -> when (currentState.step) {
            ServerSetupState.Step.SOURCE -> updateState {
                it.copy(step = ServerSetupState.Step.CONFIGURE)
            }

            ServerSetupState.Step.CONFIGURE -> validateAndConnectToServer()
        }

        is ServerSetupIntent.UpdateAuthType -> updateState {
            it.copy(authType = intent.type)
        }

        is ServerSetupIntent.UpdateDemoMode -> updateState {
            it.copy(demoMode = intent.value)
        }

        is ServerSetupIntent.UpdateHordeApiKey -> updateState {
            it.copy(hordeApiKey = intent.key, hordeApiKeyValidationError = null)
        }

        is ServerSetupIntent.UpdateHordeDefaultApiKey -> updateState {
            it.copy(hordeDefaultApiKey = intent.value)
        }

        is ServerSetupIntent.UpdateHuggingFaceApiKey -> updateState {
            it.copy(huggingFaceApiKey = intent.key)
        }

        is ServerSetupIntent.UpdateHuggingFaceModel -> updateState {
            it.copy(huggingFaceModel = intent.model)
        }

        is ServerSetupIntent.UpdateLogin -> updateState {
            it.copy(login = intent.login, loginValidationError = null)
        }

        is ServerSetupIntent.UpdateOpenAiApiKey -> updateState {
            it.copy(openAiApiKey = intent.key)
        }

        is ServerSetupIntent.UpdatePassword -> updateState {
            it.copy(password = intent.password, passwordValidationError = null)
        }

        is ServerSetupIntent.UpdatePasswordVisibility -> updateState {
            it.copy(passwordVisible = !intent.visible)
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

        is ServerSetupIntent.LaunchUrl -> {
            emitEffect(ServerSetupEffect.LaunchUrl(intent.url))
        }

        ServerSetupIntent.LaunchManageStoragePermission -> {
            emitEffect(ServerSetupEffect.LaunchManageStoragePermission)
        }

        ServerSetupIntent.NavigateBack -> if (currentState.step == ServerSetupState.Step.entries.first()) {
            mainRouter.navigateBack()
        } else {
            emitEffect(ServerSetupEffect.HideKeyboard)
            updateState {
                it.copy(step = ServerSetupState.Step.entries[it.step.ordinal - 1])
            }
        }

        is ServerSetupIntent.UpdateStabilityAiApiKey -> updateState {
            it.copy(stabilityAiApiKey = intent.key)
        }

        ServerSetupIntent.ConnectToLocalHost -> connectToServer()

        is ServerSetupIntent.SelectLocalModelPath -> updateState {
            it.copy(
                localCustomModelPath = intent.value,
                localCustomModelPathValidationError = null,
            )
        }
    }

    private fun validateAndConnectToServer() {
        if (!validate()) return
        connectToServer()
    }

    private fun connectToServer() {
        emitEffect(ServerSetupEffect.HideKeyboard)
        !when (currentState.mode) {
            ServerSource.HORDE -> connectToHorde()
            ServerSource.LOCAL -> connectToLocalDiffusion()
            ServerSource.AUTOMATIC1111 -> connectToAutomaticInstance()
            ServerSource.HUGGING_FACE -> connectToHuggingFace()
            ServerSource.OPEN_AI -> connectToOpenAi()
            ServerSource.STABILITY_AI -> connectToStabilityAi()
            ServerSource.SWARM_UI -> connectToSwarmUi()
        }
            .doOnSubscribe { setScreenModal(Modal.Communicating(canCancel = false)) }
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog) { result ->
                result.fold(
                    onSuccess = { onSetupComplete() },
                    onFailure = { t ->
                        val message = t.localizedMessage ?: "Bad key"
                        setScreenModal(Modal.Error(message.asUiText()))
                    },
                )
            }
    }

    private fun validate(): Boolean = when (currentState.mode) {
        ServerSource.AUTOMATIC1111 -> {
            if (currentState.demoMode) true
            else validateServerUrlAndCredentials(currentState.serverUrl)
        }

        ServerSource.SWARM_UI -> validateServerUrlAndCredentials(currentState.swarmUiUrl)

        ServerSource.HORDE -> {
            if (currentState.hordeDefaultApiKey) true
            else {
                val validation = stringValidator(currentState.hordeApiKey)
                updateState {
                    it.copy(hordeApiKeyValidationError = validation.mapToUi())
                }
                validation.isValid
            }
        }

        ServerSource.LOCAL -> {
            if (currentState.localCustomModel) {
                val validation = filePathValidator(currentState.localCustomModelPath)
                updateState {
                    it.copy(localCustomModelPathValidationError = validation.mapToUi())
                }
                validation.isValid
            } else {
                currentState.localModels.find { it.selected && it.downloaded } != null
            }
        }

        ServerSource.HUGGING_FACE -> {
            val validation = stringValidator(currentState.huggingFaceApiKey)
            updateState {
                it.copy(huggingFaceApiKeyValidationError = validation.mapToUi())
            }
            validation.isValid
        }

        ServerSource.OPEN_AI -> {
            val validation = stringValidator(currentState.openAiApiKey)
            updateState {
                it.copy(openAiApiKeyValidationError = validation.mapToUi())
            }
            validation.isValid
        }

        ServerSource.STABILITY_AI -> {
            val validation = stringValidator(currentState.stabilityAiApiKey)
            updateState {
                it.copy(stabilityAiApiKeyValidationError = validation.mapToUi())
            }
            validation.isValid
        }
    }

    private fun validateServerUrlAndCredentials(url: String): Boolean {
        val serverUrlValidation = urlValidator(url)
        var isValid = serverUrlValidation.isValid
        updateState { state ->
            var newState = state.copy(
                serverUrlValidationError = if (state.mode == ServerSource.AUTOMATIC1111) {
                    serverUrlValidation.mapToUi()
                } else {
                    state.serverUrlValidationError
                },
                swarmUiUrlValidationError = if (state.mode == ServerSource.SWARM_UI) {
                    serverUrlValidation.mapToUi()
                } else {
                    state.swarmUiUrlValidationError
                },
            )
            if (currentState.authType == ServerSetupState.AuthType.HTTP_BASIC) {
                val loginValidation = stringValidator(currentState.login)
                val passwordValidation = stringValidator(currentState.password)
                newState = newState.copy(
                    loginValidationError = loginValidation.mapToUi(),
                    passwordValidationError = passwordValidation.mapToUi()
                )
                isValid = isValid && loginValidation.isValid && passwordValidation.isValid
            }
            if (serverUrlValidation.validationError is UrlValidator.Error.Localhost
                && newState.loginValidationError == null
                && newState.passwordValidationError == null
            ) {
                newState = newState.copy(screenModal = Modal.ConnectLocalHost)
            }
            newState
        }
        return isValid
    }

    private fun connectToAutomaticInstance(): Single<Result<Unit>> {
        val demoMode = currentState.demoMode
        val connectUrl = if (demoMode) currentState.demoModeUrl else currentState.serverUrl
        return setupConnectionInterActor.connectToA1111(
            url = connectUrl,
            isDemo = demoMode,
            credentials = credentials,
        )
    }

    private fun connectToSwarmUi() = setupConnectionInterActor.connectToSwarmUi(
        url = currentState.swarmUiUrl,
        credentials = credentials,
    )

    private fun connectToHuggingFace() = with(currentState) {
        setupConnectionInterActor.connectToHuggingFace(
            apiKey = huggingFaceApiKey,
            model = huggingFaceModel,
        )
    }

    private fun connectToOpenAi() = setupConnectionInterActor.connectToOpenAi(
        apiKey = currentState.openAiApiKey,
    )

    private fun connectToStabilityAi() = setupConnectionInterActor.connectToStabilityAi(
        apiKey = currentState.stabilityAiApiKey,
    )

    private fun connectToHorde(): Single<Result<Unit>> {
        val testApiKey = if (currentState.hordeDefaultApiKey) {
            Constants.HORDE_DEFAULT_API_KEY
        } else {
            currentState.hordeApiKey
        }
        return setupConnectionInterActor.connectToHorde(testApiKey)
    }

    private fun connectToLocalDiffusion(): Single<Result<Unit>> {
        preferenceManager.localDiffusionCustomModelPath = currentState.localCustomModelPath
        val localModelId = currentState.localModels.find { it.selected }?.id ?: ""
        return setupConnectionInterActor.connectToLocal(localModelId)
    }

    private fun localModelDownloadClickReducer(localModel: ServerSetupState.LocalModel) {
        when {
            // User cancels download
            localModel.downloadState is DownloadState.Downloading -> {
                val index = downloadDisposables.indexOfFirst { it.first == localModel.id }
                if (index != -1) {
                    downloadDisposables[index].second.dispose()
                    downloadDisposables.removeAt(index)
                }
                !deleteModelUseCase(localModel.id)
                    .subscribeOnMainThread(schedulersProvider)
                    .subscribeBy(::errorLog)
                updateState {
                    it.copy(
                        localModels = currentState.localModels.withNewState(
                            localModel.copy(downloadState = DownloadState.Unknown),
                        ),
                    )
                }
            }
            // User deletes local model
            localModel.downloaded -> updateState {
                it.copy(screenModal = Modal.DeleteLocalModelConfirm(localModel))
            }
            // User requested new download operation
            else -> {
                updateState {
                    it.copy(
                        localModels = currentState.localModels.withNewState(
                            localModel.copy(
                                downloadState = DownloadState.Downloading(),
                            ),
                        ),
                    )
                }
                !downloadModelUseCase(localModel.id)
                    .distinctUntilChanged()
                    .doOnSubscribe { wakeLockInterActor.acquireWakelockUseCase() }
                    .doFinally { wakeLockInterActor.releaseWakeLockUseCase() }
                    .subscribeOnMainThread(schedulersProvider).subscribeBy(
                        onError = { t ->
                            errorLog(t)
                            val message = t.localizedMessage ?: "Error"
                            updateState {
                                it.copy(
                                    localModels = currentState.localModels.withNewState(
                                        localModel.copy(
                                            downloadState = DownloadState.Error(t),
                                        ),
                                    ),
                                )
                            }
                            setScreenModal(Modal.Error(message.asUiText()))
                        },
                        onNext = { downloadState ->
                            updateState {
                                when (downloadState) {
                                    is DownloadState.Complete -> it.copy(
                                        localModels = it.localModels.withNewState(
                                            localModel.copy(
                                                downloadState = downloadState,
                                                downloaded = true,
                                            ),
                                        ),
                                    )

                                    else -> it.copy(
                                        localModels = it.localModels.withNewState(
                                            localModel.copy(downloadState = downloadState),
                                        ),
                                    )
                                }
                            }
                        },
                    )
                    .also { downloadDisposables.add(localModel.id to it) }
            }
        }
    }

    private fun setScreenModal(value: Modal) = updateState {
        it.copy(screenModal = value)
    }

    private fun onSetupComplete() {
        preferenceManager.forceSetupAfterUpdate = false
        processIntent(ServerSetupIntent.DismissDialog)
        mainRouter.navigateToHomeScreen()
    }
}
