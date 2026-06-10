package com.shifthackz.aisdv1.domain.usecase.sdlora

import com.shifthackz.aisdv1.domain.mocks.mockLoRAs
import com.shifthackz.aisdv1.domain.repository.LorasRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class FetchAndGetLorasUseCaseImplTest {

    private val stubRepository = mockk<LorasRepository>()

    private val useCase = FetchAndGetLorasUseCaseImpl(stubRepository)

    @Test
    fun `given repository provided list of LoRAs, expected valid list value`() = runTest {
        coEvery {
            stubRepository.fetchAndGetLoras()
        } returns mockLoRAs

        val actual = useCase()

        Assert.assertEquals(mockLoRAs, actual)
    }

    @Test
    fun `given repository provided empty list of LoRAs, expected empty list value`() = runTest {
        coEvery {
            stubRepository.fetchAndGetLoras()
        } returns emptyList()

        val actual = useCase()

        Assert.assertEquals(emptyList<Any>(), actual)
    }

    @Test
    fun `given repository thrown exception, expected error value`() = runTest {
        val stubException = Throwable("Unknown error occurred.")

        coEvery {
            stubRepository.fetchAndGetLoras()
        } throws stubException

        val actual = runCatching { useCase() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }
}
