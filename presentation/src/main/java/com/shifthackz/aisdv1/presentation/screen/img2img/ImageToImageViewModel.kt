package com.shifthackz.aisdv1.presentation.screen.img2img

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.imageprocessing.BitmapToBase64Converter
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.feature.analytics.Analytics
import com.shifthackz.aisdv1.domain.interactor.wakelock.WakeLockInterActor
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.generation.GetRandomImageUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ImageToImageUseCase
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.core.GenerationFormUpdateEvent
import com.shifthackz.aisdv1.presentation.core.GenerationMviIntent
import com.shifthackz.aisdv1.presentation.core.GenerationMviViewModel
import com.shifthackz.aisdv1.presentation.core.ImageToImageIntent
import com.shifthackz.aisdv1.presentation.features.AiImageGenerated
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.notification.SdaiPushNotificationManager
import com.shz.imagepicker.imagepicker.model.PickedResult
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy

class ImageToImageViewModel(
    generationFormUpdateEvent: GenerationFormUpdateEvent,
    private val imageToImageUseCase: ImageToImageUseCase,
    private val getRandomImageUseCase: GetRandomImageUseCase,
    private val bitmapToBase64Converter: BitmapToBase64Converter,
    private val base64ToBitmapConverter: Base64ToBitmapConverter,
    private val preferenceManager: PreferenceManager,
    private val schedulersProvider: SchedulersProvider,
    private val notificationManager: SdaiPushNotificationManager,
    private val analytics: Analytics,
    private val wakeLockInterActor: WakeLockInterActor,
) : GenerationMviViewModel<ImageToImageState, GenerationMviIntent, ImageToImageEffect>() {

    override val initialState = ImageToImageState()

    init {
        !generationFormUpdateEvent
            .observeImg2ImgForm()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = ::errorLog,
                onNext = ::updateFormPreviousAiGeneration,
            )
    }

    override fun processIntent(intent: GenerationMviIntent) {
        when (intent) {
            ImageToImageIntent.ClearImageInput -> updateState {
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
                            updateState {
                                it.copy(imageState = ImageToImageState.ImageState.Image(bitmap))
                            }
                        },
                    )
            }

            is ImageToImageIntent.UpdateDenoisingStrength -> updateState {
                it.copy(denoisingStrength = intent.value)
            }

            ImageToImageIntent.Pick.Camera -> emitEffect(ImageToImageEffect.CameraPicker)

            ImageToImageIntent.Pick.Gallery -> emitEffect(ImageToImageEffect.GalleryPicker)

            is ImageToImageIntent.UpdateImage -> when (intent.result) {
                is PickedResult.Single -> updateState {
                    it.copy(imageState = ImageToImageState.ImageState.Image(intent.result.image.bitmap))
                }

                else -> Unit
            }

            else -> super.processIntent(intent)
        }
    }

    override fun generate() = when (currentState.imageState) {
        is ImageToImageState.ImageState.Image -> {
            Single
                .just((currentState.imageState as ImageToImageState.ImageState.Image).bitmap)
                .doOnSubscribe {
                    wakeLockInterActor.acquireWakelockUseCase()
                    setActiveModal(Modal.Communicating())
                }
                .map(BitmapToBase64Converter::Input)
                .flatMap(bitmapToBase64Converter::invoke)
                .map(currentState::preProcessed)
                .map(ImageToImageState::mapToPayload)
                .flatMap(imageToImageUseCase::invoke)
                .doFinally { wakeLockInterActor.releaseWakeLockUseCase() }
                .subscribeOnMainThread(schedulersProvider)
                .subscribeBy(
                    onError = { t ->
                        notificationManager.show(
                            R.string.notification_fail_title.asUiText(),
                            R.string.notification_fail_sub_title.asUiText(),
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
                        ai.forEach { analytics.logEvent(AiImageGenerated(it)) }
                        notificationManager.show(
                            R.string.notification_finish_title.asUiText(),
                            R.string.notification_finish_sub_title.asUiText(),
                        )
                        setActiveModal(
                            Modal.Image.create(
                                ai,
                                preferenceManager.autoSaveAiResults,
                            )
                        )
                    }
                )
        }

        else -> Disposable.empty()
    }

    override fun onReceivedHordeStatus(status: HordeProcessStatus) {
        if (currentState.screenModal is Modal.Communicating) {
            setActiveModal(Modal.Communicating(status))
        }
    }

    override fun updateFormPreviousAiGeneration(ai: AiGenerationResult): Result<Unit> {
        !base64ToBitmapConverter(Base64ToBitmapConverter.Input(ai.image))
            .map(Base64ToBitmapConverter.Output::bitmap)
            .map(ImageToImageState.ImageState::Image)
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = ::errorLog,
                onSuccess = { imageState ->
                    updateState { it.copy(imageState = imageState) }
                }
            )

        return super.updateFormPreviousAiGeneration(ai)
    }
}
