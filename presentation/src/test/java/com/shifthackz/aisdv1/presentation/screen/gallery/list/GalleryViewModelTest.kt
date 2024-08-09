@file:OptIn(ExperimentalCoroutinesApi::class)

package com.shifthackz.aisdv1.presentation.screen.gallery.list

import android.graphics.Bitmap
import android.net.Uri
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.domain.entity.MediaStoreInfo
import com.shifthackz.aisdv1.domain.entity.Settings
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.gallery.DeleteAllGalleryUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.DeleteGalleryItemsUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.GetMediaStoreInfoUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.GetGenerationResultPagedUseCase
import com.shifthackz.aisdv1.presentation.core.CoreViewModelTest
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.navigation.router.drawer.DrawerRouter
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.aisdv1.presentation.stub.stubSchedulersProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File

class GalleryViewModelTest : CoreViewModelTest<GalleryViewModel>() {

    private val stubMediaStoreInfo = MediaStoreInfo(5598)
    private val stubFile = mockk<File>()
    private val stubBitmap = mockk<Bitmap>()
    private val stubUri = mockk<Uri>()
    private val stubGetMediaStoreInfoUseCase = mockk<GetMediaStoreInfoUseCase>()
    private val stubGetGenerationResultPagedUseCase = mockk<GetGenerationResultPagedUseCase>()
    private val stubBase64ToBitmapConverter = mockk<Base64ToBitmapConverter>()
    private val stubGalleryExporter = mockk<GalleryExporter>()
    private val stubMainRouter = mockk<MainRouter>()
    private val stubDrawerRouter = mockk<DrawerRouter>()
    private val stubDeleteAllGalleryUseCase = mockk<DeleteAllGalleryUseCase>()
    private val stubDeleteGalleryItemsUseCase = mockk<DeleteGalleryItemsUseCase>()
    private val stubBackgroundWorkObserver = mockk<BackgroundWorkObserver>()
    private val stubPreferenceManager = mockk<PreferenceManager>()

    override fun initializeViewModel() = GalleryViewModel(
        getMediaStoreInfoUseCase = stubGetMediaStoreInfoUseCase,
        backgroundWorkObserver = stubBackgroundWorkObserver,
        preferenceManager = stubPreferenceManager,
        getGenerationResultPagedUseCase = stubGetGenerationResultPagedUseCase,
        base64ToBitmapConverter = stubBase64ToBitmapConverter,
        galleryExporter = stubGalleryExporter,
        schedulersProvider = stubSchedulersProvider,
        mainRouter = stubMainRouter,
        drawerRouter = stubDrawerRouter,
        deleteAllGalleryUseCase = stubDeleteAllGalleryUseCase,
        deleteGalleryItemsUseCase = stubDeleteGalleryItemsUseCase,
    )

    @Before
    override fun initialize() {
        super.initialize()

        every {
            stubPreferenceManager.observe()
        } returns Flowable.just(Settings())

        every {
            stubBackgroundWorkObserver.observeResult()
        } returns Flowable.empty()

        every {
            stubGetMediaStoreInfoUseCase()
        } returns Single.just(stubMediaStoreInfo)
    }

    @Test
    fun `initialized, expected mediaStoreInfo field in UI state equals stubMediaStoreInfo`() {
        runTest {
            val expected = stubMediaStoreInfo
            val actual = viewModel.state.value.mediaStoreInfo
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received DismissDialog intent, expected screenModal field in UI state is None`() {
        viewModel.processIntent(GalleryIntent.DismissDialog)
        runTest {
            val expected = Modal.None
            val actual = viewModel.state.value.screenModal
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Export Request intent, expected screenModal field in UI state is ConfirmExport`() {
        viewModel.processIntent(GalleryIntent.Export.All.Request)
        runTest {
            val expected = Modal.ConfirmExport(true)
            val actual = viewModel.state.value.screenModal
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Export Confirm intent, expected screenModal field in UI state is None, Share effect delivered to effect collector`() {
        every {
            stubGalleryExporter()
        } returns Single.just(stubFile)

        Dispatchers.setMain(UnconfinedTestDispatcher())

        viewModel.processIntent(GalleryIntent.Export.All.Confirm)

        runTest {
            val expectedUiState = Modal.None
            val actualUiState = viewModel.state.value.screenModal
            Assert.assertEquals(expectedUiState, actualUiState)

            val expectedEffect = GalleryEffect.Share(stubFile)
            val actualEffect = viewModel.effect.firstOrNull()
            Assert.assertEquals(expectedEffect, actualEffect)
        }
        verify {
            stubGalleryExporter()
        }
    }

    @Test
    fun `given received OpenItem intent, expected router navigateToGalleryDetails() method called`() {
        every {
            stubMainRouter.navigateToGalleryDetails(any())
        } returns Unit

        val item = GalleryGridItemUi(5598L, stubBitmap)
        viewModel.processIntent(GalleryIntent.OpenItem(item))

        verify {
            stubMainRouter.navigateToGalleryDetails(5598L)
        }
    }

    @Test
    fun `given received OpenMediaStoreFolder intent, expected OpenUri effect delivered to effect collector`() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel.processIntent(GalleryIntent.OpenMediaStoreFolder(stubUri))
        runTest {
            val expected = GalleryEffect.OpenUri(stubUri)
            val actual = viewModel.effect.firstOrNull()
            Assert.assertEquals(expected, actual)
        }
    }
}
