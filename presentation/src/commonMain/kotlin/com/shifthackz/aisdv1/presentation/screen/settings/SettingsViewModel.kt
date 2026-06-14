package com.shifthackz.aisdv1.presentation.screen.settings

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.core.common.model.Quadruple
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.core.mvi.EmptyEffect
import com.shifthackz.aisdv1.domain.entity.ColorToken
import com.shifthackz.aisdv1.domain.entity.DarkThemeToken
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.caching.ClearAppCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.GetStableDiffusionModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.SelectStableDiffusionModelUseCase
import com.shifthackz.aisdv1.domain.usecase.stabilityai.ObserveStabilityAiCreditsUseCase
import com.shifthackz.aisdv1.presentation.modal.language.applyAppLanguage
import com.shifthackz.aisdv1.presentation.model.LaunchSource
import com.shifthackz.aisdv1.presentation.navigation.router.SettingsRouter
import com.shifthackz.aisdv1.presentation.screen.debug.DebugMenuAccessor
import com.shifthackz.aisdv1.presentation.screen.drawer.DrawerIntent
import com.shifthackz.aisdv1.presentation.screen.settings.model.SettingsIntent
import com.shifthackz.aisdv1.presentation.screen.settings.model.SettingsModal
import com.shifthackz.aisdv1.presentation.screen.settings.model.SettingsState
import com.shifthackz.aisdv1.presentation.screen.settings.platform.SettingsPlatformActions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeout

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModel(
    dispatchersProvider: DispatchersProvider,
    getStableDiffusionModelsUseCase: GetStableDiffusionModelsUseCase,
    observeStabilityAiCreditsUseCase: ObserveStabilityAiCreditsUseCase,
    private val selectStableDiffusionModelUseCase: SelectStableDiffusionModelUseCase,
    private val clearAppCacheUseCase: ClearAppCacheUseCase,
    private val preferenceManager: PreferenceManager,
    private val debugMenuAccessor: DebugMenuAccessor,
    private val buildInfoProvider: BuildInfoProvider,
    private val linksProvider: LinksProvider,
    private val router: SettingsRouter,
    private val platformActions: SettingsPlatformActions,
    private val onError: (Throwable) -> Unit = {},
) : BaseMviViewModel<SettingsState, SettingsIntent, EmptyEffect>(
    initialState = SettingsState(),
    effectDispatcher = dispatchersProvider.immediate,
) {

    private val ioDispatcher = dispatchersProvider.io
    private val immediateDispatcher = dispatchersProvider.immediate

    private val appVersionProducer = flow { emit(buildInfoProvider.toString()) }

    private val sdModelsProducer = preferenceManager.observe()
        .map {
            SdModelsRefreshKey(
                source = it.source,
                serverUrl = it.serverUrl,
                demoMode = it.demoMode,
                sdModel = it.sdModel,
            )
        }
        .distinctUntilChanged()
        .map {
            runCatching {
                withTimeout(10_000L) { getStableDiffusionModelsUseCase() }
            }.getOrDefault(emptyList())
        }

    private val stabilityCreditsProducer = preferenceManager.observe()
        .map { it.source }
        .distinctUntilChanged()
        .flatMapLatest { source ->
            if (source == ServerSource.STABILITY_AI) {
                observeStabilityAiCreditsUseCase()
            } else {
                flowOf(0f)
            }
        }

    init {
        launch(ioDispatcher) {
            combine(
                appVersionProducer,
                sdModelsProducer,
                preferenceManager.observe(),
                stabilityCreditsProducer,
                ::Quadruple,
            )
                .catch { onError(it) }
                .collect { (version, modelData, settings, credits) ->
                    if (settings.backgroundGeneration && !platformActions.supportsBackgroundGeneration) {
                        preferenceManager.backgroundGeneration = false
                    }
                    val modelTitles = modelData.map { (model, _) -> model.title }
                    val selectedRemoteModel = modelData
                        .firstOrNull { (_, selected) -> selected }
                        ?.first
                        ?.title
                    updateState { state ->
                        state.copy(
                            loading = false,
                            serverSource = settings.source,
                            sdModels = modelTitles,
                            sdModelSelected = selectedRemoteModel
                                ?: settings.sdModel.takeIf(modelTitles::contains)
                                ?: modelTitles.firstOrNull()
                                ?: "",
                            stabilityAiCredits = credits,
                            localUseNNAPI = settings.localUseNNAPI,
                            backgroundGenerationAvailable = platformActions.supportsBackgroundGeneration,
                            backgroundGeneration = settings.backgroundGeneration &&
                                platformActions.supportsBackgroundGeneration,
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
                }
        }
    }

    override fun processIntent(intent: SettingsIntent) {
        when (intent) {
            SettingsIntent.Action.AppVersion -> if (debugMenuAccessor()) {
                platformActions.showDeveloperModeUnlocked()
            }

            SettingsIntent.Action.ClearAppCache.Request -> updateState {
                it.copy(screenModal = SettingsModal.ClearAppCache)
            }

            SettingsIntent.Action.ClearAppCache.Confirm -> clearAppCache()

            SettingsIntent.Action.ReportProblem -> platformActions.shareLogFile()

            SettingsIntent.DismissDialog -> updateState {
                it.copy(screenModal = SettingsModal.None)
            }

            SettingsIntent.NavigateConfiguration -> router.navigateToServerSetup(
                LaunchSource.SETTINGS,
            )

            SettingsIntent.NavigateBenchmark -> router.navigateToBenchmark()

            SettingsIntent.NavigateDeveloperMode -> router.navigateToDebugMenu()

            SettingsIntent.SdModel.OpenChooser -> updateState {
                it.copy(
                    screenModal = SettingsModal.SelectSdModel(
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

            is SettingsIntent.UpdateFlag.SaveToMediaStore -> {
                changeSaveToMediaStoreSetting(intent.flag)
            }

            SettingsIntent.LaunchUrl.OpenPolicy -> {
                platformActions.openUrl(linksProvider.privacyPolicyUrl)
            }

            SettingsIntent.LaunchUrl.OpenSourceCode -> {
                platformActions.openUrl(linksProvider.gitHubSourceUrl)
            }

            SettingsIntent.LaunchUrl.OpenProjectWebsite -> {
                platformActions.openUrl(linksProvider.projectWebsiteUrl)
            }

            SettingsIntent.LaunchUrl.OpenDeveloperWebsite -> {
                platformActions.openUrl(linksProvider.developerWebsiteUrl)
            }

            SettingsIntent.LaunchUrl.OpenLicense -> {
                platformActions.openUrl(linksProvider.licenseUrl)
            }

            SettingsIntent.LaunchUrl.OpenTelegramCommunity -> {
                platformActions.openUrl(linksProvider.telegramCommunityUrl)
            }

            SettingsIntent.LaunchUrl.OpenDiscordCommunity -> {
                platformActions.openUrl(linksProvider.discordCommunityUrl)
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
                it.copy(screenModal = SettingsModal.Language)
            }

            is SettingsIntent.Action.SetLanguage -> {
                preferenceManager.languageCode = intent.languageCode
                applyAppLanguage(intent.languageCode)
                updateState { it.copy(screenModal = SettingsModal.None) }
            }

            SettingsIntent.Action.GalleryGrid.Pick -> updateState {
                it.copy(screenModal = SettingsModal.GalleryGrid(it.galleryGrid))
            }

            is SettingsIntent.Action.GalleryGrid.Set -> {
                preferenceManager.galleryGrid = intent.grid
                updateState { it.copy(screenModal = SettingsModal.None) }
            }

            is SettingsIntent.Drawer -> when (intent.intent) {
                DrawerIntent.Close -> router.closeDrawer()
                DrawerIntent.Open -> router.openDrawer()
            }

            SettingsIntent.Action.Donate -> router.navigateToDonate()

            SettingsIntent.Action.OnBoarding -> router.navigateToOnBoarding(
                source = LaunchSource.SETTINGS,
            )

            is SettingsIntent.UpdateFlag.BackgroundGeneration -> {
                changeBackgroundGenerationSetting(intent.flag)
            }
        }
    }

    private fun selectSdModel(value: String) {
        updateState { state ->
            state.copy(screenModal = SettingsModal.Communicating)
        }

        launch(ioDispatcher) {
            try {
                selectStableDiffusionModelUseCase(value)
            } catch (t: Throwable) {
                onError(t)
            } finally {
                processIntent(SettingsIntent.DismissDialog)
            }
        }
    }

    private fun clearAppCache() {
        updateState { it.copy(screenModal = SettingsModal.Communicating) }
        launch(ioDispatcher) {
            runCatching { clearAppCacheUseCase() }
                .onFailure(onError)
            processIntent(SettingsIntent.DismissDialog)
        }
    }

    private fun changeSaveToMediaStoreSetting(value: Boolean) {
        if (!value) {
            preferenceManager.saveToMediaStore = false
            updateState { it.copy(saveToMediaStore = false) }
            return
        }

        if (!platformActions.requiresStoragePermissionForMediaStore) {
            preferenceManager.saveToMediaStore = true
            updateState { it.copy(saveToMediaStore = true) }
            return
        }

        launch(immediateDispatcher) {
            if (platformActions.requestStoragePermission()) {
                preferenceManager.saveToMediaStore = true
            } else {
                updateState {
                    it.copy(
                        screenModal = SettingsModal.ManualPermission(
                            permission = Localization.string("permission_storage"),
                        ),
                    )
                }
            }
        }
    }

    private fun changeBackgroundGenerationSetting(value: Boolean) {
        if (!platformActions.supportsBackgroundGeneration) {
            preferenceManager.backgroundGeneration = false
            updateState {
                it.copy(
                    backgroundGeneration = false,
                    backgroundGenerationAvailable = false,
                )
            }
            return
        }

        if (!value) {
            preferenceManager.backgroundGeneration = false
            updateState { it.copy(backgroundGeneration = false) }
            return
        }

        launch(immediateDispatcher) {
            if (platformActions.requestNotificationPermission()) {
                preferenceManager.backgroundGeneration = true
            } else {
                updateState {
                    it.copy(
                        screenModal = SettingsModal.ManualPermission(
                            permission = Localization.string("permission_notifications"),
                        ),
                    )
                }
            }
        }
    }
}

private data class SdModelsRefreshKey(
    val source: com.shifthackz.aisdv1.domain.entity.ServerSource,
    val serverUrl: String,
    val demoMode: Boolean,
    val sdModel: String,
)
