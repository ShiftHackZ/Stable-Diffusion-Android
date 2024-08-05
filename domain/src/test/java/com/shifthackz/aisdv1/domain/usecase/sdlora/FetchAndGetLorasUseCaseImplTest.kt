package com.shifthackz.aisdv1.domain.usecase.sdlora

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.mocks.mockLoRAs
import com.shifthackz.aisdv1.domain.repository.LorasRepository
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class FetchAndGetLorasUseCaseImplTest {

    private val stubRepository = mock<LorasRepository>()

    private val useCase = FetchAndGetLorasUseCaseImpl(stubRepository)

    @Test
    fun `given repository provided list of LoRAs, expected valid list value`() {
        whenever(stubRepository.fetchAndGetLoras())
            .thenReturn(Single.just(mockLoRAs))

        useCase()
            .test()
            .assertNoErrors()
            .assertValue(mockLoRAs)
            .await()
            .assertComplete()
    }

    @Test
    fun `given repository provided empty list of LoRAs, expected empty list value`() {
        whenever(stubRepository.fetchAndGetLoras())
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

        whenever(stubRepository.fetchAndGetLoras())
            .thenReturn(Single.error(stubException))

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
