package com.shifthackz.aisdv1.presentation.core

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.entity.Settings
import com.shifthackz.aisdv1.domain.feature.diffusion.LocalDiffusion
import com.shifthackz.aisdv1.domain.interactor.wakelock.WakeLockInterActor
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.caching.SaveLastResultToCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.InterruptGenerationUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveHordeProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveLocalDiffusionProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.SaveGenerationResultUseCase
import com.shifthackz.aisdv1.domain.usecase.sdsampler.GetStableDiffusionSamplersUseCase
import com.shifthackz.aisdv1.presentation.navigation.router.drawer.DrawerRouter
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.aisdv1.presentation.notification.SdaiPushNotificationManager
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.junit.After
import org.junit.Before
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import java.util.concurrent.Executor
import java.util.concurrent.Executors

abstract class CoreGenerationMviViewModelTest<V : GenerationMviViewModel<*, *, *>> :
    CoreViewModelTest<V>(), KoinTest {

    protected val stubSettings = BehaviorSubject.createDefault(Settings())
    protected val stubAiForm = BehaviorSubject.create<AiGenerationResult>()

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
    protected val stubSdaiPushNotificationManager = mockk<SdaiPushNotificationManager>()
    protected val stubWakeLockInterActor = mockk<WakeLockInterActor>()

    private val stubHordeProcessStatus = BehaviorSubject.create<HordeProcessStatus>()
    private val stubLdStatus = BehaviorSubject.create<LocalDiffusion.Status>()

    private val stubCustomSchedulers = object : SchedulersProvider {
        override val io: Scheduler = Schedulers.trampoline()
        override val ui: Scheduler = AndroidSchedulers.mainThread()
        override val computation: Scheduler = Schedulers.computation()
        override val singleThread: Executor = Executors.newSingleThreadExecutor()
    }

    @Before
    override fun initialize() {
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

        startKoin {
            modules(
                module {
                    single<PreferenceManager> { stubPreferenceManager }
                    single<SchedulersProvider> { stubCustomSchedulers }
                    single<SaveLastResultToCacheUseCase> { stubSaveLastResultToCacheUseCase }
                    single<SaveGenerationResultUseCase> { stubSaveGenerationResultUseCase }
                    single<GetStableDiffusionSamplersUseCase> { stubGetStableDiffusionSamplersUseCase }
                    single<ObserveHordeProcessStatusUseCase> { stubObserveHordeProcessStatusUseCase }
                    single<ObserveLocalDiffusionProcessStatusUseCase> { stubObserveLocalDiffusionProcessStatusUseCase }
                    single<InterruptGenerationUseCase> { stubInterruptGenerationUseCase }
                    single<MainRouter> { stubMainRouter }
                    single<DrawerRouter> { stubDrawerRouter }
                    single<DimensionValidator> { stubDimensionValidator }
                }
            )
        }
        super.initialize()
    }

    @After
    override fun finalize() {
        stopKoin()
        super.finalize()
    }
}
