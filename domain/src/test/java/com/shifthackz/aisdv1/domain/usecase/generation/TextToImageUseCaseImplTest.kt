package com.shifthackz.aisdv1.domain.usecase.generation

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.mocks.mockAiGenerationResult
import com.shifthackz.aisdv1.domain.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.HordeGenerationRepository
import com.shifthackz.aisdv1.domain.repository.HuggingFaceGenerationRepository
import com.shifthackz.aisdv1.domain.repository.LocalDiffusionGenerationRepository
import com.shifthackz.aisdv1.domain.repository.OpenAiGenerationRepository
import com.shifthackz.aisdv1.domain.repository.StabilityAiGenerationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository
import com.shifthackz.aisdv1.domain.repository.SwarmUiGenerationRepository
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class TextToImageUseCaseImplTest {

    private val stubException = Throwable("Unable to generate image.")
    private val stubStableDiffusionGenerationRepository = mock<StableDiffusionGenerationRepository>()
    private val stubHordeGenerationRepository = mock<HordeGenerationRepository>()
    private val stubHuggingFaceGenerationRepository = mock<HuggingFaceGenerationRepository>()
    private val stubOpenAiGenerationRepository = mock<OpenAiGenerationRepository>()
    private val stubStabilityAiGenerationRepository = mock<StabilityAiGenerationRepository>()
    private val stubSwarmUiGenerationRepository = mock<SwarmUiGenerationRepository>()
    private val stubLocalDiffusionGenerationRepository = mock<LocalDiffusionGenerationRepository>()
    private val stubPreferenceManager = mock<PreferenceManager>()

    private val useCase = TextToImageUseCaseImpl(
        stableDiffusionGenerationRepository = stubStableDiffusionGenerationRepository,
        hordeGenerationRepository = stubHordeGenerationRepository,
        huggingFaceGenerationRepository = stubHuggingFaceGenerationRepository,
        openAiGenerationRepository = stubOpenAiGenerationRepository,
        stabilityAiGenerationRepository = stubStabilityAiGenerationRepository,
        localDiffusionGenerationRepository = stubLocalDiffusionGenerationRepository,
        swarmUiGenerationRepository = stubSwarmUiGenerationRepository,
        preferenceManager = stubPreferenceManager,
    )

    @Test
    fun `given source is AUTOMATIC1111, batch count is 1, generated successfully, expected generations list with size 1`() {
        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.AUTOMATIC1111)

        whenever(stubStableDiffusionGenerationRepository.generateFromText(any()))
            .thenReturn(Single.just(mockAiGenerationResult))

        val stubBatchCount = 1
        val stubPayload = mockTextToImagePayload.copy(batchCount = stubBatchCount)

        val expectedResult = listOf(mockAiGenerationResult)

        useCase(stubPayload)
            .test()
            .assertNoErrors()
            .assertValue { generations ->
                generations.size == stubBatchCount && expectedResult == generations
            }
            .await()
            .assertComplete()
    }

    @Test
    fun `given source is AUTOMATIC1111, batch count is 10, generated successfully, expected generations list with size 10`() {
        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.AUTOMATIC1111)

        whenever(stubStableDiffusionGenerationRepository.generateFromText(any()))
            .thenReturn(Single.just(mockAiGenerationResult))

        val stubBatchCount = 10
        val stubPayload = mockTextToImagePayload.copy(batchCount = stubBatchCount)

        val expectedResult = (0 until 10).map { mockAiGenerationResult }

        useCase(stubPayload)
            .test()
            .assertNoErrors()
            .assertValue { generations ->
                generations.size == stubBatchCount && expectedResult == generations
            }
            .await()
            .assertComplete()
    }

    @Test
    fun `given source is AUTOMATIC1111, batch count is 1, generate failed, expected error`() {
        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.AUTOMATIC1111)

        whenever(stubStableDiffusionGenerationRepository.generateFromText(any()))
            .thenReturn(Single.error(stubException))

        val stubBatchCount = 1
        val stubPayload = mockTextToImagePayload.copy(batchCount = stubBatchCount)

        useCase(stubPayload)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given source is HORDE, batch count is 1, generated successfully, expected generations list with size 1`() {
        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.HORDE)

        whenever(stubHordeGenerationRepository.generateFromText(any()))
            .thenReturn(Single.just(mockAiGenerationResult))

        val stubBatchCount = 1
        val stubPayload = mockTextToImagePayload.copy(batchCount = stubBatchCount)

        val expectedResult = listOf(mockAiGenerationResult)

        useCase(stubPayload)
            .test()
            .assertNoErrors()
            .assertValue { generations ->
                generations.size == stubBatchCount && expectedResult == generations
            }
            .await()
            .assertComplete()
    }

    @Test
    fun `given source is HORDE, batch count is 10, generated successfully, expected generations list with size 10`() {
        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.HORDE)

        whenever(stubHordeGenerationRepository.generateFromText(any()))
            .thenReturn(Single.just(mockAiGenerationResult))

        val stubBatchCount = 10
        val stubPayload = mockTextToImagePayload.copy(batchCount = stubBatchCount)

        val expectedResult = (0 until 10).map { mockAiGenerationResult }

        useCase(stubPayload)
            .test()
            .assertNoErrors()
            .assertValue { generations ->
                generations.size == stubBatchCount && expectedResult == generations
            }
            .await()
            .assertComplete()
    }

    @Test
    fun `given source is HORDE, batch count is 1, generate failed, expected error`() {
        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.HORDE)

        whenever(stubHordeGenerationRepository.generateFromText(any()))
            .thenReturn(Single.error(stubException))

        val stubBatchCount = 1
        val stubPayload = mockTextToImagePayload.copy(batchCount = stubBatchCount)

        useCase(stubPayload)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given source is HUGGING_FACE, batch count is 1, generated successfully, expected generations list with size 1`() {
        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.HUGGING_FACE)

        whenever(stubHuggingFaceGenerationRepository.generateFromText(any()))
            .thenReturn(Single.just(mockAiGenerationResult))

        val stubBatchCount = 1
        val stubPayload = mockTextToImagePayload.copy(batchCount = stubBatchCount)

        val expectedResult = listOf(mockAiGenerationResult)

        useCase(stubPayload)
            .test()
            .assertNoErrors()
            .assertValue { generations ->
                generations.size == stubBatchCount && expectedResult == generations
            }
            .await()
            .assertComplete()
    }

    @Test
    fun `given source is HUGGING_FACE, batch count is 10, generated successfully, expected generations list with size 10`() {
        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.HUGGING_FACE)

        whenever(stubHuggingFaceGenerationRepository.generateFromText(any()))
            .thenReturn(Single.just(mockAiGenerationResult))

        val stubBatchCount = 10
        val stubPayload = mockTextToImagePayload.copy(batchCount = stubBatchCount)

        val expectedResult = (0 until 10).map { mockAiGenerationResult }

        useCase(stubPayload)
            .test()
            .assertNoErrors()
            .assertValue { generations ->
                generations.size == stubBatchCount && expectedResult == generations
            }
            .await()
            .assertComplete()
    }

    @Test
    fun `given source is HUGGING_FACE, batch count is 1, generate failed, expected error`() {
        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.HUGGING_FACE)

        whenever(stubHuggingFaceGenerationRepository.generateFromText(any()))
            .thenReturn(Single.error(stubException))

        val stubBatchCount = 1
        val stubPayload = mockTextToImagePayload.copy(batchCount = stubBatchCount)

        useCase(stubPayload)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given source is OPEN_AI, batch count is 1, generated successfully, expected generations list with size 1`() {
        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.OPEN_AI)

        whenever(stubOpenAiGenerationRepository.generateFromText(any()))
            .thenReturn(Single.just(mockAiGenerationResult))

        val stubBatchCount = 1
        val stubPayload = mockTextToImagePayload.copy(batchCount = stubBatchCount)

        val expectedResult = listOf(mockAiGenerationResult)

        useCase(stubPayload)
            .test()
            .assertNoErrors()
            .assertValue { generations ->
                generations.size == stubBatchCount && expectedResult == generations
            }
            .await()
            .assertComplete()
    }

    @Test
    fun `given source is OPEN_AI, batch count is 10, generated successfully, expected generations list with size 10`() {
        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.OPEN_AI)

        whenever(stubOpenAiGenerationRepository.generateFromText(any()))
            .thenReturn(Single.just(mockAiGenerationResult))

        val stubBatchCount = 10
        val stubPayload = mockTextToImagePayload.copy(batchCount = stubBatchCount)

        val expectedResult = (0 until 10).map { mockAiGenerationResult }

        useCase(stubPayload)
            .test()
            .assertNoErrors()
            .assertValue { generations ->
                generations.size == stubBatchCount && expectedResult == generations
            }
            .await()
            .assertComplete()
    }

    @Test
    fun `given source is OPEN_AI, batch count is 1, generate failed, expected error`() {
        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.OPEN_AI)

        whenever(stubOpenAiGenerationRepository.generateFromText(any()))
            .thenReturn(Single.error(stubException))

        val stubBatchCount = 1
        val stubPayload = mockTextToImagePayload.copy(batchCount = stubBatchCount)

        useCase(stubPayload)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given source is STABILITY_AI, batch count is 1, generated successfully, expected generations list with size 1`() {
        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.STABILITY_AI)

        whenever(stubStabilityAiGenerationRepository.generateFromText(any()))
            .thenReturn(Single.just(mockAiGenerationResult))

        val stubBatchCount = 1
        val stubPayload = mockTextToImagePayload.copy(batchCount = stubBatchCount)

        val expectedResult = listOf(mockAiGenerationResult)

        useCase(stubPayload)
            .test()
            .assertNoErrors()
            .assertValue { generations ->
                generations.size == stubBatchCount && expectedResult == generations
            }
            .await()
            .assertComplete()
    }

    @Test
    fun `given source is STABILITY_AI, batch count is 10, generated successfully, expected generations list with size 10`() {
        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.STABILITY_AI)

        whenever(stubStabilityAiGenerationRepository.generateFromText(any()))
            .thenReturn(Single.just(mockAiGenerationResult))

        val stubBatchCount = 10
        val stubPayload = mockTextToImagePayload.copy(batchCount = stubBatchCount)

        val expectedResult = (0 until 10).map { mockAiGenerationResult }

        useCase(stubPayload)
            .test()
            .assertNoErrors()
            .assertValue { generations ->
                generations.size == stubBatchCount && expectedResult == generations
            }
            .await()
            .assertComplete()
    }

    @Test
    fun `given source is STABILITY_AI, batch count is 1, generate failed, expected error`() {
        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.STABILITY_AI)

        whenever(stubStabilityAiGenerationRepository.generateFromText(any()))
            .thenReturn(Single.error(stubException))

        val stubBatchCount = 1
        val stubPayload = mockTextToImagePayload.copy(batchCount = stubBatchCount)

        useCase(stubPayload)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given source is LOCAL, batch count is 1, generated successfully, expected generations list with size 1`() {
        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.LOCAL)

        whenever(stubLocalDiffusionGenerationRepository.generateFromText(any()))
            .thenReturn(Single.just(mockAiGenerationResult))

        val stubBatchCount = 1
        val stubPayload = mockTextToImagePayload.copy(batchCount = stubBatchCount)

        val expectedResult = listOf(mockAiGenerationResult)

        useCase(stubPayload)
            .test()
            .assertNoErrors()
            .assertValue { generations ->
                generations.size == stubBatchCount && expectedResult == generations
            }
            .await()
            .assertComplete()
    }

    @Test
    fun `given source is LOCAL, batch count is 10, generated successfully, expected generations list with size 10`() {
        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.LOCAL)

        whenever(stubLocalDiffusionGenerationRepository.generateFromText(any()))
            .thenReturn(Single.just(mockAiGenerationResult))

        val stubBatchCount = 10
        val stubPayload = mockTextToImagePayload.copy(batchCount = stubBatchCount)

        val expectedResult = (0 until 10).map { mockAiGenerationResult }

        useCase(stubPayload)
            .test()
            .assertNoErrors()
            .assertValue { generations ->
                generations.size == stubBatchCount && expectedResult == generations
            }
            .await()
            .assertComplete()
    }

    @Test
    fun `given source is LOCAL, batch count is 1, generate failed, expected error`() {
        whenever(stubPreferenceManager.source)
            .thenReturn(ServerSource.LOCAL)

        whenever(stubLocalDiffusionGenerationRepository.generateFromText(any()))
            .thenReturn(Single.error(stubException))

        val stubBatchCount = 1
        val stubPayload = mockTextToImagePayload.copy(batchCount = stubBatchCount)

        useCase(stubPayload)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
