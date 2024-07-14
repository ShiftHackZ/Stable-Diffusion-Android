package com.shifthackz.aisdv1.domain.usecase.generation

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.mocks.mockAiGenerationResults
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class GetGenerationResultPagedUseCaseImplTest {

    private val stubException = Throwable("Can not read DB.")
    private val stubRepository = mock<GenerationResultRepository>()

    private val useCase = GetGenerationResultPagedUseCaseImpl(stubRepository)

    @Test
    fun `given repository returned page with items, expected valid list value`() {
        whenever(stubRepository.getPage(any(), any()))
            .thenReturn(Single.just(mockAiGenerationResults))

        useCase(20, 0)
            .test()
            .assertNoErrors()
            .assertValue(mockAiGenerationResults)
            .await()
            .assertComplete()
    }

    @Test
    fun `given repository returned empty page with no items, expected empty list value`() {
        whenever(stubRepository.getPage(any(), any()))
            .thenReturn(Single.just(emptyList()))

        useCase(20, 5598)
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given repository thrown exception, expected error value`() {
        whenever(stubRepository.getPage(any(), any()))
            .thenReturn(Single.error(stubException))

        useCase(20, 5598)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
