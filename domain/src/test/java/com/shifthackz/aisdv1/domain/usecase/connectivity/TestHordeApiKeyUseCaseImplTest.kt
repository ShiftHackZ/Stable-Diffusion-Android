package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.repository.HordeGenerationRepository
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class TestHordeApiKeyUseCaseImplTest {

    private val stubException = Throwable("Can not connect to Horde AI.")
    private val stubRepository = mock<HordeGenerationRepository>()

    private val useCase = TestHordeApiKeyUseCaseImpl(stubRepository)

    @Test
    fun `given horde api key passed validation, expected true`() {
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
    fun `given horde api key not passed validation, expected false`() {
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
