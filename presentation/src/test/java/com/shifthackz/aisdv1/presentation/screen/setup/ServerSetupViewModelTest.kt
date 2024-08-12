package com.shifthackz.aisdv1.presentation.screen.setup

import com.shifthackz.aisdv1.core.validation.common.CommonStringValidator
import com.shifthackz.aisdv1.core.validation.url.UrlValidator
import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.interactor.settings.SetupConnectionInterActor
import com.shifthackz.aisdv1.domain.interactor.wakelock.WakeLockInterActor
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.downloadable.DeleteModelUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.DownloadModelUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.GetLocalAiModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.huggingface.FetchAndGetHuggingFaceModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.GetConfigurationUseCase
import com.shifthackz.aisdv1.presentation.core.CoreViewModelTest
import com.shifthackz.aisdv1.presentation.mocks.mockHuggingFaceModels
import com.shifthackz.aisdv1.presentation.mocks.mockLocalAiModels
import com.shifthackz.aisdv1.presentation.mocks.mockServerSetupStateLocalModel
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.aisdv1.presentation.stub.stubSchedulersProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ServerSetupViewModelTest : CoreViewModelTest<ServerSetupViewModel>() {

    private val stubGetConfigurationUseCase = mockk<GetConfigurationUseCase>()
    private val stubGetLocalAiModelsUseCase = mockk<GetLocalAiModelsUseCase>()
    private val stubFetchAndGetHuggingFaceModelsUseCase = mockk<FetchAndGetHuggingFaceModelsUseCase>()
    private val stubUrlValidator = mockk<UrlValidator>()
    private val stubCommonStringValidator = mockk<CommonStringValidator>()
    private val stubSetupConnectionInterActor = mockk<SetupConnectionInterActor>()
    private val stubDownloadModelUseCase = mockk<DownloadModelUseCase>()
    private val stubDeleteModelUseCase = mockk<DeleteModelUseCase>()
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubWakeLockInterActor = mockk<WakeLockInterActor>()
    private val stubMainRouter = mockk<MainRouter>()

    override fun initializeViewModel() = ServerSetupViewModel(
        launchSource = ServerSetupLaunchSource.SETTINGS,
        getConfigurationUseCase = stubGetConfigurationUseCase,
        getLocalAiModelsUseCase = stubGetLocalAiModelsUseCase,
        fetchAndGetHuggingFaceModelsUseCase = stubFetchAndGetHuggingFaceModelsUseCase,
        urlValidator = stubUrlValidator,
        stringValidator = stubCommonStringValidator,
        setupConnectionInterActor = stubSetupConnectionInterActor,
        downloadModelUseCase = stubDownloadModelUseCase,
        deleteModelUseCase = stubDeleteModelUseCase,
        schedulersProvider = stubSchedulersProvider,
        preferenceManager = stubPreferenceManager,
        wakeLockInterActor = stubWakeLockInterActor,
        mainRouter = stubMainRouter,
    )

    @Before
    override fun initialize() {
        super.initialize()

        every {
            stubGetConfigurationUseCase()
        } returns Single.just(Configuration(serverUrl = "https://5598.is.my.favorite.com"))

        every {
            stubGetLocalAiModelsUseCase()
        } returns Single.just(mockLocalAiModels)

        every {
            stubFetchAndGetHuggingFaceModelsUseCase()
        } returns Single.just(mockHuggingFaceModels)
    }

    @Test
    fun `initialized, expected UI state updated with correct stub values`() {
        val state = viewModel.state.value
        Assert.assertEquals(true, state.huggingFaceModels.isNotEmpty())
        Assert.assertEquals(true, state.localModels.isNotEmpty())
        Assert.assertEquals("https://5598.is.my.favorite.com", state.serverUrl)
        Assert.assertEquals(ServerSetupState.AuthType.ANONYMOUS, state.authType)
    }

    @Test
    fun `given received AllowLocalCustomModel intent, expected Custom local model selected in UI state`() {
        viewModel.processIntent(ServerSetupIntent.AllowLocalCustomModel(true))
        val state = viewModel.state.value
        val expectedLocalModels = listOf(
            ServerSetupState.LocalModel(
                id = "CUSTOM",
                name = "Custom",
                size = "NaN",
                downloaded = false,
                selected = true,
            ),
            ServerSetupState.LocalModel(
                id = "1",
                name = "Model 1",
                size = "5 Gb",
                downloaded = false,
                selected = false,
            )
        )
        Assert.assertEquals(true, state.localCustomModel)
        Assert.assertEquals(expectedLocalModels, state.localModels)
    }

    @Test
    fun `given received DismissDialog intent, expected screenModal field in UI state is None`() {
        viewModel.processIntent(ServerSetupIntent.DismissDialog)
        val expected = Modal.None
        val actual = viewModel.state.value.screenModal
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given received LocalModel ClickReduce intent, model not downloaded, expected UI state is Downloading, wakeLocks called`() {
        every {
            stubDownloadModelUseCase(any())
        } returns Observable.just(DownloadState.Downloading(22))

        every {
            stubWakeLockInterActor.acquireWakelockUseCase()
        } returns Result.success(Unit)

        every {
            stubWakeLockInterActor.releaseWakeLockUseCase()
        } returns Result.success(Unit)

        val localModel = mockServerSetupStateLocalModel.copy(
            downloadState = DownloadState.Unknown,
        )
        val intent = ServerSetupIntent.LocalModel.ClickReduce(localModel)
        viewModel.processIntent(intent)

        val state = viewModel.state.value
        val expected = true
        val actual = state.localModels.any {
            it.downloadState == DownloadState.Downloading(22)
        }
        Assert.assertEquals(expected, actual)

        verify {
            stubWakeLockInterActor.acquireWakelockUseCase()
        }
        verify {
            stubWakeLockInterActor.releaseWakeLockUseCase()
        }
        verify {
            stubDownloadModelUseCase("1")
        }
    }

    @Test
    fun `given received LocalModel ClickReduce intent, model downloaded, expected screenModal field in UI state is DeleteLocalModelConfirm`() {
        val localModel = mockServerSetupStateLocalModel.copy(
            downloaded = true,
            downloadState = DownloadState.Unknown,
        )
        val intent = ServerSetupIntent.LocalModel.ClickReduce(localModel)
        viewModel.processIntent(intent)

        val expected = Modal.DeleteLocalModelConfirm(localModel)
        val actual = viewModel.state.value.screenModal
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given received LocalModel ClickReduce intent, model is downloading, expected UI state is Unknown`() {
        every {
            stubDeleteModelUseCase.invoke(any())
        } returns Completable.complete()

        val localModel = mockServerSetupStateLocalModel.copy(
            downloadState = DownloadState.Downloading(22),
        )
        val intent = ServerSetupIntent.LocalModel.ClickReduce(localModel)
        viewModel.processIntent(intent)

        val state = viewModel.state.value
        val expected = false
        val actual = state.localModels.any {
            it.downloadState == DownloadState.Downloading(22)
        }
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given received LocalModel DeleteConfirm intent, expected downloaded field is false for LocalModel UI state`() {
        every {
            stubDeleteModelUseCase(any())
        } returns Completable.complete()

        val localModel = mockServerSetupStateLocalModel.copy(
            downloaded = true,
            downloadState = DownloadState.Unknown,
        )
        val intent = ServerSetupIntent.LocalModel.DeleteConfirm(localModel)
        viewModel.processIntent(intent)

        runTest {
            val state = viewModel.state.value
            Assert.assertEquals(Modal.None, state.screenModal)
            Assert.assertEquals(false, state.localModels.find { it.id == "1" }!!.downloaded)
        }
        verify {
            stubDeleteModelUseCase("1")
        }
    }

    @Test
    fun `given received SelectLocalModel intent, expected passed LocalModel is selected in UI state`() {
        viewModel.processIntent(ServerSetupIntent.SelectLocalModel(mockServerSetupStateLocalModel))
        val state = viewModel.state.value
        Assert.assertEquals(true, state.localModels.find { it.id == "1" }!!.selected)
    }

    @Test
    fun `given received MainButtonClick intent, expected step field in UI state is CONFIGURE`() {
        viewModel.processIntent(ServerSetupIntent.MainButtonClick)
        val expected = ServerSetupState.Step.CONFIGURE
        val actual = viewModel.state.value.step
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given received UpdateDemoMode intent, expected demoMode field in UI state is true`() {
        viewModel.processIntent(ServerSetupIntent.UpdateDemoMode(true))
        val expected = true
        val actual = viewModel.state.value.demoMode
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given received UpdateHordeApiKey intent, expected hordeApiKey field in UI state is 5598`() {
        viewModel.processIntent(ServerSetupIntent.UpdateHordeApiKey("5598"))
        val expected = "5598"
        val actual = viewModel.state.value.hordeApiKey
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given received UpdateHordeDefaultApiKey intent, expected hordeDefaultApiKey field in UI state is true`() {
        viewModel.processIntent(ServerSetupIntent.UpdateHordeDefaultApiKey(true))
        val expected = true
        val actual = viewModel.state.value.hordeDefaultApiKey
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given received UpdateHuggingFaceApiKey intent, expected huggingFaceApiKey field in UI state is 5598`() {
        viewModel.processIntent(ServerSetupIntent.UpdateHuggingFaceApiKey("5598"))
        val expected = "5598"
        val actual = viewModel.state.value.huggingFaceApiKey
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given received UpdateHuggingFaceModel intent, expected huggingFaceModel field in UI state is 5598`() {
        viewModel.processIntent(ServerSetupIntent.UpdateHuggingFaceModel("5598"))
        val expected = "5598"
        val actual = viewModel.state.value.huggingFaceModel
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given received UpdateLogin intent, expected login field is 5598, loginValidationError is null in UI state`() {
        viewModel.processIntent(ServerSetupIntent.UpdateLogin("5598"))
        val state = viewModel.state.value
        Assert.assertEquals("5598", state.login)
        Assert.assertEquals(null, state.loginValidationError)
    }

    @Test
    fun `given received UpdatePassword intent, expected password field is 5598, passwordValidationError is null in UI state`() {
        viewModel.processIntent(ServerSetupIntent.UpdatePassword("5598"))
        val state = viewModel.state.value
        Assert.assertEquals("5598", state.password)
        Assert.assertEquals(null, state.passwordValidationError)
    }

    @Test
    fun `given received UpdateOpenAiApiKey intent, expected openAiApiKey field in UI state is 5598`() {
        viewModel.processIntent(ServerSetupIntent.UpdateOpenAiApiKey("5598"))
        val expected = "5598"
        val actual = viewModel.state.value.openAiApiKey
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given received UpdatePasswordVisibility intent, expected passwordVisible field in UI state is false`() {
        viewModel.processIntent(ServerSetupIntent.UpdatePasswordVisibility(true))
        val expected = false
        val actual = viewModel.state.value.passwordVisible
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given received UpdateServerMode intent, expected mode field in UI state is LOCAL`() {
        viewModel.processIntent(ServerSetupIntent.UpdateServerMode(ServerSource.LOCAL))
        val expected = ServerSource.LOCAL
        val actual = viewModel.state.value.mode
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given received NavigateBack intent, expected router navigateBack() method called`() {
        every {
            stubMainRouter.navigateBack()
        } returns Unit

        viewModel.processIntent(ServerSetupIntent.NavigateBack)

        verify {
            stubMainRouter.navigateBack()
        }
    }

    @Test
    fun `given received UpdateStabilityAiApiKey intent, expected stabilityAiApiKey field in UI state is 5598`() {
        viewModel.processIntent(ServerSetupIntent.UpdateStabilityAiApiKey("5598"))
        runTest {
            val expected = "5598"
            val actual = viewModel.state.value.stabilityAiApiKey
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received ConnectToLocalHost intent, expected success, router navigateToHomeScreen() method called, preference forceSetupAfterUpdate is false, dialog is None`() {
        every {
            stubSetupConnectionInterActor.connectToA1111(any(), any(), any())
        } returns Single.just(Result.success(Unit))

        every {
            stubMainRouter.navigateToHomeScreen()
        } returns Unit

        every {
            stubPreferenceManager::forceSetupAfterUpdate.set(any())
        } returns Unit

        viewModel.processIntent(ServerSetupIntent.ConnectToLocalHost)

        verify {
            stubMainRouter.navigateToHomeScreen()
        }
    }
}
