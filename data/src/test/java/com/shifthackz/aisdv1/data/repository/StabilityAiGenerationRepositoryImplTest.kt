package com.shifthackz.aisdv1.data.repository

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.data.mocks.mockAiGenerationResult
import com.shifthackz.aisdv1.data.mocks.mockImageToImagePayload
import com.shifthackz.aisdv1.data.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.StabilityAiCreditsDataSource
import com.shifthackz.aisdv1.domain.datasource.StabilityAiGenerationDataSource
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Before
import org.junit.Test

class StabilityAiGenerationRepositoryImplTest {

    private val stubBitmap = mockk<Bitmap>()
    private val stubException = Throwable("Something went wrong.")
    private val stubMediaStoreGateway = mockk<MediaStoreGateway>()
    private val stubBase64ToBitmapConverter = mockk<Base64ToBitmapConverter>()
    private val stubLocalDataSource = mockk<GenerationResultDataSource.Local>()
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubRemoteDataSource = mockk<StabilityAiGenerationDataSource.Remote>()
    private val stubCreditsRds = mockk<StabilityAiCreditsDataSource.Remote>()
    private val stubCreditsLds = mockk<StabilityAiCreditsDataSource.Local>()
    private val stubBackgroundWorkObserver = mockk<BackgroundWorkObserver>()

    private val repository = StabilityAiGenerationRepositoryImpl(
        mediaStoreGateway = stubMediaStoreGateway,
        base64ToBitmapConverter = stubBase64ToBitmapConverter,
        localDataSource = stubLocalDataSource,
        preferenceManager = stubPreferenceManager,
        generationRds = stubRemoteDataSource,
        creditsRds = stubCreditsRds,
        creditsLds = stubCreditsLds,
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

        every {
            stubPreferenceManager.stabilityAiEngineId
        } returns "engine_5598"

        every {
            stubCreditsRds.fetch()
        } returns Single.just(5598f)

        every {
            stubCreditsLds.save(any())
        } returns Completable.complete()

        every {
            stubBitmap.compress(any(), any(), any())
        } returns true
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
            stubRemoteDataSource.textToImage(any(), any())
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
            stubRemoteDataSource.textToImage(any(), any())
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
            stubRemoteDataSource.imageToImage(any(), any(), any())
        } returns Single.just(mockAiGenerationResult)

        every {
            stubBase64ToBitmapConverter(any())
        } returns Single.just(Base64ToBitmapConverter.Output(stubBitmap))

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
            stubRemoteDataSource.imageToImage(any(), any(), any())
        } returns Single.error(stubException)

        every {
            stubBase64ToBitmapConverter(any())
        } returns Single.just(Base64ToBitmapConverter.Output(stubBitmap))

        repository
            .generateFromImage(mockImageToImagePayload)
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}
