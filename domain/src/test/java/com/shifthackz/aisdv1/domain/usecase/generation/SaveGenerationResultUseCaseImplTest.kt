package com.shifthackz.aisdv1.domain.usecase.generation

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.mocks.mockAiGenerationResult
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class SaveGenerationResultUseCaseImplTest {

    private val stubException = Throwable("Error inserting into DB.")
    private val stubRepository = mock<GenerationResultRepository>()

    private val useCase = SaveGenerationResultUseCaseImpl(stubRepository)

    @Test
    fun `given repository saved generation result successfully, expected complete value`() {
        whenever(stubRepository.insert(any()))
            .thenReturn(Single.just(5598L))

        useCase(mockAiGenerationResult)
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given repository failed to save generation result, expected error value`() {
        whenever(stubRepository.insert(any()))
            .thenReturn(Single.error(stubException))

        useCase(mockAiGenerationResult)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
