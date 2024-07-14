package com.shifthackz.aisdv1.domain.usecase.sdhypernet

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.mocks.mockStableDiffusionHyperNetworks
import com.shifthackz.aisdv1.domain.repository.StableDiffusionHyperNetworksRepository
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class FetchAndGetHyperNetworksUseCaseImplTest {

    private val stubRepository = mock<StableDiffusionHyperNetworksRepository>()

    private val useCase = FetchAndGetHyperNetworksUseCaseImpl(stubRepository)

    @Test
    fun `given repository provided list of hypernetworks, expected valid list value`() {
        whenever(stubRepository.fetchAndGetHyperNetworks())
            .thenReturn(Single.just(mockStableDiffusionHyperNetworks))

        useCase()
            .test()
            .assertNoErrors()
            .assertValue(mockStableDiffusionHyperNetworks)
            .await()
            .assertComplete()
    }

    @Test
    fun `given repository provided empty list of hypernetworks, expected empty list value`() {
        whenever(stubRepository.fetchAndGetHyperNetworks())
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

        whenever(stubRepository.fetchAndGetHyperNetworks())
            .thenReturn(Single.error(stubException))

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
