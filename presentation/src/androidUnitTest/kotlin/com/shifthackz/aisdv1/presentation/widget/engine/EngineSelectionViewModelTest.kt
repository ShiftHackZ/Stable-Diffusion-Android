package com.shifthackz.aisdv1.presentation.widget.engine

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.Settings
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.downloadable.ObserveLocalOnnxModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.huggingface.FetchHuggingFaceModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.GetStableDiffusionModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.SelectStableDiffusionModelUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.GetConfigurationUseCase
import com.shifthackz.aisdv1.domain.usecase.stabilityai.FetchAndGetStabilityAiEnginesUseCase
import com.shifthackz.aisdv1.domain.usecase.swarmmodel.FetchAndGetSwarmUiModelsUseCase
import com.shifthackz.aisdv1.presentation.mocks.mockHuggingFaceModels
import com.shifthackz.aisdv1.presentation.mocks.mockLocalAiModels
import com.shifthackz.aisdv1.presentation.mocks.mockStabilityAiEngines
import com.shifthackz.aisdv1.presentation.mocks.mockStableDiffusionModels
import com.shifthackz.aisdv1.presentation.mocks.mockSwarmUiModels
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore("ToDo: Investigate why sometimes tests fail on remote worker due to race-conditions.")
class EngineSelectionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchersProvider = object : DispatchersProvider {
        override val io: CoroutineDispatcher = testDispatcher
        override val ui: CoroutineDispatcher = testDispatcher
        override val immediate: CoroutineDispatcher = testDispatcher
    }
    private val stubSettings = MutableStateFlow(Settings())
    private val stubLocalAiModels = MutableStateFlow<List<LocalAiModel>>(emptyList())
    private val stubException = Throwable("Something went wrong.")
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubGetConfigurationUseCase = mockk<GetConfigurationUseCase>()
    private val stubSelectStableDiffusionModelUseCase = mockk<SelectStableDiffusionModelUseCase>()
    private val stubGetStableDiffusionModelsUseCase = mockk<GetStableDiffusionModelsUseCase>()
    private val stubObserveLocalAiModelsUseCase = mockk<ObserveLocalOnnxModelsUseCase>()
    private val stubFetchAndGetStabilityAiEnginesUseCase = mockk<FetchAndGetStabilityAiEnginesUseCase>()
    private val stubFetchHuggingFaceModelsUseCase = mockk<FetchHuggingFaceModelsUseCase>()
    private val stubFetchAndGetSwarmUiModelsUseCase = mockk<FetchAndGetSwarmUiModelsUseCase>()

    @Before
    fun initialize() {
        every {
            stubPreferenceManager.observe()
        } returns stubSettings

        every {
            stubObserveLocalAiModelsUseCase()
        } returns stubLocalAiModels
    }

    @After
    fun finalize() {
        unmockkAll()
    }

    @Test
    fun `initialized, use cases returned data, expected UI state with correct valid stub data`() =
        runTest(testDispatcher) {
            mockInitialData(DataTestCase.Mock)
            val viewModel = createViewModel()
            advanceUntilIdle()

            val expected = EngineSelectionState(
                loading = false,
                mode = ServerSource.AUTOMATIC1111,
                sdModels = listOf("title_5598", "title_151297"),
                selectedSdModel = "title_5598",
                hfModels = emptyList(),
                selectedHfModel = HuggingFaceModel.default.alias,
                stEngines = emptyList(),
                selectedStEngine = "5598",
                localAiModels = listOf(LocalAiModel.CustomOnnx),
                selectedLocalAiModelId = "CUSTOM",
                swarmModels = emptyList(),
                selectedSwarmModel = "",
            )
            val actual = viewModel.state.value
            Assert.assertEquals(expected, actual)
        }

    @Test
    fun `initialized, use cases returned empty data, expected UI state with empty stub data`() =
        runTest(testDispatcher) {
            mockInitialData(DataTestCase.Empty)
            val viewModel = createViewModel()
            advanceUntilIdle()

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

    @Test
    fun `initialized, use cases thrown exceptions, expected UI state with empty stub data`() =
        runTest(testDispatcher) {
            mockInitialData(DataTestCase.Exception)
            val viewModel = createViewModel()
            advanceUntilIdle()

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

    @Test
    fun `given received EngineSelectionIntent, source is AUTOMATIC1111, expected selectedSdModel changed in UI state`() =
        runTest(testDispatcher) {
            mockInitialData(DataTestCase.Mock, ServerSource.AUTOMATIC1111)
            val viewModel = createViewModel()
            advanceUntilIdle()

            coEvery {
                stubSelectStableDiffusionModelUseCase(any())
            } returns Unit

            coEvery {
                stubGetStableDiffusionModelsUseCase()
            } returns mockStableDiffusionModels.map { (model, selected) -> model to !selected }

            viewModel.processIntent(EngineSelectionIntent("title_151297"))
            advanceUntilIdle()

            val state = viewModel.state.value
            Assert.assertEquals(false, state.loading)
            Assert.assertEquals(listOf("title_5598", "title_151297"), state.sdModels)
            Assert.assertEquals("title_151297", state.selectedSdModel)
        }

    @Test
    fun `given received EngineSelectionIntent, source is SWARM_UI, expected swarmModel changed in preference`() =
        runTest(testDispatcher) {
            mockInitialData(DataTestCase.Mock, ServerSource.SWARM_UI)
            val viewModel = createViewModel()
            advanceUntilIdle()

            every {
                stubPreferenceManager::swarmUiModel.set(any())
            } returns Unit

            viewModel.processIntent(EngineSelectionIntent("151297"))

            verify {
                stubPreferenceManager::swarmUiModel.set("151297")
            }
        }

    @Test
    fun `given received EngineSelectionIntent, source is HUGGING_FACE, expected huggingFaceModel changed in preference`() =
        runTest(testDispatcher) {
            mockInitialData(DataTestCase.Mock, ServerSource.HUGGING_FACE)
            val viewModel = createViewModel()
            advanceUntilIdle()

            every {
                stubPreferenceManager::huggingFaceModel.set(any())
            } returns Unit

            viewModel.processIntent(EngineSelectionIntent(HuggingFaceModel.default.alias))

            verify {
                stubPreferenceManager::huggingFaceModel.set(HuggingFaceModel.default.alias)
            }
        }

    @Test
    fun `given received EngineSelectionIntent, source is STABILITY_AI, expected stabilityAiEngineId changed in preference`() =
        runTest(testDispatcher) {
            mockInitialData(DataTestCase.Mock, ServerSource.STABILITY_AI)
            val viewModel = createViewModel()
            advanceUntilIdle()

            every {
                stubPreferenceManager::stabilityAiEngineId.set(any())
            } returns Unit

            viewModel.processIntent(EngineSelectionIntent("st_5598"))

            verify {
                stubPreferenceManager::stabilityAiEngineId.set("st_5598")
            }
        }

    @Test
    fun `given received EngineSelectionIntent, source is LOCAL, expected localModelId changed in preference`() =
        runTest(testDispatcher) {
            mockInitialData(DataTestCase.Mock, ServerSource.LOCAL_MICROSOFT_ONNX)
            val viewModel = createViewModel()
            advanceUntilIdle()

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
        stubSettings.value = Settings(source = source)

        when (testCase) {
            DataTestCase.Mock -> coEvery {
                stubGetConfigurationUseCase()
            } returns Configuration(
                serverUrl = if (source == ServerSource.AUTOMATIC1111) {
                    "https://a1111.example.com"
                } else {
                    ""
                },
                swarmUiUrl = if (source == ServerSource.SWARM_UI) {
                    "https://swarm.example.com"
                } else {
                    ""
                },
                huggingFaceModel = HuggingFaceModel.default.alias,
                stabilityAiApiKey = if (source == ServerSource.STABILITY_AI) {
                    "stub-api-key"
                } else {
                    ""
                },
                stabilityAiEngineId = "5598",
                swarmUiModel = "5598",
                localOnnxModelId = "CUSTOM",
                source = source,
            )

            DataTestCase.Empty -> coEvery {
                stubGetConfigurationUseCase()
            } returns Configuration()

            DataTestCase.Exception -> coEvery {
                stubGetConfigurationUseCase()
            } throws stubException
        }

        when (testCase) {
            DataTestCase.Mock -> coEvery {
                stubGetStableDiffusionModelsUseCase()
            } returns mockStableDiffusionModels

            DataTestCase.Empty -> coEvery {
                stubGetStableDiffusionModelsUseCase()
            } returns emptyList()

            DataTestCase.Exception -> coEvery {
                stubGetStableDiffusionModelsUseCase()
            } throws stubException
        }

        coEvery {
            stubFetchHuggingFaceModelsUseCase()
        } coAnswers {
            when (testCase) {
                DataTestCase.Mock -> mockHuggingFaceModels
                DataTestCase.Empty -> emptyList()
                DataTestCase.Exception -> throw stubException
            }
        }

        coEvery {
            stubFetchAndGetStabilityAiEnginesUseCase()
        } coAnswers {
            when (testCase) {
                DataTestCase.Mock -> mockStabilityAiEngines
                DataTestCase.Empty -> emptyList()
                DataTestCase.Exception -> throw stubException
            }
        }

        coEvery {
            stubFetchAndGetSwarmUiModelsUseCase()
        } coAnswers {
            when (testCase) {
                DataTestCase.Mock -> mockSwarmUiModels
                DataTestCase.Empty -> emptyList()
                DataTestCase.Exception -> throw stubException
            }
        }

        stubLocalAiModels.value = when (testCase) {
            DataTestCase.Mock -> mockLocalAiModels
            DataTestCase.Empty -> emptyList()
            DataTestCase.Exception -> emptyList()
        }
    }

    private fun TestScope.createViewModel() = EngineSelectionViewModel(
        dispatchersProvider = dispatchersProvider,
        preferenceManager = stubPreferenceManager,
        getConfigurationUseCase = stubGetConfigurationUseCase,
        selectStableDiffusionModelUseCase = stubSelectStableDiffusionModelUseCase,
        getStableDiffusionModelsUseCase = stubGetStableDiffusionModelsUseCase,
        observeLocalOnnxModelsUseCase = stubObserveLocalAiModelsUseCase,
        fetchAndGetStabilityAiEnginesUseCase = stubFetchAndGetStabilityAiEnginesUseCase,
        getHuggingFaceModelsUseCase = stubFetchHuggingFaceModelsUseCase,
        fetchAndGetSwarmUiModelsUseCase = stubFetchAndGetSwarmUiModelsUseCase,
    )

    private enum class DataTestCase {
        Mock,
        Empty,
        Exception,
    }
}
