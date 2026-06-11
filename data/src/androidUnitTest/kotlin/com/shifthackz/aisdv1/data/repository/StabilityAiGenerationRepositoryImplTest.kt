package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockAiGenerationResult
import com.shifthackz.aisdv1.data.mocks.mockImageToImagePayload
import com.shifthackz.aisdv1.data.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.StabilityAiCreditsDataSource
import com.shifthackz.aisdv1.domain.datasource.StabilityAiCreditsRemoteDataSource
import com.shifthackz.aisdv1.domain.datasource.StabilityAiGenerationDataSource
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

class StabilityAiGenerationRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubMediaStoreGateway = mockk<MediaStoreGateway>()
    private val stubLocalDataSource = mockk<GenerationResultDataSource.Local>()
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubRemoteDataSource = mockk<StabilityAiGenerationDataSource.Remote>()
    private val stubCreditsRds = mockk<StabilityAiCreditsRemoteDataSource>()
    private val stubCreditsLds = mockk<StabilityAiCreditsDataSource.Local>()
    private val stubBackgroundWorkObserver = mockk<BackgroundWorkObserver>()

    private val repository = StabilityAiGenerationRepositoryImpl(
        mediaStoreGateway = stubMediaStoreGateway,
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
            stubPreferenceManager.stabilityAiApiKey
        } returns API_KEY

        every {
            stubPreferenceManager.stabilityAiEngineId
        } returns ENGINE_ID

        coEvery {
            stubCreditsRds.fetch(API_KEY)
        } returns 5598f

        coEvery {
            stubCreditsLds.save(any())
        } returns Unit
    }

    @Test
    fun `given attempt to validate api key, remote returns true, expected true value`() = runTest {
        coEvery {
            stubRemoteDataSource.validateApiKey(API_KEY)
        } returns true

        val actual = repository.validateApiKey()

        Assert.assertTrue(actual)
    }

    @Test
    fun `given attempt to validate api key, remote returns false, expected false value`() = runTest {
        coEvery {
            stubRemoteDataSource.validateApiKey(API_KEY)
        } returns false

        val actual = repository.validateApiKey()

        Assert.assertFalse(actual)
    }

    @Test
    fun `given attempt to validate api key, remote throws exception, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.validateApiKey(API_KEY)
        } throws stubException

        val actual = runCatching { repository.validateApiKey() }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to generate from text, remote returns result, expected valid domain model value`() = runTest {
        coEvery {
            stubRemoteDataSource.textToImage(API_KEY, ENGINE_ID, mockTextToImagePayload)
        } returns mockAiGenerationResult

        val actual = repository.generateFromText(mockTextToImagePayload)

        Assert.assertEquals(mockAiGenerationResult.copy(modelName = ENGINE_ID), actual)
    }

    @Test
    fun `given attempt to generate from text, remote throws exception, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.textToImage(API_KEY, ENGINE_ID, mockTextToImagePayload)
        } throws stubException

        val actual = runCatching { repository.generateFromText(mockTextToImagePayload) }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to generate from image, remote returns result, expected valid domain model value`() = runTest {
        coEvery {
            stubRemoteDataSource.imageToImage(API_KEY, ENGINE_ID, mockImageToImagePayload)
        } returns mockAiGenerationResult

        val actual = repository.generateFromImage(mockImageToImagePayload)

        Assert.assertEquals(mockAiGenerationResult.copy(modelName = ENGINE_ID), actual)
    }

    @Test
    fun `given attempt to generate from image, remote throws exception, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.imageToImage(API_KEY, ENGINE_ID, mockImageToImagePayload)
        } throws stubException

        val actual = runCatching { repository.generateFromImage(mockImageToImagePayload) }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to generate from text, credits refresh throws exception, expected generation value`() = runTest {
        coEvery {
            stubRemoteDataSource.textToImage(API_KEY, ENGINE_ID, mockTextToImagePayload)
        } returns mockAiGenerationResult

        coEvery {
            stubCreditsRds.fetch(API_KEY)
        } throws stubException

        val actual = repository.generateFromText(mockTextToImagePayload)

        Assert.assertEquals(mockAiGenerationResult.copy(modelName = ENGINE_ID), actual)
    }

    private companion object {
        const val API_KEY = "sk-5598"
        const val ENGINE_ID = "engine_5598"
    }
}
