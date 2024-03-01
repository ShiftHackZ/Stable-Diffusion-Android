package com.shifthackz.aisdv1.presentation.screen.settings

import com.shifthackz.aisdv1.core.common.extensions.EmptyLambda
import com.shifthackz.aisdv1.core.common.extensions.shouldUseNewMediaStore
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.feature.analytics.Analytics
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.caching.ClearAppCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.SelectStableDiffusionModelUseCase
import com.shifthackz.aisdv1.presentation.features.SdModelSelected
import com.shifthackz.aisdv1.presentation.features.SettingsCacheCleared
import com.shifthackz.aisdv1.presentation.navigation.Router
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupLaunchSource
import io.reactivex.rxjava3.kotlin.subscribeBy

class SettingsViewModel(
    private val settingsStateProducer: SettingsStateProducer,
    private val selectStableDiffusionModelUseCase: SelectStableDiffusionModelUseCase,
    private val clearAppCacheUseCase: ClearAppCacheUseCase,
    private val schedulersProvider: SchedulersProvider,
    private val preferenceManager: PreferenceManager,
    private val analytics: Analytics,
    private val router: Router,
) : MviRxViewModel<SettingsState, SettingsIntent, SettingsEffect>() {

    override val initialState = SettingsState()

    init {
        !settingsStateProducer()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog, EmptyLambda) { state ->
                updateState { state }
            }
    }

    override fun processIntent(intent: SettingsIntent) {
        when (intent) {
            SettingsIntent.Action.AppVersion -> router.navigateToDebugMenu()

            SettingsIntent.Action.ClearAppCache.Request -> updateState {
                it.copy(screenDialog = SettingsState.Dialog.ClearAppCache)
            }

            SettingsIntent.Action.ClearAppCache.Confirm -> clearAppCache()

            SettingsIntent.Action.ReportProblem -> emitEffect(SettingsEffect.ShareLogFile)

            SettingsIntent.DismissDialog -> updateState {
                it.copy(screenDialog = SettingsState.Dialog.None)
            }

            SettingsIntent.NavigateConfiguration -> router.navigateToServerSetup(
                ServerSetupLaunchSource.SETTINGS
            )

            SettingsIntent.SdModel.OpenChooser -> updateState {
                it.copy(
                    screenDialog = SettingsState.Dialog.SelectSdModel(
                        it.sdModels,
                        it.sdModelSelected
                    )
                )
            }

            is SettingsIntent.SdModel.Select -> selectStableDiffusionModel(intent.model)

            is SettingsIntent.UpdateFlag.AdvancedFormVisibility -> updateState {
                it.copy(formAdvancedOptionsAlwaysShow = intent.flag)
            }

            is SettingsIntent.UpdateFlag.AutoSaveResult -> updateState {
                it.copy(autoSaveAiResults = intent.flag)
            }

            is SettingsIntent.UpdateFlag.MonitorConnection -> updateState {
                it.copy(monitorConnectivity = intent.flag)
            }

            is SettingsIntent.UpdateFlag.NNAPI -> updateState {
                it.copy(localUseNNAPI = intent.flag)
            }

            is SettingsIntent.UpdateFlag.SaveToMediaStore -> changeSaveToMediaStoreSetting(
                intent.flag
            )

            is SettingsIntent.LaunchUrl -> emitEffect(SettingsEffect.OpenUrl(intent.url))
        }
    }

    //region BUSINESS LOGIC METHODS
    private fun selectStableDiffusionModel(value: String) =
        !selectStableDiffusionModelUseCase(value)
            .andThen(settingsStateProducer())
            .doOnSubscribe {
                updateState {
                    it.copy(screenDialog = SettingsState.Dialog.Communicating)
                }
            }
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog) { state ->
                analytics.logEvent(SdModelSelected(value))
                updateState { state }
            }

    private fun clearAppCache() = !clearAppCacheUseCase()
        .andThen(settingsStateProducer())
        .doOnSubscribe { processIntent(SettingsIntent.DismissDialog) }
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(::errorLog) { state ->
            analytics.logEvent(SettingsCacheCleared)
            updateState { state }
        }

    private fun changeSaveToMediaStoreSetting(value: Boolean) {
        val oldImpl: () -> Unit = {
            if (value) {
                emitEffect(SettingsEffect.RequestStoragePermission)
            } else {
                preferenceManager.saveToMediaStore = false
                updateState { it.copy(saveToMediaStore = false) }
            }
        }
        val newImpl: () -> Unit = {
            preferenceManager.saveToMediaStore = value
            updateState { it.copy(saveToMediaStore = false) }
        }
        if (shouldUseNewMediaStore()) newImpl() else oldImpl()
    }
}
