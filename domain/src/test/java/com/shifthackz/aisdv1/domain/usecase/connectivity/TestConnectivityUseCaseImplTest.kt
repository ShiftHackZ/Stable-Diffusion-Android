package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository
import io.reactivex.rxjava3.core.Completable
import org.junit.Test

class TestConnectivityUseCaseImplTest {

    companion object {
        private const val STUB_URL = "https://5598.is.my.favourite.com"
    }

    private val stubException = Throwable("Can not establish connection to server.")
    private val stubRepository = mock<StableDiffusionGenerationRepository>()

    private val useCase = TestConnectivityUseCaseImpl(stubRepository)

    @Test
    fun `given connection to server can be established, expected complete value`() {
        whenever(stubRepository.checkApiAvailability(any()))
            .thenReturn(Completable.complete())

        useCase(STUB_URL)
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given connection to server can not be established, expected error value`() {
        whenever(stubRepository.checkApiAvailability(any()))
            .thenReturn(Completable.error(stubException))

        useCase(STUB_URL)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
