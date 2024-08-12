package com.shifthackz.aisdv1.presentation.screen.settings

import android.os.Build
import app.cash.turbine.test
import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.domain.entity.ColorToken
import com.shifthackz.aisdv1.domain.entity.DarkThemeToken
import com.shifthackz.aisdv1.domain.entity.Settings
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.caching.ClearAppCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.GetStableDiffusionModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.SelectStableDiffusionModelUseCase
import com.shifthackz.aisdv1.domain.usecase.stabilityai.ObserveStabilityAiCreditsUseCase
import com.shifthackz.aisdv1.presentation.core.CoreViewModelTest
import com.shifthackz.aisdv1.presentation.mocks.mockStableDiffusionModels
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.navigation.router.drawer.DrawerRouter
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.aisdv1.presentation.screen.debug.DebugMenuAccessor
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupLaunchSource
import com.shifthackz.aisdv1.presentation.stub.stubSchedulersProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier

class SettingsViewModelTest : CoreViewModelTest<SettingsViewModel>() {

    private val stubSettings = BehaviorSubject.createDefault(Settings())
    private val stubStabilityCredits = BehaviorSubject.createDefault(5598f)
    private val stubGetStableDiffusionModelsUseCase = mockk<GetStableDiffusionModelsUseCase>()
    private val stubObserveStabilityAiCreditsUseCase = mockk<ObserveStabilityAiCreditsUseCase>()
    private val stubSelectStableDiffusionModelUseCase = mockk<SelectStableDiffusionModelUseCase>()
    private val stubClearAppCacheUseCase = mockk<ClearAppCacheUseCase>()
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubBuildInfoProvider = mockk<BuildInfoProvider>()
    private val stubMainRouter = mockk<MainRouter>()
    private val stubDrawerRouter = mockk<DrawerRouter>()
    private val stubDebugMenuAccessor = mockk<DebugMenuAccessor>()

    override fun initializeViewModel() = SettingsViewModel(
        getStableDiffusionModelsUseCase = stubGetStableDiffusionModelsUseCase,
        observeStabilityAiCreditsUseCase = stubObserveStabilityAiCreditsUseCase,
        selectStableDiffusionModelUseCase = stubSelectStableDiffusionModelUseCase,
        clearAppCacheUseCase = stubClearAppCacheUseCase,
        schedulersProvider = stubSchedulersProvider,
        preferenceManager = stubPreferenceManager,
        buildInfoProvider = stubBuildInfoProvider,
        mainRouter = stubMainRouter,
        drawerRouter = stubDrawerRouter,
        debugMenuAccessor = stubDebugMenuAccessor,
    )

    @Before
    override fun initialize() {
        super.initialize()

        every {
            stubBuildInfoProvider.toString()
        } returns "5.5.98"

        every {
            stubGetStableDiffusionModelsUseCase()
        } returns Single.just(mockStableDiffusionModels)

        every {
            stubPreferenceManager.observe()
        } returns stubSettings.toFlowable(BackpressureStrategy.LATEST)

        every {
            stubObserveStabilityAiCreditsUseCase()
        } returns stubStabilityCredits.toFlowable(BackpressureStrategy.LATEST)
    }

    @Test
    fun `initialized, expected UI state updated with correct stub values`() {
        runTest {
            val expected = "5.5.98"
            val actual = viewModel.state.value.appVersion
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Action AppVersion intent, expected DeveloperModeUnlocked effect delivered to effect collector`() {
        every {
            stubDebugMenuAccessor.invoke()
        } returns true

        viewModel.processIntent(SettingsIntent.Action.AppVersion)

        runTest {
            viewModel.effect.test {
                Assert.assertEquals(SettingsEffect.DeveloperModeUnlocked, awaitItem())
            }
        }
    }

    @Test
    fun `given received Action ClearAppCache Request intent, expected screenModal field in UI state is ClearAppCache`() {
        viewModel.processIntent(SettingsIntent.Action.ClearAppCache.Request)
        runTest {
            val expected = Modal.ClearAppCache
            val actual = viewModel.state.value.screenModal
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Action ClearAppCache Confirm intent, expected screenModal field in UI state is None`() {
        every {
            stubClearAppCacheUseCase()
        } returns Completable.complete()

        viewModel.processIntent(SettingsIntent.Action.ClearAppCache.Confirm)

        runTest {
            val expected = Modal.None
            val actual = viewModel.state.value.screenModal
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Action ReportProblem intent, expected ShareLogFile effect delivered to effect collector`() {
        viewModel.processIntent(SettingsIntent.Action.ReportProblem)
        runTest {
            viewModel.effect.test {
                Assert.assertEquals(SettingsEffect.ShareLogFile, awaitItem())
            }
        }
    }

    @Test
    fun `given received DismissDialog intent, expected screenModal field in UI state is None`() {
        viewModel.processIntent(SettingsIntent.DismissDialog)
        runTest {
            val expected = Modal.None
            val actual = viewModel.state.value.screenModal
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received NavigateConfiguration intent, expected router navigateToServerSetup() method called`() {
        every {
            stubMainRouter.navigateToServerSetup(any())
        } returns Unit

        viewModel.processIntent(SettingsIntent.NavigateConfiguration)

        verify {
            stubMainRouter.navigateToServerSetup(ServerSetupLaunchSource.SETTINGS)
        }
    }

    @Test
    fun `given received SdModel OpenChooser intent, expected screenModal field in UI state is SelectSdModel`() {
        viewModel.processIntent(SettingsIntent.SdModel.OpenChooser)
        runTest {
            val expected = Modal.SelectSdModel(
                models = listOf("title_5598", "title_151297"),
                selected = "title_5598",
            )
            val actual = viewModel.state.value.screenModal
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received SdModel OpenChooser intent, expected screenModal field in UI state is None`() {
        every {
            stubSelectStableDiffusionModelUseCase(any())
        } returns Completable.complete()

        viewModel.processIntent(SettingsIntent.SdModel.Select("title_151297"))

        runTest {
            val expected = Modal.None
            val actual = viewModel.state.value.screenModal
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received UpdateFlag AdvancedFormVisibility intent, expected formAdvancedOptionsAlwaysShow preference updated`() {
        every {
            stubPreferenceManager::formAdvancedOptionsAlwaysShow.set(any())
        } returns Unit

        viewModel.processIntent(SettingsIntent.UpdateFlag.AdvancedFormVisibility(true))

        verify {
            stubPreferenceManager::formAdvancedOptionsAlwaysShow.set(true)
        }
    }

    @Test
    fun `given received UpdateFlag AutoSaveResult intent, expected autoSaveAiResults preference updated`() {
        every {
            stubPreferenceManager::autoSaveAiResults.set(any())
        } returns Unit

        viewModel.processIntent(SettingsIntent.UpdateFlag.AutoSaveResult(true))

        verify {
            stubPreferenceManager::autoSaveAiResults.set(true)
        }
    }

    @Test
    fun `given received UpdateFlag MonitorConnection intent, expected monitorConnectivity preference updated`() {
        every {
            stubPreferenceManager::monitorConnectivity.set(any())
        } returns Unit

        viewModel.processIntent(SettingsIntent.UpdateFlag.MonitorConnection(true))

        verify {
            stubPreferenceManager::monitorConnectivity.set(true)
        }
    }

    @Test
    fun `given received UpdateFlag NNAPI intent, expected localUseNNAPI preference updated`() {
        every {
            stubPreferenceManager::localUseNNAPI.set(any())
        } returns Unit

        viewModel.processIntent(SettingsIntent.UpdateFlag.NNAPI(true))

        verify {
            stubPreferenceManager::localUseNNAPI.set(true)
        }
    }

    @Test
    fun `given received UpdateFlag TaggedInput intent, expected formPromptTaggedInput preference updated`() {
        every {
            stubPreferenceManager::formPromptTaggedInput.set(any())
        } returns Unit

        viewModel.processIntent(SettingsIntent.UpdateFlag.TaggedInput(true))

        verify {
            stubPreferenceManager::formPromptTaggedInput.set(true)
        }
    }

    @Test
    fun `given received UpdateFlag SaveToMediaStore intent with true value, app running on Android SDK 34, expected newImpl() called, saveToMediaStore preference updated, saveToMediaStore field in UI state is false`() {
        every {
            stubPreferenceManager::saveToMediaStore.set(any())
        } returns Unit

        mockSdkInt(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)

        viewModel.processIntent(SettingsIntent.UpdateFlag.SaveToMediaStore(true))

        runTest {
            val expected = false
            val actual = viewModel.state.value.saveToMediaStore
            Assert.assertEquals(expected, actual)
        }
        verify {
            stubPreferenceManager::saveToMediaStore.set(true)
        }
    }

    @Test
    fun `given received UpdateFlag SaveToMediaStore intent with false value, app running on Android SDK 34, expected newImpl() called, saveToMediaStore preference updated, saveToMediaStore field in UI state is false`() {
        every {
            stubPreferenceManager::saveToMediaStore.set(any())
        } returns Unit

        mockSdkInt(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)

        viewModel.processIntent(SettingsIntent.UpdateFlag.SaveToMediaStore(false))

        runTest {
            val expected = false
            val actual = viewModel.state.value.saveToMediaStore
            Assert.assertEquals(expected, actual)
        }
        verify {
            stubPreferenceManager::saveToMediaStore.set(false)
        }
    }

    @Test
    fun `given received UpdateFlag SaveToMediaStore intent with true value, app running on Android SDK 26, expected oldImpl() called, RequestPermission Storage effect delivered to effect collector`() {
        mockSdkInt(Build.VERSION_CODES.O)

        viewModel.processIntent(SettingsIntent.UpdateFlag.SaveToMediaStore(true))

        runTest {
            val expected = SettingsEffect.RequestPermission.Storage
            val actual = viewModel.effect.firstOrNull()
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received UpdateFlag SaveToMediaStore intent with false value, app running on Android SDK 26, expected oldImpl() called,  saveToMediaStore preference updated, saveToMediaStore field in UI state is false`() {
        every {
            stubPreferenceManager::saveToMediaStore.set(any())
        } returns Unit

        mockSdkInt(Build.VERSION_CODES.O)

        viewModel.processIntent(SettingsIntent.UpdateFlag.SaveToMediaStore(false))

        runTest {
            val expected = false
            val actual = viewModel.state.value.saveToMediaStore
            Assert.assertEquals(expected, actual)
        }
        verify {
            stubPreferenceManager::saveToMediaStore.set(false)
        }
    }

    @Test
    fun `given received StoragePermissionGranted intent, expected saveToMediaStore preference set to true`() {
        every {
            stubPreferenceManager::saveToMediaStore.set(any())
        } returns Unit

        viewModel.processIntent(SettingsIntent.Permission.Storage(true))

        verify {
            stubPreferenceManager::saveToMediaStore.set(true)
        }
    }

    @Test
    fun `given received UpdateFlag DynamicColors intent, expected designUseSystemColorPalette preference updated`() {
        every {
            stubPreferenceManager::designUseSystemColorPalette.set(any())
        } returns Unit

        viewModel.processIntent(SettingsIntent.UpdateFlag.DynamicColors(true))

        verify {
            stubPreferenceManager::designUseSystemColorPalette.set(true)
        }
    }

    @Test
    fun `given received UpdateFlag SystemDarkTheme intent, expected designUseSystemDarkTheme preference updated`() {
        every {
            stubPreferenceManager::designUseSystemDarkTheme.set(any())
        } returns Unit

        viewModel.processIntent(SettingsIntent.UpdateFlag.SystemDarkTheme(true))

        verify {
            stubPreferenceManager::designUseSystemDarkTheme.set(true)
        }
    }

    @Test
    fun `given received UpdateFlag DarkTheme intent, expected designDarkTheme preference updated`() {
        every {
            stubPreferenceManager::designDarkTheme.set(any())
        } returns Unit

        viewModel.processIntent(SettingsIntent.UpdateFlag.DarkTheme(true))

        verify {
            stubPreferenceManager::designDarkTheme.set(true)
        }
    }

    @Test
    fun `given received NewColorToken intent, expected designColorToken preference updated`() {
        every {
            stubPreferenceManager::designColorToken.set(any())
        } returns Unit

        viewModel.processIntent(SettingsIntent.NewColorToken(ColorToken.MAUVE))

        verify {
            stubPreferenceManager::designColorToken.set("${ColorToken.MAUVE}")
        }
    }

    @Test
    fun `given received NewDarkThemeToken intent, expected designDarkThemeToken preference updated`() {
        every {
            stubPreferenceManager::designDarkThemeToken.set(any())
        } returns Unit

        viewModel.processIntent(SettingsIntent.NewDarkThemeToken(DarkThemeToken.MACCHIATO))

        verify {
            stubPreferenceManager::designDarkThemeToken.set("${DarkThemeToken.MACCHIATO}")
        }
    }

    @Test
    fun `given received Action PickLanguage intent, expected screenModal field in UI state is Language`() {
        viewModel.processIntent(SettingsIntent.Action.PickLanguage)
        runTest {
            val expected = Modal.Language
            val actual = viewModel.state.value.screenModal
            Assert.assertEquals(expected, actual)
        }
    }

    private fun mockSdkInt(sdkInt: Int) {
        val sdkIntField = Build.VERSION::class.java.getField("SDK_INT")
        sdkIntField.isAccessible = true
        getModifiersField().also {
            it.isAccessible = true
            it.set(sdkIntField, sdkIntField.modifiers and Modifier.FINAL.inv())
        }
        sdkIntField.set(null, sdkInt)
    }

    private fun getModifiersField(): Field {
        return try {
            Field::class.java.getDeclaredField("modifiers")
        } catch (e: NoSuchFieldException) {
            try {
                val getDeclaredFields0: Method =
                    Class::class.java.getDeclaredMethod("getDeclaredFields0", Boolean::class.javaPrimitiveType)
                getDeclaredFields0.isAccessible = true
                val fields = getDeclaredFields0.invoke(Field::class.java, false) as Array<Field>
                for (field in fields) {
                    if ("modifiers" == field.name) {
                        return field
                    }
                }
            } catch (ex: ReflectiveOperationException) {
                e.addSuppressed(ex)
            }
            throw e
        }
    }
}
