package com.shifthackz.aisdv1.presentation.screen.txt2img

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.feature.analytics.Analytics
import com.shifthackz.aisdv1.domain.feature.diffusion.LocalDiffusion
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.caching.SaveLastResultToCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveHordeProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveLocalDiffusionProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.SaveGenerationResultUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.TextToImageUseCase
import com.shifthackz.aisdv1.domain.usecase.sdsampler.GetStableDiffusionSamplersUseCase
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.core.GenerationFormUpdateEvent
import com.shifthackz.aisdv1.presentation.core.GenerationMviEffect
import com.shifthackz.aisdv1.presentation.core.GenerationMviViewModel
import com.shifthackz.aisdv1.presentation.features.AiImageGenerated
import com.shifthackz.aisdv1.presentation.notification.SdaiPushNotificationManager
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputMode
import io.reactivex.rxjava3.kotlin.subscribeBy

class TextToImageViewModel(
    getStableDiffusionSamplersUseCase: GetStableDiffusionSamplersUseCase,
    observeHordeProcessStatusUseCase: ObserveHordeProcessStatusUseCase,
    observeLocalDiffusionProcessStatusUseCase: ObserveLocalDiffusionProcessStatusUseCase,
    generationFormUpdateEvent: GenerationFormUpdateEvent,
    private val textToImageUseCase: TextToImageUseCase,
    private val saveLastResultToCacheUseCase: SaveLastResultToCacheUseCase,
    private val saveGenerationResultUseCase: SaveGenerationResultUseCase,
    private val schedulersProvider: SchedulersProvider,
    private val dimensionValidator: DimensionValidator,
    private val preferenceManager: PreferenceManager,
    private val notificationManager: SdaiPushNotificationManager,
    private val analytics: Analytics,
) : GenerationMviViewModel<TextToImageState, GenerationMviEffect>(
    schedulersProvider,
    saveLastResultToCacheUseCase,
    saveGenerationResultUseCase,
    preferenceManager,
    getStableDiffusionSamplersUseCase,
    observeHordeProcessStatusUseCase,
    observeLocalDiffusionProcessStatusUseCase,
) {

    private val progressModal: TextToImageState.Modal
        get() {
            if (currentState.mode == GenerationInputMode.LOCAL) {
                return TextToImageState.Modal.Generating()
            }
            return TextToImageState.Modal.Communicating()
        }

    override val emptyState = TextToImageState()

    init {
        !generationFormUpdateEvent.observeTxt2ImgForm()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = ::errorLog,
                onNext = ::updateFormPreviousAiGeneration,
            )
    }

    override fun setState(state: TextToImageState) = super.setState(
        state.copy(
            widthValidationError = dimensionValidator(state.width).mapToUi(),
            heightValidationError = dimensionValidator(state.height).mapToUi(),
        )
    )

    override fun onReceivedHordeStatus(status: HordeProcessStatus) {
        if (currentState.screenModal is TextToImageState.Modal.Communicating) {
            setActiveModal(TextToImageState.Modal.Communicating(status))
        }
    }

    override fun onReceivedLocalDiffusionStatus(status: LocalDiffusion.Status) {
        if (currentState.screenModal is TextToImageState.Modal.Generating) {
            setActiveModal(TextToImageState.Modal.Generating(status))
        }
    }

    override fun dismissScreenModal() = setActiveModal(TextToImageState.Modal.None)

    fun openPreviousGenerationInput() = setActiveModal(TextToImageState.Modal.PromptBottomSheet)

    fun generate() {
        !currentState
            .mapToPayload()
            .let(textToImageUseCase::invoke)
            .doOnSubscribe { setActiveModal(progressModal) }
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = { t ->
                    notificationManager.show(
                        R.string.notification_fail_title.asUiText(),
                        R.string.notification_fail_sub_title.asUiText(),
                    )
                    setActiveModal(
                        TextToImageState.Modal.Error(
                            (t.localizedMessage ?: "Something went wrong").asUiText()
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
                        TextToImageState.Modal.Image.create(ai, preferenceManager.autoSaveAiResults)
                    )
                },
            )
    }

    private fun setActiveModal(modal: TextToImageState.Modal) = currentState
        .copy(screenModal = modal)
        .let(::setState)
}
