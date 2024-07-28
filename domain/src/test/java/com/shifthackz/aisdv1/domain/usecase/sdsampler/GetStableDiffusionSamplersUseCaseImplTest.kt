package com.shifthackz.aisdv1.domain.usecase.sdsampler

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.mocks.mockStableDiffusionSampler
import com.shifthackz.aisdv1.domain.repository.StableDiffusionSamplersRepository
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class GetStableDiffusionSamplersUseCaseImplTest {

    private val stubStableDiffusionSamplersRepository = mock<StableDiffusionSamplersRepository>()

    private val useCase = GetStableDiffusionSamplersUseCaseImpl(
        repository = stubStableDiffusionSamplersRepository,
    )

    @Test
    fun `expected got samplers from repository, expected valid samplers value`() {
        whenever(stubStableDiffusionSamplersRepository.getSamplers())
            .thenReturn(Single.just(mockStableDiffusionSampler))

        useCase()
            .test()
            .assertNoErrors()
            .assertValue(mockStableDiffusionSampler)
            .await()
            .assertComplete()
    }

    @Test
    fun `expected got empty list from repository, expected empty value`() {
        whenever(stubStableDiffusionSamplersRepository.getSamplers())
            .thenReturn(Single.just(emptyList()))

        useCase()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `expected got error from repository, expected the same error`() {
        val stubException = Throwable("Error query database.")

        whenever(stubStableDiffusionSamplersRepository.getSamplers())
            .thenReturn(Single.error(stubException))

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
