package com.shifthackz.aisdv1.presentation.screen.setup

import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.validation.horde.CommonStringValidator
import com.shifthackz.aisdv1.core.validation.url.UrlValidator
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.analytics.Analytics
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.interactor.settings.SetupConnectionInterActor
import com.shifthackz.aisdv1.domain.interactor.wakelock.WakeLockInterActor
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.downloadable.DeleteModelUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.DownloadModelUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.GetLocalAiModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.huggingface.FetchAndGetHuggingFaceModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.GetConfigurationUseCase
import com.shifthackz.aisdv1.presentation.features.SetupConnectEvent
import com.shifthackz.aisdv1.presentation.features.SetupConnectFailure
import com.shifthackz.aisdv1.presentation.features.SetupConnectSuccess
import com.shifthackz.aisdv1.presentation.screen.setup.mappers.mapLocalCustomModelSwitchState
import com.shifthackz.aisdv1.presentation.screen.setup.mappers.mapToUi
import com.shifthackz.aisdv1.presentation.screen.setup.mappers.withNewState
import com.shifthackz.aisdv1.presentation.utils.Constants
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy

class ServerSetupViewModel(
    launchSource: ServerSetupLaunchSource,
    private val demoModeUrl: String,
    getConfigurationUseCase: GetConfigurationUseCase,
    getLocalAiModelsUseCase: GetLocalAiModelsUseCase,
    fetchAndGetHuggingFaceModelsUseCase: FetchAndGetHuggingFaceModelsUseCase,
    private val urlValidator: UrlValidator,
    private val stringValidator: CommonStringValidator,
    private val setupConnectionInterActor: SetupConnectionInterActor,
    private val downloadModelUseCase: DownloadModelUseCase,
    private val deleteModelUseCase: DeleteModelUseCase,
    private val schedulersProvider: SchedulersProvider,
    private val preferenceManager: PreferenceManager,
    private val analytics: Analytics,
    private val wakeLockInterActor: WakeLockInterActor,
) : MviRxViewModel<ServerSetupState, ServerSetupEffect>() {

    override val emptyState = ServerSetupState(
        showBackNavArrow = launchSource == ServerSetupLaunchSource.SETTINGS,
    )

    private var downloadDisposable: Disposable? = null

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
                    state
                        .copy(
                            huggingFaceModels = hfModels.map(HuggingFaceModel::alias),
                            huggingFaceModel = configuration.huggingFaceModel,
                            huggingFaceApiKey = configuration.huggingFaceApiKey,
                            openAiApiKey = configuration.openAiApiKey,
                            localModels = localModels.mapToUi(),
                            localCustomModel = localModels.mapLocalCustomModelSwitchState(),
                            mode = configuration.source,
                            demoMode = configuration.demoMode,
                            serverUrl = configuration.serverUrl,
                            authType = configuration.authType,
                        )
                        .withCredentials(configuration.authCredentials)
                        .withHordeApiKey(configuration.hordeApiKey)
                }
            }
    }

    fun updateServerMode(value: ServerSource) = updateState {
        it.copy(mode = value)
    }

    fun updateServerUrl(value: String) = updateState {
        it.copy(serverUrl = value, serverUrlValidationError = null)
    }

    fun updateAuthType(value: ServerSetupState.AuthType) = updateState {
        it.copy(authType = value)
    }

    fun updateLogin(value: String) = updateState {
        it.copy(login = value, loginValidationError = null)
    }

    fun updatePassword(value: String) = updateState {
        it.copy(password = value, passwordValidationError = null)
    }

    fun updatePasswordVisibility(value: Boolean) = updateState {
        it.copy(passwordVisible = !value)
    }

    fun updateHordeApiKey(value: String) = updateState {
        it.copy(hordeApiKey = value, hordeApiKeyValidationError = null)
    }

    fun updateHuggingFaceApiKey(value: String) = updateState {
        it.copy(huggingFaceApiKey = value)
    }

    fun updateHuggingFaceModel(value: String) = updateState {
        it.copy(huggingFaceModel = value)
    }

    fun updateOpenAiApiKey(value: String) = updateState {
        it.copy(openAiApiKey = value)
    }

    fun updateDemoMode(value: Boolean) = updateState {
        it.copy(demoMode = value)
    }

    fun updateHordeDefaultApiKeyUsage(value: Boolean) = updateState {
        it.copy(hordeDefaultApiKey = value)
    }

    fun updateAllowLocalCustomModel(value: Boolean) = updateState {
        it.copy(
            localCustomModel = value,
            localModels = currentState.localModels.withNewState(
                currentState.localModels.find { m -> m.id == LocalAiModel.CUSTOM.id }!!.copy(
                    selected = value,
                ),
            ),
        )
    }

    fun connectToServer() {
        if (!validate()) return
        !when(currentState.mode) {
            ServerSource.HORDE -> connectToHorde()
            ServerSource.LOCAL -> connectToLocalDiffusion()
            ServerSource.AUTOMATIC1111 -> connectToAutomaticInstance()
            ServerSource.HUGGING_FACE -> connectToHuggingFace()
            ServerSource.OPEN_AI -> connectToOpenAi()
        }
            .doOnSubscribe { setScreenDialog(ServerSetupState.Dialog.Communicating) }
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog) { result ->
                result.fold(
                    onSuccess = { onSetupComplete() },
                    onFailure = { t ->
                        val message = t.localizedMessage ?: "Bad key"
                        analytics.logEvent(SetupConnectFailure(message))
                        setScreenDialog(ServerSetupState.Dialog.Error(message.asUiText()))
                    },
                )
            }
    }

    fun dismissScreenDialog() = setScreenDialog(ServerSetupState.Dialog.None)

    private fun validate(): Boolean = when (currentState.mode) {
        ServerSource.AUTOMATIC1111 -> {
            if (currentState.demoMode) true
            else {
                val serverUrlValidation = urlValidator(currentState.serverUrl)
                var isValid = serverUrlValidation.isValid
                updateState {
                    var newState = it.copy(
                        serverUrlValidationError = serverUrlValidation.mapToUi()
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
                    newState
                }
                isValid
            }
        }

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
            currentState.localModels.find { it.selected && it.downloaded } != null
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
    }

    private fun connectToAutomaticInstance(): Single<Result<Unit>> {
        val demoMode = currentState.demoMode
        val connectUrl = if (demoMode) demoModeUrl else currentState.serverUrl
        val credentials = when (currentState.mode) {
            ServerSource.AUTOMATIC1111 -> {
                if (!demoMode) currentState.credentialsDomain()
                else AuthorizationCredentials.None
            }

            else -> AuthorizationCredentials.None
        }
        analytics.logEvent(SetupConnectEvent(connectUrl, demoMode))
        return setupConnectionInterActor.connectToA1111(
            connectUrl,
            demoMode,
            credentials,
        )
    }

    private fun connectToHuggingFace() = with(currentState) {
        setupConnectionInterActor.connectToHuggingFace(
            huggingFaceApiKey,
            huggingFaceModel,
        )
    }

    private fun connectToOpenAi() = setupConnectionInterActor.connectToOpenAi(
        currentState.openAiApiKey,
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
        val localModelId = currentState.localModels.find { it.selected }?.id ?: ""
        return setupConnectionInterActor.connectToLocal(localModelId)
    }

    fun localModelSelect(localModel: ServerSetupState.LocalModel) {
        if (currentState.localModels.any { it.downloadState is DownloadState.Downloading }) {
            return
        }
        updateState {
            it.copy(
                localModels = currentState.localModels.withNewState(
                    localModel.copy(selected = true),
                ),
            )
        }
    }

    fun localModelDownloadClickReducer(localModel: ServerSetupState.LocalModel) {
        when {
            // User cancels download
            localModel.downloadState is DownloadState.Downloading -> {
                downloadDisposable?.dispose()
                downloadDisposable = null
                updateState {
                    it.copy(
                        localModels = currentState.localModels.withNewState(
                            localModel.copy(downloadState = DownloadState.Unknown),
                        ),
                    )
                }
            }
            // User deletes local model
            localModel.downloaded -> {
                updateState {
                    it.copy(
                        localModels = currentState.localModels.withNewState(
                            localModel.copy(
                                downloadState = DownloadState.Unknown,
                                downloaded = false,
                            ),
                        ),
                    )
                }
                !deleteModelUseCase(localModel.id)
                    .subscribeOnMainThread(schedulersProvider)
                    .subscribeBy(::errorLog)
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
                downloadDisposable?.dispose()
                downloadDisposable = null
                downloadDisposable = downloadModelUseCase(localModel.id)
                    .distinctUntilChanged()
                    .doOnSubscribe { wakeLockInterActor.acquireWakelockUseCase() }
                    .doFinally { wakeLockInterActor.releaseWakeLockUseCase() }
                    .subscribeOnMainThread(schedulersProvider)
                    .subscribeBy(
                        onError = { t ->
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
                            setScreenDialog(ServerSetupState.Dialog.Error(message.asUiText()))
                        },
                        onNext = { downloadState ->
                            debugLog("DOWNLOAD STATE : $downloadState")
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
                    .addToDisposable()
            }
        }
    }

    private fun setScreenDialog(value: ServerSetupState.Dialog) = updateState {
        it.copy(screenDialog = value)
    }

    private fun onSetupComplete() {
        preferenceManager.forceSetupAfterUpdate = false
        analytics.logEvent(SetupConnectSuccess)
        dismissScreenDialog()
        emitEffect(ServerSetupEffect.CompleteSetup)
    }
}
