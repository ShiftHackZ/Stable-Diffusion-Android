package com.shifthackz.aisdv1.domain.usecase.gallery

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.mocks.mockAiGenerationResults
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class GetAllGalleryUseCaseImplTest {

    private val stubRepository = mock<GenerationResultRepository>()

    private val useCase = GetAllGalleryUseCaseImpl(stubRepository)

    @Test
    fun `given repository returned list of generations, expected valid list value`() {
        whenever(stubRepository.getAll())
            .thenReturn(Single.just(mockAiGenerationResults))

        useCase()
            .test()
            .assertNoErrors()
            .assertValue(mockAiGenerationResults)
            .await()
            .assertComplete()
    }

    @Test
    fun `given repository returned empty list of generations, expected empty list value`() {
        whenever(stubRepository.getAll())
            .thenReturn(Single.just(emptyList()))

        useCase()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given repository thrown exception, expected error value`() {
        val stubException = Throwable("Database communication error.")

        whenever(stubRepository.getAll())
            .thenReturn(Single.error(stubException))

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
