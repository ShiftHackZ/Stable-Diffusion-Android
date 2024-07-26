package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.model.asUiText
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
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GalleryDetailViewModelTest : CoreViewModelTest<GalleryDetailViewModel>() {

    private val stubBitmap = mockk<Bitmap>()
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
    fun `given received Delete_Request intent, expected modal field in UI state is DeleteImageConfirm`() {
        viewModel.processIntent(GalleryDetailIntent.Delete.Request)
        runTest {
            val expected = Modal.DeleteImageConfirm
            val actual = (viewModel.state.value as? GalleryDetailState.Content)?.screenModal
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Delete_Confirm intent, expected modal field in UI state is None, deleteGalleryItemUseCase() method is called`() {
        every {
            stubDeleteGalleryItemUseCase(any())
        } returns Completable.complete()
        viewModel.processIntent(GalleryDetailIntent.Delete.Confirm)
        runTest {
            val expected = Modal.None
            val actual = (viewModel.state.value as? GalleryDetailState.Content)?.screenModal
            Assert.assertEquals(expected, actual)
        }
        verify {
            stubDeleteGalleryItemUseCase(5598L)
        }
    }
}
