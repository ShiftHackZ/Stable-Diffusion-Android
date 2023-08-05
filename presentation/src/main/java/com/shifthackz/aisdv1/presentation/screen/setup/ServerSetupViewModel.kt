package com.shifthackz.aisdv1.presentation.screen.setup

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
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
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.analytics.Analytics
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.caching.DataPreLoaderUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestConnectivityUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestHordeApiKeyUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.CheckDownloadedModelUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.DownloadModelUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.GetConfigurationUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.SetServerConfigurationUseCase
import com.shifthackz.aisdv1.presentation.features.SetupConnectEvent
import com.shifthackz.aisdv1.presentation.features.SetupConnectFailure
import com.shifthackz.aisdv1.presentation.features.SetupConnectSuccess
import com.shifthackz.aisdv1.presentation.screen.setup.mappers.mapToUi
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
    private val cloudUrl: String,
    private val urlValidator: UrlValidator,
    private val stringValidator: CommonStringValidator,
    private val testConnectivityUseCase: TestConnectivityUseCase,
    private val testHordeApiKeyUseCase: TestHordeApiKeyUseCase,
    private val setServerConfigurationUseCase: SetServerConfigurationUseCase,
    private val downloadModelUseCase: DownloadModelUseCase,
    private val checkDownloadedModelUseCase: CheckDownloadedModelUseCase,
    private val dataPreLoaderUseCase: DataPreLoaderUseCase,
    private val schedulersProvider: SchedulersProvider,
    private val buildInfoProvider: BuildInfoProvider,
    private val preferenceManager: PreferenceManager,
    private val analytics: Analytics,
) : MviRxViewModel<ServerSetupState, ServerSetupEffect>() {

    override val emptyState = ServerSetupState(
        showBackNavArrow = launchSource == ServerSetupLaunchSource.SETTINGS
    )

    private var downloadDisposable: Disposable? = null

    init {
        !getConfigurationUseCase()
            .zipWith(checkDownloadedModelUseCase(), ::Pair)
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog) { (configuration, isDownloaded) ->
                currentState
                    .copy(allowedModes = buildInfoProvider.buildType.allowedModes)
                    .copy(localModelDownloaded = isDownloaded)
                    .withSource(configuration.source)
                    .withDemoMode(configuration.demoMode)
                    .withServerUrl(configuration.serverUrl)
                    .withAuthType(configuration.authType)
                    .withCredentials(configuration.authCredentials)
                    .withHordeApiKey(configuration.hordeApiKey)
                    .let(::setState)
            }
    }

    fun updateServerMode(value: ServerSetupState.Mode) = currentState
        .copy(mode = value)
        .let(::setState)

    fun updateServerUrl(value: String) = currentState
        .copy(serverUrl = value, serverUrlValidationError = null)
        .let(::setState)

    fun updateAuthType(value: ServerSetupState.AuthType) = currentState
        .copy(authType = value)
        .let(::setState)

    fun updateLogin(value: String) = currentState
        .copy(login = value, loginValidationError = null)
        .let(::setState)

    fun updatePassword(value: String) = currentState
        .copy(password = value, passwordValidationError = null)
        .let(::setState)

    fun updatePasswordVisibility(value: Boolean) = currentState
        .copy(passwordVisible = !value)
        .let(::setState)

    fun updateHordeApiKey(value: String) = currentState
        .copy(hordeApiKey = value, hordeApiKeyValidationError = null)
        .let(::setState)

    fun updateDemoMode(value: Boolean) = currentState
        .copy(demoMode = value)
        .let(::setState)

    fun updateHordeDefaultApiKeyUsage(value: Boolean) = currentState
        .copy(hordeDefaultApiKey = value)
        .let(::setState)

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
                var newState = currentState.copy(
                    serverUrlValidationError = serverUrlValidation.mapToUi()
                )
                var isValid = serverUrlValidation.isValid
                if (currentState.authType == ServerSetupState.AuthType.HTTP_BASIC) {
                    val loginValidation = stringValidator(currentState.login)
                    val passwordValidation = stringValidator(currentState.password)
                    newState = newState.copy(
                        loginValidationError = loginValidation.mapToUi(),
                        passwordValidationError = passwordValidation.mapToUi()
                    )
                    isValid = isValid && loginValidation.isValid && passwordValidation.isValid
                }
                setState(newState)
                isValid
            }
        }
        ServerSetupState.Mode.HORDE -> {
            if (currentState.hordeDefaultApiKey) true
            else {
                val validation = stringValidator(currentState.hordeApiKey)
                currentState.copy(hordeApiKeyValidationError = validation.mapToUi()).let(::setState)
                validation.isValid
            }
        }
        ServerSetupState.Mode.LOCAL -> currentState.localModelDownloaded
        else -> true
    }

    private fun connectToAutomaticInstance() {
        val demoMode = currentState.demoMode
        val connectUrl = when (currentState.mode) {
            ServerSetupState.Mode.SD_AI_CLOUD -> cloudUrl
            else -> if (demoMode) demoModeUrl else currentState.serverUrl
        }
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
                        demoMode =  currentState.originalDemoMode,
                        source = currentState.originalMode.toSource(),
                        hordeApiKey = currentState.originalHordeApiKey,
                        authCredentials = currentState.credentialsDomain(true),
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
                        demoMode =  currentState.originalDemoMode,
                        source = currentState.originalMode.toSource(),
                        hordeApiKey = currentState.originalHordeApiKey,
                        authCredentials = AuthorizationCredentials.None,
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

    fun downloadClickReducer() = when {
        currentState.downloadState is DownloadState.Downloading -> {
            downloadDisposable?.dispose()
            downloadDisposable = null
            setState(currentState.copy(downloadState = DownloadState.Unknown))
        }
        currentState.localModelDownloaded -> {
            //ToDo delete download, and reset server setup state as non-ever-set-up
        }
        else -> {
            setState(currentState.copy(downloadState = DownloadState.Downloading()))
            downloadDisposable?.dispose()
            downloadDisposable = null
            downloadDisposable = downloadModelUseCase()
                .distinctUntilChanged()
                .subscribeOnMainThread(schedulersProvider)
                .subscribeBy(
                    onError = { t ->
                        val message = t.localizedMessage ?: "Error"
                        setState(currentState.copy(downloadState = DownloadState.Error(t)))
                        setScreenDialog(ServerSetupState.Dialog.Error(message.asUiText()))
                    },
                    onNext = { downloadState ->
                        debugLog("DOWNLOAD STATE : $downloadState")
                        val newState = when (downloadState) {
                            is DownloadState.Complete -> currentState.copy(
                                downloadState = downloadState,
                                localModelDownloaded = true,
                            )
                            else -> currentState.copy(downloadState = downloadState)
                        }
                        setState(newState)
                    },
                )
                .apply { addToDisposable() }
        }
    }

    private fun setScreenDialog(value: ServerSetupState.Dialog) = currentState
        .copy(screenDialog = value)
        .let(::setState)

    private fun onSetupComplete() {
        preferenceManager.forceSetupAfterUpdate = false
        analytics.logEvent(SetupConnectSuccess)
        dismissScreenDialog()
        emitEffect(ServerSetupEffect.CompleteSetup)
    }
}
