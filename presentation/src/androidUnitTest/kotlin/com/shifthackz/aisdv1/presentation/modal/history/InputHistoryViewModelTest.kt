package com.shifthackz.aisdv1.presentation.modal.history

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.usecase.generation.GetGenerationResultPagedUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InputHistoryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchersProvider = object : DispatchersProvider {
        override val io: CoroutineDispatcher = testDispatcher
        override val ui: CoroutineDispatcher = testDispatcher
        override val immediate: CoroutineDispatcher = testDispatcher
    }
    private val getGenerationResultPagedUseCase = mockk<GetGenerationResultPagedUseCase>()

    @Before
    fun initialize() {
        Localization.setLanguageCode("en")
        coEvery {
            getGenerationResultPagedUseCase(limit = PAGE_SIZE, offset = 0)
        } returns listOf(generationResult(id = 1L))
        coEvery {
            getGenerationResultPagedUseCase(limit = PAGE_SIZE, offset = PAGE_SIZE)
        } returns emptyList()
    }

    @Test
    fun `given initialized, expected first page loaded`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            Assert.assertEquals(
                InputHistoryState(
                    loading = false,
                    items = listOf(
                        InputHistoryItemUi(generationResult = generationResult(id = 1L)),
                    ),
                    nextPage = 1,
                    canLoadMore = true,
                ),
                viewModel.state.value,
            )
            coVerify {
                getGenerationResultPagedUseCase(limit = PAGE_SIZE, offset = 0)
            }
        }

    @Test
    fun `given load next page intent, expected next page appended`() =
        runTest(testDispatcher) {
            coEvery {
                getGenerationResultPagedUseCase(limit = PAGE_SIZE, offset = PAGE_SIZE)
            } returns listOf(generationResult(id = 2L))
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.processIntent(InputHistoryIntent.LoadNextPage)
            advanceUntilIdle()

            Assert.assertEquals(
                InputHistoryState(
                    loading = false,
                    items = listOf(
                        InputHistoryItemUi(generationResult = generationResult(id = 1L)),
                        InputHistoryItemUi(generationResult = generationResult(id = 2L)),
                    ),
                    nextPage = 2,
                    canLoadMore = true,
                ),
                viewModel.state.value,
            )
            coVerify {
                getGenerationResultPagedUseCase(limit = PAGE_SIZE, offset = PAGE_SIZE)
            }
        }

    @Test
    fun `given empty next page, expected loading disabled`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.processIntent(InputHistoryIntent.LoadNextPage)
            advanceUntilIdle()

            Assert.assertEquals(
                InputHistoryState(
                    loading = false,
                    items = listOf(
                        InputHistoryItemUi(generationResult = generationResult(id = 1L)),
                    ),
                    nextPage = 2,
                    canLoadMore = false,
                ),
                viewModel.state.value,
            )
        }

    @Test
    fun `given initial load failed and retry received, expected data reloaded`() =
        runTest(testDispatcher) {
            coEvery {
                getGenerationResultPagedUseCase(limit = PAGE_SIZE, offset = 0)
            } throws IllegalStateException()
            val viewModel = createViewModel()
            advanceUntilIdle()

            Assert.assertEquals(Localization.string("error_generic"), viewModel.state.value.error)

            coEvery {
                getGenerationResultPagedUseCase(limit = PAGE_SIZE, offset = 0)
            } returns listOf(generationResult(id = 1L))

            viewModel.processIntent(InputHistoryIntent.Retry)
            advanceUntilIdle()

            Assert.assertEquals(
                InputHistoryState(
                    loading = false,
                    items = listOf(
                        InputHistoryItemUi(generationResult = generationResult(id = 1L)),
                    ),
                    nextPage = 1,
                    canLoadMore = true,
                ),
                viewModel.state.value,
            )
        }

    private fun TestScope.createViewModel() = InputHistoryViewModel(
        dispatchersProvider = dispatchersProvider,
        getGenerationResultPagedUseCase = getGenerationResultPagedUseCase,
    )

    private fun generationResult(id: Long) = AiGenerationResult(
        id = id,
        image = "",
        inputImage = "",
        createdAt = 0L,
        type = AiGenerationResult.Type.TEXT_TO_IMAGE,
        prompt = "Prompt $id",
        negativePrompt = "",
        width = 512,
        height = 512,
        samplingSteps = 20,
        cfgScale = 7f,
        restoreFaces = false,
        sampler = "Euler",
        seed = "seed-$id",
        subSeed = "",
        subSeedStrength = 0f,
        denoisingStrength = 0f,
        hidden = false,
    )

    private companion object {
        const val PAGE_SIZE = 1000
    }
}
