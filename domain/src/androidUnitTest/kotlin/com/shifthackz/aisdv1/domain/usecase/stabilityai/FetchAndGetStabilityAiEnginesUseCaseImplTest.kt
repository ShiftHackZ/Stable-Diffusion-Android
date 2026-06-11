package com.shifthackz.aisdv1.domain.usecase.stabilityai

import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.mocks.mockStabilityAiEngines
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class FetchAndGetStabilityAiEnginesUseCaseImplTest {

    private val stubFetchStabilityAiEnginesUseCase = mockk<FetchStabilityAiEnginesUseCase>()
    private val stubPreferenceManager = mockk<PreferenceManager>(relaxed = true)

    private val useCase = FetchAndGetStabilityAiEnginesUseCaseImpl(
        fetchStabilityAiEnginesUseCase = stubFetchStabilityAiEnginesUseCase,
        preferenceManager = stubPreferenceManager,
    )

    @Test
    fun `given repository returned engines list, id present in preference, expected the same engines list, id not changed`() = runTest {
        every {
            stubPreferenceManager.source
        } returns ServerSource.STABILITY_AI

        every {
            stubPreferenceManager.stabilityAiApiKey
        } returns API_KEY

        every {
            stubPreferenceManager.stabilityAiEngineId
        } returns "engine_1"

        coEvery {
            stubFetchStabilityAiEnginesUseCase(API_KEY)
        } returns mockStabilityAiEngines

        val actual = useCase()

        Assert.assertEquals(mockStabilityAiEngines, actual)
        verify(exactly = 0) {
            stubPreferenceManager.stabilityAiEngineId = "engine_1"
        }
    }

    @Test
    fun `given repository returned engines list, id missing in preference, expected first id saved`() = runTest {
        every {
            stubPreferenceManager.source
        } returns ServerSource.STABILITY_AI

        every {
            stubPreferenceManager.stabilityAiApiKey
        } returns API_KEY

        every {
            stubPreferenceManager.stabilityAiEngineId
        } returns "missing_engine"

        coEvery {
            stubFetchStabilityAiEnginesUseCase(API_KEY)
        } returns mockStabilityAiEngines

        val actual = useCase()

        Assert.assertEquals(mockStabilityAiEngines, actual)
        verify {
            stubPreferenceManager.stabilityAiEngineId = "engine_1"
        }
    }

    @Test
    fun `given repository thrown exception, expected the same exception`() = runTest {
        val stubException = Throwable("Network exception")

        every {
            stubPreferenceManager.source
        } returns ServerSource.STABILITY_AI

        every {
            stubPreferenceManager.stabilityAiApiKey
        } returns API_KEY

        coEvery {
            stubFetchStabilityAiEnginesUseCase(API_KEY)
        } throws stubException

        val actual = runCatching { useCase() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given inactive source, expected empty list and no remote fetch`() = runTest {
        every {
            stubPreferenceManager.source
        } returns ServerSource.AUTOMATIC1111

        val actual = useCase()

        Assert.assertEquals(emptyList<Any>(), actual)
        coVerify(exactly = 0) {
            stubFetchStabilityAiEnginesUseCase(any())
        }
    }

    private companion object {
        const val API_KEY = "api_key"
    }
}
