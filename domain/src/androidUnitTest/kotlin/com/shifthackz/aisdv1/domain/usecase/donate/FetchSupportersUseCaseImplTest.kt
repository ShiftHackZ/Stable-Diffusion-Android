package com.shifthackz.aisdv1.domain.usecase.donate

import com.shifthackz.aisdv1.domain.mocks.mockSupporters
import com.shifthackz.aisdv1.domain.repository.SupportersRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class FetchSupportersUseCaseImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubRepository = mockk<SupportersRepository>()

    private val useCase = FetchSupportersUseCaseImpl(stubRepository)

    @Test
    fun `given repository returned data, expected valid domain model list value`() = runTest {
        coEvery {
            stubRepository.fetchAndGetSupporters()
        } returns mockSupporters

        val actual = useCase()

        Assert.assertEquals(mockSupporters, actual)
    }

    @Test
    fun `given repository returned empty data, expected empty domain model list value`() = runTest {
        coEvery {
            stubRepository.fetchAndGetSupporters()
        } returns emptyList()

        val actual = useCase()

        Assert.assertEquals(emptyList<Any>(), actual)
    }

    @Test
    fun `given repository thrown exception, expected error value`() = runTest {
        coEvery {
            stubRepository.fetchAndGetSupporters()
        } throws stubException

        val actual = runCatching { useCase() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }
}
