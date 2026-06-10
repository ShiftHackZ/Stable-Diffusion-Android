package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.StabilityAiCreditsDataSource
import com.shifthackz.aisdv1.domain.datasource.StabilityAiCreditsRemoteDataSource
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class StabilityAiCreditsRepositoryImplTest {

    private val stubException = RuntimeException("Something went wrong.")
    private val stubCredits = MutableStateFlow(0f)
    private val stubRemoteDataSource = mockk<StabilityAiCreditsRemoteDataSource>()
    private val stubLocalDataSource = mockk<StabilityAiCreditsDataSource.Local>()
    private val stubPreferenceManager = mockk<PreferenceManager>()

    private val repository = StabilityAiCreditsRepositoryImpl(
        remoteDataSource = stubRemoteDataSource,
        localDataSource = stubLocalDataSource,
        preferenceManager = stubPreferenceManager,
    )

    @Before
    fun initialize() {
        every {
            stubPreferenceManager.stabilityAiApiKey
        } returns API_KEY

        every {
            stubLocalDataSource.observe()
        } returns stubCredits

        coEvery {
            stubLocalDataSource.save(any())
        } coAnswers {
            stubCredits.value = firstArg()
        }
    }

    @Test
    fun `given server source is not STABILITY_AI, attempt to fetch, expected IllegalStateException error value`() = runTest {
        stubWrongServerSource()

        val actual = runCatching { repository.fetch() }.exceptionOrNull()

        assertWrongServerSourceSelected(actual)
    }

    @Test
    fun `given server source is not STABILITY_AI, attempt to fetch and get, expected IllegalStateException error value`() = runTest {
        stubWrongServerSource()

        val actual = runCatching { repository.fetchAndGet() }.exceptionOrNull()

        assertWrongServerSourceSelected(actual)
    }

    @Test
    fun `given server source is not STABILITY_AI, attempt to fetch and observe, expected IllegalStateException error value`() = runTest {
        stubWrongServerSource()

        val actual = runCatching { repository.fetchAndObserve().first() }.exceptionOrNull()

        assertWrongServerSourceSelected(actual)
    }

    @Test
    fun `given server source is not STABILITY_AI, attempt to get, expected IllegalStateException error value`() = runTest {
        stubWrongServerSource()

        val actual = runCatching { repository.get() }.exceptionOrNull()

        assertWrongServerSourceSelected(actual)
    }

    @Test
    fun `given server source is not STABILITY_AI, attempt to observe, expected IllegalStateException error value`() {
        stubWrongServerSource()

        val actual = runCatching { repository.observe() }.exceptionOrNull()

        assertWrongServerSourceSelected(actual)
    }

    @Test
    fun `given server source is STABILITY_AI, attempt to fetch, remote returns data, local save success, expected complete value`() = runTest {
        stubStabilityAiSource()
        coEvery {
            stubRemoteDataSource.fetch(API_KEY)
        } returns 5598f

        val actual = runCatching { repository.fetch() }

        assertTrue(actual.isSuccess)
        assertEquals(5598f, stubCredits.value)
    }

    @Test
    fun `given server source is STABILITY_AI, attempt to fetch, remote returns error, local save success, expected error value`() = runTest {
        stubStabilityAiSource()
        coEvery {
            stubRemoteDataSource.fetch(API_KEY)
        } throws stubException

        val actual = runCatching { repository.fetch() }.exceptionOrNull()

        assertSame(stubException, actual)
    }

    @Test
    fun `given server source is STABILITY_AI, attempt to fetch, remote returns data, local save fails, expected error value`() = runTest {
        stubStabilityAiSource()
        coEvery {
            stubRemoteDataSource.fetch(API_KEY)
        } returns 5598f
        coEvery {
            stubLocalDataSource.save(any())
        } throws stubException

        val actual = runCatching { repository.fetch() }.exceptionOrNull()

        assertSame(stubException, actual)
    }

    @Test
    fun `given server source is STABILITY_AI, attempt to fetch and get, fetch success, get success, expected valid credits value`() = runTest {
        stubStabilityAiSource()
        coEvery {
            stubRemoteDataSource.fetch(API_KEY)
        } returns 5598f
        coEvery {
            stubLocalDataSource.get()
        } returns 5598f

        val actual = repository.fetchAndGet()

        assertEquals(5598f, actual)
    }

    @Test
    fun `given server source is STABILITY_AI, attempt to fetch and get, fetch fails, get success, expected valid credits value`() = runTest {
        stubStabilityAiSource()
        coEvery {
            stubRemoteDataSource.fetch(API_KEY)
        } throws stubException
        coEvery {
            stubLocalDataSource.get()
        } returns 5598f

        val actual = repository.fetchAndGet()

        assertEquals(5598f, actual)
    }

    @Test
    fun `given server source is STABILITY_AI, attempt to fetch and get, fetch fails, get fails, expected error value`() = runTest {
        stubStabilityAiSource()
        coEvery {
            stubRemoteDataSource.fetch(API_KEY)
        } throws stubException
        coEvery {
            stubLocalDataSource.get()
        } throws stubException

        val actual = runCatching { repository.fetchAndGet() }.exceptionOrNull()

        assertSame(stubException, actual)
    }

    @Test
    fun `given server source is STABILITY_AI, attempt to fetch and observe, fetch success, expected valid credits value`() = runTest {
        stubStabilityAiSource()
        coEvery {
            stubRemoteDataSource.fetch(API_KEY)
        } returns 5598f

        val actual = repository.fetchAndObserve().first()

        assertEquals(5598f, actual)
    }

    @Test
    fun `given server source is STABILITY_AI, attempt to fetch and observe, fetch fails, expected valid credits value`() = runTest {
        stubStabilityAiSource()
        coEvery {
            stubRemoteDataSource.fetch(API_KEY)
        } throws stubException

        val actual = repository.fetchAndObserve().first()

        assertEquals(0f, actual)
    }

    @Test
    fun `given server source is STABILITY_AI, attempt to get, local returns data, expected valid credits value`() = runTest {
        stubStabilityAiSource()
        coEvery {
            stubLocalDataSource.get()
        } returns 5598f

        val actual = repository.get()

        assertEquals(5598f, actual)
    }

    @Test
    fun `given server source is STABILITY_AI, attempt to get, local throws exception, expected error value`() = runTest {
        stubStabilityAiSource()
        coEvery {
            stubLocalDataSource.get()
        } throws stubException

        val actual = runCatching { repository.get() }.exceptionOrNull()

        assertSame(stubException, actual)
    }

    @Test
    fun `given server source is STABILITY_AI, attempt to observe, local emits value, expected valid credits value`() = runTest {
        stubStabilityAiSource()
        stubCredits.value = 5598f

        val actual = repository.observe().first()

        assertEquals(5598f, actual)
    }

    @Test
    fun `given server source is STABILITY_AI, attempt to observe, local throws exception, expected error value`() {
        stubStabilityAiSource()
        every {
            stubLocalDataSource.observe()
        } throws stubException

        val actual = runCatching { repository.observe() }.exceptionOrNull()

        assertSame(stubException, actual)
    }

    private fun stubWrongServerSource() {
        every {
            stubPreferenceManager.source
        } returns ServerSource.LOCAL_MICROSOFT_ONNX
    }

    private fun stubStabilityAiSource() {
        every {
            stubPreferenceManager.source
        } returns ServerSource.STABILITY_AI
    }

    private fun assertWrongServerSourceSelected(actual: Throwable?) {
        assertTrue(actual is IllegalStateException)
        assertEquals("Wrong server source selected.", actual?.message)
    }

    private companion object {
        const val API_KEY = "sk-5598"
    }
}
