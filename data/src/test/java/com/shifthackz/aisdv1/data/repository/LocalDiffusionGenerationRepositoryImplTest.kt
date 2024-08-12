package com.shifthackz.aisdv1.data.repository

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersToken
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.imageprocessing.BitmapToBase64Converter
import com.shifthackz.aisdv1.data.mocks.mockLocalAiModel
import com.shifthackz.aisdv1.data.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.feature.diffusion.LocalDiffusion
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class LocalDiffusionGenerationRepositoryImplTest {

    private val stubBitmap = mockk<Bitmap>()
    private val stubException = Throwable("Something went wrong.")
    private val stubStatus = BehaviorSubject.create<LocalDiffusion.Status>()
    private val stubMediaStoreGateway = mockk<MediaStoreGateway>()
    private val stubBase64ToBitmapConverter = mockk<Base64ToBitmapConverter>()
    private val stubBitmapToBase64Converter = mockk<BitmapToBase64Converter>()
    private val stubLocalDataSource = mockk<GenerationResultDataSource.Local>()
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubLocalDiffusion = mockk<LocalDiffusion>()
    private val stubDownloadableLocalDataSource = mockk<DownloadableModelDataSource.Local>()
    private val stubBackgroundWorkObserver = mockk<BackgroundWorkObserver>()

    private val stubSchedulersProvider = object : SchedulersProvider {
        override val io: Scheduler = Schedulers.trampoline()
        override val ui: Scheduler = Schedulers.trampoline()
        override val computation: Scheduler = Schedulers.trampoline()
        override val singleThread: Executor = Executors.newSingleThreadExecutor()
    }

    private val repository = LocalDiffusionGenerationRepositoryImpl(
        mediaStoreGateway = stubMediaStoreGateway,
        base64ToBitmapConverter = stubBase64ToBitmapConverter,
        localDataSource = stubLocalDataSource,
        preferenceManager = stubPreferenceManager,
        localDiffusion = stubLocalDiffusion,
        downloadableLocalDataSource = stubDownloadableLocalDataSource,
        bitmapToBase64Converter = stubBitmapToBase64Converter,
        schedulersProvider = stubSchedulersProvider,
        backgroundWorkObserver = stubBackgroundWorkObserver,
    )

    @Before
    fun initialize() {
        every {
            stubPreferenceManager::localDiffusionSchedulerThread.get()
        } returns SchedulersToken.COMPUTATION

        every {
            stubBackgroundWorkObserver.hasActiveTasks()
        } returns false

        every {
            stubLocalDiffusion.observeStatus()
        } returns stubStatus

        every {
            stubPreferenceManager.autoSaveAiResults
        } returns false
    }

    @Test
    fun `given attempt to observe status, local emits two values, expected same values with same order`() {
        val stubObserver = repository.observeStatus().test()

        stubStatus.onNext(LocalDiffusion.Status(1, 2))

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, LocalDiffusion.Status(1, 2))

        stubStatus.onNext(LocalDiffusion.Status(2, 2))

        stubObserver
            .assertNoErrors()
            .assertValueAt(1, LocalDiffusion.Status(2, 2))
    }

    @Test
    fun `given attempt to observe status, local throws exception, expected error value`() {
        every {
            stubLocalDiffusion.observeStatus()
        } returns Observable.error(stubException)

        repository
            .observeStatus()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to interrupt generation, remote completes, expected complete value`() {
        every {
            stubLocalDiffusion.interrupt()
        } returns Completable.complete()

        repository
            .interruptGeneration()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to interrupt generation, remote throws exception, expected error value`() {
        every {
            stubLocalDiffusion.interrupt()
        } returns Completable.error(stubException)

        repository
            .interruptGeneration()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to generate from text, no selected model, expected error value`() {
        every {
            stubDownloadableLocalDataSource.getSelected()
        } returns Single.error(stubException)

        repository
            .generateFromText(mockTextToImagePayload)
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to generate from text, has selected not downloaded model, expected IllegalStateException error value`() {
        every {
            stubDownloadableLocalDataSource.getSelected()
        } returns Single.just(mockLocalAiModel.copy(downloaded = false))

        every {
            stubLocalDiffusion.process(any())
        } returns Single.just(stubBitmap)

        every {
            stubBitmapToBase64Converter(any())
        } returns Single.just(BitmapToBase64Converter.Output("base64"))

        repository
            .generateFromText(mockTextToImagePayload)
            .test()
            .assertError { t ->
                t is IllegalStateException && t.message == "Model not downloaded."
            }
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to generate from text, has selected downloaded model, local process success, expected valid domain model value`() {
        every {
            stubDownloadableLocalDataSource.getSelected()
        } returns Single.just(mockLocalAiModel.copy(downloaded = true))

        every {
            stubLocalDiffusion.process(any())
        } returns Single.just(stubBitmap)

        every {
            stubBitmapToBase64Converter(any())
        } returns Single.just(BitmapToBase64Converter.Output("base64"))

        repository
            .generateFromText(mockTextToImagePayload)
            .test()
            .assertNoErrors()
            .assertValue { it is AiGenerationResult }
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to generate from text, has selected downloaded model, local process fails, expected error value`() {
        every {
            stubDownloadableLocalDataSource.getSelected()
        } returns Single.just(mockLocalAiModel.copy(downloaded = true))

        every {
            stubLocalDiffusion.process(any())
        } returns Single.error(stubException)

        every {
            stubBitmapToBase64Converter(any())
        } returns Single.just(BitmapToBase64Converter.Output("base64"))

        repository
            .generateFromText(mockTextToImagePayload)
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}