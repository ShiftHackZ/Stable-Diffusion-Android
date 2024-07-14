package com.shifthackz.aisdv1.domain.usecase.caching

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.mocks.mockAiGenerationResult
import com.shifthackz.aisdv1.domain.repository.TemporaryGenerationResultRepository
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class GetLastResultFromCacheUseCaseImplTest {

    private val stubException = Throwable("No last cached result")
    private val stubRepository = mock<TemporaryGenerationResultRepository>()

    private val useCase = GetLastResultFromCacheUseCaseImpl(stubRepository)

    @Test
    fun `given repository returned last ai result, expected valid result value`() {
        whenever(stubRepository.get())
            .thenReturn(Single.just(mockAiGenerationResult))

        useCase()
            .test()
            .assertNoErrors()
            .assertValue(mockAiGenerationResult)
            .await()
            .assertComplete()
    }

    @Test
    fun `given repository has no last ai result, expected error value`() {
        whenever(stubRepository.get())
            .thenReturn(Single.error(stubException))

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
