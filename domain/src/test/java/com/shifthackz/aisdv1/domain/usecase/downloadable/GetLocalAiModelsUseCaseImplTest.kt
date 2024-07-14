package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.mocks.mockLocalAiModels
import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class GetLocalAiModelsUseCaseImplTest {

    private val stubRepository = mock<DownloadableModelRepository>()

    private val useCase = GetLocalAiModelsUseCaseImpl(stubRepository)

    @Test
    fun `given repository returned models list, expected valid models list value`() {
        whenever(stubRepository.getAll())
            .thenReturn(Single.just(mockLocalAiModels))

        useCase()
            .test()
            .assertNoErrors()
            .assertValue(mockLocalAiModels)
            .await()
            .assertComplete()
    }

    @Test
    fun `given repository returned empty models list, expected empty models list value`() {
        whenever(stubRepository.getAll())
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
        val stubException = Throwable("Unable to collect local models.")

        whenever(stubRepository.getAll())
            .thenReturn(Single.error(stubException))

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
