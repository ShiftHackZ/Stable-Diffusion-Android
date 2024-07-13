package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository
import io.reactivex.rxjava3.core.Completable
import org.junit.Test

class DeleteModelUseCaseImplTest {

    private val stubRepository = mock<DownloadableModelRepository>()

    private val useCase = DeleteModelUseCaseImpl(stubRepository)

    @Test
    fun `given model deleted successfully, expected completion`() {
        whenever(stubRepository.delete(any()))
            .thenReturn(Completable.complete())

        useCase("5598")
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given model delete failed, expected error value`() {
        val stubException = Throwable("Failed to delete model.")

        whenever(stubRepository.delete(any()))
            .thenReturn(Completable.error(stubException))

        useCase("5598")
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
