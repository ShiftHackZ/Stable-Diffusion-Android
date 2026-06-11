package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.mocks.mockAiGenerationResult
import com.shifthackz.aisdv1.domain.mocks.mockImageToImagePayload
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.CoreMlGenerationRepository
import com.shifthackz.aisdv1.domain.repository.FalAiGenerationRepository
import com.shifthackz.aisdv1.domain.repository.HordeGenerationRepository
import com.shifthackz.aisdv1.domain.repository.HuggingFaceGenerationRepository
import com.shifthackz.aisdv1.domain.repository.StabilityAiGenerationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository
import com.shifthackz.aisdv1.domain.repository.SwarmUiGenerationRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ImageToImageUseCaseImplTest {

    private val stubException = RuntimeException("Unable to generate image.")
    private val stubStableDiffusionGenerationRepository = mockk<StableDiffusionGenerationRepository>()
    private val stubSwarmUiGenerationRepository = mockk<SwarmUiGenerationRepository>()
    private val stubHordeGenerationRepository = mockk<HordeGenerationRepository>()
    private val stubHuggingFaceGenerationRepository = mockk<HuggingFaceGenerationRepository>()
    private val stubStabilityAiGenerationRepository = mockk<StabilityAiGenerationRepository>()
    private val stubCoreMlGenerationRepository = mockk<CoreMlGenerationRepository>()
    private val stubFalAiGenerationRepository = mockk<FalAiGenerationRepository>()
    private val stubPreferenceManager = mockk<PreferenceManager>()

    private val useCase = ImageToImageUseCaseImpl(
        stableDiffusionGenerationRepository = stubStableDiffusionGenerationRepository,
        swarmUiGenerationRepository = stubSwarmUiGenerationRepository,
        hordeGenerationRepository = stubHordeGenerationRepository,
        huggingFaceGenerationRepository = stubHuggingFaceGenerationRepository,
        stabilityAiGenerationRepository = stubStabilityAiGenerationRepository,
        coreMlGenerationRepository = stubCoreMlGenerationRepository,
        falAiGenerationRepository = stubFalAiGenerationRepository,
        preferenceManager = stubPreferenceManager,
    )

    @Test
    fun `given source is AUTOMATIC1111, expected stable diffusion generation`() = runTest {
        every { stubPreferenceManager.source } returns ServerSource.AUTOMATIC1111
        coEvery { stubStableDiffusionGenerationRepository.generateFromImage(any()) } returns listOf(mockAiGenerationResult)

        val actual = useCase(mockImageToImagePayload.copy(batchCount = 1))

        assertEquals(listOf(mockAiGenerationResult), actual)
        coVerify(exactly = 1) { stubStableDiffusionGenerationRepository.generateFromImage(any()) }
    }

    @Test
    fun `given source is SWARM_UI, expected swarm ui generation`() = runTest {
        every { stubPreferenceManager.source } returns ServerSource.SWARM_UI
        coEvery { stubSwarmUiGenerationRepository.generateFromImage(any()) } returns mockAiGenerationResult

        val actual = useCase(mockImageToImagePayload.copy(batchCount = 1))

        assertEquals(listOf(mockAiGenerationResult), actual)
        coVerify(exactly = 1) { stubSwarmUiGenerationRepository.generateFromImage(any()) }
    }

    @Test
    fun `given source is HORDE, expected horde generation`() = runTest {
        every { stubPreferenceManager.source } returns ServerSource.HORDE
        coEvery { stubHordeGenerationRepository.generateFromImage(any()) } returns mockAiGenerationResult

        val actual = useCase(mockImageToImagePayload.copy(batchCount = 1))

        assertEquals(listOf(mockAiGenerationResult), actual)
        coVerify(exactly = 1) { stubHordeGenerationRepository.generateFromImage(any()) }
    }

    @Test
    fun `given source is HUGGING_FACE, expected hugging face generation`() = runTest {
        every { stubPreferenceManager.source } returns ServerSource.HUGGING_FACE
        coEvery { stubHuggingFaceGenerationRepository.generateFromImage(any()) } returns mockAiGenerationResult

        val actual = useCase(mockImageToImagePayload.copy(batchCount = 1))

        assertEquals(listOf(mockAiGenerationResult), actual)
        coVerify(exactly = 1) { stubHuggingFaceGenerationRepository.generateFromImage(any()) }
    }

    @Test
    fun `given source is STABILITY_AI, expected stability ai generation`() = runTest {
        every { stubPreferenceManager.source } returns ServerSource.STABILITY_AI
        coEvery { stubStabilityAiGenerationRepository.generateFromImage(any()) } returns mockAiGenerationResult

        val actual = useCase(mockImageToImagePayload.copy(batchCount = 1))

        assertEquals(listOf(mockAiGenerationResult), actual)
        coVerify(exactly = 1) { stubStabilityAiGenerationRepository.generateFromImage(any()) }
    }

    @Test
    fun `given source is LOCAL_APPLE_CORE_ML, expected core ml generation`() = runTest {
        every { stubPreferenceManager.source } returns ServerSource.LOCAL_APPLE_CORE_ML
        coEvery { stubCoreMlGenerationRepository.generateFromImage(any()) } returns mockAiGenerationResult

        val actual = useCase(mockImageToImagePayload.copy(batchCount = 1))

        assertEquals(listOf(mockAiGenerationResult), actual)
        coVerify(exactly = 1) { stubCoreMlGenerationRepository.generateFromImage(any()) }
    }

    @Test
    fun `given source is FAL_AI, expected fal ai generation`() = runTest {
        every { stubPreferenceManager.source } returns ServerSource.FAL_AI
        coEvery { stubFalAiGenerationRepository.generateFromImage(any()) } returns listOf(mockAiGenerationResult)

        val actual = useCase(mockImageToImagePayload.copy(batchCount = 1))

        assertEquals(listOf(mockAiGenerationResult), actual)
        coVerify(exactly = 1) { stubFalAiGenerationRepository.generateFromImage(any()) }
    }

    @Test
    fun `given automatic1111 batch count is 10, expected batch generated by repository`() = runTest {
        every { stubPreferenceManager.source } returns ServerSource.AUTOMATIC1111
        coEvery { stubStableDiffusionGenerationRepository.generateFromImage(any()) } returns List(10) { mockAiGenerationResult }

        val actual = useCase(mockImageToImagePayload.copy(batchCount = 10))

        assertEquals(List(10) { mockAiGenerationResult }, actual)
        coVerify(exactly = 1) { stubStableDiffusionGenerationRepository.generateFromImage(any()) }
    }

    @Test
    fun `given non batch source batch count is 10, expected generation repeated 10 times`() = runTest {
        every { stubPreferenceManager.source } returns ServerSource.HORDE
        coEvery { stubHordeGenerationRepository.generateFromImage(any()) } returns mockAiGenerationResult

        val actual = useCase(mockImageToImagePayload.copy(batchCount = 10))

        assertEquals(List(10) { mockAiGenerationResult }, actual)
        coVerify(exactly = 10) { stubHordeGenerationRepository.generateFromImage(any()) }
    }

    @Test
    fun `given fal ai batch count is 4, expected batch generated by repository`() = runTest {
        every { stubPreferenceManager.source } returns ServerSource.FAL_AI
        coEvery { stubFalAiGenerationRepository.generateFromImage(any()) } returns List(4) { mockAiGenerationResult }

        val actual = useCase(mockImageToImagePayload.copy(batchCount = 4))

        assertEquals(List(4) { mockAiGenerationResult }, actual)
        coVerify(exactly = 1) { stubFalAiGenerationRepository.generateFromImage(any()) }
    }

    @Test
    fun `given generation fails, expected error propagated`() = runTest {
        every { stubPreferenceManager.source } returns ServerSource.AUTOMATIC1111
        coEvery { stubStableDiffusionGenerationRepository.generateFromImage(any()) } throws stubException

        val actual = runCatching { useCase(mockImageToImagePayload.copy(batchCount = 1)) }

        assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given unsupported source, expected unsupported error`() = runTest {
        every { stubPreferenceManager.source } returns ServerSource.LOCAL_MICROSOFT_ONNX

        val actual = runCatching { useCase(mockImageToImagePayload.copy(batchCount = 1)) }

        assertTrue(actual.exceptionOrNull() is IllegalStateException)
        assertTrue(actual.exceptionOrNull()?.message?.startsWith("Img2Img not yet supported") == true)
    }
}
