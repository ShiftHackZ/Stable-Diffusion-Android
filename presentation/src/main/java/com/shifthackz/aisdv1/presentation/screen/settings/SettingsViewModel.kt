package com.shifthackz.aisdv1.presentation.screen.settings

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.feature.analytics.Analytics
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.caching.ClearAppCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.dev.SpawnGalleryPageUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.SelectStableDiffusionModelUseCase
import com.shifthackz.aisdv1.presentation.features.AutoSaveAiResultsChanged
import com.shifthackz.aisdv1.presentation.features.FormAdvancedOptionsAlwaysShowChanged
import com.shifthackz.aisdv1.presentation.features.MonitorConnectionChanged
import com.shifthackz.aisdv1.presentation.features.SdModelSelected
import com.shifthackz.aisdv1.presentation.features.SettingsCacheCleared
import com.shifthackz.aisdv1.presentation.utils.Constants.PAGINATION_PAYLOAD_SIZE
import io.reactivex.rxjava3.kotlin.subscribeBy

class SettingsViewModel(
    private val settingsStateProducer: SettingsStateProducer,
    private val selectStableDiffusionModelUseCase: SelectStableDiffusionModelUseCase,
    private val clearAppCacheUseCase: ClearAppCacheUseCase,
    private val devSpawnGalleryPageUseCase: SpawnGalleryPageUseCase,
    private val schedulersProvider: SchedulersProvider,
    private val preferenceManager: PreferenceManager,
    private val analytics: Analytics,
) : MviRxViewModel<SettingsState, EmptyEffect>() {

    override val emptyState = SettingsState.Uninitialized

    init {
        !settingsStateProducer()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog, ::setState)
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
        .subscribeBy(::errorLog) { state ->
            analytics.logEvent(SdModelSelected(value))
            setState(state)
        }

    fun clearAppCache() = !clearAppCacheUseCase()
        .andThen(settingsStateProducer())
        .doOnSubscribe { dismissScreenDialog() }
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(::errorLog) { state ->
            analytics.logEvent(SettingsCacheCleared)
            setState(state)
        }

    fun changeMonitorConnectivitySetting(value: Boolean) = (currentState as? SettingsState.Content)
        ?.also { preferenceManager.monitorConnectivity = value }
        ?.copy(monitorConnectivity = value)
        ?.let(::setState)
        ?.also { analytics.logEvent(MonitorConnectionChanged(value)) }

    fun changeAutoSaveAiResultSetting(value: Boolean) = (currentState as? SettingsState.Content)
        ?.also { preferenceManager.autoSaveAiResults = value }
        ?.copy(autoSaveAiResults = value)
        ?.let(::setState)
        ?.also { analytics.logEvent(AutoSaveAiResultsChanged(value)) }

    fun changeFormAdvancedOptionsAlwaysShow(value: Boolean) = (currentState as? SettingsState.Content)
        ?.also { preferenceManager.formAdvancedOptionsAlwaysShow = value }
        ?.copy(formAdvancedOptionsAlwaysShow = value)
        ?.let(::setState)
        ?.also { analytics.logEvent(FormAdvancedOptionsAlwaysShowChanged(value)) }
    //endregion

    //region DEVELOPER OPTIONS METHODS
    fun devSpawnGalleryPage() = !devSpawnGalleryPageUseCase(PAGINATION_PAYLOAD_SIZE)
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(::errorLog)
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
