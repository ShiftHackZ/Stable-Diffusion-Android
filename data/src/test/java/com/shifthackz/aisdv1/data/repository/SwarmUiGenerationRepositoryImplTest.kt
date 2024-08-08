package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.data.mocks.mockAiGenerationResult
import com.shifthackz.aisdv1.data.mocks.mockImageToImagePayload
import com.shifthackz.aisdv1.data.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.SwarmUiGenerationDataSource
import com.shifthackz.aisdv1.domain.datasource.SwarmUiSessionDataSource
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import org.junit.Before
import org.junit.Test

class SwarmUiGenerationRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubMediaStoreGateway = mockk<MediaStoreGateway>()
    private val stubBase64ToBitmapConverter = mockk<Base64ToBitmapConverter>()
    private val stubLocalDataSource = mockk<GenerationResultDataSource.Local>()
    private val stubRemoteDataSource = mockk<SwarmUiGenerationDataSource.Remote>()
    private val stubSession = mockk<SwarmUiSessionDataSource>()
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubBackgroundWorkObserver = mockk<BackgroundWorkObserver>()
    
    private val repository = SwarmUiGenerationRepositoryImpl(
        mediaStoreGateway = stubMediaStoreGateway,
        base64ToBitmapConverter = stubBase64ToBitmapConverter,
        localDataSource = stubLocalDataSource,
        remoteDataSource = stubRemoteDataSource,
        session = stubSession,
        preferenceManager = stubPreferenceManager,
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
            stubSession.handleSessionError(any<Single<Any>>())
        } returnsArgument 0
    }

    @Test
    fun `given attempt to check api availability, remote completes, expected complete value`() {
        every {
            stubSession.getSessionId()
        } returns Single.just("5598")

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
            stubSession.getSessionId()
        } returns Single.error(stubException)

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
            stubSession.getSessionId(any())
        } returns Single.just("5598")

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
            stubSession.getSessionId(any())
        } returns Single.error(stubException)

        repository
            .checkApiAvailability("https://5598.is.my.favourite.com")
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to generate from text, remote returns result, expected valid domain model value`() {
        every {
            stubPreferenceManager::swarmUiModel.get()
        } returns "5598"

        every {
            stubSession.getSessionId(any())
        } returns Single.just("5598")

        every {
            stubSession.getSessionId()
        } returns Single.just("5598")

        every {
            stubRemoteDataSource.textToImage(any(), any(), any())
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
            stubPreferenceManager::swarmUiModel.get()
        } returns "5598"

        every {
            stubSession.getSessionId(any())
        } returns Single.just("5598")

        every {
            stubSession.getSessionId()
        } returns Single.just("5598")

        every {
            stubRemoteDataSource.textToImage(any(), any(), any())
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
            stubPreferenceManager::swarmUiModel.get()
        } returns "5598"

        every {
            stubSession.getSessionId(any())
        } returns Single.just("5598")

        every {
            stubSession.getSessionId()
        } returns Single.just("5598")

        every {
            stubRemoteDataSource.imageToImage(any(), any(), any())
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
            stubPreferenceManager::swarmUiModel.get()
        } returns "5598"

        every {
            stubSession.getSessionId(any())
        } returns Single.just("5598")

        every {
            stubSession.getSessionId()
        } returns Single.just("5598")

        every {
            stubRemoteDataSource.imageToImage(any(), any(), any())
        } returns Single.error(stubException)

        repository
            .generateFromImage(mockImageToImagePayload)
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}
