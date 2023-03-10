package com.shifthackz.aisdv1.presentation.screen.setup

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.validation.url.UrlValidator
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.usecase.caching.DataPreLoaderUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestConnectivityUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.GetConfigurationUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.SetServerConfigurationUseCase
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.util.concurrent.TimeUnit

class ServerSetupViewModel(
    launchSource: ServerSetupLaunchSource,
    getConfigurationUseCase: GetConfigurationUseCase,
    private val demoModeUrl: String,
    private val urlValidator: UrlValidator,
    private val testConnectivityUseCase: TestConnectivityUseCase,
    private val setServerConfigurationUseCase: SetServerConfigurationUseCase,
    private val dataPreLoaderUseCase: DataPreLoaderUseCase,
    private val schedulersProvider: SchedulersProvider,
) : MviRxViewModel<ServerSetupState, ServerSetupEffect>() {

    override val emptyState = ServerSetupState(
        showBackNavArrow = launchSource == ServerSetupLaunchSource.SETTINGS
    )

    init {
        !getConfigurationUseCase()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(Throwable::printStackTrace) { (url, demoMode) ->
                currentState
                    .copy(
                        serverUrl = url,
                        originalSeverUrl = url,
                        demoMode = demoMode,
                        originalDemoMode = demoMode,
                    )
                    .let(::setState)
            }
    }

    fun updateServerUrl(value: String) = currentState
        .copy(serverUrl = value, validationError = null)
        .let(::setState)

    fun updateDemoMode(value: Boolean) = currentState
        .copy(demoMode = value)
        .let(::setState)

    fun connectToServer() {
        if (!validate()) return
        val demoMode = currentState.demoMode
        val connectUrl = if (demoMode) demoModeUrl else currentState.serverUrl
        !testConnectivityUseCase(connectUrl)
            .doOnSubscribe { setScreenDialog(ServerSetupState.Dialog.Communicating) }
            .andThen(setServerConfigurationUseCase(connectUrl, demoMode))
            .andThen(dataPreLoaderUseCase())
            .andThen(Single.just(Result.success(Unit)))
            .timeout(30L, TimeUnit.SECONDS)
            .subscribeOnMainThread(schedulersProvider)
            .onErrorResumeNext { t ->
                setServerConfigurationUseCase(
                    currentState.originalSeverUrl,
                    currentState.originalDemoMode
                )
                    .andThen(Single.just(Result.failure(t)))
            }
            .subscribeBy(Throwable::printStackTrace) { result ->
                result.fold(
                    onSuccess = {
                        dismissScreenDialog()
                        emitEffect(ServerSetupEffect.CompleteSetup)
                    },
                    onFailure = { t ->
                        setScreenDialog(
                            ServerSetupState.Dialog.Error(
                                (t.localizedMessage ?: "Error connecting to server").asUiText(),
                            )
                        )
                    }
                )
            }
    }

    fun dismissScreenDialog() = setScreenDialog(ServerSetupState.Dialog.None)

    private fun validate(): Boolean {
        if (currentState.demoMode) return true
        val validation = urlValidator(currentState.serverUrl)
        currentState.copy(validationError = validation.mapToUi()).let(::setState)
        return validation.isValid
    }

    private fun setScreenDialog(value: ServerSetupState.Dialog) = currentState
        .copy(screenDialog = value)
        .let(::setState)
}
