package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.mocks.mockAiGenerationResults
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class GetGenerationResultPagedUseCaseImplTest {

    private val stubException = Throwable("Can not read DB.")
    private val stubRepository = mockk<GenerationResultRepository>()

    private val useCase = GetGenerationResultPagedUseCaseImpl(stubRepository)

    @Test
    fun `given repository returned page with items, expected valid list value`() = runTest {
        coEvery {
            stubRepository.getPage(any(), any())
        } returns mockAiGenerationResults

        val actual = useCase(20, 0)

        assertEquals(mockAiGenerationResults, actual)
    }

    @Test
    fun `given repository returned empty page with no items, expected empty list value`() = runTest {
        coEvery {
            stubRepository.getPage(any(), any())
        } returns emptyList()

        val actual = useCase(20, 5598)

        assertTrue(actual.isEmpty())
    }

    @Test
    fun `given repository thrown exception, expected error value`() = runTest {
        coEvery {
            stubRepository.getPage(any(), any())
        } throws stubException

        val actual = runCatching { useCase(20, 5598) }.exceptionOrNull()

        assertSame(stubException, actual)
    }
}
