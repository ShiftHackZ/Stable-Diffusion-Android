package com.shifthackz.aisdv1.presentation.modal.embedding

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.sdembedding.FetchAndGetEmbeddingsUseCase
import com.shifthackz.aisdv1.presentation.mocks.mockEmbeddings
import com.shifthackz.aisdv1.presentation.modal.extras.ExtrasEffect
import com.shifthackz.aisdv1.presentation.model.ErrorState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore("ToDo: Investigate why sometimes tests fail on remote worker due to race-conditions.")
class EmbeddingViewModelTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubFetchAndGetEmbeddingsUseCase = mockk<FetchAndGetEmbeddingsUseCase>()
    private val stubPreferenceManager = mockk<PreferenceManager>()

    @Before
    fun initialize() {
        every {
            stubPreferenceManager.source
        } returns ServerSource.AUTOMATIC1111
    }

    @Test
    fun `given update data, fetch embeddings successful, expected UI state with embeddings list`() = runTest {
        coEvery {
            stubFetchAndGetEmbeddingsUseCase()
        } returns mockEmbeddings

        val viewModel = createViewModel()
        advanceUntilIdle()

        val expected = EmbeddingState(
            loading = false,
            error = ErrorState.None,
            prompt = "prompt",
            negativePrompt = "negative",
            embeddings = listOf(
                EmbeddingItemUi(
                    keyword = "5598",
                    isInPrompt = false,
                    isInNegativePrompt = false,
                ),
                EmbeddingItemUi(
                    keyword = "151297",
                    isInPrompt = false,
                    isInNegativePrompt = false,
                ),
            ),
            selector = false,
        )
        val actual = viewModel.state.value
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given update data, fetch embeddings failed, expected UI state with Generic error`() = runTest {
        coEvery {
            stubFetchAndGetEmbeddingsUseCase()
        } throws stubException

        val viewModel = createViewModel()
        advanceUntilIdle()

        Assert.assertEquals(ErrorState.Generic, viewModel.state.value.error)
    }

    @Test
    fun `given received ApplyNewPrompts intent, expected ApplyPrompts effect delivered to effect collector`() = runTest {
        coEvery {
            stubFetchAndGetEmbeddingsUseCase()
        } returns mockEmbeddings
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.processIntent(EmbeddingIntent.ApplyNewPrompts)
        advanceUntilIdle()

        val expected = ExtrasEffect.ApplyPrompts("prompt", "negative")
        val actual = viewModel.effect.firstOrNull()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given received ChangeSelector intent, expected selector field updated in UI state from intent`() = runTest {
        coEvery {
            stubFetchAndGetEmbeddingsUseCase()
        } returns mockEmbeddings
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.processIntent(EmbeddingIntent.ChangeSelector(true))
        advanceUntilIdle()

        Assert.assertEquals(true, viewModel.state.value.selector)
    }

    @Test
    fun `given received Close intent, expected Close effect delivered to effect collector`() = runTest {
        coEvery {
            stubFetchAndGetEmbeddingsUseCase()
        } returns mockEmbeddings
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.processIntent(EmbeddingIntent.Close)
        advanceUntilIdle()

        val expected = ExtrasEffect.Close
        val actual = viewModel.effect.firstOrNull()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given received ToggleItem intent, expected item from intent isInNegativePrompt changed in UI state`() = runTest {
        coEvery {
            stubFetchAndGetEmbeddingsUseCase()
        } returns mockEmbeddings
        val viewModel = createViewModel()
        advanceUntilIdle()

        val embedding = EmbeddingItemUi(
            keyword = "5598",
            isInPrompt = false,
            isInNegativePrompt = false,
        )
        val intent = EmbeddingIntent.ToggleItem(embedding)
        viewModel.processIntent(intent)
        advanceUntilIdle()

        val expected = listOf(
            EmbeddingItemUi(
                keyword = "5598",
                isInPrompt = false,
                isInNegativePrompt = true,
            ),
            EmbeddingItemUi(
                keyword = "151297",
                isInPrompt = false,
                isInNegativePrompt = false,
            ),
        )
        val actual = viewModel.state.value.embeddings
        Assert.assertEquals(expected, actual)
    }

    private fun TestScope.createViewModel(): EmbeddingViewModel {
        val dispatcher = StandardTestDispatcher(testScheduler)
        return EmbeddingViewModel(
            dispatchersProvider = testDispatchersProvider(dispatcher),
            fetchAndGetEmbeddingsUseCase = stubFetchAndGetEmbeddingsUseCase,
            preferenceManager = stubPreferenceManager,
            prompt = "prompt",
            negativePrompt = "negative",
        )
    }

    private fun testDispatchersProvider(dispatcher: CoroutineDispatcher) = object : DispatchersProvider {
        override val io: CoroutineDispatcher = dispatcher
        override val ui: CoroutineDispatcher = dispatcher
        override val immediate: CoroutineDispatcher = dispatcher
    }
}
