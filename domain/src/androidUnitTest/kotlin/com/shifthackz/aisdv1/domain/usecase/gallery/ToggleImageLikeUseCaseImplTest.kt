package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Test

class ToggleImageLikeUseCaseImplTest {

    private val stubRepository = mockk<GenerationResultRepository>()

    private val useCase = ToggleImageLikeUseCaseImpl(stubRepository)

    @Test
    fun `given repository returned value, expected valid boolean value`() = runTest {
        coEvery {
            stubRepository.toggleLike(any())
        } returns false

        val actual = useCase(5598L)

        assertFalse(actual)
    }

    @Test
    fun `given repository thrown exception, expected error value`() = runTest {
        val stubException = Throwable("Error communicating with MediaStore.")

        coEvery {
            stubRepository.toggleLike(any())
        } throws stubException

        val actual = runCatching { useCase(5598L) }.exceptionOrNull()

        assertSame(stubException, actual)
    }
}
