package com.shifthackz.aisdv1.presentation.screen.settings

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.common.appbuild.BuildVersion
import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.Settings
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.caching.ClearAppCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.GetStableDiffusionModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.SelectStableDiffusionModelUseCase
import com.shifthackz.aisdv1.domain.usecase.stabilityai.ObserveStabilityAiCreditsUseCase
import com.shifthackz.aisdv1.presentation.mocks.mockStableDiffusionModels
import com.shifthackz.aisdv1.presentation.model.LaunchSource
import com.shifthackz.aisdv1.presentation.navigation.router.SettingsRouter
import com.shifthackz.aisdv1.presentation.screen.debug.DebugMenuAccessor
import com.shifthackz.aisdv1.presentation.screen.settings.model.SettingsIntent
import com.shifthackz.aisdv1.presentation.screen.settings.model.SettingsModal
import com.shifthackz.aisdv1.presentation.screen.settings.platform.SettingsPlatformActions
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val settings = MutableStateFlow(Settings(sdModel = "title_5598"))
    private val stabilityCredits = MutableStateFlow(5598f)
    private val getStableDiffusionModelsUseCase = mockk<GetStableDiffusionModelsUseCase>()
    private val observeStabilityAiCreditsUseCase = mockk<ObserveStabilityAiCreditsUseCase>()
    private val selectStableDiffusionModelUseCase = mockk<SelectStableDiffusionModelUseCase>()
    private val clearAppCacheUseCase = mockk<ClearAppCacheUseCase>()
    private val preferenceManager = mockk<PreferenceManager>(relaxed = true)
    private val router = TestSettingsRouter()
    private val platformActions = TestSettingsPlatformActions()
    private val linksProvider = TestLinksProvider()
    private lateinit var dispatchersProvider: DispatchersProvider
    private var viewModelScope: CoroutineScope? = null

    @Before
    fun initialize() {
        coEvery { getStableDiffusionModelsUseCase() } returns mockStableDiffusionModels
        every { observeStabilityAiCreditsUseCase() } returns stabilityCredits
        every { preferenceManager.observe() } returns settings
        every { preferenceManager.developerMode } returns false
    }

    @After
    fun tearDown() {
        viewModelScope?.cancel()
        viewModelScope = null
    }

    @Test
    fun `initialized, expected UI state updated with settings and models`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val actual = viewModel.state.value
        assertEquals(false, actual.loading)
        assertEquals("title_5598", actual.sdModelSelected)
        assertEquals(0f, actual.stabilityAiCredits)
        assertEquals("test-version", actual.appVersion)
    }

    @Test
    fun `given provider changed to stability ai, expected credits updated`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        settings.value = settings.value.copy(source = ServerSource.STABILITY_AI)
        advanceUntilIdle()

        assertEquals(5598f, viewModel.state.value.stabilityAiCredits)
    }

    @Test
    fun `given launch url intents, expected platform opens configured urls`() = runTest {
        val viewModel = createViewModel()

        viewModel.processIntent(SettingsIntent.LaunchUrl.OpenPolicy)
        viewModel.processIntent(SettingsIntent.LaunchUrl.OpenSourceCode)

        assertEquals(listOf("https://policy.test", "https://source.test"), platformActions.openedUrls)
    }

    @Test
    fun `given background generation enabled and permission granted, expected preference updated`() = runTest {
        val viewModel = createViewModel()

        viewModel.processIntent(SettingsIntent.UpdateFlag.BackgroundGeneration(true))
        advanceUntilIdle()

        verify { preferenceManager.backgroundGeneration = true }
    }

    @Test
    fun `given denied storage permission, expected manual permission modal`() = runTest {
        platformActions.requiresStoragePermissionForMediaStoreOverride = true
        platformActions.storagePermissionGranted = false
        val viewModel = createViewModel()

        viewModel.processIntent(SettingsIntent.UpdateFlag.SaveToMediaStore(true))
        advanceUntilIdle()

        assertTrue(viewModel.state.value.screenModal is SettingsModal.ManualPermission)
    }

    @Test
    fun `given navigation intent, expected router opens server setup from settings`() = runTest {
        val viewModel = createViewModel()

        viewModel.processIntent(SettingsIntent.NavigateConfiguration)

        assertEquals(LaunchSource.SETTINGS, router.serverSetupSource)
    }

    @Test
    fun `given language selected, expected preference updated and modal closed`() = runTest {
        val viewModel = createViewModel()

        viewModel.processIntent(SettingsIntent.Action.PickLanguage)
        viewModel.processIntent(SettingsIntent.Action.SetLanguage("uk"))

        verify { preferenceManager.languageCode = "uk" }
        assertEquals(SettingsModal.None, viewModel.state.value.screenModal)
    }

    private fun TestScope.createViewModel(): SettingsViewModel {
        val dispatcher = StandardTestDispatcher(testScheduler)
        dispatchersProvider = object : DispatchersProvider {
            override val io: CoroutineDispatcher = dispatcher
            override val ui: CoroutineDispatcher = dispatcher
            override val immediate: CoroutineDispatcher = dispatcher
        }
        viewModelScope = CoroutineScope(SupervisorJob() + dispatcher)
        return SettingsViewModel(
            dispatchersProvider = dispatchersProvider,
            getStableDiffusionModelsUseCase = getStableDiffusionModelsUseCase,
            observeStabilityAiCreditsUseCase = observeStabilityAiCreditsUseCase,
            selectStableDiffusionModelUseCase = selectStableDiffusionModelUseCase,
            clearAppCacheUseCase = clearAppCacheUseCase,
            preferenceManager = preferenceManager,
            debugMenuAccessor = DebugMenuAccessor(preferenceManager),
            buildInfoProvider = TestBuildInfoProvider,
            linksProvider = linksProvider,
            router = router,
            platformActions = platformActions,
        )
    }
}

private object TestBuildInfoProvider : BuildInfoProvider {
    override val isDebug: Boolean = true
    override val buildNumber: Int = 5598
    override val version: BuildVersion = BuildVersion()
    override val type: BuildType = BuildType.FOSS

    override fun toString(): String = "test-version"
}

private class TestSettingsRouter : SettingsRouter {
    var serverSetupSource: LaunchSource? = null

    override fun openDrawer() = Unit

    override fun closeDrawer() = Unit

    override fun navigateToServerSetup(source: LaunchSource) {
        serverSetupSource = source
    }

    override fun navigateToBenchmark() = Unit

    override fun navigateToDebugMenu() = Unit

    override fun navigateToDonate() = Unit

    override fun navigateToOnBoarding(source: LaunchSource) = Unit
}

private class TestSettingsPlatformActions : SettingsPlatformActions {
    var requiresStoragePermissionForMediaStoreOverride = false
    var storagePermissionGranted = true
    var notificationPermissionGranted = true
    val openedUrls = mutableListOf<String>()

    override val requiresStoragePermissionForMediaStore: Boolean
        get() = requiresStoragePermissionForMediaStoreOverride
    override val supportsBackgroundGeneration: Boolean = true
    override val backgroundGenerationWarningKey = "settings_item_background_generation_warning"

    override suspend fun requestStoragePermission(): Boolean = storagePermissionGranted

    override suspend fun requestNotificationPermission(): Boolean = notificationPermissionGranted

    override fun openUrl(url: String) {
        openedUrls += url
    }

    override fun shareLogFile() = Unit

    override fun showDeveloperModeUnlocked() = Unit

    override fun openAppSettings() = Unit
}

private class TestLinksProvider : LinksProvider {
    override val hordeUrl = ""
    override val hordeSignUpUrl = ""
    override val huggingFaceUrl = ""
    override val openAiInfoUrl = ""
    override val stabilityAiInfoUrl = ""
    override val falAiInfoUrl = ""
    override val privacyPolicyUrl = "https://policy.test"
    override val donateUrl = ""
    override val projectWebsiteUrl = "https://project.test"
    override val developerWebsiteUrl = "https://developer.test"
    override val gitHubSourceUrl = "https://source.test"
    override val licenseUrl = "https://license.test"
    override val setupInstructionsUrl = ""
    override val swarmUiInfoUrl = ""
    override val demoModeUrl = ""
    override val telegramCommunityUrl = "https://telegram.test"
    override val discordCommunityUrl = "https://discord.test"
}
