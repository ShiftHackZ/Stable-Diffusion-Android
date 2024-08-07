package com.shifthackz.aisdv1.presentation.screen.img2img

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.imageprocessing.BitmapToBase64Converter
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.notification.PushNotificationManager
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.feature.work.BackgroundTaskManager
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.interactor.wakelock.WakeLockInterActor
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.caching.SaveLastResultToCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.GetRandomImageUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ImageToImageUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.InterruptGenerationUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveHordeProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveLocalDiffusionProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.SaveGenerationResultUseCase
import com.shifthackz.aisdv1.domain.usecase.sdsampler.GetStableDiffusionSamplersUseCase
import com.shifthackz.aisdv1.presentation.core.GenerationFormUpdateEvent
import com.shifthackz.aisdv1.presentation.core.GenerationMviIntent
import com.shifthackz.aisdv1.presentation.core.GenerationMviViewModel
import com.shifthackz.aisdv1.presentation.core.ImageToImageIntent
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.navigation.router.drawer.DrawerRouter
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.aisdv1.presentation.screen.inpaint.InPaintStateProducer
import com.shz.imagepicker.imagepicker.model.PickedResult
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

class ImageToImageViewModel(
    generationFormUpdateEvent: GenerationFormUpdateEvent,
    getStableDiffusionSamplersUseCase: GetStableDiffusionSamplersUseCase,
    observeHordeProcessStatusUseCase: ObserveHordeProcessStatusUseCase,
    observeLocalDiffusionProcessStatusUseCase: ObserveLocalDiffusionProcessStatusUseCase,
    saveLastResultToCacheUseCase: SaveLastResultToCacheUseCase,
    saveGenerationResultUseCase: SaveGenerationResultUseCase,
    interruptGenerationUseCase: InterruptGenerationUseCase,
    drawerRouter: DrawerRouter,
    dimensionValidator: DimensionValidator,
    private val imageToImageUseCase: ImageToImageUseCase,
    private val getRandomImageUseCase: GetRandomImageUseCase,
    private val bitmapToBase64Converter: BitmapToBase64Converter,
    private val base64ToBitmapConverter: Base64ToBitmapConverter,
    private val preferenceManager: PreferenceManager,
    private val schedulersProvider: SchedulersProvider,
    private val notificationManager: PushNotificationManager,
    private val wakeLockInterActor: WakeLockInterActor,
    private val inPaintStateProducer: InPaintStateProducer,
    private val mainRouter: MainRouter,
    private val backgroundTaskManager: BackgroundTaskManager,
    private val backgroundWorkObserver: BackgroundWorkObserver,
) : GenerationMviViewModel<ImageToImageState, GenerationMviIntent, ImageToImageEffect>(
    preferenceManager = preferenceManager,
    getStableDiffusionSamplersUseCase = getStableDiffusionSamplersUseCase,
    observeHordeProcessStatusUseCase = observeHordeProcessStatusUseCase,
    observeLocalDiffusionProcessStatusUseCase = observeLocalDiffusionProcessStatusUseCase,
    saveLastResultToCacheUseCase = saveLastResultToCacheUseCase,
    saveGenerationResultUseCase = saveGenerationResultUseCase,
    interruptGenerationUseCase = interruptGenerationUseCase,
    mainRouter = mainRouter,
    drawerRouter = drawerRouter,
    dimensionValidator = dimensionValidator,
    schedulersProvider = schedulersProvider,
    backgroundWorkObserver = backgroundWorkObserver,
) {

    override val initialState = ImageToImageState()

    init {
        !generationFormUpdateEvent
            .observeImg2ImgForm()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = ::errorLog,
                onNext = { payload ->
                    (payload as? GenerationFormUpdateEvent.Payload.I2IForm)
                        ?.let(::updateFormPreviousAiGeneration)
                        ?.also { generationFormUpdateEvent.clear() }
                },
            )

        !inPaintStateProducer
            .observeInPaint()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog) { inPaint ->
                updateState { it.copy(inPaintModel = inPaint) }
            }
    }

    override fun processIntent(intent: GenerationMviIntent) {
        when (intent) {
            ImageToImageIntent.ClearImageInput -> updateState {
                inPaintStateProducer.updateInPaint(it.inPaintModel.clear())
                it.copy(imageState = ImageToImageState.ImageState.None)
            }

            ImageToImageIntent.FetchRandomPhoto -> fetchRandomImage {
                getRandomImageUseCase()
                    .doOnSubscribe { setActiveModal(Modal.LoadingRandomImage) }
                    .subscribeOnMainThread(schedulersProvider)
                    .subscribeBy(
                        onError = { t ->
                            setActiveModal(
                                Modal.Error(
                                    UiText.Static(
                                        t.localizedMessage ?: "Error"
                                    )
                                )
                            )
                            errorLog(t)
                        },
                        onSuccess = { bitmap ->
                            setActiveModal(Modal.None)
                            updateState { state ->
                                inPaintStateProducer.updateInPaint(state.inPaintModel.clear())
                                state.copy(imageState = ImageToImageState.ImageState.Image(bitmap))
                            }
                        },
                    )
            }

            is ImageToImageIntent.UpdateDenoisingStrength -> updateState {
                it.copy(denoisingStrength = intent.value)
            }

            ImageToImageIntent.Pick.Camera -> emitEffect(ImageToImageEffect.CameraPicker)

            ImageToImageIntent.Pick.Gallery -> emitEffect(ImageToImageEffect.GalleryPicker)

            is ImageToImageIntent.CropImage -> when (intent.result) {
                is PickedResult.Single -> updateState {
                    it.copy(screenModal = Modal.Image.Crop(intent.result.image.bitmap))
                }

                else -> Unit
            }

            is ImageToImageIntent.UpdateImage -> updateState {
                it.copy(
                    screenModal = Modal.None,
                    imageState = ImageToImageState.ImageState.Image(intent.bitmap),
                )
            }

            ImageToImageIntent.InPaint -> (currentState.imageState as? ImageToImageState.ImageState.Image)
                ?.let { image -> inPaintStateProducer.updateBitmap(image.bitmap) }
                ?.also { inPaintStateProducer.updateInPaint(currentState.inPaintModel) }
                ?.also { mainRouter.navigateToInPaint() }

            else -> super.processIntent(intent)
        }
    }

    override fun generateDisposable() = when (currentState.imageState) {
        is ImageToImageState.ImageState.Image -> generateImageToImagePayload()
            .doOnSubscribe {
                wakeLockInterActor.acquireWakelockUseCase()
                setActiveModal(Modal.Communicating())
            }
            .flatMap(imageToImageUseCase::invoke)
            .doFinally { wakeLockInterActor.releaseWakeLockUseCase() }
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = { t ->
                    notificationManager.createAndShowInstant(
                        LocalizationR.string.notification_fail_title.asUiText(),
                        LocalizationR.string.notification_fail_sub_title.asUiText(),
                    )
                    setActiveModal(
                        Modal.Error(
                            UiText.Static(
                                t.localizedMessage ?: "Error"
                            )
                        )
                    )
                    errorLog(t)
                },
                onSuccess = { ai ->
                    notificationManager.createAndShowInstant(
                        LocalizationR.string.notification_finish_title.asUiText(),
                        LocalizationR.string.notification_finish_sub_title.asUiText(),
                    )
                    setActiveModal(
                        Modal.Image.create(
                            ai,
                            preferenceManager.autoSaveAiResults,
                        )
                    )
                }
            )

        else -> Disposable.empty()
    }

    override fun generateBackground() {
        if (currentState.imageState !is ImageToImageState.ImageState.Image) return
        !generateImageToImagePayload()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog) { payload ->
                backgroundTaskManager.scheduleImageToImageTask(payload)
            }
    }

    override fun onReceivedHordeStatus(status: HordeProcessStatus) {
        if (currentState.screenModal is Modal.Communicating) {
            setActiveModal(Modal.Communicating(hordeProcessStatus = status))
        }
    }

    override fun updateFormPreviousAiGeneration(payload: GenerationFormUpdateEvent.Payload) {
        if (payload !is GenerationFormUpdateEvent.Payload.I2IForm) return
        val base64 = if (payload.inputImage) payload.ai.inputImage else payload.ai.image
        !base64ToBitmapConverter(Base64ToBitmapConverter.Input(base64))
            .map(Base64ToBitmapConverter.Output::bitmap)
            .map(ImageToImageState.ImageState::Image)
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = ::errorLog,
                onSuccess = { imageState ->
                    updateState { state ->
                        state.copy(
                            imageState = imageState,
                            inPaintModel = state.inPaintModel.clear(),
                        )
                    }
                }
            )

        return super.updateFormPreviousAiGeneration(payload)
    }

    private fun generateImageToImagePayload(): Single<ImageToImagePayload> = Single
        .just(
            Pair(
                (currentState.imageState as ImageToImageState.ImageState.Image).bitmap,
                currentState.inPaintModel.bitmap,
            )
        )
        .flatMap { (bmp, maskBmp) ->
            bitmapToBase64Converter(BitmapToBase64Converter.Input(bmp))
                .map(BitmapToBase64Converter.Output::base64ImageString)
                .flatMap { base64 ->
                    maskBmp?.let {
                        bitmapToBase64Converter(BitmapToBase64Converter.Input(maskBmp))
                            .map(BitmapToBase64Converter.Output::base64ImageString)
                            .map { maskBase64 -> base64 to maskBase64 }
                    } ?: run {
                        Single.just(base64 to "")
                    }
                }
        }
        .map(currentState::preProcessed)
        .map(ImageToImageState::mapToPayload)
}
