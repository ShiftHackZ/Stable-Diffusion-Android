package com.shifthackz.aisdv1.presentation.screen.settings

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.extensions.EmptyLambda
import com.shifthackz.aisdv1.core.common.extensions.shouldUseNewMediaStore
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.caching.ClearAppCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.GetStableDiffusionModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.SelectStableDiffusionModelUseCase
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupLaunchSource
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.subscribeBy

class SettingsViewModel(
    getStableDiffusionModelsUseCase: GetStableDiffusionModelsUseCase,
    private val selectStableDiffusionModelUseCase: SelectStableDiffusionModelUseCase,
    private val clearAppCacheUseCase: ClearAppCacheUseCase,
    private val schedulersProvider: SchedulersProvider,
    private val preferenceManager: PreferenceManager,
    private val buildInfoProvider: BuildInfoProvider,
    private val mainRouter: MainRouter,
) : MviRxViewModel<SettingsState, SettingsIntent, SettingsEffect>() {

    override val initialState = SettingsState()

    private val appVersionProducer = Flowable.fromCallable { buildInfoProvider.toString() }

    private val sdModelsProducer = getStableDiffusionModelsUseCase()
        .toFlowable()
        .onErrorReturn { emptyList() }

    init {
        !Flowable.combineLatest(
            appVersionProducer,
            sdModelsProducer,
            preferenceManager.observe(),
            ::Triple,
        )
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog, EmptyLambda) { (version, modelData, settings) ->
                updateState { state ->
                    state.copy(
                        loading = false,
                        sdModels = modelData.map { (model, _) -> model.title },
                        sdModelSelected = modelData.firstOrNull { it.second }?.first?.title ?: "",
                        localUseNNAPI = settings.localUseNNAPI,
                        monitorConnectivity = settings.monitorConnectivity,
                        autoSaveAiResults = settings.autoSaveAiResults,
                        saveToMediaStore = settings.saveToMediaStore,
                        formAdvancedOptionsAlwaysShow = settings.formAdvancedOptionsAlwaysShow,
                        formPromptTaggedInput = settings.formPromptTaggedInput,
                        appVersion = version,
                        showLocalUseNNAPI = settings.source == ServerSource.LOCAL,
                        showSdModelSelector = settings.source == ServerSource.AUTOMATIC1111,
                        showMonitorConnectionOption = settings.source == ServerSource.AUTOMATIC1111,
                        showFormAdvancedOption = settings.source != ServerSource.OPEN_AI,
                    )
                }
            }
    }

    override fun processIntent(intent: SettingsIntent) {
        when (intent) {
            SettingsIntent.Action.AppVersion -> mainRouter.navigateToDebugMenu()

            SettingsIntent.Action.ClearAppCache.Request -> updateState {
                it.copy(screenModal = Modal.ClearAppCache)
            }

            SettingsIntent.Action.ClearAppCache.Confirm -> clearAppCache()

            SettingsIntent.Action.ReportProblem -> emitEffect(SettingsEffect.ShareLogFile)

            SettingsIntent.DismissDialog -> updateState {
                it.copy(screenModal = Modal.None)
            }

            SettingsIntent.NavigateConfiguration -> mainRouter.navigateToServerSetup(
                ServerSetupLaunchSource.SETTINGS
            )

            SettingsIntent.SdModel.OpenChooser -> updateState {
                it.copy(
                    screenModal = Modal.SelectSdModel(
                        models = it.sdModels,
                        selected = it.sdModelSelected,
                    ),
                )
            }

            is SettingsIntent.SdModel.Select -> selectSdModel(intent.model)

            is SettingsIntent.UpdateFlag.AdvancedFormVisibility -> updateState {
                preferenceManager.formAdvancedOptionsAlwaysShow = intent.flag
                it.copy(formAdvancedOptionsAlwaysShow = intent.flag)
            }

            is SettingsIntent.UpdateFlag.AutoSaveResult -> updateState {
                preferenceManager.autoSaveAiResults = intent.flag
                it.copy(autoSaveAiResults = intent.flag)
            }

            is SettingsIntent.UpdateFlag.MonitorConnection -> updateState {
                preferenceManager.monitorConnectivity = intent.flag
                it.copy(monitorConnectivity = intent.flag)
            }

            is SettingsIntent.UpdateFlag.NNAPI -> updateState {
                preferenceManager.localUseNNAPI = intent.flag
                it.copy(localUseNNAPI = intent.flag)
            }

            is SettingsIntent.UpdateFlag.TaggedInput -> updateState {
                preferenceManager.formPromptTaggedInput = intent.flag
                it.copy(formPromptTaggedInput = intent.flag)
            }

            is SettingsIntent.UpdateFlag.SaveToMediaStore -> changeSaveToMediaStoreSetting(
                intent.flag
            )

            is SettingsIntent.LaunchUrl -> emitEffect(SettingsEffect.OpenUrl(intent.url))

            SettingsIntent.StoragePermissionGranted -> preferenceManager.saveToMediaStore = true
        }
    }

    //region BUSINESS LOGIC METHODS
    private fun selectSdModel(value: String) = !selectStableDiffusionModelUseCase(value)
        .doOnSubscribe {
            updateState { state ->
                state.copy(screenModal = Modal.Communicating(canCancel = false))
            }
        }
        .doFinally { processIntent(SettingsIntent.DismissDialog) }
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(::errorLog)

    private fun clearAppCache() = !clearAppCacheUseCase()
        .doOnSubscribe { processIntent(SettingsIntent.DismissDialog) }
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(::errorLog)

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
