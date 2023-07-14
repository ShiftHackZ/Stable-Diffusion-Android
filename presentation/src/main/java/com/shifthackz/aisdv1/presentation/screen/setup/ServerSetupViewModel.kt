package com.shifthackz.aisdv1.presentation.screen.setup

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.reactive.retryWithDelay
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.validation.url.UrlValidator
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.analytics.Analytics
import com.shifthackz.aisdv1.domain.usecase.caching.DataPreLoaderUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestConnectivityUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.GetConfigurationUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.SetServerConfigurationUseCase
import com.shifthackz.aisdv1.presentation.features.SetupConnectEvent
import com.shifthackz.aisdv1.presentation.features.SetupConnectFailure
import com.shifthackz.aisdv1.presentation.features.SetupConnectSuccess
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.util.concurrent.TimeUnit

class ServerSetupViewModel(
    launchSource: ServerSetupLaunchSource,
    getConfigurationUseCase: GetConfigurationUseCase,
    private val demoModeUrl: String,
    private val cloudUrl: String,
    private val urlValidator: UrlValidator,
    private val testConnectivityUseCase: TestConnectivityUseCase,
    private val setServerConfigurationUseCase: SetServerConfigurationUseCase,
    private val dataPreLoaderUseCase: DataPreLoaderUseCase,
    private val schedulersProvider: SchedulersProvider,
    private val buildInfoProvider: BuildInfoProvider,
    private val analytics: Analytics,
) : MviRxViewModel<ServerSetupState, ServerSetupEffect>() {

    override val emptyState = ServerSetupState(
        showBackNavArrow = launchSource == ServerSetupLaunchSource.SETTINGS
    )

    init {
        !getConfigurationUseCase()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog) { configuration ->
                currentState
                    .copy(allowedModes = buildInfoProvider.buildType.allowedModes)
                    .withSource(configuration.source)
                    .withDemoMode(configuration.demoMode)
                    .withServerUrl(configuration.serverUrl)
                    .let(::setState)
            }
    }

    fun updateServerMode(value: ServerSetupState.Mode) = currentState
        .copy(mode = value)
        .let(::setState)

    fun updateServerUrl(value: String) = currentState
        .copy(serverUrl = value, validationError = null)
        .let(::setState)

    fun updateDemoMode(value: Boolean) = currentState
        .copy(demoMode = value)
        .let(::setState)

    fun connectToServer() {
        if (!validate()) return
        if (currentState.mode != ServerSetupState.Mode.HORDE) {
            return connectToAutomaticInstance()
        }
        return connectToHorde();
    }

    fun dismissScreenDialog() = setScreenDialog(ServerSetupState.Dialog.None)

    private fun validate(): Boolean {
        if (currentState.mode != ServerSetupState.Mode.OWN_SERVER) return true
        if (currentState.demoMode) return true
        val validation = urlValidator(currentState.serverUrl)
        currentState.copy(validationError = validation.mapToUi()).let(::setState)
        return validation.isValid
    }

    private fun connectToAutomaticInstance() {
        val demoMode = currentState.demoMode
        val connectUrl = when (currentState.mode) {
            ServerSetupState.Mode.SD_AI_CLOUD -> cloudUrl
            else -> if (demoMode) demoModeUrl else currentState.serverUrl
        }
        analytics.logEvent(SetupConnectEvent(connectUrl, demoMode))
        !testConnectivityUseCase(connectUrl)
            .doOnSubscribe { setScreenDialog(ServerSetupState.Dialog.Communicating) }
            .andThen(
                Completable.concatArray(
                    setServerConfigurationUseCase(
                        url = connectUrl,
                        demoMode = demoMode,
                        source = currentState.mode.toSource(),
                    ),
                    Observable
                        .timer(5L, TimeUnit.SECONDS)
                        .flatMapCompletable {
                            dataPreLoaderUseCase()
                                .retryWithDelay(3L, 1L, TimeUnit.SECONDS)
                        }
                )
            )
            .andThen(Single.just(Result.success(Unit)))
            .timeout(30L, TimeUnit.SECONDS)
            .subscribeOnMainThread(schedulersProvider)
            .onErrorResumeNext { t ->
                setServerConfigurationUseCase(
                    currentState.originalSeverUrl,
                    currentState.originalDemoMode,
                    currentState.originalMode.toSource(),
                ).andThen(Single.just(Result.failure(t)))
            }
            .subscribeBy(::errorLog) { result ->
                result.fold(
                    onSuccess = {
                        analytics.logEvent(SetupConnectSuccess)
                        dismissScreenDialog()
                        emitEffect(ServerSetupEffect.CompleteSetup)
                    },
                    onFailure = { t ->
                        val message = t.localizedMessage ?: "Error connecting to server"
                        analytics.logEvent(SetupConnectFailure(message))
                        setScreenDialog(ServerSetupState.Dialog.Error(message.asUiText()))
                    }
                )
            }
    }

    private fun connectToHorde() = !setServerConfigurationUseCase(
        url = "http://127.0.0.1",
        demoMode = false,
        source = ServerSource.HORDE,
    )
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(::errorLog) {
            analytics.logEvent(SetupConnectSuccess)
            emitEffect(ServerSetupEffect.CompleteSetup)
        }

    private fun setScreenDialog(value: ServerSetupState.Dialog) = currentState
        .copy(screenDialog = value)
        .let(::setState)
}
