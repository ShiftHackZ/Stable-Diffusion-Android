package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockAiGenerationResult
import com.shifthackz.aisdv1.data.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.OpenAiGenerationDataSource
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Assert
import org.junit.Test

class OpenAiGenerationRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubMediaStoreGateway = mockk<MediaStoreGateway>()
    private val stubLocalDataSource = mockk<GenerationResultDataSource.Local>()
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubRemoteDataSource = mockk<OpenAiGenerationDataSource.Remote>()
    private val stubBackgroundWorkObserver = mockk<BackgroundWorkObserver>()

    private val repository = OpenAiGenerationRepositoryImpl(
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
            stubPreferenceManager.openAiApiKey
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

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to generate from text, remote returns result, expected valid domain model value`() = runTest {
        coEvery {
            stubRemoteDataSource.textToImage(API_KEY, any())
        } returns mockAiGenerationResult

        val actual = repository.generateFromText(mockTextToImagePayload)

        Assert.assertEquals(mockAiGenerationResult, actual)
    }

    @Test
    fun `given attempt to generate from text, remote throws exception, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.textToImage(API_KEY, any())
        } throws stubException

        val actual = runCatching { repository.generateFromText(mockTextToImagePayload) }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    private companion object {
        const val API_KEY = "openai-api-key"
    }
}
