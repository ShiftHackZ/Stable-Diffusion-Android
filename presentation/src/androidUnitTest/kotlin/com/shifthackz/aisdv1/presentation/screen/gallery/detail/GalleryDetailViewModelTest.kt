package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.common.appbuild.BuildVersion
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.usecase.caching.GetLastResultFromCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.DeleteGalleryItemUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.GetAllGalleryUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.ToggleImageLikeUseCase
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
    private val getAllGalleryUseCase = mockk<GetAllGalleryUseCase>()
    private val getLastResultFromCacheUseCase = mockk<GetLastResultFromCacheUseCase>()
    private val deleteGalleryItemUseCase = mockk<DeleteGalleryItemUseCase>()
    private val toggleImageVisibilityUseCase = mockk<ToggleImageVisibilityUseCase>()
    private val toggleImageLikeUseCase = mockk<ToggleImageLikeUseCase>()
    private val generationFormUpdateEvent = mockk<GenerationFormUpdateEvent>(relaxed = true)
    private val router = mockk<GalleryDetailRouter>(relaxed = true)
    private val platformActions = mockk<GalleryDetailPlatformActions>()

    @Before
    fun initialize() {
        coEvery { getGenerationResultUseCase(ITEM_ID) } returns mockAiGenerationResult
        coEvery { getAllGalleryUseCase() } returns listOf(mockAiGenerationResult)
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
                    galleryItemIds = listOf(mockAiGenerationResult.id),
                    content = expectedContent,
                    pagerContents = listOf(expectedContent),
                ),
                viewModel.state.value,
            )
        }

    @Test
    fun `given initialized with gallery items, expected pager content buffer loaded`() =
        runTest(testDispatcher) {
            val items = (1L..8L).map { id ->
                mockAiGenerationResult.copy(id = id)
            }
            coEvery { getAllGalleryUseCase() } returns items
            val viewModel = createViewModel(itemId = 4L)
            advanceUntilIdle()

            Assert.assertEquals(0, viewModel.state.value.pagerContentStartIndex)
            Assert.assertEquals(3, viewModel.state.value.pagerCurrentIndex)
            Assert.assertEquals(
                listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L),
                viewModel.state.value.pagerContents.map(GalleryDetailContent::id),
            )
        }

    @Test
    fun `given navigate to buffered page, expected current content and pager buffer updated`() =
        runTest(testDispatcher) {
            val items = (1L..8L).map { id ->
                mockAiGenerationResult.copy(id = id)
            }
            coEvery { getAllGalleryUseCase() } returns items
            val viewModel = createViewModel(itemId = 4L)
            advanceUntilIdle()

            viewModel.processIntent(GalleryDetailIntent.NavigateToPage(5))
            advanceUntilIdle()

            Assert.assertEquals(6L, viewModel.state.value.content?.id)
            Assert.assertEquals(2, viewModel.state.value.pagerContentStartIndex)
            Assert.assertEquals(5, viewModel.state.value.pagerCurrentIndex)
            Assert.assertEquals(
                listOf(3L, 4L, 5L, 6L, 7L, 8L),
                viewModel.state.value.pagerContents.map(GalleryDetailContent::id),
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
    fun `given delete confirmed with neighbour item, expected neighbour opened`() =
        runTest(testDispatcher) {
            val items = (1L..3L).map { id ->
                mockAiGenerationResult.copy(id = id)
            }
            coEvery { getAllGalleryUseCase() } returns items
            coEvery { deleteGalleryItemUseCase(2L) } returns Unit
            val viewModel = createViewModel(itemId = 2L)
            advanceUntilIdle()

            viewModel.processIntent(GalleryDetailIntent.Delete.Confirm)

            Assert.assertEquals(3L, viewModel.state.value.content?.id)
            Assert.assertEquals(listOf(1L, 3L), viewModel.state.value.galleryItemIds)
            verify(exactly = 0) { router.navigateBack() }

            advanceUntilIdle()
            coVerify { deleteGalleryItemUseCase(2L) }
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

    @Test
    fun `given toggle like, expected liked state updated`() =
        runTest(testDispatcher) {
            coEvery { toggleImageLikeUseCase(ITEM_ID) } returns true
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.processIntent(GalleryDetailIntent.ToggleLike)

            Assert.assertEquals(true, viewModel.state.value.content?.liked)

            advanceUntilIdle()

            Assert.assertEquals(true, viewModel.state.value.content?.liked)
        }

    private fun TestScope.createViewModel(
        itemId: Long = ITEM_ID,
        pagerBuffer: Int = GALLERY_DETAIL_PAGER_BUFFER,
    ) = GalleryDetailViewModel(
        itemId = itemId,
        dispatchersProvider = dispatchersProvider,
        buildInfoProvider = buildInfoProvider,
        getGenerationResultUseCase = getGenerationResultUseCase,
        getAllGalleryUseCase = getAllGalleryUseCase,
        getLastResultFromCacheUseCase = getLastResultFromCacheUseCase,
        deleteGalleryItemUseCase = deleteGalleryItemUseCase,
        toggleImageVisibilityUseCase = toggleImageVisibilityUseCase,
        toggleImageLikeUseCase = toggleImageLikeUseCase,
        generationFormUpdateEvent = generationFormUpdateEvent,
        router = router,
        platformActions = platformActions,
        pagerBuffer = pagerBuffer,
    )

    private companion object {
        const val ITEM_ID = 5598L
    }
}
