package com.shifthackz.aisdv1.presentation.screen.settings

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.caching.ClearAppCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.SelectStableDiffusionModelUseCase
import io.reactivex.rxjava3.kotlin.subscribeBy

class SettingsViewModel(
    private val settingsStateProducer: SettingsStateProducer,
    private val selectStableDiffusionModelUseCase: SelectStableDiffusionModelUseCase,
    private val clearAppCacheUseCase: ClearAppCacheUseCase,
    private val schedulersProvider: SchedulersProvider,
    private val preferenceManager: PreferenceManager,
) : MviRxViewModel<SettingsState, EmptyEffect>() {

    override val emptyState = SettingsState.Uninitialized

    init {
        !settingsStateProducer()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(Throwable::printStackTrace, ::setState)
    }

    //region DIALOG LAUNCHER METHODS
    fun launchSdModelSelectionDialog() = (currentState as? SettingsState.Content)?.let { state ->
        setActiveDialog(SettingsState.Dialog.SelectSdModel(state.sdModels, state.sdModelSelected))
    }

    fun launchClearAppCacheDialog() = setActiveDialog(SettingsState.Dialog.ClearAppCache)

    fun dismissScreenDialog() = setActiveDialog(SettingsState.Dialog.None)
    //endregion

    //region BOTTOM SHEET LAUNCHER METHODS
    fun launchLanguageBottomSheet() = setActiveSheet(SettingsState.Sheet.SelectLanguage)

    fun dismissBottomSheet() = setActiveSheet(SettingsState.Sheet.None)
    //endregion

    //region BUSINESS LOGIC METHODS
    fun selectStableDiffusionModel(value: String) = !selectStableDiffusionModelUseCase(value)
        .andThen(settingsStateProducer())
        .doOnSubscribe { setActiveDialog(SettingsState.Dialog.Communicating) }
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(Throwable::printStackTrace, ::setState)

    fun clearAppCache() = !clearAppCacheUseCase()
        .andThen(settingsStateProducer())
        .doOnSubscribe { dismissScreenDialog() }
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(Throwable::printStackTrace, ::setState)

    fun changeMonitorConnectivitySetting(value: Boolean) = (currentState as? SettingsState.Content)
        ?.also { preferenceManager.monitorConnectivity = value }
        ?.copy(monitorConnectivity = value)
        ?.let(::setState)

    fun changeAutoSaveAiResultSetting(value: Boolean) = (currentState as? SettingsState.Content)
        ?.also { preferenceManager.autoSaveAiResults = value }
        ?.copy(autoSaveAiResults = value)
        ?.let(::setState)
    //endregion

    //region UI STATES METHODS
    private fun setActiveDialog(dialog: SettingsState.Dialog) = currentState
        .withDialog(value = dialog)
        .let(::setState)

    private fun setActiveSheet(sheet: SettingsState.Sheet) = currentState
        .withSheet(value = sheet)
        .let(::setState)
    //endregion
}
