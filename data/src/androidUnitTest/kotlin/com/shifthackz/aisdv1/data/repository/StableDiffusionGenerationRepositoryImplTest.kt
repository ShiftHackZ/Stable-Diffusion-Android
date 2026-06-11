package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockAiGenerationResult
import com.shifthackz.aisdv1.data.mocks.mockImageToImagePayload
import com.shifthackz.aisdv1.data.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionGenerationDataSource
import com.shifthackz.aisdv1.domain.demo.ImageToImageDemo
import com.shifthackz.aisdv1.domain.demo.TextToImageDemo
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class StableDiffusionGenerationRepositoryImplTest {

    private val stubException = RuntimeException("Something went wrong.")
    private val stubMediaStoreGateway = mockk<MediaStoreGateway>()
    private val stubLocalDataSource = mockk<GenerationResultDataSource.Local>()
    private val stubRemoteDataSource = mockk<StableDiffusionGenerationDataSource.Remote>()
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubAuthorizationStore = mockk<AuthorizationStore>()
    private val stubTextToImageDemo = mockk<TextToImageDemo>()
    private val stubImageToImageDemo = mockk<ImageToImageDemo>()
    private val stubBackgroundWorkObserver = mockk<BackgroundWorkObserver>()

    private val repository = StableDiffusionGenerationRepositoryImpl(
        mediaStoreGateway = stubMediaStoreGateway,
        localDataSource = stubLocalDataSource,
        remoteDataSource = stubRemoteDataSource,
        preferenceManager = stubPreferenceManager,
        authorizationStore = stubAuthorizationStore,
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

        every {
            stubPreferenceManager.automatic1111ServerUrl
        } returns BASE_URL

        every {
            stubPreferenceManager.sdModel
        } returns SD_MODEL

        every {
            stubAuthorizationStore.getAuthorizationCredentials()
        } returns AUTHORIZATION_CREDENTIALS
    }

    @Test
    fun `given attempt to check api availability, remote completes, expected complete value`() = runTest {
        coEvery {
            stubRemoteDataSource.checkAvailability(BASE_URL, AUTHORIZATION_CREDENTIALS)
        } returns Unit

        val actual = runCatching { repository.checkApiAvailability() }

        Assert.assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to check api availability, remote throws exception, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.checkAvailability(BASE_URL, AUTHORIZATION_CREDENTIALS)
        } throws stubException

        val actual = runCatching { repository.checkApiAvailability() }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to check api availability by url, remote completes, expected complete value`() = runTest {
        coEvery {
            stubRemoteDataSource.checkAvailability(CUSTOM_BASE_URL, AUTHORIZATION_CREDENTIALS)
        } returns Unit

        val actual = runCatching { repository.checkApiAvailability(CUSTOM_BASE_URL) }

        Assert.assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to check api availability by url, remote throws exception, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.checkAvailability(CUSTOM_BASE_URL, AUTHORIZATION_CREDENTIALS)
        } throws stubException

        val actual = runCatching { repository.checkApiAvailability(CUSTOM_BASE_URL) }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to generate from text, demo mode is on, demo returns result, expected valid domain model value`() = runTest {
        every {
            stubPreferenceManager.demoMode
        } returns true

        coEvery {
            stubTextToImageDemo.getDemoBase64(mockTextToImagePayload)
        } returns mockAiGenerationResult

        val actual = repository.generateFromText(mockTextToImagePayload)

        Assert.assertEquals(
            listOf(mockAiGenerationResult.copy(modelName = SD_MODEL)),
            actual,
        )
    }

    @Test
    fun `given attempt to generate from text, demo mode is on, demo throws exception, expected error value`() = runTest {
        every {
            stubPreferenceManager.demoMode
        } returns true

        coEvery {
            stubTextToImageDemo.getDemoBase64(mockTextToImagePayload)
        } throws stubException

        val actual = runCatching { repository.generateFromText(mockTextToImagePayload) }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to generate from text, demo mode is off, remote returns result, expected valid domain model value`() = runTest {
        every {
            stubPreferenceManager.demoMode
        } returns false

        coEvery {
            stubRemoteDataSource.textToImage(BASE_URL, AUTHORIZATION_CREDENTIALS, mockTextToImagePayload)
        } returns listOf(mockAiGenerationResult)

        val actual = repository.generateFromText(mockTextToImagePayload)

        Assert.assertEquals(
            listOf(mockAiGenerationResult.copy(modelName = SD_MODEL)),
            actual,
        )
    }

    @Test
    fun `given attempt to generate from text, demo mode is off, remote throws exception, expected error value`() = runTest {
        every {
            stubPreferenceManager.demoMode
        } returns false

        coEvery {
            stubRemoteDataSource.textToImage(BASE_URL, AUTHORIZATION_CREDENTIALS, mockTextToImagePayload)
        } throws stubException

        val actual = runCatching { repository.generateFromText(mockTextToImagePayload) }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to generate from image, demo mode is on, demo returns result, expected valid domain model value`() = runTest {
        every {
            stubPreferenceManager.demoMode
        } returns true

        coEvery {
            stubImageToImageDemo.getDemoBase64(mockImageToImagePayload)
        } returns mockAiGenerationResult

        val actual = repository.generateFromImage(mockImageToImagePayload)

        Assert.assertEquals(
            listOf(mockAiGenerationResult.copy(modelName = SD_MODEL)),
            actual,
        )
    }

    @Test
    fun `given attempt to generate from image, demo mode is on, demo throws exception, expected error value`() = runTest {
        every {
            stubPreferenceManager.demoMode
        } returns true

        coEvery {
            stubImageToImageDemo.getDemoBase64(mockImageToImagePayload)
        } throws stubException

        val actual = runCatching { repository.generateFromImage(mockImageToImagePayload) }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to generate from image, demo mode is off, remote returns result, expected valid domain model value`() = runTest {
        every {
            stubPreferenceManager.demoMode
        } returns false

        coEvery {
            stubRemoteDataSource.imageToImage(BASE_URL, AUTHORIZATION_CREDENTIALS, mockImageToImagePayload)
        } returns listOf(mockAiGenerationResult)

        val actual = repository.generateFromImage(mockImageToImagePayload)

        Assert.assertEquals(
            listOf(mockAiGenerationResult.copy(modelName = SD_MODEL)),
            actual,
        )
    }

    @Test
    fun `given attempt to generate from image, demo mode is off, remote throws exception, expected error value`() = runTest {
        every {
            stubPreferenceManager.demoMode
        } returns false

        coEvery {
            stubRemoteDataSource.imageToImage(BASE_URL, AUTHORIZATION_CREDENTIALS, mockImageToImagePayload)
        } throws stubException

        val actual = runCatching { repository.generateFromImage(mockImageToImagePayload) }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to interrupt generation, remote completes, expected complete value`() = runTest {
        coEvery {
            stubRemoteDataSource.interruptGeneration(BASE_URL, AUTHORIZATION_CREDENTIALS)
        } returns Unit

        val actual = runCatching { repository.interruptGeneration() }

        Assert.assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to interrupt generation, remote throws exception, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.interruptGeneration(BASE_URL, AUTHORIZATION_CREDENTIALS)
        } throws stubException

        val actual = runCatching { repository.interruptGeneration() }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    private companion object {
        const val BASE_URL = "http://192.168.0.1:7860"
        const val CUSTOM_BASE_URL = "https://5598.is.my.favourite.com"
        const val SD_MODEL = "sd-model"
        val AUTHORIZATION_CREDENTIALS = AuthorizationCredentials.HttpBasic(
            login = "5598",
            password = "151297",
        )
    }
}
