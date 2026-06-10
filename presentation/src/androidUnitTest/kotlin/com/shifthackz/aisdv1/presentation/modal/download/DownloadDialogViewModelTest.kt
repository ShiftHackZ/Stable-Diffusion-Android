package com.shifthackz.aisdv1.presentation.modal.download

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.usecase.downloadable.GetLocalModelUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DownloadDialogViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchersProvider = object : DispatchersProvider {
        override val io: CoroutineDispatcher = testDispatcher
        override val ui: CoroutineDispatcher = testDispatcher
        override val immediate: CoroutineDispatcher = testDispatcher
    }
    private val stubGetLocalModelUseCase = mockk<GetLocalModelUseCase>()

    @Before
    fun initialize() {
        coEvery {
            stubGetLocalModelUseCase(MODEL_ID)
        } returns LocalAiModel(
            id = MODEL_ID,
            type = LocalAiModel.Type.ONNX,
            name = "Model",
            size = "1 GB",
            sources = listOf(FIRST_SOURCE, SECOND_SOURCE),
        )
    }

    @Test
    fun `given initialized, expected model sources loaded and first source selected`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            Assert.assertEquals(
                DownloadDialogState(
                    sources = listOf(
                        FIRST_SOURCE to true,
                        SECOND_SOURCE to false,
                    ),
                ),
                viewModel.state.value,
            )
            coVerify {
                stubGetLocalModelUseCase(MODEL_ID)
            }
        }

    @Test
    fun `given received SelectSource intent, expected selected source changed`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.processIntent(DownloadDialogIntent.SelectSource(SECOND_SOURCE))

            Assert.assertEquals(
                DownloadDialogState(
                    sources = listOf(
                        FIRST_SOURCE to false,
                        SECOND_SOURCE to true,
                    ),
                ),
                viewModel.state.value,
            )
        }

    @Test
    fun `given received StartDownload intent, expected selected source emitted`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.processIntent(DownloadDialogIntent.SelectSource(SECOND_SOURCE))
            viewModel.processIntent(DownloadDialogIntent.StartDownload)
            advanceUntilIdle()

            Assert.assertEquals(
                DownloadDialogEffect.StartDownload(SECOND_SOURCE),
                viewModel.effect.firstOrNull(),
            )
        }

    @Test
    fun `given received Close intent, expected close effect emitted`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()

            viewModel.processIntent(DownloadDialogIntent.Close)
            advanceUntilIdle()

            Assert.assertEquals(
                DownloadDialogEffect.Close,
                viewModel.effect.firstOrNull(),
            )
        }

    private fun TestScope.createViewModel() = DownloadDialogViewModel(
        modelId = MODEL_ID,
        getLocalModelUseCase = stubGetLocalModelUseCase,
        dispatchersProvider = dispatchersProvider,
    )

    private companion object {
        const val MODEL_ID = "model-id"
        const val FIRST_SOURCE = "https://github.com/shifthackz/model.zip"
        const val SECOND_SOURCE = "https://share.moroz.cc/model.zip"
    }
}
