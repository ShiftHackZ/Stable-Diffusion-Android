package com.shifthackz.aisdv1.domain.usecase.generation

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.HordeGenerationRepository
import com.shifthackz.aisdv1.domain.repository.LocalDiffusionGenerationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository
import io.reactivex.rxjava3.core.Completable
import org.junit.Test

class InterruptGenerationUseCaseImplTest {

    private val stubException = Throwable("Can not interrupt generation.")
    private val stubStableDiffusionGenerationRepository = mock<StableDiffusionGenerationRepository>()
    private val stubHordeGenerationRepository = mock<HordeGenerationRepository>()
    private val stubLocalDiffusionGenerationRepository = mock<LocalDiffusionGenerationRepository>()
    private val stubPreferenceManager = mock<PreferenceManager>()

    private val useCase = InterruptGenerationUseCaseImpl(
        stableDiffusionGenerationRepository = stubStableDiffusionGenerationRepository,
        hordeGenerationRepository = stubHordeGenerationRepository,
        localDiffusionGenerationRepository = stubLocalDiffusionGenerationRepository,
        preferenceManager = stubPreferenceManager,
    )

    @Test
    fun `given source is AUTOMATIC1111, api interrupt success, expected complete value`() {
        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.AUTOMATIC1111)

        whenever(stubStableDiffusionGenerationRepository.interruptGeneration())
            .thenReturn(Completable.complete())

        useCase()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given source is AUTOMATIC1111, api interrupt fail, expected error value`() {
        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.AUTOMATIC1111)

        whenever(stubStableDiffusionGenerationRepository.interruptGeneration())
            .thenReturn(Completable.error(stubException))

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given source is HORDE, api interrupt success, expected complete value`() {
        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.HORDE)

        whenever(stubHordeGenerationRepository.interruptGeneration())
            .thenReturn(Completable.complete())

        useCase()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given source is HORDE, api interrupt fail, expected error value`() {
        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.HORDE)

        whenever(stubHordeGenerationRepository.interruptGeneration())
            .thenReturn(Completable.error(stubException))

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given source is LOCAL, api interrupt success, expected complete value`() {
        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.LOCAL_MICROSOFT_ONNX)

        whenever(stubLocalDiffusionGenerationRepository.interruptGeneration())
            .thenReturn(Completable.complete())

        useCase()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given source is LOCAL, api interrupt fail, expected error value`() {
        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.LOCAL_MICROSOFT_ONNX)

        whenever(stubLocalDiffusionGenerationRepository.interruptGeneration())
            .thenReturn(Completable.error(stubException))

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    //--

    @Test
    fun `given source is HUGGING_FACE, expected complete value`() {
        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.HUGGING_FACE)

        useCase()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given source is OPEN_AI, expected complete value`() {
        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.OPEN_AI)

        useCase()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given source is STABILITY_AI, expected complete value`() {
        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.STABILITY_AI)

        useCase()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }
}
