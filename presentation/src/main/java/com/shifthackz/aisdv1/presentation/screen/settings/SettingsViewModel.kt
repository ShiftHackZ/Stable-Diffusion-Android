package com.shifthackz.aisdv1.presentation.screen.settings

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.usecase.sdmodel.SelectStableDiffusionModelUseCase
import io.reactivex.rxjava3.kotlin.subscribeBy

class SettingsViewModel(
    private val settingsStateProducer: SettingsStateProducer,
    private val selectStableDiffusionModelUseCase: SelectStableDiffusionModelUseCase,
    private val schedulersProvider: SchedulersProvider,
) : MviRxViewModel<SettingsState, EmptyEffect>() {

    override val emptyState = SettingsState.Uninitialized

    init {
        !settingsStateProducer()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(Throwable::printStackTrace, ::setState)
    }

    fun launchSdModelSelectionDialog() = (currentState as? SettingsState.Content)?.let { state ->
        setActiveDialog(SettingsState.Dialog.SelectSdModel(state.sdModels, state.sdModelSelected))
    }

    fun dismissScreenDialog() = setActiveDialog(SettingsState.Dialog.None)

    fun selectStableDiffusionModel(value: String) = !selectStableDiffusionModelUseCase(value)
        .andThen(settingsStateProducer())
        .doOnSubscribe { setActiveDialog(SettingsState.Dialog.Communicating) }
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(Throwable::printStackTrace, ::setState)

    private fun setActiveDialog(dialog: SettingsState.Dialog) = currentState
        .withDialog(value = dialog)
        .let(::setState)
}
