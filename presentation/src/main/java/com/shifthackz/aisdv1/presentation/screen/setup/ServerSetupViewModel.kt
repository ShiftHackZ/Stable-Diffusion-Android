package com.shifthackz.aisdv1.presentation.screen.setup

import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.reactive.retryWithDelay
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.validation.horde.CommonStringValidator
import com.shifthackz.aisdv1.core.validation.url.UrlValidator
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.analytics.Analytics
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.interactor.wakelock.WakeLockInterActor
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.caching.DataPreLoaderUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestConnectivityUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestHordeApiKeyUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.DeleteModelUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.DownloadModelUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.GetLocalAiModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.GetConfigurationUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.SetServerConfigurationUseCase
import com.shifthackz.aisdv1.presentation.features.SetupConnectEvent
import com.shifthackz.aisdv1.presentation.features.SetupConnectFailure
import com.shifthackz.aisdv1.presentation.features.SetupConnectSuccess
import com.shifthackz.aisdv1.presentation.screen.setup.mappers.mapLocalCustomModelSwitchState
import com.shifthackz.aisdv1.presentation.screen.setup.mappers.mapToUi
import com.shifthackz.aisdv1.presentation.screen.setup.mappers.withNewState
import com.shifthackz.aisdv1.presentation.utils.Constants
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.util.concurrent.TimeUnit

class ServerSetupViewModel(
    launchSource: ServerSetupLaunchSource,
    getConfigurationUseCase: GetConfigurationUseCase,
    private val demoModeUrl: String,
    private val urlValidator: UrlValidator,
    private val stringValidator: CommonStringValidator,
    private val testConnectivityUseCase: TestConnectivityUseCase,
    private val testHordeApiKeyUseCase: TestHordeApiKeyUseCase,
    private val setServerConfigurationUseCase: SetServerConfigurationUseCase,
    private val downloadModelUseCase: DownloadModelUseCase,
    private val deleteModelUseCase: DeleteModelUseCase,
    private val getLocalAiModelsUseCase: GetLocalAiModelsUseCase,
    private val dataPreLoaderUseCase: DataPreLoaderUseCase,
    private val schedulersProvider: SchedulersProvider,
    private val preferenceManager: PreferenceManager,
    private val analytics: Analytics,
    private val wakeLockInterActor: WakeLockInterActor,
) : MviRxViewModel<ServerSetupState, ServerSetupEffect>() {

    override val emptyState = ServerSetupState(
        showBackNavArrow = launchSource == ServerSetupLaunchSource.SETTINGS
    )

    private var downloadDisposable: Disposable? = null

    init {
        !getConfigurationUseCase()
            .zipWith(getLocalAiModelsUseCase(), ::Pair)
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog) { (configuration, localModels) ->
                updateState {
                    it
                        .copy(localModels = localModels.mapToUi())
                        .copy(localCustomModel = localModels.mapLocalCustomModelSwitchState())
                        .withSource(configuration.source)
                        .withDemoMode(configuration.demoMode)
                        .withServerUrl(configuration.serverUrl)
                        .withAuthType(configuration.authType)
                        .withCredentials(configuration.authCredentials)
                        .withHordeApiKey(configuration.hordeApiKey)
                }
            }
    }

    fun updateServerMode(value: ServerSetupState.Mode) = updateState {
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
        return when (currentState.mode) {
            ServerSetupState.Mode.HORDE -> connectToHorde()
            ServerSetupState.Mode.LOCAL -> connectToLocalDiffusion()
            else -> connectToAutomaticInstance()
        }
    }

    fun dismissScreenDialog() = setScreenDialog(ServerSetupState.Dialog.None)

    private fun validate(): Boolean = when (currentState.mode) {
        ServerSetupState.Mode.OWN_SERVER -> {
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

        ServerSetupState.Mode.HORDE -> {
            if (currentState.hordeDefaultApiKey) true
            else {
                val validation = stringValidator(currentState.hordeApiKey)
                updateState {
                    it.copy(hordeApiKeyValidationError = validation.mapToUi())
                }
                validation.isValid
            }
        }

        ServerSetupState.Mode.LOCAL -> {
            currentState.localModels.find { it.selected && it.downloaded } != null
        }
    }

    private fun connectToAutomaticInstance() {
        val demoMode = currentState.demoMode
        val connectUrl = if (demoMode) demoModeUrl else currentState.serverUrl
        val credentials = when (currentState.mode) {
            ServerSetupState.Mode.OWN_SERVER -> {
                if (!demoMode) currentState.credentialsDomain()
                else AuthorizationCredentials.None
            }

            else -> AuthorizationCredentials.None
        }
        analytics.logEvent(SetupConnectEvent(connectUrl, demoMode))
        !setServerConfigurationUseCase(
            Configuration(
                serverUrl = connectUrl,
                demoMode = demoMode,
                source = currentState.mode.toSource(),
                hordeApiKey = currentState.hordeApiKey,
                authCredentials = credentials,
                localModelId = currentState.localModels.find { it.selected }?.id ?: "",
            )
        )
            .doOnSubscribe { setScreenDialog(ServerSetupState.Dialog.Communicating) }
            .andThen(testConnectivityUseCase(connectUrl))
            .andThen(
                Observable
                    .timer(5L, TimeUnit.SECONDS)
                    .flatMapCompletable {
                        dataPreLoaderUseCase().retryWithDelay(3L, 1L, TimeUnit.SECONDS)
                    }
            )
            .andThen(Single.just(Result.success(Unit)))
            .timeout(30L, TimeUnit.SECONDS)
            .subscribeOnMainThread(schedulersProvider)
            .onErrorResumeNext { t ->
                setServerConfigurationUseCase(
                    Configuration(
                        serverUrl = currentState.originalSeverUrl,
                        demoMode = currentState.originalDemoMode,
                        source = currentState.originalMode.toSource(),
                        hordeApiKey = currentState.originalHordeApiKey,
                        authCredentials = currentState.credentialsDomain(true),
                        localModelId = currentState.localModels.find { it.selected }?.id ?: "",
                    ),
                ).andThen(Single.just(Result.failure(t)))
            }
            .subscribeBy(::errorLog) { result ->
                result.fold(
                    onSuccess = { onSetupComplete() },
                    onFailure = { t ->
                        val message = t.localizedMessage ?: "Error connecting to server"
                        analytics.logEvent(SetupConnectFailure(message))
                        setScreenDialog(ServerSetupState.Dialog.Error(message.asUiText()))
                    }
                )
            }
    }

    private fun connectToHorde() {
        val testApiKey = if (currentState.hordeDefaultApiKey) Constants.HORDE_DEFAULT_API_KEY
        else currentState.hordeApiKey

        !setServerConfigurationUseCase(
            Configuration(
                serverUrl = "",
                demoMode = false,
                source = ServerSource.HORDE,
                hordeApiKey = testApiKey,
                authCredentials = AuthorizationCredentials.None,
                localModelId = currentState.localModels.find { it.selected }?.id ?: "",
            ),
        )
            .andThen(testHordeApiKeyUseCase())
            .flatMap {
                if (it) Single.just(Result.success(Unit))
                else Single.error(Throwable("Bad key"))
            }
            .onErrorResumeNext { t ->
                debugLog("Reverting old api key")
                setServerConfigurationUseCase(
                    Configuration(
                        serverUrl = currentState.originalSeverUrl,
                        demoMode = currentState.originalDemoMode,
                        source = currentState.originalMode.toSource(),
                        hordeApiKey = currentState.originalHordeApiKey,
                        authCredentials = AuthorizationCredentials.None,
                        localModelId = currentState.localModels.find { it.selected }?.id ?: "",
                    )
                ).andThen(Single.just(Result.failure(t)))
            }
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

    private fun connectToLocalDiffusion() {
        !setServerConfigurationUseCase(
            Configuration(
                serverUrl = "",
                demoMode = false,
                source = ServerSource.LOCAL,
                hordeApiKey = Constants.HORDE_DEFAULT_API_KEY,
                authCredentials = AuthorizationCredentials.None,
                localModelId = currentState.localModels.find { it.selected }?.id ?: "",
            ),
        )
            .andThen(Single.just(Result.success(Unit)))
            .onErrorResumeNext { t -> Single.just(Result.failure(t)) }
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog) { result ->
                result.fold(
                    onSuccess = { onSetupComplete() },
                    onFailure = { t ->
                        val message = t.localizedMessage ?: "Error"
                        analytics.logEvent(SetupConnectFailure(message))
                        setScreenDialog(ServerSetupState.Dialog.Error(message.asUiText()))
                    }
                )
            }
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
                    .apply { addToDisposable() }
            }
        }
    }

    private fun setScreenDialog(value: ServerSetupState.Dialog) =  updateState {
        it.copy(screenDialog = value)
    }

    private fun onSetupComplete() {
        preferenceManager.forceSetupAfterUpdate = false
        analytics.logEvent(SetupConnectSuccess)
        dismissScreenDialog()
        emitEffect(ServerSetupEffect.CompleteSetup)
    }
}
