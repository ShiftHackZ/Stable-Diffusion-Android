package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class PingStableDiffusionServiceUseCaseImplTest {

    private val stubException = RuntimeException("Can not establish connection to server.")
    private val stubRepository = mockk<StableDiffusionGenerationRepository>()

    private val useCase = PingStableDiffusionServiceUseCaseImpl(stubRepository)

    @Test
    fun `given connection to server can be established, expected complete value`() = runTest {
        coEvery {
            stubRepository.checkApiAvailability()
        } returns Unit

        useCase()
    }

    @Test
    fun `given connection to server can not be established, expected error value`() = runTest {
        coEvery {
            stubRepository.checkApiAvailability()
        } throws stubException

        val actual = runCatching { useCase() }.exceptionOrNull()

        assertTrue(actual === stubException)
    }
}
