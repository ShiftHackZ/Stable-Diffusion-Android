package com.shifthackz.aisdv1.presentation.screen.gallery.list

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.Grid
import com.shifthackz.aisdv1.domain.entity.MediaStoreInfo
import com.shifthackz.aisdv1.domain.entity.Settings
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.gallery.DeleteAllGalleryUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.DeleteGalleryItemsUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.GetMediaStoreInfoUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.SetGalleryItemsLikedUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.SetGalleryItemsVisibilityUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.GetGenerationResultPagedUseCase
import com.shifthackz.aisdv1.presentation.navigation.router.GalleryRouter
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GalleryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchersProvider = object : DispatchersProvider {
        override val io: CoroutineDispatcher = testDispatcher
        override val ui: CoroutineDispatcher = testDispatcher
        override val immediate: CoroutineDispatcher = testDispatcher
    }
    private val getMediaStoreInfoUseCase = mockk<GetMediaStoreInfoUseCase>()
    private val backgroundWorkObserver = mockk<BackgroundWorkObserver>()
    private val preferenceManager = mockk<PreferenceManager>()
    private val deleteAllGalleryUseCase = mockk<DeleteAllGalleryUseCase>()
    private val deleteGalleryItemsUseCase = mockk<DeleteGalleryItemsUseCase>()
    private val setGalleryItemsVisibilityUseCase = mockk<SetGalleryItemsVisibilityUseCase>()
    private val setGalleryItemsLikedUseCase = mockk<SetGalleryItemsLikedUseCase>()
    private val getGenerationResultPagedUseCase = mockk<GetGenerationResultPagedUseCase>()
    private val galleryExportService = mockk<GalleryExportService>()
    private val galleryRouter = mockk<GalleryRouter>(relaxed = true)

    @Before
    fun initialize() {
        every { preferenceManager.galleryGrid } returns Grid.Fixed2
        every { preferenceManager.observe() } returns flowOf(Settings(galleryGrid = Grid.Fixed3))
        every { backgroundWorkObserver.observeResult() } returns emptyFlow()
        coEvery { getMediaStoreInfoUseCase() } returns MediaStoreInfo(
            count = 5,
            folderUri = "content://gallery",
        )
        every {
            getGenerationResultPagedUseCase.observe(limit = PAGE_SIZE, offset = 0)
        } returns flowOf(listOf(generationResult(id = 1L, hidden = false)))
        every { getGenerationResultPagedUseCase.observeCount() } returns flowOf(5)
    }

    @Test
    fun `given initialized, expected media store and first page loaded`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            Assert.assertEquals(
                GalleryState(
                    loading = false,
                    items = listOf(
                        GalleryGridItemUi(id = 1L, image = null, hidden = false),
                    ),
                    nextPage = 1,
                    canLoadMore = true,
                    mediaStoreInfo = MediaStoreInfo(5, "content://gallery"),
                    grid = Grid.Fixed3,
                ),
                viewModel.state.value,
            )
        }

    @Test
    fun `given open item intent, expected gallery router called`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.processIntent(GalleryIntent.OpenItem(1L))

            verify { galleryRouter.navigateToGalleryDetails(1L) }
        }

    @Test
    fun `given export all confirmed, expected share export effect`() =
        runTest(testDispatcher) {
            coEvery {
                galleryExportService.export(null)
            } returns GalleryExportResult("/tmp/gallery.zip")
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.processIntent(GalleryIntent.Export.All.Confirm)
            advanceUntilIdle()

            Assert.assertEquals(
                GalleryEffect.ShareExport("/tmp/gallery.zip"),
                viewModel.effect.firstOrNull(),
            )
            coVerify { galleryExportService.export(null) }
        }

    @Test
    fun `given delete selection confirmed, expected selection cleared and gallery reloaded`() =
        runTest(testDispatcher) {
            coEvery { deleteGalleryItemsUseCase(listOf(1L)) } returns Unit
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.processIntent(GalleryIntent.ChangeSelectionMode(true))
            viewModel.processIntent(GalleryIntent.ToggleItemSelection(1L))
            viewModel.processIntent(GalleryIntent.Delete.Selection.Confirm)
            advanceUntilIdle()

            Assert.assertFalse(viewModel.state.value.selectionMode)
            Assert.assertTrue(viewModel.state.value.selection.isEmpty())
            coVerify { deleteGalleryItemsUseCase(listOf(1L)) }
            coVerify(atLeast = 2) { getMediaStoreInfoUseCase() }
        }

    @Test
    fun `given visible selection visibility toggled, expected items hidden and selection cleared`() =
        runTest(testDispatcher) {
            coEvery { setGalleryItemsVisibilityUseCase(listOf(1L), true) } returns Unit
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.processIntent(GalleryIntent.ChangeSelectionMode(true))
            viewModel.processIntent(GalleryIntent.ToggleItemSelection(1L))
            viewModel.processIntent(GalleryIntent.ToggleSelectionVisibility)
            advanceUntilIdle()

            Assert.assertFalse(viewModel.state.value.selectionMode)
            Assert.assertTrue(viewModel.state.value.selection.isEmpty())
            coVerify { setGalleryItemsVisibilityUseCase(listOf(1L), true) }
            coVerify(atLeast = 2) { getMediaStoreInfoUseCase() }
        }

    @Test
    fun `given hidden selection visibility toggled, expected items unhidden and selection cleared`() =
        runTest(testDispatcher) {
            every {
                getGenerationResultPagedUseCase.observe(limit = PAGE_SIZE, offset = 0)
            } returns flowOf(listOf(generationResult(id = 1L, hidden = true)))
            coEvery { setGalleryItemsVisibilityUseCase(listOf(1L), false) } returns Unit
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.processIntent(GalleryIntent.ChangeSelectionMode(true))
            viewModel.processIntent(GalleryIntent.ToggleItemSelection(1L))
            viewModel.processIntent(GalleryIntent.ToggleSelectionVisibility)
            advanceUntilIdle()

            Assert.assertFalse(viewModel.state.value.selectionMode)
            Assert.assertTrue(viewModel.state.value.selection.isEmpty())
            coVerify { setGalleryItemsVisibilityUseCase(listOf(1L), false) }
            coVerify(atLeast = 2) { getMediaStoreInfoUseCase() }
        }

    @Test
    fun `given unliked selection like toggled, expected items liked and selection cleared`() =
        runTest(testDispatcher) {
            coEvery { setGalleryItemsLikedUseCase(listOf(1L), true) } returns Unit
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.processIntent(GalleryIntent.ChangeSelectionMode(true))
            viewModel.processIntent(GalleryIntent.ToggleItemSelection(1L))
            viewModel.processIntent(GalleryIntent.ToggleSelectionLike)
            advanceUntilIdle()

            Assert.assertFalse(viewModel.state.value.selectionMode)
            Assert.assertTrue(viewModel.state.value.selection.isEmpty())
            coVerify { setGalleryItemsLikedUseCase(listOf(1L), true) }
            coVerify(atLeast = 2) { getMediaStoreInfoUseCase() }
        }

    @Test
    fun `given liked selection like toggled, expected items unliked and selection cleared`() =
        runTest(testDispatcher) {
            every {
                getGenerationResultPagedUseCase.observe(limit = PAGE_SIZE, offset = 0)
            } returns flowOf(listOf(generationResult(id = 1L, hidden = false, liked = true)))
            coEvery { setGalleryItemsLikedUseCase(listOf(1L), false) } returns Unit
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.processIntent(GalleryIntent.ChangeSelectionMode(true))
            viewModel.processIntent(GalleryIntent.ToggleItemSelection(1L))
            viewModel.processIntent(GalleryIntent.ToggleSelectionLike)
            advanceUntilIdle()

            Assert.assertFalse(viewModel.state.value.selectionMode)
            Assert.assertTrue(viewModel.state.value.selection.isEmpty())
            coVerify { setGalleryItemsLikedUseCase(listOf(1L), false) }
            coVerify(atLeast = 2) { getMediaStoreInfoUseCase() }
        }

    private fun TestScope.createViewModel() = GalleryViewModel(
        dispatchersProvider = dispatchersProvider,
        getMediaStoreInfoUseCase = getMediaStoreInfoUseCase,
        backgroundWorkObserver = backgroundWorkObserver,
        preferenceManager = preferenceManager,
        deleteAllGalleryUseCase = deleteAllGalleryUseCase,
        deleteGalleryItemsUseCase = deleteGalleryItemsUseCase,
        setGalleryItemsVisibilityUseCase = setGalleryItemsVisibilityUseCase,
        setGalleryItemsLikedUseCase = setGalleryItemsLikedUseCase,
        getGenerationResultPagedUseCase = getGenerationResultPagedUseCase,
        galleryExportService = galleryExportService,
        galleryRouter = galleryRouter,
    )

    private fun generationResult(id: Long, hidden: Boolean, liked: Boolean = false) = AiGenerationResult(
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
        hidden = hidden,
        liked = liked,
    )

    private companion object {
        const val PAGE_SIZE = 60
    }
}
