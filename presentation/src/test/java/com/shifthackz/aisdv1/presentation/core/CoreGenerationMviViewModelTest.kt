package com.shifthackz.aisdv1.presentation.core

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.notification.PushNotificationManager
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.entity.Settings
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
import com.shifthackz.aisdv1.domain.usecase.sdsampler.GetStableDiffusionSamplersUseCase
import com.shifthackz.aisdv1.domain.usecase.wakelock.AcquireWakelockUseCase
import com.shifthackz.aisdv1.domain.usecase.wakelock.ReleaseWakeLockUseCase
import com.shifthackz.aisdv1.presentation.navigation.router.drawer.DrawerRouter
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.junit.Before
import java.util.concurrent.Executor
import java.util.concurrent.Executors

abstract class CoreGenerationMviViewModelTest<V : GenerationMviViewModel<*, *, *>> :
    CoreViewModelTest<V>() {

    protected val stubSettings = BehaviorSubject.createDefault(Settings())
    protected val stubAiForm = BehaviorSubject.create<GenerationFormUpdateEvent.Payload>()

    protected val stubPreferenceManager = mockk<PreferenceManager>()
    protected val stubSaveLastResultToCacheUseCase = mockk<SaveLastResultToCacheUseCase>()
    protected val stubSaveGenerationResultUseCase = mockk<SaveGenerationResultUseCase>()
    protected val stubGetStableDiffusionSamplersUseCase = mockk<GetStableDiffusionSamplersUseCase>()
    protected val stubObserveHordeProcessStatusUseCase = mockk<ObserveHordeProcessStatusUseCase>()
    protected val stubObserveLocalDiffusionProcessStatusUseCase = mockk<ObserveLocalDiffusionProcessStatusUseCase>()
    protected val stubInterruptGenerationUseCase = mockk<InterruptGenerationUseCase>()
    protected val stubMainRouter = mockk<MainRouter>()
    protected val stubDrawerRouter = mockk<DrawerRouter>()
    protected val stubDimensionValidator = mockk<DimensionValidator>()
    protected val stubSdaiPushNotificationManager = mockk<PushNotificationManager>()

    protected val stubAcquireWakelockUseCase = mockk<AcquireWakelockUseCase>()
    protected val stubReleaseWakelockUseCase = mockk<ReleaseWakeLockUseCase>()
    protected val stubWakeLockInterActor = mockk<WakeLockInterActor>()
    protected val stubBackgroundWorkObserver = mockk<BackgroundWorkObserver>()
    protected val stubBackgroundTaskManager = mockk<BackgroundTaskManager>()

    private val stubHordeProcessStatus = BehaviorSubject.create<HordeProcessStatus>()
    private val stubLdStatus = BehaviorSubject.create<LocalDiffusion.Status>()


    protected val stubCustomSchedulers = object : SchedulersProvider {
        override val io: Scheduler = Schedulers.io()
        override val ui: Scheduler = AndroidSchedulers.mainThread()
        override val computation: Scheduler = Schedulers.trampoline()
        override val singleThread: Executor = Executors.newSingleThreadExecutor()
    }

    @Before
    override fun initialize() {
        super.initialize()

        every {
            stubPreferenceManager.observe()
        } returns stubSettings.toFlowable(BackpressureStrategy.LATEST)

        every {
            stubObserveHordeProcessStatusUseCase()
        } returns stubHordeProcessStatus.toFlowable(BackpressureStrategy.LATEST)

        every {
            stubObserveLocalDiffusionProcessStatusUseCase()
        } returns stubLdStatus

        every {
            stubGetStableDiffusionSamplersUseCase()
        } returns Single.just(emptyList())

        every {
            stubAcquireWakelockUseCase.invoke(any())
        } returns Result.success(Unit)

        every {
            stubAcquireWakelockUseCase.invoke()
        } returns Result.success(Unit)

        every {
            stubReleaseWakelockUseCase.invoke()
        } returns Result.success(Unit)

        every {
            stubWakeLockInterActor::acquireWakelockUseCase.get()
        } returns stubAcquireWakelockUseCase

        every {
            stubWakeLockInterActor::releaseWakeLockUseCase.get()
        } returns stubReleaseWakelockUseCase
    }
}
