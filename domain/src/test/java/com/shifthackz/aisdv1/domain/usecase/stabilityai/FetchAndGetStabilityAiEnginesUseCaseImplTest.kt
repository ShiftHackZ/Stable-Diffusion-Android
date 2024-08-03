package com.shifthackz.aisdv1.domain.usecase.stabilityai

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.mocks.mockStabilityAiEngines
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.StabilityAiEnginesRepository
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class FetchAndGetStabilityAiEnginesUseCaseImplTest {

    private val stubRepository = mock<StabilityAiEnginesRepository>()

    private val stubPreferenceManager = mock<PreferenceManager>()

    private val useCase = FetchAndGetStabilityAiEnginesUseCaseImpl(
        repository = stubRepository,
        preferenceManager = stubPreferenceManager,
    )

    @Test
    fun `given repository returned engines list, id present in preference, expected the same engines list, id not changed`() {
        whenever(stubRepository.fetchAndGet())
            .thenReturn(Single.just(mockStabilityAiEngines))

        whenever(stubPreferenceManager::stabilityAiEngineId.get())
            .thenReturn("engine_1")

        useCase()
            .test()
            .assertNoErrors()
            .assertValue(mockStabilityAiEngines)
            .assertComplete()
    }

    @Test
    fun `given repository thrown exception, expected the same exception`() {
        val stubException = Throwable("Network exception")

        whenever(stubRepository.fetchAndGet())
            .thenReturn(Single.error(stubException))

        useCase()
            .test()
            .assertError(stubException)
            .assertNotComplete()
    }
}
