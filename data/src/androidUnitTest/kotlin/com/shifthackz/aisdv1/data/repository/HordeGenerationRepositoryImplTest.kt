package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockAiGenerationResult
import com.shifthackz.aisdv1.data.mocks.mockImageToImagePayload
import com.shifthackz.aisdv1.data.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.HordeGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HordeGenerationRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubStatus = MutableSharedFlow<HordeProcessStatus>()
    private val stubMediaStoreGateway = mockk<MediaStoreGateway>()
    private val stubLocalDataSource = mockk<GenerationResultDataSource.Local>()
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubRemoteDataSource = mockk<HordeGenerationDataSource.Remote>()
    private val stubStatusSource = mockk<HordeGenerationDataSource.StatusSource>()
    private val stubBackgroundWorkObserver = mockk<BackgroundWorkObserver>()

    private val repository = HordeGenerationRepositoryImpl(
        mediaStoreGateway = stubMediaStoreGateway,
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
        } returns stubStatus

        every {
            stubPreferenceManager.autoSaveAiResults
        } returns false

        every {
            stubPreferenceManager.hordeApiKey
        } returns API_KEY
    }

    @Test
    fun `given attempt to observe status, status source emits two values, expected valid values in same order`() = runTest {
        val values = mutableListOf<HordeProcessStatus>()
        val job = launch {
            repository
                .observeStatus()
                .take(2)
                .toList(values)
        }
        runCurrent()

        stubStatus.emit(HordeProcessStatus(5598, 1504))
        stubStatus.emit(HordeProcessStatus(0, 0))
        job.join()

        Assert.assertEquals(
            listOf(HordeProcessStatus(5598, 1504), HordeProcessStatus(0, 0)),
            values,
        )
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
    fun `given attempt to validate api key, horde api key is empty, expected default key used`() = runTest {
        every {
            stubPreferenceManager.hordeApiKey
        } returns ""

        coEvery {
            stubRemoteDataSource.validateApiKey(DEFAULT_HORDE_API_KEY)
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
        } returns mockAiGenerationResult

        val actual = repository.generateFromText(mockTextToImagePayload)

        Assert.assertEquals(mockAiGenerationResult, actual)
    }

    @Test
    fun `given attempt to generate from text, remote throws exception, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.textToImage(API_KEY, mockTextToImagePayload)
        } throws stubException

        val actual = runCatching { repository.generateFromText(mockTextToImagePayload) }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to generate from image, remote returns result, expected valid domain model value`() = runTest {
        coEvery {
            stubRemoteDataSource.imageToImage(API_KEY, mockImageToImagePayload)
        } returns mockAiGenerationResult

        val actual = repository.generateFromImage(mockImageToImagePayload)

        Assert.assertEquals(mockAiGenerationResult, actual)
    }

    @Test
    fun `given attempt to generate from image, remote throws exception, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.imageToImage(API_KEY, mockImageToImagePayload)
        } throws stubException

        val actual = runCatching { repository.generateFromImage(mockImageToImagePayload) }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to interrupt generation, remote completes, expected complete value`() = runTest {
        coEvery {
            stubRemoteDataSource.interruptGeneration(API_KEY)
        } returns Unit

        val actual = runCatching { repository.interruptGeneration() }

        Assert.assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to interrupt generation, remote throws exception, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.interruptGeneration(API_KEY)
        } throws stubException

        val actual = runCatching { repository.interruptGeneration() }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given observe status job is cancelled, expected no error`() = runTest {
        val job = launch {
            repository.observeStatus().collect {}
        }

        job.cancelAndJoin()

        Assert.assertTrue(job.isCancelled)
    }

    private companion object {
        const val API_KEY = "sk-5598"
        const val DEFAULT_HORDE_API_KEY = "0000000000"
    }
}
