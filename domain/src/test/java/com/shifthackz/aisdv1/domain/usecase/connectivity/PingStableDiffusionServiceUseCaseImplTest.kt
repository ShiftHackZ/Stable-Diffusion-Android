package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository
import io.reactivex.rxjava3.core.Completable
import org.junit.Test

class PingStableDiffusionServiceUseCaseImplTest {

    private val stubException = Throwable("Can not establish connection to server.")
    private val stubRepository = mock<StableDiffusionGenerationRepository>()

    private val useCase = PingStableDiffusionServiceUseCaseImpl(stubRepository)

    @Test
    fun `given connection to server can be established, expected complete value`() {
        whenever(stubRepository.checkApiAvailability())
            .thenReturn(Completable.complete())

        useCase()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given connection to server can not be established, expected error value`() {
        whenever(stubRepository.checkApiAvailability())
            .thenReturn(Completable.error(stubException))

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
