package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.common.appbuild.BuildVersion
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.usecase.caching.GetLastResultFromCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.DeleteGalleryItemUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.ToggleImageVisibilityUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.GetGenerationResultUseCase
import com.shifthackz.aisdv1.presentation.core.GenerationFormUpdateEvent
import com.shifthackz.aisdv1.presentation.mocks.mockAiGenerationResult
import com.shifthackz.aisdv1.presentation.navigation.router.GalleryDetailRouter
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
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
class GalleryDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchersProvider = object : DispatchersProvider {
        override val io: CoroutineDispatcher = testDispatcher
        override val ui: CoroutineDispatcher = testDispatcher
        override val immediate: CoroutineDispatcher = testDispatcher
    }
    private val buildInfoProvider = object : BuildInfoProvider {
        override val isDebug = true
        override val buildNumber = 1
        override val version = BuildVersion()
        override val type = BuildType.FULL
    }
    private val getGenerationResultUseCase = mockk<GetGenerationResultUseCase>()
    private val getLastResultFromCacheUseCase = mockk<GetLastResultFromCacheUseCase>()
    private val deleteGalleryItemUseCase = mockk<DeleteGalleryItemUseCase>()
    private val toggleImageVisibilityUseCase = mockk<ToggleImageVisibilityUseCase>()
    private val generationFormUpdateEvent = mockk<GenerationFormUpdateEvent>(relaxed = true)
    private val router = mockk<GalleryDetailRouter>(relaxed = true)
    private val platformActions = mockk<GalleryDetailPlatformActions>()

    @Before
    fun initialize() {
        coEvery { getGenerationResultUseCase(ITEM_ID) } returns mockAiGenerationResult
        coEvery { getLastResultFromCacheUseCase() } returns mockAiGenerationResult
        coEvery { platformActions.copyText(any()) } returns GalleryDetailActionResult.Done
        coEvery { platformActions.saveImage(any()) } returns GalleryDetailActionResult.Done
        coEvery { platformActions.shareImage(any()) } returns GalleryDetailActionResult.Done
        coEvery { platformActions.shareText(any()) } returns GalleryDetailActionResult.Done
    }

    @Test
    fun `given initialized, expected generation result loaded`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            val expectedContent = mockAiGenerationResult.toGalleryDetailContent(
                showReportButton = true,
            )
            Assert.assertEquals(
                GalleryDetailState(
                    loading = false,
                    tabs = GalleryDetailTab.consume(mockAiGenerationResult.type),
                    selectedTab = GalleryDetailTab.IMAGE,
                    content = expectedContent,
                ),
                viewModel.state.value,
            )
        }

    @Test
    fun `given copy intent, expected platform clipboard action called`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.processIntent(GalleryDetailIntent.CopyToClipboard("prompt"))
            advanceUntilIdle()

            coVerify { platformActions.copyText("prompt") }
        }

    @Test
    fun `given delete request, expected delete dialog shown`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.processIntent(GalleryDetailIntent.Delete.Request)

            Assert.assertEquals(GalleryDetailDialog.DeleteConfirm, viewModel.state.value.dialog)
        }

    @Test
    fun `given delete confirmed, expected item deleted and back navigation called`() =
        runTest(testDispatcher) {
            coEvery { deleteGalleryItemUseCase(ITEM_ID) } returns Unit
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.processIntent(GalleryDetailIntent.Delete.Confirm)
            advanceUntilIdle()

            coVerify { deleteGalleryItemUseCase(ITEM_ID) }
            verify { router.navigateBack() }
            Assert.assertEquals(GalleryDetailDialog.None, viewModel.state.value.dialog)
        }

    @Test
    fun `given image export on original tab, expected original image saved`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.processIntent(GalleryDetailIntent.SelectTab(GalleryDetailTab.ORIGINAL))
            viewModel.processIntent(GalleryDetailIntent.Export.Image)
            advanceUntilIdle()

            coVerify { platformActions.saveImage(mockAiGenerationResult.inputImage) }
        }

    @Test
    fun `given params export, expected params text shared`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.processIntent(GalleryDetailIntent.Export.Params)
            advanceUntilIdle()

            coVerify {
                platformActions.shareText(
                    match { text ->
                        text.contains(mockAiGenerationResult.prompt) &&
                            text.contains(mockAiGenerationResult.negativePrompt)
                    },
                )
            }
        }

    @Test
    fun `given send to txt2img, expected form update and text to image navigation`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.processIntent(GalleryDetailIntent.SendTo.Txt2Img)
            advanceUntilIdle()

            verify {
                generationFormUpdateEvent.update(
                    generation = mockAiGenerationResult,
                    route = AiGenerationResult.Type.TEXT_TO_IMAGE,
                    inputImage = false,
                )
            }
            verify { router.navigateToTextToImage() }
        }

    @Test
    fun `given send to img2img from original tab, expected form update and image to image navigation`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.processIntent(GalleryDetailIntent.SelectTab(GalleryDetailTab.ORIGINAL))
            viewModel.processIntent(GalleryDetailIntent.SendTo.Img2Img)
            advanceUntilIdle()

            verify {
                generationFormUpdateEvent.update(
                    generation = mockAiGenerationResult,
                    route = AiGenerationResult.Type.IMAGE_TO_IMAGE,
                    inputImage = true,
                )
            }
            verify { router.navigateToImageToImage() }
        }

    @Test
    fun `given toggle visibility, expected hidden state updated`() =
        runTest(testDispatcher) {
            coEvery { toggleImageVisibilityUseCase(ITEM_ID) } returns true
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.processIntent(GalleryDetailIntent.ToggleVisibility)
            advanceUntilIdle()

            Assert.assertEquals(true, viewModel.state.value.content?.hidden)
        }

    private fun TestScope.createViewModel() = GalleryDetailViewModel(
        itemId = ITEM_ID,
        dispatchersProvider = dispatchersProvider,
        buildInfoProvider = buildInfoProvider,
        getGenerationResultUseCase = getGenerationResultUseCase,
        getLastResultFromCacheUseCase = getLastResultFromCacheUseCase,
        deleteGalleryItemUseCase = deleteGalleryItemUseCase,
        toggleImageVisibilityUseCase = toggleImageVisibilityUseCase,
        generationFormUpdateEvent = generationFormUpdateEvent,
        router = router,
        platformActions = platformActions,
    )

    private companion object {
        const val ITEM_ID = 5598L
    }
}
