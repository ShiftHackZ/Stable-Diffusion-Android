package com.shifthackz.aisdv1.presentation.widget.engine

import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.Settings
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.downloadable.ObserveLocalOnnxModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.huggingface.FetchAndGetHuggingFaceModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.GetStableDiffusionModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.SelectStableDiffusionModelUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.GetConfigurationUseCase
import com.shifthackz.aisdv1.domain.usecase.stabilityai.FetchAndGetStabilityAiEnginesUseCase
import com.shifthackz.aisdv1.domain.usecase.swarmmodel.FetchAndGetSwarmUiModelsUseCase
import com.shifthackz.aisdv1.presentation.core.CoreViewModelTest
import com.shifthackz.aisdv1.presentation.mocks.mockHuggingFaceModels
import com.shifthackz.aisdv1.presentation.mocks.mockLocalAiModels
import com.shifthackz.aisdv1.presentation.mocks.mockStabilityAiEngines
import com.shifthackz.aisdv1.presentation.mocks.mockStableDiffusionModels
import com.shifthackz.aisdv1.presentation.mocks.mockSwarmUiModels
import com.shifthackz.aisdv1.presentation.stub.stubDispatchersProvider
import com.shifthackz.aisdv1.presentation.stub.stubSchedulersProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class EngineSelectionViewModelTest : CoreViewModelTest<EngineSelectionViewModel>() {

    private val stubSettings = BehaviorSubject.create<Result<Settings>>()
    private val stubLocalAiModels = BehaviorSubject.create<Result<List<LocalAiModel>>>()
    private val stubException = Throwable("Something went wrong.")
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubGetConfigurationUseCase = mockk<GetConfigurationUseCase>()
    private val stubSelectStableDiffusionModelUseCase = mockk<SelectStableDiffusionModelUseCase>()
    private val stubGetStableDiffusionModelsUseCase = mockk<GetStableDiffusionModelsUseCase>()
    private val stubObserveLocalAiModelsUseCase = mockk<ObserveLocalOnnxModelsUseCase>()
    private val stubFetchAndGetStabilityAiEnginesUseCase = mockk<FetchAndGetStabilityAiEnginesUseCase>()
    private val stubFetchAndGetHuggingFaceModelsUseCase = mockk<FetchAndGetHuggingFaceModelsUseCase>()
    private val stubFetchAndGetSwarmUiModelsUseCase = mockk<FetchAndGetSwarmUiModelsUseCase>()

    override fun initializeViewModel() = EngineSelectionViewModel(
        dispatchersProvider = stubDispatchersProvider,
        preferenceManager = stubPreferenceManager,
        schedulersProvider = stubSchedulersProvider,
        getConfigurationUseCase = stubGetConfigurationUseCase,
        selectStableDiffusionModelUseCase = stubSelectStableDiffusionModelUseCase,
        getStableDiffusionModelsUseCase = stubGetStableDiffusionModelsUseCase,
        observeLocalOnnxModelsUseCase = stubObserveLocalAiModelsUseCase,
        fetchAndGetStabilityAiEnginesUseCase = stubFetchAndGetStabilityAiEnginesUseCase,
        getHuggingFaceModelsUseCase = stubFetchAndGetHuggingFaceModelsUseCase,
        fetchAndGetSwarmUiModelsUseCase = stubFetchAndGetSwarmUiModelsUseCase,
    )

    @Before
    override fun initialize() {
        super.initialize()

        every {
            stubPreferenceManager.observe()
        } returns stubSettings
            .toFlowable(BackpressureStrategy.LATEST)
            .flatMap { result ->
                result.fold(
                    onSuccess = { settings -> Flowable.just(settings) },
                    onFailure = { t -> Flowable.error(t) },
                )
            }

        every {
            stubObserveLocalAiModelsUseCase()
        } returns stubLocalAiModels
            .toFlowable(BackpressureStrategy.LATEST)
            .flatMap { result ->
                result.fold(
                    onSuccess = { list -> Flowable.just(list) },
                    onFailure = { t -> Flowable.error(t) },
                )
            }
    }

    @After
    override fun finalize() {
        super.finalize()
        unmockkAll()
    }

    @Test
    fun `initialized, use cases returned data, expected UI state with correct valid stub data`() {
        mockInitialData(DataTestCase.Mock)
        runTest {
            val expected = EngineSelectionState(
                loading = false,
                mode = ServerSource.AUTOMATIC1111,
                sdModels = listOf("title_5598", "title_151297"),
                selectedSdModel = "title_5598",
                hfModels = listOf("prompthero/openjourney-v4", "wavymulder/Analog-Diffusion"),
                selectedHfModel = "prompthero/openjourney-v4",
                stEngines = listOf("5598"),
                selectedStEngine = "5598",
                localAiModels = listOf(LocalAiModel.CustomOnnx),
                selectedLocalAiModelId = "CUSTOM",
                swarmModels = listOf("5598"),
                selectedSwarmModel = "5598",
            )
            val actual = viewModel.state.value
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `initialized, use cases returned empty data, expected UI state with empty stub data`() {
        mockInitialData(DataTestCase.Empty)
        runTest {
            val expected = EngineSelectionState(
                loading = false,
                mode = ServerSource.AUTOMATIC1111,
                sdModels = emptyList(),
                selectedSdModel = "",
                hfModels = emptyList(),
                selectedHfModel = "",
                stEngines = emptyList(),
                selectedStEngine = "",
                localAiModels = emptyList(),
                selectedLocalAiModelId = "",
            )
            val actual = viewModel.state.value
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `initialized, use cases thrown exceptions, expected UI state with empty stub data`() {
        mockInitialData(DataTestCase.Exception)
        runTest {
            val expected = EngineSelectionState(
                loading = false,
                mode = ServerSource.AUTOMATIC1111,
                sdModels = emptyList(),
                selectedSdModel = "",
                hfModels = emptyList(),
                selectedHfModel = "",
                stEngines = emptyList(),
                selectedStEngine = "",
                localAiModels = emptyList(),
                selectedLocalAiModelId = "",
            )
            val actual = viewModel.state.value
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received EngineSelectionIntent, source is AUTOMATIC1111, expected selectedSdModel changed in UI state`() {
        mockInitialData(DataTestCase.Mock, ServerSource.AUTOMATIC1111)

        every {
            stubSelectStableDiffusionModelUseCase(any())
        } returns Completable.complete()

        every {
            stubGetStableDiffusionModelsUseCase()
        } returns Single.just(mockStableDiffusionModels.map { (f, s) -> f to !s })

        viewModel.processIntent(EngineSelectionIntent("title_151297"))

        runTest {
            val state = viewModel.state.value
            Assert.assertEquals(false, state.loading)
            Assert.assertEquals(listOf("title_5598", "title_151297"), state.sdModels)
            Assert.assertEquals("title_151297", state.selectedSdModel)
        }
    }

    @Test
    fun `given received EngineSelectionIntent, source is SWARM_UI, expected swarmModel changed in preference`() {
        mockInitialData(DataTestCase.Mock, ServerSource.SWARM_UI)

        every {
            stubPreferenceManager::swarmUiModel.set(any())
        } returns Unit

        viewModel.processIntent(EngineSelectionIntent("151297"))

        verify {
            stubPreferenceManager::swarmUiModel.set("151297")
        }
    }

    @Test
    fun `given received EngineSelectionIntent, source is HUGGING_FACE, expected huggingFaceModel changed in preference`() {
        mockInitialData(DataTestCase.Mock, ServerSource.HUGGING_FACE)

        every {
            stubPreferenceManager::huggingFaceModel.set(any())
        } returns Unit

        viewModel.processIntent(EngineSelectionIntent("hf_5598"))

        verify {
            stubPreferenceManager::huggingFaceModel.set("hf_5598")
        }
    }

    @Test
    fun `given received EngineSelectionIntent, source is STABILITY_AI, expected stabilityAiEngineId changed in preference`() {
        mockInitialData(DataTestCase.Mock, ServerSource.STABILITY_AI)

        every {
            stubPreferenceManager::stabilityAiEngineId.set(any())
        } returns Unit

        viewModel.processIntent(EngineSelectionIntent("st_5598"))

        verify {
            stubPreferenceManager::stabilityAiEngineId.set("st_5598")
        }
    }

    @Test
    fun `given received EngineSelectionIntent, source is LOCAL, expected localModelId changed in preference`() {
        mockInitialData(DataTestCase.Mock, ServerSource.LOCAL_MICROSOFT_ONNX)

        every {
            stubPreferenceManager::localOnnxModelId.set(any())
        } returns Unit

        viewModel.processIntent(EngineSelectionIntent("llm_5598"))

        verify {
            stubPreferenceManager::localOnnxModelId.set("llm_5598")
        }
    }

    private fun mockInitialData(
        testCase: DataTestCase,
        source: ServerSource = ServerSource.AUTOMATIC1111,
    ) {
        stubSettings.onNext(
            when (testCase) {
                DataTestCase.Mock -> Result.success(Settings())
                DataTestCase.Empty -> Result.success(Settings())
                DataTestCase.Exception -> Result.failure(stubException)
            }
        )

        every {
            stubGetConfigurationUseCase()
        } returns when (testCase) {
            DataTestCase.Mock -> Single.just(
                Configuration(
                    huggingFaceModel = "prompthero/openjourney-v4",
                    stabilityAiEngineId = "5598",
                    swarmUiModel = "5598",
                    localOnnxModelId = "CUSTOM",
                    source = source,
                ),
            )

            DataTestCase.Empty -> Single.just(Configuration())
            DataTestCase.Exception -> Single.error(stubException)
        }

        every {
            stubGetStableDiffusionModelsUseCase()
        } returns when (testCase) {
            DataTestCase.Mock -> Single.just(mockStableDiffusionModels)
            DataTestCase.Empty -> Single.just(emptyList())
            DataTestCase.Exception -> Single.error(stubException)
        }

        every {
            stubFetchAndGetHuggingFaceModelsUseCase()
        } returns when (testCase) {
            DataTestCase.Mock -> Single.just(mockHuggingFaceModels)
            DataTestCase.Empty -> Single.just(emptyList())
            DataTestCase.Exception -> Single.error(stubException)
        }

        every {
            stubFetchAndGetStabilityAiEnginesUseCase()
        } returns when (testCase) {
            DataTestCase.Mock -> Single.just(mockStabilityAiEngines)
            DataTestCase.Empty -> Single.just(emptyList())
            DataTestCase.Exception -> Single.error(stubException)
        }

        every {
            stubFetchAndGetSwarmUiModelsUseCase()
        } returns when (testCase) {
            DataTestCase.Mock -> Single.just(mockSwarmUiModels)
            DataTestCase.Empty -> Single.just(emptyList())
            DataTestCase.Exception -> Single.error(stubException)
        }

        stubLocalAiModels.onNext(
            when (testCase) {
                DataTestCase.Mock -> Result.success(mockLocalAiModels)
                DataTestCase.Empty -> Result.success(emptyList())
                DataTestCase.Exception -> Result.failure(stubException)
            },
        )
    }

    private enum class DataTestCase {
        Mock,
        Empty,
        Exception,
    }
}
