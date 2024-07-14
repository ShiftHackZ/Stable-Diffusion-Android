package com.shifthackz.aisdv1.domain.usecase.generation

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.mocks.mockAiGenerationResult
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class GetGenerationResultUseCaseImplTest {

    private val stubException = Throwable("Ai generation result not found.")
    private val stubRepository = mock<GenerationResultRepository>()

    private val useCase = GetGenerationResultUseCaseImpl(stubRepository)

    @Test
    fun `given repository has ai result with provided id, expected valid ai generation result value`() {
        whenever(stubRepository.getById(any()))
            .thenReturn(Single.just(mockAiGenerationResult))

        useCase(5598L)
            .test()
            .assertNoErrors()
            .assertValue(mockAiGenerationResult)
            .await()
            .assertComplete()
    }

    @Test
    fun `given repository has no ai result with provided id, expected error value`() {
        whenever(stubRepository.getById(any()))
            .thenReturn(Single.error(stubException))

        useCase(5598L)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
