package com.shifthackz.aisdv1.domain.usecase.sdembedding

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.entity.mockStableDiffusionEmbeddings
import com.shifthackz.aisdv1.domain.repository.StableDiffusionEmbeddingsRepository
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class FetchAndGetEmbeddingsUseCaseImplTest {

    private val stubRepository = mock<StableDiffusionEmbeddingsRepository>()

    private val useCase = FetchAndGetEmbeddingsUseCaseImpl(stubRepository)

    @Test
    fun `given repository provided embeddings list, expected valid list value`() {
        whenever(stubRepository.fetchAndGetEmbeddings())
            .doReturn(Single.just(mockStableDiffusionEmbeddings))

        useCase()
            .test()
            .assertNoErrors()
            .assertValue(mockStableDiffusionEmbeddings)
            .await()
            .assertComplete()
    }

    @Test
    fun `given repository provided empty embeddings list, expected empty list value`() {
        whenever(stubRepository.fetchAndGetEmbeddings())
            .doReturn(Single.just(emptyList()))

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

        whenever(stubRepository.fetchAndGetEmbeddings())
            .doReturn(Single.error(stubException))

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
