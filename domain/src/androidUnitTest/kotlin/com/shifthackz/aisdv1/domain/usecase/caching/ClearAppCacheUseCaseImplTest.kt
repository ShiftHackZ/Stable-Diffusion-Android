package com.shifthackz.aisdv1.domain.usecase.caching

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertSame
import org.junit.Test

class ClearAppCacheUseCaseImplTest {

    private val stubException = Throwable("Fatal error.")
    private val stubAppCacheCleaner = mockk<AppCacheCleaner>()
    private val stubRepository = mockk<GenerationResultRepository>()

    private val useCase = ClearAppCacheUseCaseImpl(
        appCacheCleaner = stubAppCacheCleaner,
        repository = stubRepository,
    )

    @Test
    fun `given repository and app cache clear success, expected complete value`() = runTest {
        coEvery {
            stubRepository.deleteAll()
        } returns Unit

        coEvery {
            stubAppCacheCleaner.clear()
        } returns Unit

        useCase()
    }

    @Test
    fun `given repository clear fails, expected error value`() = runTest {
        coEvery {
            stubRepository.deleteAll()
        } throws stubException

        val actual = runCatching { useCase() }.exceptionOrNull()

        assertSame(stubException, actual)
    }

    @Test
    fun `given repository clear success, app cache clear fails, expected error value`() = runTest {
        coEvery {
            stubRepository.deleteAll()
        } returns Unit

        coEvery {
            stubAppCacheCleaner.clear()
        } throws stubException

        val actual = runCatching { useCase() }.exceptionOrNull()

        assertSame(stubException, actual)
    }
}
