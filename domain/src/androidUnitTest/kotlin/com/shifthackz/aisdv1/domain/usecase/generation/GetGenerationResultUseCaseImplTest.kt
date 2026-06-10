package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.mocks.mockAiGenerationResult
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class GetGenerationResultUseCaseImplTest {

    private val stubException = Throwable("Ai generation result not found.")
    private val stubRepository = mockk<GenerationResultRepository>()

    private val useCase = GetGenerationResultUseCaseImpl(stubRepository)

    @Test
    fun `given repository has ai result with provided id, expected valid ai generation result value`() = runTest {
        coEvery {
            stubRepository.getById(any())
        } returns mockAiGenerationResult

        val actual = useCase(5598L)

        assertEquals(mockAiGenerationResult, actual)
    }

    @Test
    fun `given repository has no ai result with provided id, expected error value`() = runTest {
        coEvery {
            stubRepository.getById(any())
        } throws stubException

        val actual = runCatching { useCase(5598L) }.exceptionOrNull()

        assertSame(stubException, actual)
    }
}
