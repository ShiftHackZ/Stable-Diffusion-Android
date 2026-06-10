package com.shifthackz.aisdv1.domain.usecase.debug

import com.shifthackz.aisdv1.core.common.time.TimeProvider
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertSame
import org.junit.Test

class DebugInsertBadBase64UseCaseImplTest {

    private val stubRepository = mockk<GenerationResultRepository>()
    private val stubTimeProvider = mockk<TimeProvider>()

    private val useCase = DebugInsertBadBase64UseCaseImpl(
        repository = stubRepository,
        timeProvider = stubTimeProvider,
    )

    @Test
    fun `given inserted value with bad BASE64 into DB, expected complete value`() = runTest {
        every {
            stubTimeProvider.currentTimeMillis()
        } returns 5598L

        coEvery {
            stubRepository.insert(any())
        } returns 5598L

        useCase()
    }

    @Test
    fun `given failed to insert value with bad BASE64 into DB, expected error value`() = runTest {
        val stubException = Throwable("DB error.")

        every {
            stubTimeProvider.currentTimeMillis()
        } returns 5598L

        coEvery {
            stubRepository.insert(any())
        } throws stubException

        val actual = runCatching { useCase() }.exceptionOrNull()

        assertSame(stubException, actual)
    }
}
