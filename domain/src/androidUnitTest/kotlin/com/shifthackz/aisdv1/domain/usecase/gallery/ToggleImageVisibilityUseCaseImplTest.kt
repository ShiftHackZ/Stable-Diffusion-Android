package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ToggleImageVisibilityUseCaseImplTest {

    private val stubRepository = mockk<GenerationResultRepository>()

    private val useCase = ToggleImageVisibilityUseCaseImpl(stubRepository)

    @Test
    fun `given repository returned value, expected valid boolean value`() = runTest {
        coEvery {
            stubRepository.toggleVisibility(any())
        } returns true

        val actual = useCase(5598L)

        assertTrue(actual)
    }

    @Test
    fun `given repository thrown exception, expected error value`() = runTest {
        val stubException = Throwable("Error communicating with MediaStore.")

        coEvery {
            stubRepository.toggleVisibility(any())
        } throws stubException

        val actual = runCatching { useCase(5598L) }.exceptionOrNull()

        assertSame(stubException, actual)
    }
}
