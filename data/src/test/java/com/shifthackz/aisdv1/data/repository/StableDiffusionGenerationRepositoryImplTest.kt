package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.data.mocks.mockAiGenerationResult
import com.shifthackz.aisdv1.data.mocks.mockImageToImagePayload
import com.shifthackz.aisdv1.data.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionGenerationDataSource
import com.shifthackz.aisdv1.domain.demo.ImageToImageDemo
import com.shifthackz.aisdv1.domain.demo.TextToImageDemo
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Before
import org.junit.Test

class StableDiffusionGenerationRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubMediaStoreGateway = mockk<MediaStoreGateway>()
    private val stubBase64ToBitmapConverter = mockk<Base64ToBitmapConverter>()
    private val stubLocalDataSource = mockk<GenerationResultDataSource.Local>()
    private val stubRemoteDataSource = mockk<StableDiffusionGenerationDataSource.Remote>()
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubTextToImageDemo = mockk<TextToImageDemo>()
    private val stubImageToImageDemo = mockk<ImageToImageDemo>()
    private val stubBackgroundWorkObserver = mockk<BackgroundWorkObserver>()

    private val repository = StableDiffusionGenerationRepositoryImpl(
        mediaStoreGateway = stubMediaStoreGateway,
        base64ToBitmapConverter = stubBase64ToBitmapConverter,
        localDataSource = stubLocalDataSource,
        remoteDataSource = stubRemoteDataSource,
        preferenceManager = stubPreferenceManager,
        textToImageDemo = stubTextToImageDemo,
        imageToImageDemo = stubImageToImageDemo,
        backgroundWorkObserver = stubBackgroundWorkObserver,
    )

    @Before
    fun initialize() {
        every {
            stubBackgroundWorkObserver.hasActiveTasks()
        } returns false

        every {
            stubPreferenceManager.autoSaveAiResults
        } returns false
    }

    @Test
    fun `given attempt to check api availability, remote completes, expected complete value`() {
        every {
            stubRemoteDataSource.checkAvailability()
        } returns Completable.complete()

        repository
            .checkApiAvailability()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to check api availability, remote throws exception, expected error value`() {
        every {
            stubRemoteDataSource.checkAvailability()
        } returns Completable.error(stubException)

        repository
            .checkApiAvailability()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to check api availability by url, remote completes, expected complete value`() {
        every {
            stubRemoteDataSource.checkAvailability(any())
        } returns Completable.complete()

        repository
            .checkApiAvailability("https://5598.is.my.favourite.com")
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to check api availability by url, remote throws exception, expected error value`() {
        every {
            stubRemoteDataSource.checkAvailability(any())
        } returns Completable.error(stubException)

        repository
            .checkApiAvailability("https://5598.is.my.favourite.com")
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to generate from text, demo mode is on, demo returns result, expected valid domain model value`() {
        every {
            stubPreferenceManager.demoMode
        } returns true

        every {
            stubTextToImageDemo.getDemoBase64(any())
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
    fun `given attempt to generate from text, demo mode is on, demo throws exception, expected error value`() {
        every {
            stubPreferenceManager.demoMode
        } returns true

        every {
            stubTextToImageDemo.getDemoBase64(any())
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
    fun `given attempt to generate from text, demo mode is off, remote returns result, expected valid domain model value`() {
        every {
            stubPreferenceManager.demoMode
        } returns false

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
    fun `given attempt to generate from text, demo mode is off, remote throws exception, expected error value`() {
        every {
            stubPreferenceManager.demoMode
        } returns false

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
    fun `given attempt to generate from image, demo mode is on, demo returns result, expected valid domain model value`() {
        every {
            stubPreferenceManager.demoMode
        } returns true

        every {
            stubImageToImageDemo.getDemoBase64(any())
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
    fun `given attempt to generate from image, demo mode is on, demo throws exception, expected error value`() {
        every {
            stubPreferenceManager.demoMode
        } returns true

        every {
            stubImageToImageDemo.getDemoBase64(any())
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
    fun `given attempt to generate from image, demo mode is off, remote returns result, expected valid domain model value`() {
        every {
            stubPreferenceManager.demoMode
        } returns false

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
    fun `given attempt to generate from image, demo mode is off, remote throws exception, expected error value`() {
        every {
            stubPreferenceManager.demoMode
        } returns false

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
