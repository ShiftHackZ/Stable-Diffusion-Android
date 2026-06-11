package com.shifthackz.aisdv1.domain.usecase.swarmmodel

import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.mocks.mockSwarmUiModels
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.SwarmUiModelsRepository
import io.mockk.coEvery
import io.mockk.coVerify
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
            stubPreferenceManager.source
        } returns ServerSource.SWARM_UI

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
            stubPreferenceManager.source
        } returns ServerSource.SWARM_UI

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

        every {
            stubPreferenceManager.source
        } returns ServerSource.SWARM_UI

        coEvery {
            stubRepository.fetchAndGetModels()
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
            stubRepository.fetchAndGetModels()
        }
    }
}
