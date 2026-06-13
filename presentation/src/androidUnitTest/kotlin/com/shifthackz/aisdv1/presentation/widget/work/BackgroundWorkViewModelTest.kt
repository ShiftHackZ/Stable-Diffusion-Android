package com.shifthackz.aisdv1.presentation.widget.work

import androidx.compose.ui.graphics.ImageBitmap
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.BackgroundWorkResult
import com.shifthackz.aisdv1.domain.entity.BackgroundWorkStatus
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BackgroundWorkViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchersProvider = object : DispatchersProvider {
        override val io: CoroutineDispatcher = testDispatcher
        override val ui: CoroutineDispatcher = testDispatcher
        override val immediate: CoroutineDispatcher = testDispatcher
    }
    private val imageBitmap = mockk<ImageBitmap>(relaxed = true)
    private val observer = TestBackgroundWorkObserver()
    private val imageLoader = TestBackgroundWorkImageLoader(imageBitmap)

    @Before
    fun initialize() {
        Localization.setLanguageCode("en")
        observer.reset()
        imageLoader.reset()
    }

    @Test
    fun `given running background work, expected status visible`() =
        runTest(testDispatcher) {
            observer.status = flowOf(
                BackgroundWorkStatus(
                    running = true,
                    statusTitle = "Running",
                    statusSubTitle = "Almost there",
                ),
            )

            val viewModel = createViewModel()
            advanceUntilIdle()

            Assert.assertEquals(
                BackgroundWorkState(
                    visible = true,
                    running = true,
                    title = "Running",
                    subTitle = "Almost there",
                ),
                viewModel.state.value,
            )
        }

    @Test
    fun `given background work success, expected finish title and image loaded`() =
        runTest(testDispatcher) {
            observer.result = flowOf(
                BackgroundWorkResult.Success(
                    listOf(aiGenerationResult(image = "base64-result")),
                ),
            )

            val viewModel = createViewModel()
            advanceUntilIdle()

            Assert.assertEquals(
                BackgroundWorkState(
                    visible = true,
                    dismissible = true,
                    title = Localization.string("notification_finish_title", languageCode = "en"),
                    image = imageBitmap,
                ),
                viewModel.state.value,
            )
            Assert.assertEquals("base64-result", imageLoader.lastBase64)
        }

    @Test
    fun `given background work success without preview image, expected finish title and dismiss action`() =
        runTest(testDispatcher) {
            observer.result = flowOf(BackgroundWorkResult.Success(emptyList()))

            val viewModel = createViewModel()
            advanceUntilIdle()

            Assert.assertEquals(
                BackgroundWorkState(
                    visible = true,
                    dismissible = true,
                    title = Localization.string("notification_finish_title", languageCode = "en"),
                ),
                viewModel.state.value,
            )
            Assert.assertEquals(null, imageLoader.lastBase64)
        }

    @Test
    fun `given running background work with stale success result, expected loading state without old image`() =
        runTest(testDispatcher) {
            observer.status = flowOf(
                BackgroundWorkStatus(
                    running = true,
                    statusTitle = "Running",
                    statusSubTitle = "Please wait",
                ),
            )
            observer.result = flowOf(
                BackgroundWorkResult.Success(
                    listOf(aiGenerationResult(image = "old-base64-result")),
                ),
            )

            val viewModel = createViewModel()
            advanceUntilIdle()

            Assert.assertEquals(
                BackgroundWorkState(
                    visible = true,
                    running = true,
                    title = "Running",
                    subTitle = "Please wait",
                ),
                viewModel.state.value,
            )
            Assert.assertEquals(null, imageLoader.lastBase64)
        }

    @Test
    fun `given dismiss intent, expected state hidden and observer result dismissed`() =
        runTest(testDispatcher) {
            observer.result = flowOf(BackgroundWorkResult.Error(IllegalStateException()))
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.processIntent(BackgroundWorkIntent.Dismiss)

            Assert.assertEquals(
                BackgroundWorkState(visible = false),
                viewModel.state.value,
            )
            Assert.assertTrue(observer.dismissed)
        }

    private fun TestScope.createViewModel() = BackgroundWorkViewModel(
        dispatchersProvider = dispatchersProvider,
        backgroundWorkObserver = observer,
        imageLoader = imageLoader,
    )

    private fun aiGenerationResult(image: String) = AiGenerationResult(
        id = 1L,
        image = image,
        inputImage = "",
        createdAt = 0L,
        type = AiGenerationResult.Type.TEXT_TO_IMAGE,
        prompt = "",
        negativePrompt = "",
        width = 512,
        height = 512,
        samplingSteps = 20,
        cfgScale = 7f,
        restoreFaces = false,
        sampler = "",
        seed = "",
        subSeed = "",
        subSeedStrength = 0f,
        denoisingStrength = 0f,
        hidden = false,
    )

    private class TestBackgroundWorkObserver : BackgroundWorkObserver {
        var status: Flow<BackgroundWorkStatus> = flowOf(BackgroundWorkStatus(false, "", ""))
        var result: Flow<BackgroundWorkResult> = flowOf(BackgroundWorkResult.None)
        var dismissed = false

        fun reset() {
            status = flowOf(BackgroundWorkStatus(false, "", ""))
            result = flowOf(BackgroundWorkResult.None)
            dismissed = false
        }

        override fun observeStatus(): Flow<BackgroundWorkStatus> = status
        override fun observeResult(): Flow<BackgroundWorkResult> = result
        override fun dismissResult() {
            dismissed = true
        }
        override fun refreshStatus() = Unit
        override fun postStatusMessage(title: String, subTitle: String) = Unit
        override fun postSuccessSignal(result: List<AiGenerationResult>) = Unit
        override fun postCancelSignal() = Unit
        override fun postFailedSignal(t: Throwable) = Unit
        override fun hasActiveTasks(): Boolean = false
    }

    private class TestBackgroundWorkImageLoader(
        private val imageBitmap: ImageBitmap,
    ) : BackgroundWorkImageLoader {
        var lastBase64: String? = null

        fun reset() {
            lastBase64 = null
        }

        override suspend fun load(base64: String): ImageBitmap {
            lastBase64 = base64
            return imageBitmap
        }
    }
}
