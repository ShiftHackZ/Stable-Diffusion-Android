package com.shifthackz.aisdv1.presentation.screen.img2img

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.imageprocessing.BitmapToBase64Converter
import com.shifthackz.aisdv1.core.validation.ValidationResult
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator.Error
import com.shifthackz.aisdv1.domain.entity.OpenAiModel
import com.shifthackz.aisdv1.domain.entity.OpenAiQuality
import com.shifthackz.aisdv1.domain.entity.OpenAiSize
import com.shifthackz.aisdv1.domain.entity.OpenAiStyle
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.Settings
import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import com.shifthackz.aisdv1.domain.usecase.generation.GetRandomImageUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ImageToImageUseCase
import com.shifthackz.aisdv1.presentation.core.CoreGenerationMviViewModelTest
import com.shifthackz.aisdv1.presentation.core.GenerationFormUpdateEvent
import com.shifthackz.aisdv1.presentation.core.GenerationMviIntent
import com.shifthackz.aisdv1.presentation.mocks.mockAiGenerationResult
import com.shifthackz.aisdv1.presentation.model.InPaintModel
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.screen.drawer.DrawerIntent
import com.shifthackz.aisdv1.presentation.screen.inpaint.InPaintStateProducer
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupLaunchSource
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ImageToImageViewModelTest : CoreGenerationMviViewModelTest<ImageToImageViewModel>() {

    private val stubBitmap = mockk<Bitmap>()
    private val stubInPainModel = BehaviorSubject.create<InPaintModel>()

    private val stubGenerationFormUpdateEvent = mockk<GenerationFormUpdateEvent>()
    private val stubImageToImageUseCase = mockk<ImageToImageUseCase>()
    private val stubGetRandomImageUseCase = mockk<GetRandomImageUseCase>()
    private val stubBitmapToBase64Converter = mockk<BitmapToBase64Converter>()
    private val stubBase64ToBitmapConverter = mockk<Base64ToBitmapConverter>()
    private val stubInPaintStateProducer = mockk<InPaintStateProducer>()

    override fun initializeViewModel() = ImageToImageViewModel(
        generationFormUpdateEvent = stubGenerationFormUpdateEvent,
        getStableDiffusionSamplersUseCase = stubGetStableDiffusionSamplersUseCase,
        observeHordeProcessStatusUseCase = stubObserveHordeProcessStatusUseCase,
        observeLocalDiffusionProcessStatusUseCase = stubObserveLocalDiffusionProcessStatusUseCase,
        saveLastResultToCacheUseCase = stubSaveLastResultToCacheUseCase,
        saveGenerationResultUseCase = stubSaveGenerationResultUseCase,
        interruptGenerationUseCase = stubInterruptGenerationUseCase,
        drawerRouter = stubDrawerRouter,
        dimensionValidator = stubDimensionValidator,
        imageToImageUseCase = stubImageToImageUseCase,
        getRandomImageUseCase = stubGetRandomImageUseCase,
        bitmapToBase64Converter = stubBitmapToBase64Converter,
        base64ToBitmapConverter = stubBase64ToBitmapConverter,
        preferenceManager = stubPreferenceManager,
        schedulersProvider = stubCustomSchedulers,
        notificationManager = stubSdaiPushNotificationManager,
        wakeLockInterActor = stubWakeLockInterActor,
        inPaintStateProducer = stubInPaintStateProducer,
        mainRouter = stubMainRouter,
        backgroundWorkObserver = stubBackgroundWorkObserver,
        backgroundTaskManager = stubBackgroundTaskManager,
    )

    @Before
    override fun initialize() {
        super.initialize()

        every {
            stubGenerationFormUpdateEvent.observeImg2ImgForm()
        } returns stubAiForm.toFlowable(BackpressureStrategy.LATEST)

        every {
            stubInPaintStateProducer.observeInPaint()
        } returns stubInPainModel.toFlowable(BackpressureStrategy.LATEST)

        stubSettings.onNext(Settings(source = ServerSource.AUTOMATIC1111))
    }

    @After
    override fun finalize() {
        super.finalize()
        unmockkAll()
    }

    @Test
    fun `initialized, expected UI state update with correct stub values`() {
        runTest {
            val state = viewModel.state.value
            Assert.assertNotNull(viewModel)
            Assert.assertNotNull(viewModel.initialState)
            Assert.assertNotNull(viewModel.state.value)
            Assert.assertEquals(ServerSource.AUTOMATIC1111, state.mode)
            Assert.assertEquals(emptyList<StableDiffusionSampler>(), state.availableSamplers)
        }
        verify {
            stubGetStableDiffusionSamplersUseCase()
        }
        verify {
            stubPreferenceManager.observe()
        }
    }

    @Test
    fun `given received NewPrompts intent, expected prompt, negativePrompt updated in UI state`() {
        val intent = GenerationMviIntent.NewPrompts(
            positive = "prompt",
            negative = "negative",
        )
        viewModel.processIntent(intent)
        runTest {
            val state = viewModel.state.value
            Assert.assertEquals("prompt", state.prompt)
            Assert.assertEquals("negative", state.negativePrompt)
        }
    }

    @Test
    fun `given received SetAdvancedOptionsVisibility intent, expected advancedOptionsVisible updated in UI state`() {
        val intent = GenerationMviIntent.SetAdvancedOptionsVisibility(true)
        viewModel.processIntent(intent)
        runTest {
            val expected = true
            val actual = viewModel.state.value.advancedOptionsVisible
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Update Prompt intent, expected prompt updated in UI state`() {
        val intent = GenerationMviIntent.Update.Prompt("5598")
        viewModel.processIntent(intent)
        runTest {
            val expected = "5598"
            val actual = viewModel.state.value.prompt
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Update NegativePrompt intent, expected negativePrompt updated in UI state`() {
        val intent = GenerationMviIntent.Update.NegativePrompt("5598")
        viewModel.processIntent(intent)
        runTest {
            val expected = "5598"
            val actual = viewModel.state.value.negativePrompt
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Update Size Width intent with valid value, expected width updated, widthValidationError is null in UI state`() {
        every {
            stubDimensionValidator(any())
        } returns ValidationResult(true)

        val intent = GenerationMviIntent.Update.Size.Width("512")
        viewModel.processIntent(intent)

        runTest {
            val state = viewModel.state.value
            Assert.assertEquals("512", state.width)
            Assert.assertNull(state.widthValidationError)
        }
    }

    @Test
    fun `given received Update Size Width intent with invalid value, expected width updated, widthValidationError is NOT null in UI state`() {
        every {
            stubDimensionValidator(any())
        } returns ValidationResult(false, Error.Unexpected)

        val intent = GenerationMviIntent.Update.Size.Width("512d")
        viewModel.processIntent(intent)

        runTest {
            val state = viewModel.state.value
            Assert.assertEquals("512d", state.width)
            Assert.assertNotNull(state.widthValidationError)
        }
    }

    @Test
    fun `given received Update Size Height intent with valid value, expected height updated, heightValidationError is null in UI state`() {
        every {
            stubDimensionValidator(any())
        } returns ValidationResult(true)

        val intent = GenerationMviIntent.Update.Size.Height("512")
        viewModel.processIntent(intent)

        runTest {
            val state = viewModel.state.value
            Assert.assertEquals("512", state.height)
            Assert.assertNull(state.heightValidationError)
        }
    }

    @Test
    fun `given received Update Size Height intent with invalid value, expected height updated, heightValidationError is NOT null in UI state`() {
        every {
            stubDimensionValidator(any())
        } returns ValidationResult(false, Error.Unexpected)

        val intent = GenerationMviIntent.Update.Size.Height("512d")
        viewModel.processIntent(intent)

        runTest {
            val state = viewModel.state.value
            Assert.assertEquals("512d", state.height)
            Assert.assertNotNull(state.heightValidationError)
        }
    }

    @Test
    fun `given received Update SamplingSteps intent, expected samplingSteps updated in UI state`() {
        val intent = GenerationMviIntent.Update.SamplingSteps(12)
        viewModel.processIntent(intent)
        runTest {
            val expected = 12
            val actual = viewModel.state.value.samplingSteps
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Update CfgScale intent, expected cfgScale updated in UI state`() {
        val intent = GenerationMviIntent.Update.CfgScale(12f)
        viewModel.processIntent(intent)
        runTest {
            val expected = 12f
            val actual = viewModel.state.value.cfgScale
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Update RestoreFaces intent, expected restoreFaces updated in UI state`() {
        val intent = GenerationMviIntent.Update.RestoreFaces(true)
        viewModel.processIntent(intent)
        runTest {
            val expected = true
            val actual = viewModel.state.value.restoreFaces
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Update Seed intent, expected seed updated in UI state`() {
        val intent = GenerationMviIntent.Update.Seed("5598")
        viewModel.processIntent(intent)
        runTest {
            val expected = "5598"
            val actual = viewModel.state.value.seed
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Update SubSeed intent, expected subSeed updated in UI state`() {
        val intent = GenerationMviIntent.Update.SubSeed("5598")
        viewModel.processIntent(intent)
        runTest {
            val expected = "5598"
            val actual = viewModel.state.value.subSeed
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Update SubSeedStrength intent, expected subSeed updated in UI state`() {
        val intent = GenerationMviIntent.Update.SubSeedStrength(7f)
        viewModel.processIntent(intent)
        runTest {
            val expected = 7f
            val actual = viewModel.state.value.subSeedStrength
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Update Sampler intent, expected selectedSampler updated in UI state`() {
        val intent = GenerationMviIntent.Update.Sampler("5598")
        viewModel.processIntent(intent)
        runTest {
            val expected = "5598"
            val actual = viewModel.state.value.selectedSampler
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Update Nsfw intent, expected nsfw updated in UI state`() {
        val intent = GenerationMviIntent.Update.Nsfw(true)
        viewModel.processIntent(intent)
        runTest {
            val expected = true
            val actual = viewModel.state.value.nsfw
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Update Batch intent, expected batchCount updated in UI state`() {
        val intent = GenerationMviIntent.Update.Batch(26)
        viewModel.processIntent(intent)
        runTest {
            val expected = 26
            val actual = viewModel.state.value.batchCount
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Update OpenAi Model intent, expected openAiModel updated in UI state`() {
        val intent = GenerationMviIntent.Update.OpenAi.Model(OpenAiModel.DALL_E_2)
        viewModel.processIntent(intent)
        runTest {
            val expected = OpenAiModel.DALL_E_2
            val actual = viewModel.state.value.openAiModel
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Update OpenAi Size intent, expected openAiSize updated in UI state`() {
        val intent = GenerationMviIntent.Update.OpenAi.Size(OpenAiSize.W256_H256)
        viewModel.processIntent(intent)
        runTest {
            val expected = OpenAiSize.W256_H256
            val actual = viewModel.state.value.openAiSize
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Update OpenAi Quality intent, expected openAiQuality updated in UI state`() {
        val intent = GenerationMviIntent.Update.OpenAi.Quality(OpenAiQuality.HD)
        viewModel.processIntent(intent)
        runTest {
            val expected = OpenAiQuality.HD
            val actual = viewModel.state.value.openAiQuality
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Update OpenAi Style intent, expected openAiStyle updated in UI state`() {
        val intent = GenerationMviIntent.Update.OpenAi.Style(OpenAiStyle.NATURAL)
        viewModel.processIntent(intent)
        runTest {
            val expected = OpenAiStyle.NATURAL
            val actual = viewModel.state.value.openAiStyle
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Result Save intent, expected screenModal is None in UI state`() {
        every {
            stubSaveGenerationResultUseCase(any())
        } returns Completable.complete()

        val intent = GenerationMviIntent.Result.Save(listOf(mockAiGenerationResult))
        viewModel.processIntent(intent)

        runTest {
            val state = viewModel.state.value
            Assert.assertEquals(Modal.None, state.screenModal)
        }
    }

    @Test
    fun `given received Result View intent, expected saveGenerationResultUseCase() called`() {
        every {
            stubSaveLastResultToCacheUseCase(any())
        } returns Single.just(mockAiGenerationResult)

        every {
            stubMainRouter.navigateToGalleryDetails(any())
        } returns Unit

        val intent = GenerationMviIntent.Result.View(mockAiGenerationResult)
        viewModel.processIntent(intent)

        verify {
            stubSaveLastResultToCacheUseCase(mockAiGenerationResult)
        }
    }

    @Test
    fun `given received SetModal intent, expected screenModal updated in UI state`() {
        val intent = GenerationMviIntent.SetModal(Modal.Communicating())
        viewModel.processIntent(intent)
        runTest {
            val expected = Modal.Communicating()
            val actual = viewModel.state.value.screenModal
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Cancel Generation intent, expected interruptGenerationUseCase() called`() {
        every {
            stubInterruptGenerationUseCase()
        } returns Completable.complete()

        val intent = GenerationMviIntent.Cancel.Generation
        viewModel.processIntent(intent)

        verify {
            stubInterruptGenerationUseCase()
        }
    }

    @Test
    fun `given received Cancel FetchRandomImage intent, expected screenModal is None in UI state`() {
        val intent = GenerationMviIntent.Cancel.FetchRandomImage
        viewModel.processIntent(intent)
        runTest {
            Assert.assertEquals(
                Modal.None,
                viewModel.state.value.screenModal,
            )
        }
    }

    @Test
    fun `given received Configuration intent, expected router navigateToServerSetup() called`() {
        every {
            stubMainRouter.navigateToServerSetup(any())
        } returns Unit

        val intent = GenerationMviIntent.Configuration
        viewModel.processIntent(intent)

        verify {
            stubMainRouter.navigateToServerSetup(ServerSetupLaunchSource.SETTINGS)
        }
    }

    @Test
    fun `given received UpdateFromGeneration intent, expected UI state fields are same as intent model`() {
        every {
            stubBase64ToBitmapConverter.invoke(any())
        } returns Single.just(Base64ToBitmapConverter.Output(stubBitmap))

        val intent = GenerationMviIntent.UpdateFromGeneration(
            GenerationFormUpdateEvent.Payload.I2IForm(mockAiGenerationResult, false)
        )
        viewModel.processIntent(intent)

        runTest {
            val state = viewModel.state.value
            Assert.assertEquals(true, state.advancedOptionsVisible)
            Assert.assertEquals(mockAiGenerationResult.prompt, state.prompt)
            Assert.assertEquals(mockAiGenerationResult.negativePrompt, state.negativePrompt)
            Assert.assertEquals(mockAiGenerationResult.width.toString(), state.width)
            Assert.assertEquals(mockAiGenerationResult.height.toString(), state.height)
            Assert.assertEquals(mockAiGenerationResult.seed, state.seed)
            Assert.assertEquals(mockAiGenerationResult.subSeed, state.subSeed)
            Assert.assertEquals(mockAiGenerationResult.subSeedStrength, state.subSeedStrength)
            Assert.assertEquals(mockAiGenerationResult.samplingSteps, state.samplingSteps)
            Assert.assertEquals(mockAiGenerationResult.cfgScale, state.cfgScale)
            Assert.assertEquals(mockAiGenerationResult.restoreFaces, state.restoreFaces)
        }
    }

    @Test
    fun `given received Drawer Open intent, expected router openDrawer() called`() {
        every {
            stubDrawerRouter.openDrawer()
        } returns Unit

        val intent = GenerationMviIntent.Drawer(DrawerIntent.Open)
        viewModel.processIntent(intent)

        verify {
            stubDrawerRouter.openDrawer()
        }
    }

    @Test
    fun `given received Drawer Close intent, expected router closeDrawer() called`() {
        every {
            stubDrawerRouter.closeDrawer()
        } returns Unit

        val intent = GenerationMviIntent.Drawer(DrawerIntent.Close)
        viewModel.processIntent(intent)

        verify {
            stubDrawerRouter.closeDrawer()
        }
    }
}
