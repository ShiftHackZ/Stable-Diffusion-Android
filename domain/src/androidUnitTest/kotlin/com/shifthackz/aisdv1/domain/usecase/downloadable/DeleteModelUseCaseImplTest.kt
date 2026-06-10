package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertSame
import org.junit.Test

class DeleteModelUseCaseImplTest {

    private val stubRepository = mockk<DownloadableModelRepository>()

    private val useCase = DeleteModelUseCaseImpl(stubRepository)

    @Test
    fun `given model deleted successfully, expected completion`() = runTest {
        coEvery {
            stubRepository.delete(any())
        } returns Unit

        useCase("5598")
    }

    @Test
    fun `given model delete failed, expected error value`() = runTest {
        val stubException = Throwable("Failed to delete model.")

        coEvery {
            stubRepository.delete(any())
        } throws stubException

        val actual = runCatching { useCase("5598") }.exceptionOrNull()

        assertSame(stubException, actual)
    }
}
