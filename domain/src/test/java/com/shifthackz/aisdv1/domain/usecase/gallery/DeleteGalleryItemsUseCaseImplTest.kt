package com.shifthackz.aisdv1.domain.usecase.gallery

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.reactivex.rxjava3.core.Completable
import org.junit.Test

class DeleteGalleryItemsUseCaseImplTest {

    private val stubRepository = mock<GenerationResultRepository>()

    private val useCase = DeleteGalleryItemsUseCaseImpl(stubRepository)

    @Test
    fun `given repository deleted data successfully, expected complete`() {
        whenever(stubRepository.deleteByIdList(any()))
            .thenReturn(Completable.complete())

        useCase(listOf(5598L, 151297L))
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given repository deleted data with fail, expected error`() {
        val stubException = Throwable("Database communication error.")

        whenever(stubRepository.deleteByIdList(any()))
            .thenReturn(Completable.error(stubException))

        useCase(listOf(5598L, 151297L))
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
