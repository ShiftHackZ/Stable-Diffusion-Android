package com.shifthackz.aisdv1.presentation.modal.embedding

import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.sdembedding.FetchAndGetEmbeddingsUseCase
import com.shifthackz.aisdv1.presentation.core.CoreViewModelTest
import com.shifthackz.aisdv1.presentation.mocks.mockEmbeddings
import com.shifthackz.aisdv1.presentation.modal.extras.ExtrasEffect
import com.shifthackz.aisdv1.presentation.model.ErrorState
import com.shifthackz.aisdv1.presentation.stub.stubDispatchersProvider
import com.shifthackz.aisdv1.presentation.stub.stubSchedulersProvider
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class EmbeddingViewModelTest : CoreViewModelTest<EmbeddingViewModel>() {

    private val stubException = Throwable("Something went wrong.")
    private val stubFetchAndGetEmbeddingsUseCase = mockk<FetchAndGetEmbeddingsUseCase>()
    private val stubPreferenceManager = mockk<PreferenceManager>()

    override fun initializeViewModel() = EmbeddingViewModel(
        fetchAndGetEmbeddingsUseCase = stubFetchAndGetEmbeddingsUseCase,
        preferenceManager = stubPreferenceManager,
        dispatchersProvider = stubDispatchersProvider,
        schedulersProvider = stubSchedulersProvider,
    )

    @Before
    override fun initialize() {
        super.initialize()

        every {
            stubPreferenceManager.source
        } returns ServerSource.AUTOMATIC1111
    }

    @Test
    fun `given update data, fetch embeddings successful, expected UI state with embeddings list`() {
        every {
            stubFetchAndGetEmbeddingsUseCase()
        } returns Single.just(mockEmbeddings)

        viewModel.updateData("prompt", "negative")

        runTest {
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
    }

    @Test
    fun `given update data, fetch embeddings failed, expected UI state with Generic error`() {
        every {
            stubFetchAndGetEmbeddingsUseCase()
        } returns Single.error(stubException)

        viewModel.updateData("prompt", "negative")

        runTest {
            val expected = ErrorState.Generic
            val actual = viewModel.state.value.error
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received ApplyNewPrompts intent, expected ApplyPrompts effect delivered to effect collector`() {
        viewModel.processIntent(EmbeddingIntent.ApplyNewPrompts)
        runTest {
            val expected = ExtrasEffect.ApplyPrompts("", "")
            val actual = viewModel.effect.firstOrNull()
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received ChangeSelector intent, expected selector field updated in UI state from intent`() {
        viewModel.processIntent(EmbeddingIntent.ChangeSelector(true))
        runTest {
            val expected = true
            val actual = viewModel.state.value.selector
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Close intent, expected Close effect delivered to effect collector`() {
        viewModel.processIntent(EmbeddingIntent.Close)
        runTest {
            val expected = ExtrasEffect.Close
            val actual = viewModel.effect.firstOrNull()
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received ToggleItem intent, expected item from intent isInNegativePrompt changed in UI state`() {
        every {
            stubFetchAndGetEmbeddingsUseCase()
        } returns Single.just(mockEmbeddings)

        viewModel.updateData("prompt", "negative")

        val embedding = EmbeddingItemUi(
            keyword = "5598",
            isInPrompt = false,
            isInNegativePrompt = false,
        )
        val intent = EmbeddingIntent.ToggleItem(embedding)
        viewModel.processIntent(intent)

        runTest {
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
    }
}
