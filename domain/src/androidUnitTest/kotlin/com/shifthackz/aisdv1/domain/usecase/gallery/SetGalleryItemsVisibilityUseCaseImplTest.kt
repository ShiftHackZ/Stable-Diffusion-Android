package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertSame
import org.junit.Test

class SetGalleryItemsVisibilityUseCaseImplTest {

    private val stubRepository = mockk<GenerationResultRepository>()

    private val useCase = SetGalleryItemsVisibilityUseCaseImpl(stubRepository)

    @Test
    fun `given repository updated data successfully, expected complete`() = runTest {
        coEvery {
            stubRepository.setVisibilityByIds(any(), any())
        } returns Unit

        useCase(listOf(5598L, 151297L), true)

        coVerify {
            stubRepository.setVisibilityByIds(listOf(5598L, 151297L), true)
        }
    }

    @Test
    fun `given repository updated data with fail, expected error`() = runTest {
        val stubException = Throwable("Database communication error.")

        coEvery {
            stubRepository.setVisibilityByIds(any(), any())
        } throws stubException

        val actual = runCatching { useCase(listOf(5598L, 151297L), false) }.exceptionOrNull()

        assertSame(stubException, actual)
    }
}
