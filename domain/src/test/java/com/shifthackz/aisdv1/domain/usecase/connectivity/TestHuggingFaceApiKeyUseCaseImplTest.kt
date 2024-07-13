package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.repository.HuggingFaceGenerationRepository
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class TestHuggingFaceApiKeyUseCaseImplTest {

    private val stubException = Throwable("Can not connect to Hugging Face AI.")
    private val stubRepository = mock<HuggingFaceGenerationRepository>()

    private val useCase = TestHuggingFaceApiKeyUseCaseImpl(stubRepository)

    @Test
    fun `given hugging face api key passed validation, expected true`() {
        whenever(stubRepository.validateApiKey())
            .thenReturn(Single.just(true))

        useCase()
            .test()
            .assertNoErrors()
            .assertValue(true)
            .await()
            .assertComplete()
    }

    @Test
    fun `given hugging face api key not passed validation, expected false`() {
        whenever(stubRepository.validateApiKey())
            .thenReturn(Single.just(false))

        useCase()
            .test()
            .assertNoErrors()
            .assertValue(false)
            .await()
            .assertComplete()
    }

    @Test
    fun `given validator thrown exception, expected error value`() {
        whenever(stubRepository.validateApiKey())
            .thenReturn(Single.error(stubException))

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
