package com.shifthackz.aisdv1.presentation.screen.settings

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.extensions.EmptyLambda
import com.shifthackz.aisdv1.core.common.extensions.shouldUseNewMediaStore
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.model.Quadruple
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.entity.ColorToken
import com.shifthackz.aisdv1.domain.entity.DarkThemeToken
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.caching.ClearAppCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.GetStableDiffusionModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.SelectStableDiffusionModelUseCase
import com.shifthackz.aisdv1.domain.usecase.stabilityai.ObserveStabilityAiCreditsUseCase
import com.shifthackz.aisdv1.presentation.model.LaunchSource
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.navigation.router.drawer.DrawerRouter
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.aisdv1.presentation.screen.debug.DebugMenuAccessor
import com.shifthackz.aisdv1.presentation.screen.drawer.DrawerIntent
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.util.concurrent.TimeUnit
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

class SettingsViewModel(
    dispatchersProvider: DispatchersProvider,
    getStableDiffusionModelsUseCase: GetStableDiffusionModelsUseCase,
    observeStabilityAiCreditsUseCase: ObserveStabilityAiCreditsUseCase,
    private val selectStableDiffusionModelUseCase: SelectStableDiffusionModelUseCase,
    private val clearAppCacheUseCase: ClearAppCacheUseCase,
    private val schedulersProvider: SchedulersProvider,
    private val preferenceManager: PreferenceManager,
    private val debugMenuAccessor: DebugMenuAccessor,
    private val buildInfoProvider: BuildInfoProvider,
    private val mainRouter: MainRouter,
    private val drawerRouter: DrawerRouter,
) : MviRxViewModel<SettingsState, SettingsIntent, SettingsEffect>() {

    override val initialState = SettingsState()

    override val effectDispatcher = dispatchersProvider.immediate

    private val appVersionProducer = Flowable.fromCallable { buildInfoProvider.toString() }

    private val sdModelsProducer = getStableDiffusionModelsUseCase()
        .timeout(10L, TimeUnit.SECONDS)
        .toFlowable()
        .onErrorReturn { emptyList() }

    init {
        !Flowable.combineLatest(
            appVersionProducer,
            sdModelsProducer,
            preferenceManager.observe(),
            observeStabilityAiCreditsUseCase(),
            ::Quadruple,
        )
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = ::errorLog,
                onComplete = EmptyLambda,
                onNext = { (version, modelData, settings, credits) ->
                    updateState { state ->
                        state.copy(
                            loading = false,
                            serverSource = settings.source,
                            sdModels = modelData.map { (model, _) -> model.title },
                            sdModelSelected = settings.sdModel.takeIf(String::isNotBlank)
                                ?: modelData.firstOrNull { it.second }?.first?.title
                                ?: "",
                            stabilityAiCredits = credits,
                            localUseNNAPI = settings.localUseNNAPI,
                            backgroundGeneration = settings.backgroundGeneration,
                            monitorConnectivity = settings.monitorConnectivity,
                            autoSaveAiResults = settings.autoSaveAiResults,
                            saveToMediaStore = settings.saveToMediaStore,
                            formAdvancedOptionsAlwaysShow = settings.formAdvancedOptionsAlwaysShow,
                            formPromptTaggedInput = settings.formPromptTaggedInput,
                            useSystemColorPalette = settings.designUseSystemColorPalette,
                            useSystemDarkTheme = settings.designUseSystemDarkTheme,
                            darkTheme = settings.designDarkTheme,
                            colorToken = ColorToken.parse(settings.designColorToken),
                            darkThemeToken = DarkThemeToken.parse(settings.designDarkThemeToken),
                            galleryGrid = settings.galleryGrid,
                            developerMode = settings.developerMode,
                            appVersion = version,
                        )
                    }
                },
            )
    }

    override fun processIntent(intent: SettingsIntent) {
        when (intent) {
            SettingsIntent.Action.AppVersion -> if (debugMenuAccessor()) {
                emitEffect(SettingsEffect.DeveloperModeUnlocked)
            }

            SettingsIntent.Action.ClearAppCache.Request -> updateState {
                it.copy(screenModal = Modal.ClearAppCache)
            }

            SettingsIntent.Action.ClearAppCache.Confirm -> clearAppCache()

            SettingsIntent.Action.ReportProblem -> emitEffect(SettingsEffect.ShareLogFile)

            SettingsIntent.DismissDialog -> updateState {
                it.copy(screenModal = Modal.None)
            }

            SettingsIntent.NavigateConfiguration -> mainRouter.navigateToServerSetup(
                LaunchSource.SETTINGS
            )

            SettingsIntent.NavigateDeveloperMode -> mainRouter.navigateToDebugMenu()

            SettingsIntent.SdModel.OpenChooser -> updateState {
                it.copy(
                    screenModal = Modal.SelectSdModel(
                        models = it.sdModels,
                        selected = it.sdModelSelected,
                    ),
                )
            }

            is SettingsIntent.SdModel.Select -> selectSdModel(intent.model)

            is SettingsIntent.UpdateFlag.AdvancedFormVisibility -> {
                preferenceManager.formAdvancedOptionsAlwaysShow = intent.flag
            }

            is SettingsIntent.UpdateFlag.AutoSaveResult -> {
                preferenceManager.autoSaveAiResults = intent.flag
            }

            is SettingsIntent.UpdateFlag.MonitorConnection -> {
                preferenceManager.monitorConnectivity = intent.flag
            }

            is SettingsIntent.UpdateFlag.NNAPI -> {
                preferenceManager.localOnnxUseNNAPI = intent.flag
            }

            is SettingsIntent.UpdateFlag.TaggedInput -> {
                preferenceManager.formPromptTaggedInput = intent.flag
            }

            is SettingsIntent.UpdateFlag.SaveToMediaStore -> changeSaveToMediaStoreSetting(
                intent.flag
            )

            is SettingsIntent.LaunchUrl -> emitEffect(SettingsEffect.OpenUrl(intent.url))

            is SettingsIntent.Permission.Storage -> if (intent.isGranted) {
                preferenceManager.saveToMediaStore = true
            } else updateState {
                it.copy(
                    screenModal = Modal.ManualPermission(
                        permission = LocalizationR.string.permission_storage.asUiText(),
                    ),
                )
            }

            is SettingsIntent.Permission.Notification -> if (intent.isGranted) {
                preferenceManager.backgroundGeneration = true
            } else updateState {
                it.copy(
                    screenModal = Modal.ManualPermission(
                        permission = LocalizationR.string.permission_notifications.asUiText(),
                    ),
                )
            }

            is SettingsIntent.UpdateFlag.DynamicColors -> {
                preferenceManager.designUseSystemColorPalette = intent.flag
            }

            is SettingsIntent.UpdateFlag.SystemDarkTheme -> {
                preferenceManager.designUseSystemDarkTheme = intent.flag
            }

            is SettingsIntent.UpdateFlag.DarkTheme -> {
                preferenceManager.designDarkTheme = intent.flag
            }

            is SettingsIntent.NewColorToken -> {
                preferenceManager.designColorToken = "${intent.token}"
            }

            is SettingsIntent.NewDarkThemeToken -> {
                preferenceManager.designDarkThemeToken = "${intent.token}"
            }

            SettingsIntent.Action.PickLanguage -> updateState {
                it.copy(screenModal = Modal.Language)
            }

            SettingsIntent.Action.GalleryGrid.Pick -> updateState {
                it.copy(screenModal = Modal.GalleryGrid(it.galleryGrid))
            }

            is SettingsIntent.Action.GalleryGrid.Set -> {
                preferenceManager.galleryGrid = intent.grid
            }

            is SettingsIntent.Drawer -> when (intent.intent) {
                DrawerIntent.Close -> drawerRouter.closeDrawer()
                DrawerIntent.Open -> drawerRouter.openDrawer()
            }

            SettingsIntent.Action.Donate -> mainRouter.navigateToDonate()

            SettingsIntent.Action.OnBoarding -> mainRouter.navigateToOnBoarding(
                source = LaunchSource.SETTINGS,
            )

            is SettingsIntent.UpdateFlag.BackgroundGeneration -> {
                if (intent.flag) {
                    emitEffect(SettingsEffect.RequestPermission.Notifications)
                } else {
                    preferenceManager.backgroundGeneration = false
                }
            }
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
                emitEffect(SettingsEffect.RequestPermission.Storage)
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
