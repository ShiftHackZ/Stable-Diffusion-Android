package com.shifthackz.aisdv1.domain.usecase.huggingface

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.mocks.mockHuggingFaceModels
import com.shifthackz.aisdv1.domain.repository.HuggingFaceModelsRepository
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class FetchAndGetHuggingFaceModelsUseCaseImplTest {

    private val stubRepository = mock<HuggingFaceModelsRepository>()

    private val useCase = FetchAndGetHuggingFaceModelsUseCaseImpl(stubRepository)

    @Test
    fun `given repository provided models list, expected valid list value`() {
        whenever(stubRepository.fetchAndGetHuggingFaceModels())
            .thenReturn(Single.just(mockHuggingFaceModels))

        useCase()
            .test()
            .assertNoErrors()
            .assertValue(mockHuggingFaceModels)
            .await()
            .assertComplete()
    }

    @Test
    fun `given repository provided empty models list, expected empty list value`() {
        whenever(stubRepository.fetchAndGetHuggingFaceModels())
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
        val stubException = Throwable("Unknown error occurred.")

        whenever(stubRepository.fetchAndGetHuggingFaceModels())
            .thenReturn(Single.error(stubException))

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
