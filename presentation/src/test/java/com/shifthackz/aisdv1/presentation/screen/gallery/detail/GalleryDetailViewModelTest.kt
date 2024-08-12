@file:OptIn(ExperimentalCoroutinesApi::class)

package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.usecase.caching.GetLastResultFromCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.DeleteGalleryItemUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.GetGenerationResultUseCase
import com.shifthackz.aisdv1.presentation.core.CoreViewModelTest
import com.shifthackz.aisdv1.presentation.core.GenerationFormUpdateEvent
import com.shifthackz.aisdv1.presentation.extensions.mapToUi
import com.shifthackz.aisdv1.presentation.mocks.mockAiGenerationResult
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.aisdv1.presentation.stub.stubSchedulersProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File

class GalleryDetailViewModelTest : CoreViewModelTest<GalleryDetailViewModel>() {

    private val stubBitmap = mockk<Bitmap>()
    private val stubFile = mockk<File>()
    private val stubGetGenerationResultUseCase = mockk<GetGenerationResultUseCase>()
    private val stubGetLastResultFromCacheUseCase = mockk<GetLastResultFromCacheUseCase>()
    private val stubDeleteGalleryItemUseCase = mockk<DeleteGalleryItemUseCase>()
    private val stubGalleryDetailBitmapExporter = mockk<GalleryDetailBitmapExporter>()
    private val stubBase64ToBitmapConverter = mockk<Base64ToBitmapConverter>()
    private val stubGenerationFormUpdateEvent = mockk<GenerationFormUpdateEvent>()
    private val stubMainRouter = mockk<MainRouter>()

    override fun initializeViewModel() = GalleryDetailViewModel(
        itemId = 5598L,
        getGenerationResultUseCase = stubGetGenerationResultUseCase,
        getLastResultFromCacheUseCase = stubGetLastResultFromCacheUseCase,
        deleteGalleryItemUseCase = stubDeleteGalleryItemUseCase,
        galleryDetailBitmapExporter = stubGalleryDetailBitmapExporter,
        base64ToBitmapConverter = stubBase64ToBitmapConverter,
        schedulersProvider = stubSchedulersProvider,
        generationFormUpdateEvent = stubGenerationFormUpdateEvent,
        mainRouter = stubMainRouter,
    )

    @Before
    override fun initialize() {
        super.initialize()

        every {
            stubGetLastResultFromCacheUseCase()
        } returns Single.just(mockAiGenerationResult)

        every {
            stubGetGenerationResultUseCase(any())
        } returns Single.just(mockAiGenerationResult)

        every {
            stubBase64ToBitmapConverter(any())
        } returns Single.just(Base64ToBitmapConverter.Output(stubBitmap))
    }

    @After
    override fun finalize() {
        super.finalize()
        unmockkAll()
    }

    @Test
    fun `initialized, loaded ai generation result, expected UI state is Content`() {
        runTest {
            val expected = GalleryDetailState.Content(
                tabs = GalleryDetailState.Tab.consume(mockAiGenerationResult.type),
                generationType = mockAiGenerationResult.type,
                id = mockAiGenerationResult.id,
                bitmap = stubBitmap,
                inputBitmap = stubBitmap,
                createdAt = mockAiGenerationResult.createdAt.toString().asUiText(),
                type = mockAiGenerationResult.type.key.asUiText(),
                prompt = mockAiGenerationResult.prompt.asUiText(),
                negativePrompt = mockAiGenerationResult.negativePrompt.asUiText(),
                size = "512 X 512".asUiText(),
                samplingSteps = mockAiGenerationResult.samplingSteps.toString().asUiText(),
                cfgScale = mockAiGenerationResult.cfgScale.toString().asUiText(),
                restoreFaces = mockAiGenerationResult.restoreFaces.mapToUi(),
                sampler = mockAiGenerationResult.sampler.asUiText(),
                seed = mockAiGenerationResult.seed.asUiText(),
                subSeed = mockAiGenerationResult.subSeed.asUiText(),
                subSeedStrength = mockAiGenerationResult.subSeedStrength.toString().asUiText(),
                denoisingStrength = mockAiGenerationResult.denoisingStrength.toString().asUiText(),
            )
            val actual = viewModel.state.value
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received CopyToClipboard intent, expected ShareClipBoard effect delivered to effect collector`() {
        viewModel.processIntent(GalleryDetailIntent.CopyToClipboard("text"))
        runTest {
            val expected = GalleryDetailEffect.ShareClipBoard("text")
            val actual = viewModel.effect.firstOrNull()
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Delete Request intent, expected modal field in UI state is DeleteImageConfirm`() {
        viewModel.processIntent(GalleryDetailIntent.Delete.Request)
        val expected = Modal.DeleteImageConfirm(isAll = false, isMultiple = false)
        val actual = (viewModel.state.value as? GalleryDetailState.Content)?.screenModal
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given received Delete Confirm intent, expected screenModal field in UI state is None, deleteGalleryItemUseCase() method is called`() {
        every {
            stubDeleteGalleryItemUseCase(any())
        } returns Completable.complete()

        every {
            stubMainRouter.navigateBack()
        } returns Unit

        viewModel.processIntent(GalleryDetailIntent.Delete.Confirm)

        val expected = Modal.None
        val actual = (viewModel.state.value as? GalleryDetailState.Content)?.screenModal
        Assert.assertEquals(expected, actual)

        verify {
            stubDeleteGalleryItemUseCase(5598L)
        }
    }

    @Test
    fun `given received Export Image intent, expected galleryDetailBitmapExporter() method is called, ShareImageFile effect delivered to effect collector`() {
        every {
            stubGalleryDetailBitmapExporter(any())
        } returns Single.just(stubFile)
        viewModel.processIntent(GalleryDetailIntent.Export.Image)
        verify {
            stubGalleryDetailBitmapExporter(stubBitmap)
        }
        runTest {
            val expected = GalleryDetailEffect.ShareImageFile(stubFile)
            val actual = viewModel.effect.firstOrNull()
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Export Params intent, expected ShareGenerationParams effect delivered to effect collector`() {
        viewModel.processIntent(GalleryDetailIntent.Export.Params)
        runTest {
            val expected = GalleryDetailEffect.ShareGenerationParams(viewModel.state.value)
            val actual = viewModel.effect.firstOrNull()
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received NavigateBack intent, expected router navigateBack() method called`() {
        every {
            stubMainRouter.navigateBack()
        } returns Unit
        viewModel.processIntent(GalleryDetailIntent.NavigateBack)
        verify {
            stubMainRouter.navigateBack()
        }
    }

    @Test
    fun `given received SelectTab intent with IMAGE tab, expected expected selectedTab field in UI state is IMAGE`() {
        viewModel.processIntent(GalleryDetailIntent.SelectTab(GalleryDetailState.Tab.IMAGE))
        val expected = GalleryDetailState.Tab.IMAGE
        val actual = viewModel.state.value.selectedTab
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given received SelectTab intent with INFO tab, expected expected selectedTab field in UI state is INFO`() {
        viewModel.processIntent(GalleryDetailIntent.SelectTab(GalleryDetailState.Tab.INFO))
        val expected = GalleryDetailState.Tab.INFO
        val actual = viewModel.state.value.selectedTab
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given received SelectTab intent with ORIGINAL tab, expected expected selectedTab field in UI state is ORIGINAL`() {
        viewModel.processIntent(GalleryDetailIntent.SelectTab(GalleryDetailState.Tab.ORIGINAL))
        val expected = GalleryDetailState.Tab.ORIGINAL
        val actual = viewModel.state.value.selectedTab
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given received SendTo Txt2Img intent, expected router navigateBack() and form event update() methods called`() {
        every {
            stubGenerationFormUpdateEvent.update(any(), any(), any())
        } returns Unit

        every {
            stubMainRouter.navigateBack()
        } returns Unit

        viewModel.processIntent(GalleryDetailIntent.SendTo.Txt2Img)

        verify {
            stubMainRouter.navigateBack()
        }
        verify {
            stubGenerationFormUpdateEvent.update(
                generation = mockAiGenerationResult,
                route = AiGenerationResult.Type.TEXT_TO_IMAGE,
                inputImage = false,
            )
        }
    }

    @Test
    fun `given received SendTo Img2Img intent, expected router navigateBack() and form event update() methods called`() {
        every {
            stubGenerationFormUpdateEvent.update(any(), any(), any())
        } returns Unit

        every {
            stubMainRouter.navigateBack()
        } returns Unit

        viewModel.processIntent(GalleryDetailIntent.SendTo.Img2Img)

        verify {
            stubMainRouter.navigateBack()
        }
        verify {
            stubGenerationFormUpdateEvent.update(
                generation = mockAiGenerationResult,
                route = AiGenerationResult.Type.IMAGE_TO_IMAGE,
                inputImage = false,
            )
        }
    }

    @Test
    fun `given received DismissDialog intent, expected screenModal field in UI state is None`() {
        viewModel.processIntent(GalleryDetailIntent.DismissDialog)
        val expected = Modal.None
        val actual = viewModel.state.value.screenModal
        Assert.assertEquals(expected, actual)
    }
}
