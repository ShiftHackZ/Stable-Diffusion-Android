package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertSame
import org.junit.Test

class DeleteAllGalleryUseCaseImplTest {

    private val stubRepository = mockk<GenerationResultRepository>()

    private val useCase = DeleteAllGalleryUseCaseImpl(stubRepository)

    @Test
    fun `given repository deleted data successfully, expected complete`() = runTest {
        coEvery {
            stubRepository.deleteAll()
        } returns Unit

        useCase()
    }

    @Test
    fun `given repository deleted data with fail, expected error`() = runTest {
        val stubException = Throwable("Database communication error.")

        coEvery {
            stubRepository.deleteAll()
        } throws stubException

        val actual = runCatching { useCase() }.exceptionOrNull()

        assertSame(stubException, actual)
    }
}
