package com.shifthackz.aisdv1.presentation.screen.img2img

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.imageprocessing.BitmapToBase64Converter
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.feature.analytics.Analytics
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.caching.SaveLastResultToCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.coin.ObserveCoinsUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.GetRandomImageUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ImageToImageUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveHordeProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.SaveGenerationResultUseCase
import com.shifthackz.aisdv1.domain.usecase.sdsampler.GetStableDiffusionSamplersUseCase
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.core.GenerationFormUpdateEvent
import com.shifthackz.aisdv1.presentation.core.GenerationMviViewModel
import com.shifthackz.aisdv1.presentation.features.AiImageGenerated
import com.shifthackz.aisdv1.presentation.notification.SdaiPushNotificationManager
import com.shifthackz.aisdv1.presentation.screen.txt2img.mapToUi
import com.shz.imagepicker.imagepicker.model.PickedResult
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy

class ImageToImageViewModel(
    getStableDiffusionSamplersUseCase: GetStableDiffusionSamplersUseCase,
    observeHordeProcessStatusUseCase: ObserveHordeProcessStatusUseCase,
    observeCoinsUseCase: ObserveCoinsUseCase,
    buildInfoProvider: BuildInfoProvider,
    generationFormUpdateEvent: GenerationFormUpdateEvent,
    private val imageToImageUseCase: ImageToImageUseCase,
    private val saveLastResultToCacheUseCase: SaveLastResultToCacheUseCase,
    private val saveGenerationResultUseCase: SaveGenerationResultUseCase,
    private val getRandomImageUseCase: GetRandomImageUseCase,
    private val bitmapToBase64Converter: BitmapToBase64Converter,
    private val base64ToBitmapConverter: Base64ToBitmapConverter,
    private val dimensionValidator: DimensionValidator,
    private val preferenceManager: PreferenceManager,
    private val schedulersProvider: SchedulersProvider,
    private val notificationManager: SdaiPushNotificationManager,
    private val analytics: Analytics,
) : GenerationMviViewModel<ImageToImageState, ImageToImageEffect>(
    buildInfoProvider,
    preferenceManager,
    observeCoinsUseCase,
    getStableDiffusionSamplersUseCase,
    observeHordeProcessStatusUseCase,
    schedulersProvider,
) {

    override val emptyState = ImageToImageState()

    init {
        !generationFormUpdateEvent.observeImg2ImgForm()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = ::errorLog,
                onNext = ::updateFormPreviousAiGeneration,
            )
    }

    override fun setState(state: ImageToImageState) = super.setState(
        state.copy(
            widthValidationError = dimensionValidator(state.width).mapToUi(),
            heightValidationError = dimensionValidator(state.height).mapToUi(),
        )
    )

    override fun onReceivedHordeStatus(status: HordeProcessStatus) {
        if (currentState.screenModal is ImageToImageState.Modal.Communicating) {
            setActiveDialog(ImageToImageState.Modal.Communicating(status))
        }
    }

    override fun updateFormPreviousAiGeneration(ai: AiGenerationResult): Result<Unit> {
        !base64ToBitmapConverter(Base64ToBitmapConverter.Input(ai.image))
            .map(Base64ToBitmapConverter.Output::bitmap)
            .map(ImageToImageState.ImageState::Image)
            .map { imageState -> currentState.copy(imageState = imageState) }
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = ::errorLog,
                onSuccess = ::setState
            )

        return super.updateFormPreviousAiGeneration(ai)
    }

    fun openPreviousGenerationInput() = setActiveDialog(ImageToImageState.Modal.PromptBottomSheet)

    fun dismissScreenDialog() = setActiveDialog(ImageToImageState.Modal.None)

    fun updateDenoisingStrength(value: Float) = currentState
        .copy(denoisingStrength = value)
        .let(::setState)

    fun updateInputImage(value: PickedResult) = when (value) {
        is PickedResult.Single -> currentState
            .copy(imageState = ImageToImageState.ImageState.Image(value.image.bitmap))
            .let(::setState)
        else -> Unit
    }

    fun clearInputImage() = currentState
        .copy(imageState = ImageToImageState.ImageState.None)
        .let(::setState)

    fun generate() {
        when (currentState.imageState) {
            is ImageToImageState.ImageState.Image -> {
                if (!currentState.generateButtonEnabled) {
                    setActiveDialog(ImageToImageState.Modal.NoSdAiCoins)
                    return
                }
                !Single
                    .just((currentState.imageState as ImageToImageState.ImageState.Image).bitmap)
                    .doOnSubscribe { setActiveDialog(ImageToImageState.Modal.Communicating()) }
                    .map(BitmapToBase64Converter::Input)
                    .flatMap(bitmapToBase64Converter::invoke)
                    .map(currentState::preProcessed)
                    .map(ImageToImageState::mapToPayload)
                    .flatMap(imageToImageUseCase::invoke)
                    .flatMap(saveLastResultToCacheUseCase::invoke)
                    .subscribeOnMainThread(schedulersProvider)
                    .subscribeBy(
                        onError = { t ->
                            notificationManager.show(
                                R.string.notification_fail_title.asUiText(),
                                R.string.notification_fail_sub_title.asUiText(),
                            )
                            setActiveDialog(
                                ImageToImageState.Modal.Error(
                                    UiText.Static(
                                        t.localizedMessage ?: "Error"
                                    )
                                )
                            )
                            errorLog(t)
                        },
                        onSuccess = { ai ->
                            analytics.logEvent(AiImageGenerated(ai))
                            notificationManager.show(
                                R.string.notification_finish_title.asUiText(),
                                R.string.notification_finish_sub_title.asUiText(),
                            )
                            setActiveDialog(
                                ImageToImageState.Modal.Image(
                                    ai,
                                    preferenceManager.autoSaveAiResults,
                                )
                            )
                        }
                    )
            }
            else -> Unit
        }
    }

    fun fetchRandomImage() = !getRandomImageUseCase()
        .doOnSubscribe { setActiveDialog(ImageToImageState.Modal.LoadingRandomImage) }
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(
            onError = { t ->
                setActiveDialog(
                    ImageToImageState.Modal.Error(
                        UiText.Static(
                            t.localizedMessage ?: "Error"
                        )
                    )
                )
                errorLog(t)
            },
            onSuccess = { bitmap ->
                dismissScreenDialog()
                currentState
                    .copy(imageState = ImageToImageState.ImageState.Image(bitmap))
                    .let(::setState)
            },
        )

    fun saveGeneratedResult(ai: AiGenerationResult) = !saveGenerationResultUseCase(ai)
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(::errorLog) { dismissScreenDialog() }

    private fun setActiveDialog(modal: ImageToImageState.Modal) = currentState
        .copy(screenModal = modal)
        .let(::setState)
}
