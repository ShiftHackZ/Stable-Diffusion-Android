package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.data.mocks.mockAiGenerationResult
import com.shifthackz.aisdv1.data.mocks.mockImageToImagePayload
import com.shifthackz.aisdv1.data.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.HordeGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.junit.Before
import org.junit.Test

class HordeGenerationRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubStatus = BehaviorSubject.create<HordeProcessStatus>()
    private val stubMediaStoreGateway = mockk<MediaStoreGateway>()
    private val stubBase64ToBitmapConverter = mockk<Base64ToBitmapConverter>()
    private val stubLocalDataSource = mockk<GenerationResultDataSource.Local>()
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubRemoteDataSource = mockk<HordeGenerationDataSource.Remote>()
    private val stubStatusSource = mockk<HordeGenerationDataSource.StatusSource>()
    private val stubBackgroundWorkObserver = mockk<BackgroundWorkObserver>()

    private val repository = HordeGenerationRepositoryImpl(
        mediaStoreGateway = stubMediaStoreGateway,
        base64ToBitmapConverter = stubBase64ToBitmapConverter,
        localDataSource = stubLocalDataSource,
        preferenceManager = stubPreferenceManager,
        remoteDataSource = stubRemoteDataSource,
        statusSource = stubStatusSource,
        backgroundWorkObserver = stubBackgroundWorkObserver,
    )

    @Before
    fun initialize() {
        every {
            stubBackgroundWorkObserver.hasActiveTasks()
        } returns false

        every {
            stubStatusSource.observe()
        } returns stubStatus.toFlowable(BackpressureStrategy.LATEST)

        every {
            stubPreferenceManager.autoSaveAiResults
        } returns false
    }

    @Test
    fun `given attempt to observe status, status source emits two values, expected valid values in same order`() {
        val stubObserver = repository.observeStatus().test()

        stubStatus.onNext(HordeProcessStatus(5598, 1504))

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, HordeProcessStatus(5598, 1504))

        stubStatus.onNext(HordeProcessStatus(0, 0))

        stubObserver
            .assertNoErrors()
            .assertValueAt(1, HordeProcessStatus(0, 0))
    }

    @Test
    fun `given attempt to observe status, status source throws exception, expected error value`() {
        every {
            stubStatusSource.observe()
        } returns Flowable.error(stubException)

        repository
            .observeStatus()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to validate api key, remote returns true, expected true value`() {
        every {
            stubRemoteDataSource.validateApiKey()
        } returns Single.just(true)

        repository
            .validateApiKey()
            .test()
            .assertNoErrors()
            .assertValue(true)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to validate api key, remote returns false, expected false value`() {
        every {
            stubRemoteDataSource.validateApiKey()
        } returns Single.just(false)

        repository
            .validateApiKey()
            .test()
            .assertNoErrors()
            .assertValue(false)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to validate api key, remote throws exception, expected error value`() {
        every {
            stubRemoteDataSource.validateApiKey()
        } returns Single.error(stubException)

        repository
            .validateApiKey()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to generate from text, remote returns result, expected valid domain model value`() {
        every {
            stubRemoteDataSource.textToImage(any())
        } returns Single.just(mockAiGenerationResult)

        repository
            .generateFromText(mockTextToImagePayload)
            .test()
            .assertNoErrors()
            .assertValue(mockAiGenerationResult)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to generate from text, remote throws exception, expected error value`() {
        every {
            stubRemoteDataSource.textToImage(any())
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
    fun `given attempt to generate from image, remote returns result, expected valid domain model value`() {
        every {
            stubRemoteDataSource.imageToImage(any())
        } returns Single.just(mockAiGenerationResult)

        repository
            .generateFromImage(mockImageToImagePayload)
            .test()
            .assertNoErrors()
            .assertValue(mockAiGenerationResult)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to generate from image, remote throws exception, expected error value`() {
        every {
            stubRemoteDataSource.imageToImage(any())
        } returns Single.error(stubException)

        repository
            .generateFromImage(mockImageToImagePayload)
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to interrupt generation, remote completes, expected complete value`() {
        every {
            stubRemoteDataSource.interruptGeneration()
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
            stubRemoteDataSource.interruptGeneration()
        } returns Completable.error(stubException)

        repository
            .interruptGeneration()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
