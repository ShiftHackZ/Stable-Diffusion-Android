package com.shifthackz.aisdv1.presentation.screen.setup

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.validation.url.UrlValidator
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.usecase.caching.DataPreLoaderUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestConnectivityUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.GetServerUrlUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.SetServerUrlUseCase
import io.reactivex.rxjava3.kotlin.subscribeBy

class ServerSetupViewModel(
    private val urlValidator: UrlValidator,
    private val getServerUrlUseCase: GetServerUrlUseCase,
    private val testConnectivityUseCase: TestConnectivityUseCase,
    private val setServerUrlUseCase: SetServerUrlUseCase,
    private val dataPreLoaderUseCase: DataPreLoaderUseCase,
    private val schedulersProvider: SchedulersProvider,
) : MviRxViewModel<ServerSetupState, ServerSetupEffect>() {

    override val emptyState = ServerSetupState()

    init {
        !getServerUrlUseCase()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(Throwable::printStackTrace) { url ->
                currentState.copy(serverUrl = url).let(::setState)
            }
    }

    fun updateServerUrl(value: String) = currentState
        .copy(serverUrl = value, validationError = null)
        .let(::setState)

    fun connectToServer() {
        if (!validate()) return
        !testConnectivityUseCase(currentState.serverUrl)
            .doOnSubscribe { setScreenDialog(ServerSetupState.Dialog.Communicating) }
            .andThen(setServerUrlUseCase(currentState.serverUrl))
            .andThen(dataPreLoaderUseCase())
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = { t ->
                    setScreenDialog(
                        ServerSetupState.Dialog.Error(
                            (t.localizedMessage ?: "Error connecting to server").asUiText(),
                        )
                    )
                },
                onComplete = {
                    dismissScreenDialog()
                    emitEffect(ServerSetupEffect.CompleteSetup)
                }
            )
    }

    fun dismissScreenDialog() = setScreenDialog(ServerSetupState.Dialog.None)

    private fun validate(): Boolean {
        val validation = urlValidator(currentState.serverUrl)
        currentState.copy(validationError = validation.mapToUi()).let(::setState)
        return validation.isValid
    }

    private fun setScreenDialog(value: ServerSetupState.Dialog) = currentState
        .copy(screenDialog = value)
        .let(::setState)
}
