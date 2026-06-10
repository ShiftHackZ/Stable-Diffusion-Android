package com.shifthackz.aisdv1.domain.usecase.caching

import com.shifthackz.aisdv1.domain.mocks.mockAiGenerationResult
import com.shifthackz.aisdv1.domain.repository.TemporaryGenerationResultRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class GetLastResultFromCacheUseCaseImplTest {

    private val stubException = Throwable("No last cached result")
    private val stubRepository = mockk<TemporaryGenerationResultRepository>()

    private val useCase = GetLastResultFromCacheUseCaseImpl(stubRepository)

    @Test
    fun `given repository returned last ai result, expected valid result value`() = runTest {
        coEvery {
            stubRepository.get()
        } returns mockAiGenerationResult

        val actual = useCase()

        assertEquals(mockAiGenerationResult, actual)
    }

    @Test
    fun `given repository has no last ai result, expected error value`() = runTest {
        coEvery {
            stubRepository.get()
        } throws stubException

        val actual = runCatching { useCase() }.exceptionOrNull()

        assertSame(stubException, actual)
    }
}
