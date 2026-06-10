package com.shifthackz.aisdv1.domain.usecase.sdmodel

import com.shifthackz.aisdv1.domain.entity.ServerConfiguration
import com.shifthackz.aisdv1.domain.mocks.mockServerConfiguration
import com.shifthackz.aisdv1.domain.mocks.mockStableDiffusionModels
import com.shifthackz.aisdv1.domain.repository.ServerConfigurationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionModelsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetStableDiffusionModelsUseCaseImplTest {

    private val stubServerConfigurationRepository = mockk<ServerConfigurationRepository>()
    private val stubSdModelsRepository = mockk<StableDiffusionModelsRepository>()

    private val useCase = GetStableDiffusionModelsUseCaseImpl(
        serverConfigurationRepository = stubServerConfigurationRepository,
        sdModelsRepository = stubSdModelsRepository,
    )

    @Test
    fun `given repository returns list with value present in configuration, expected list with selected value`() = runTest {
        coEvery {
            stubServerConfigurationRepository.fetchAndGetConfiguration()
        } returns mockServerConfiguration

        coEvery {
            stubSdModelsRepository.fetchAndGetModels()
        } returns mockStableDiffusionModels

        val expectedValue = mockStableDiffusionModels.map {
            it to (it.title == mockServerConfiguration.sdModelCheckpoint)
        }

        val actual = useCase()

        assertEquals(expectedValue, actual)
        assertTrue(expectedValue.any { (_, selected) -> selected })
    }

    @Test
    fun `given repository returns list with no value present in configuration, expected list without selected value`() = runTest {
        val stubServerConfiguration = ServerConfiguration("nonsense")

        coEvery {
            stubServerConfigurationRepository.fetchAndGetConfiguration()
        } returns stubServerConfiguration

        coEvery {
            stubSdModelsRepository.fetchAndGetModels()
        } returns mockStableDiffusionModels

        val expectedValue = mockStableDiffusionModels.map {
            it to (it.title == stubServerConfiguration.sdModelCheckpoint)
        }

        val actual = useCase()

        assertEquals(expectedValue, actual)
        assertTrue(!expectedValue.any { (_, selected) -> selected })
    }

    @Test
    fun `given exception while fetching configuration, expected error value`() = runTest {
        val stubException = RuntimeException("Network error.")

        coEvery {
            stubServerConfigurationRepository.fetchAndGetConfiguration()
        } throws stubException

        val actual = runCatching { useCase() }.exceptionOrNull()

        assertTrue(actual === stubException)
    }

    @Test
    fun `given exception while fetching models, expected error value`() = runTest {
        val stubException = RuntimeException("Network error.")

        coEvery {
            stubServerConfigurationRepository.fetchAndGetConfiguration()
        } returns mockServerConfiguration

        coEvery {
            stubSdModelsRepository.fetchAndGetModels()
        } throws stubException

        val actual = runCatching { useCase() }.exceptionOrNull()

        assertTrue(actual === stubException)
    }
}
