package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.repository.RandomImageRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertSame
import org.junit.Test

class GetRandomImageUseCaseImplTest {

    private val stubException = Throwable("Can not generate random image.")
    private val stubBytes = byteArrayOf(1, 2, 3)
    private val stubRepository = mockk<RandomImageRepository>()

    private val useCase = GetRandomImageUseCaseImpl(stubRepository)

    @Test
    fun `given repository provided bytes with random image, expected valid bytes value`() = runTest {
        coEvery {
            stubRepository.fetchAndGet()
        } returns stubBytes

        val actual = useCase()

        assertSame(stubBytes, actual)
    }

    @Test
    fun `given repository thrown exception, expected error value`() = runTest {
        coEvery {
            stubRepository.fetchAndGet()
        } throws stubException

        val actual = runCatching { useCase() }.exceptionOrNull()

        assertSame(stubException, actual)
    }
}
