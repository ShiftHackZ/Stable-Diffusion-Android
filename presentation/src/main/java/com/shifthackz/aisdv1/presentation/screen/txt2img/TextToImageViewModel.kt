package com.shifthackz.aisdv1.presentation.screen.txt2img

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.notification.PushNotificationManager
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.diffusion.LocalDiffusion
import com.shifthackz.aisdv1.domain.feature.work.BackgroundTaskManager
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.interactor.wakelock.WakeLockInterActor
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.caching.SaveLastResultToCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.InterruptGenerationUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveHordeProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveLocalDiffusionProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.SaveGenerationResultUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.TextToImageUseCase
import com.shifthackz.aisdv1.domain.usecase.sdsampler.GetStableDiffusionSamplersUseCase
import com.shifthackz.aisdv1.presentation.core.GenerationFormUpdateEvent
import com.shifthackz.aisdv1.presentation.core.GenerationMviIntent
import com.shifthackz.aisdv1.presentation.core.GenerationMviViewModel
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.navigation.router.drawer.DrawerRouter
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.android.core.mvi.EmptyEffect
import io.reactivex.rxjava3.kotlin.subscribeBy
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

class TextToImageViewModel(
    generationFormUpdateEvent: GenerationFormUpdateEvent,
    getStableDiffusionSamplersUseCase: GetStableDiffusionSamplersUseCase,
    observeHordeProcessStatusUseCase: ObserveHordeProcessStatusUseCase,
    observeLocalDiffusionProcessStatusUseCase: ObserveLocalDiffusionProcessStatusUseCase,
    saveLastResultToCacheUseCase: SaveLastResultToCacheUseCase,
    saveGenerationResultUseCase: SaveGenerationResultUseCase,
    interruptGenerationUseCase: InterruptGenerationUseCase,
    mainRouter: MainRouter,
    drawerRouter: DrawerRouter,
    dimensionValidator: DimensionValidator,
    private val textToImageUseCase: TextToImageUseCase,
    private val schedulersProvider: SchedulersProvider,
    private val preferenceManager: PreferenceManager,
    private val notificationManager: PushNotificationManager,
    private val wakeLockInterActor: WakeLockInterActor,
    private val backgroundTaskManager: BackgroundTaskManager,
    private val backgroundWorkObserver: BackgroundWorkObserver,
) : GenerationMviViewModel<TextToImageState, GenerationMviIntent, EmptyEffect>(
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

    private val progressModal: Modal
        get() {
            if (currentState.mode == ServerSource.LOCAL) {
                return Modal.Generating(canCancel = preferenceManager.localDiffusionAllowCancel)
            }
            return Modal.Communicating()
        }

    override val initialState = TextToImageState()

    init {
        !generationFormUpdateEvent
            .observeTxt2ImgForm()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = ::errorLog,
                onNext = { payload ->
                    (payload as? GenerationFormUpdateEvent.Payload.T2IForm)
                        ?.let(::updateFormPreviousAiGeneration)
                        ?.also { generationFormUpdateEvent.clear() }
                },
            )
    }

    override fun generateDisposable() = currentState
        .mapToPayload()
        .let(textToImageUseCase::invoke)
        .doOnSubscribe {
            wakeLockInterActor.acquireWakelockUseCase()
            setActiveModal(progressModal)
        }
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
                        (t.localizedMessage ?: "Something went wrong").asUiText()
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
                    Modal.Image.create(ai, preferenceManager.autoSaveAiResults)
                )
            },
        )

    override fun generateBackground() {
        val payload = currentState.mapToPayload()
        backgroundTaskManager.scheduleTextToImageTask(payload)
    }

    override fun onReceivedHordeStatus(status: HordeProcessStatus) {
        (currentState.screenModal as? Modal.Communicating)
            ?.copy(hordeProcessStatus = status)
            ?.let(::setActiveModal)
    }

    override fun onReceivedLocalDiffusionStatus(status: LocalDiffusion.Status) {
        (currentState.screenModal as? Modal.Generating)
            ?.copy(status = status)
            ?.let(::setActiveModal)
    }
}
