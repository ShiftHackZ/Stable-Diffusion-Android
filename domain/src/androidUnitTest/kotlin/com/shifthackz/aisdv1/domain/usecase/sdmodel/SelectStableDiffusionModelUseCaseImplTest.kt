package com.shifthackz.aisdv1.domain.usecase.sdmodel

import com.shifthackz.aisdv1.domain.mocks.mockServerConfiguration
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.ServerConfigurationRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class SelectStableDiffusionModelUseCaseImplTest {

    private val stubException = RuntimeException("Unknown error occurred.")
    private val updatedServerConfiguration = mockServerConfiguration.copy(sdModelCheckpoint = "model")
    private val stubServerConfigurationRepository = mockk<ServerConfigurationRepository>()
    private val stubPreferenceManager = mockk<PreferenceManager>(relaxed = true)

    private val useCase = SelectStableDiffusionModelUseCaseImpl(
        serverConfigurationRepository = stubServerConfigurationRepository,
        preferenceManager = stubPreferenceManager,
    )

    @Test
    fun `expected get, update, fetch completed, expected complete without errors`() = runTest {
        stubSuccess()

        val actual = runCatching { useCase("model") }

        assertTrue(actual.isSuccess)
    }

    @Test
    fun `expected get failed, update, fetch completed, expected complete with error`() = runTest {
        stubSuccess()
        coEvery {
            stubServerConfigurationRepository.getConfiguration()
        } throws stubException

        assertUseCaseFails(stubException)
    }

    @Test
    fun `expected update failed, get, fetch completed, expected complete with error`() = runTest {
        stubSuccess()
        coEvery {
            stubServerConfigurationRepository.updateConfiguration(updatedServerConfiguration)
        } throws stubException

        assertUseCaseFails(stubException)
    }

    @Test
    fun `expected get, update completed, fetch failed, expected complete with error`() = runTest {
        stubSuccess()
        coEvery {
            stubServerConfigurationRepository.fetchConfiguration()
        } throws stubException

        assertUseCaseFails(stubException)
    }

    private suspend fun stubSuccess() {
        coEvery {
            stubServerConfigurationRepository.getConfiguration()
        } returns mockServerConfiguration

        coEvery {
            stubServerConfigurationRepository.updateConfiguration(updatedServerConfiguration)
        } returns Unit

        coEvery {
            stubServerConfigurationRepository.fetchConfiguration()
        } returns Unit
    }

    private suspend fun assertUseCaseFails(expected: Throwable) {
        val actual = runCatching { useCase("model") }.exceptionOrNull()
        assertTrue(actual === expected)
    }
}
