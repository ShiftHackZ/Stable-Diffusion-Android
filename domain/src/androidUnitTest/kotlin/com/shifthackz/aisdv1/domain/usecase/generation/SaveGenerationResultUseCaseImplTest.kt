package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.mocks.mockAiGenerationResult
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class SaveGenerationResultUseCaseImplTest {

    private val stubException = Throwable("Error inserting into DB.")
    private val stubRepository = mockk<GenerationResultRepository>()

    private val useCase = SaveGenerationResultUseCaseImpl(stubRepository)

    @Test
    fun `given repository saved generation result successfully, expected complete value`() = runTest {
        val unsavedResult = mockAiGenerationResult.copy(id = 0L)
        coEvery {
            stubRepository.insert(any())
        } returns 5598L

        val actual = useCase(unsavedResult)

        assertEquals(5598L, actual)
    }

    @Test
    fun `given repository failed to save generation result, expected error value`() = runTest {
        val unsavedResult = mockAiGenerationResult.copy(id = 0L)
        coEvery {
            stubRepository.insert(any())
        } throws stubException

        val actual = runCatching { useCase(unsavedResult) }.exceptionOrNull()

        assertSame(stubException, actual)
    }

    @Test
    fun `given generation result already has id, expected existing id and no repository call`() = runTest {
        val actual = useCase(mockAiGenerationResult)

        assertEquals(mockAiGenerationResult.id, actual)
        coVerify(exactly = 0) {
            stubRepository.insert(any())
        }
    }
}
