package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.mocks.mockAiGenerationResults
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class GetAllGalleryUseCaseImplTest {

    private val stubRepository = mockk<GenerationResultRepository>()

    private val useCase = GetAllGalleryUseCaseImpl(stubRepository)

    @Test
    fun `given repository returned list of generations, expected valid list value`() = runTest {
        coEvery {
            stubRepository.getAll()
        } returns mockAiGenerationResults

        val actual = useCase()

        assertEquals(mockAiGenerationResults, actual)
    }

    @Test
    fun `given repository returned empty list of generations, expected empty list value`() = runTest {
        coEvery {
            stubRepository.getAll()
        } returns emptyList()

        val actual = useCase()

        assertTrue(actual.isEmpty())
    }

    @Test
    fun `given repository thrown exception, expected error value`() = runTest {
        val stubException = Throwable("Database communication error.")

        coEvery {
            stubRepository.getAll()
        } throws stubException

        val actual = runCatching { useCase() }.exceptionOrNull()

        assertSame(stubException, actual)
    }
}
