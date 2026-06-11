package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockAiGenerationResult
import com.shifthackz.aisdv1.data.mocks.mockImageToImagePayload
import com.shifthackz.aisdv1.data.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.domain.datasource.FalAiGenerationDataSource
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.entity.FalAiModel
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

class FalAiGenerationRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubMediaStoreGateway = mockk<MediaStoreGateway>()
    private val stubLocalDataSource = mockk<GenerationResultDataSource.Local>()
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubRemoteDataSource = mockk<FalAiGenerationDataSource.Remote>()
    private val stubBackgroundWorkObserver = mockk<BackgroundWorkObserver>()

    private val repository = FalAiGenerationRepositoryImpl(
        mediaStoreGateway = stubMediaStoreGateway,
        localDataSource = stubLocalDataSource,
        preferenceManager = stubPreferenceManager,
        remoteDataSource = stubRemoteDataSource,
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
            stubPreferenceManager.falAiApiKey
        } returns API_KEY
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
            stubRemoteDataSource.textToImage(API_KEY, mockTextToImagePayload)
        } returns listOf(mockAiGenerationResult)

        val actual = repository.generateFromText(mockTextToImagePayload)

        Assert.assertEquals(
            listOf(mockAiGenerationResult.copy(modelName = FalAiModel.defaultTextToImage.displayName)),
            actual,
        )
    }

    @Test
    fun `given attempt to generate from image, remote returns result, expected valid domain model value`() = runTest {
        coEvery {
            stubRemoteDataSource.imageToImage(API_KEY, mockImageToImagePayload)
        } returns listOf(mockAiGenerationResult)

        val actual = repository.generateFromImage(mockImageToImagePayload)

        Assert.assertEquals(
            listOf(mockAiGenerationResult.copy(modelName = FalAiModel.defaultImageToImage.displayName)),
            actual,
        )
    }

    @Test
    fun `given attempt to generate from text, remote throws exception, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.textToImage(API_KEY, mockTextToImagePayload)
        } throws stubException

        val actual = runCatching { repository.generateFromText(mockTextToImagePayload) }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    private companion object {
        const val API_KEY = "fal-ai-api-key"
    }
}
