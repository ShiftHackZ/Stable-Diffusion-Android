package com.shifthackz.aisdv1.domain.usecase.swarmmodel

import com.shifthackz.aisdv1.domain.mocks.mockSwarmUiModels
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.SwarmUiModelsRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class FetchAndGetSwarmUiModelsUseCaseImplTest {

    private val stubRepository = mockk<SwarmUiModelsRepository>()
    private val stubPreferenceManager = mockk<PreferenceManager>(relaxed = true)

    private val useCase = FetchAndGetSwarmUiModelsUseCaseImpl(
        preferenceManager = stubPreferenceManager,
        repository = stubRepository,
    )

    @Test
    fun `given repository returned models list, model present in preference, expected the same models list, preference not changed`() = runTest {
        every {
            stubPreferenceManager.swarmUiModel
        } returns "5598"

        coEvery {
            stubRepository.fetchAndGetModels()
        } returns mockSwarmUiModels

        val actual = useCase()

        Assert.assertEquals(mockSwarmUiModels, actual)
        verify(exactly = 0) {
            stubPreferenceManager.swarmUiModel = "5598"
        }
    }

    @Test
    fun `given repository returned models list, model missing in preference, expected first model saved`() = runTest {
        every {
            stubPreferenceManager.swarmUiModel
        } returns "missing_model"

        coEvery {
            stubRepository.fetchAndGetModels()
        } returns mockSwarmUiModels

        val actual = useCase()

        Assert.assertEquals(mockSwarmUiModels, actual)
        verify {
            stubPreferenceManager.swarmUiModel = "5598"
        }
    }

    @Test
    fun `given repository thrown exception, expected the same exception`() = runTest {
        val stubException = Throwable("Network exception")

        coEvery {
            stubRepository.fetchAndGetModels()
        } throws stubException

        val actual = runCatching { useCase() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }
}
