package com.shifthackz.aisdv1.domain.usecase.caching

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.mocks.mockAiGenerationResult
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.TemporaryGenerationResultRepository
import io.reactivex.rxjava3.core.Completable
import org.junit.Test

class SaveLastResultToCacheUseCaseImplTest {

    private val stubException = Throwable("No last cached result")
    private val stubRepository = mock<TemporaryGenerationResultRepository>()
    private val stubPreferenceManager = mock<PreferenceManager>()

    private val useCase = SaveLastResultToCacheUseCaseImpl(
        temporaryGenerationResultRepository = stubRepository,
        preferenceManager = stubPreferenceManager,
    )

    @Test
    fun `given user has enabled autosave, try to save, expected valid ai result value`() {
        whenever(stubPreferenceManager.autoSaveAiResults)
            .thenReturn(true)

        useCase(mockAiGenerationResult)
            .test()
            .assertNoErrors()
            .assertValue(mockAiGenerationResult)
            .await()
            .assertComplete()
    }

    @Test
    fun `given user has disabled autosave, save completed successfully, expected valid ai result value`() {
        whenever(stubPreferenceManager.autoSaveAiResults)
            .thenReturn(false)

        whenever(stubRepository.put(any()))
            .thenReturn(Completable.complete())

        useCase(mockAiGenerationResult)
            .test()
            .assertNoErrors()
            .assertValue(mockAiGenerationResult)
            .await()
            .assertComplete()
    }

    @Test
    fun `given user has disabled autosave, save fails, expected error value`() {
        whenever(stubPreferenceManager.autoSaveAiResults)
            .thenReturn(false)

        whenever(stubRepository.put(any()))
            .thenReturn(Completable.error(stubException))

        useCase(mockAiGenerationResult)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
