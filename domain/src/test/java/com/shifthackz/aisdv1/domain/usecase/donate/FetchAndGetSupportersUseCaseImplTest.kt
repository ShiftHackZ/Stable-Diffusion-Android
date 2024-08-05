package com.shifthackz.aisdv1.domain.usecase.donate

import com.shifthackz.aisdv1.domain.mocks.mockSupporters
import com.shifthackz.aisdv1.domain.repository.SupportersRepository
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class FetchAndGetSupportersUseCaseImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubRepository = mockk<SupportersRepository>()

    private val useCase = FetchAndGetSupportersUseCaseImpl(stubRepository)

    @Test
    fun `given repository returned data, expected valid domain model list value`() {
        every {
            stubRepository.fetchAndGetSupporters()
        } returns Single.just(mockSupporters)

        useCase()
            .test()
            .assertNoErrors()
            .await()
            .assertValue(mockSupporters)
            .assertComplete()
    }

    @Test
    fun `given repository returned empty data, expected empty domain model list value`() {
        every {
            stubRepository.fetchAndGetSupporters()
        } returns Single.just(emptyList())

        useCase()
            .test()
            .assertNoErrors()
            .await()
            .assertValue(emptyList())
            .assertComplete()
    }

    @Test
    fun `given repository thrown exception, expected error value`() {
        every {
            stubRepository.fetchAndGetSupporters()
        } returns Single.error(stubException)

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNoValues()
            .assertNotComplete()
    }
}
