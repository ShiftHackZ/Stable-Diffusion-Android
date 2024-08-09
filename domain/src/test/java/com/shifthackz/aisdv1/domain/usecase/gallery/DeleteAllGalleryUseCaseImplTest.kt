package com.shifthackz.aisdv1.domain.usecase.gallery

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.reactivex.rxjava3.core.Completable
import org.junit.Test

class DeleteAllGalleryUseCaseImplTest {

    private val stubRepository = mock<GenerationResultRepository>()

    private val useCase = DeleteAllGalleryUseCaseImpl(stubRepository)

    @Test
    fun `given repository deleted data successfully, expected complete`() {
        whenever(stubRepository.deleteAll())
            .thenReturn(Completable.complete())

        useCase()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given repository deleted data with fail, expected error`() {
        val stubException = Throwable("Database communication error.")

        whenever(stubRepository.deleteAll())
            .thenReturn(Completable.error(stubException))

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
