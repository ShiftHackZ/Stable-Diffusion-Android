package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.CoreMlGenerationRepository
import com.shifthackz.aisdv1.domain.repository.HordeGenerationRepository
import com.shifthackz.aisdv1.domain.repository.LocalDiffusionGenerationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionCppGenerationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertSame
import org.junit.Test

class InterruptGenerationUseCaseImplTest {

    private val stubException = RuntimeException("Can not interrupt generation.")
    private val stubStableDiffusionGenerationRepository = mockk<StableDiffusionGenerationRepository>()
    private val stubHordeGenerationRepository = mockk<HordeGenerationRepository>()
    private val stubLocalDiffusionGenerationRepository = mockk<LocalDiffusionGenerationRepository>()
    private val stubStableDiffusionCppGenerationRepository = mockk<StableDiffusionCppGenerationRepository>()
    private val stubCoreMlGenerationRepository = mockk<CoreMlGenerationRepository>()
    private val stubPreferenceManager = mockk<PreferenceManager>()

    private val useCase = InterruptGenerationUseCaseImpl(
        stableDiffusionGenerationRepository = stubStableDiffusionGenerationRepository,
        hordeGenerationRepository = stubHordeGenerationRepository,
        localDiffusionGenerationRepository = stubLocalDiffusionGenerationRepository,
        stableDiffusionCppGenerationRepository = stubStableDiffusionCppGenerationRepository,
        coreMlGenerationRepository = stubCoreMlGenerationRepository,
        preferenceManager = stubPreferenceManager,
    )

    @Test
    fun `given source is AUTOMATIC1111, expected stable diffusion interrupt`() = runTest {
        every { stubPreferenceManager.source } returns ServerSource.AUTOMATIC1111
        coEvery { stubStableDiffusionGenerationRepository.interruptGeneration() } returns Unit

        useCase()

        coVerify(exactly = 1) { stubStableDiffusionGenerationRepository.interruptGeneration() }
    }

    @Test
    fun `given source is HORDE, expected horde interrupt`() = runTest {
        every { stubPreferenceManager.source } returns ServerSource.HORDE
        coEvery { stubHordeGenerationRepository.interruptGeneration() } returns Unit

        useCase()

        coVerify(exactly = 1) { stubHordeGenerationRepository.interruptGeneration() }
    }

    @Test
    fun `given source is LOCAL_MICROSOFT_ONNX, expected local diffusion interrupt`() = runTest {
        every { stubPreferenceManager.source } returns ServerSource.LOCAL_MICROSOFT_ONNX
        coEvery { stubLocalDiffusionGenerationRepository.interruptGeneration() } returns Unit

        useCase()

        coVerify(exactly = 1) { stubLocalDiffusionGenerationRepository.interruptGeneration() }
    }

    @Test
    fun `given source is LOCAL_STABLE_DIFFUSION_CPP, expected stable diffusion cpp interrupt`() = runTest {
        every { stubPreferenceManager.source } returns ServerSource.LOCAL_STABLE_DIFFUSION_CPP
        coEvery { stubStableDiffusionCppGenerationRepository.interruptGeneration() } returns Unit

        useCase()

        coVerify(exactly = 1) { stubStableDiffusionCppGenerationRepository.interruptGeneration() }
    }

    @Test
    fun `given source is LOCAL_APPLE_CORE_ML, expected core ml interrupt`() = runTest {
        every { stubPreferenceManager.source } returns ServerSource.LOCAL_APPLE_CORE_ML
        coEvery { stubCoreMlGenerationRepository.interruptGeneration() } returns Unit

        useCase()

        coVerify(exactly = 1) { stubCoreMlGenerationRepository.interruptGeneration() }
    }

    @Test
    fun `given interrupt fails, expected error propagated`() = runTest {
        every { stubPreferenceManager.source } returns ServerSource.AUTOMATIC1111
        coEvery { stubStableDiffusionGenerationRepository.interruptGeneration() } throws stubException

        val actual = runCatching { useCase() }

        assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given source does not support interrupt, expected no-op`() = runTest {
        every { stubPreferenceManager.source } returns ServerSource.OPEN_AI

        useCase()

        coVerify(exactly = 0) { stubStableDiffusionGenerationRepository.interruptGeneration() }
        coVerify(exactly = 0) { stubHordeGenerationRepository.interruptGeneration() }
        coVerify(exactly = 0) { stubLocalDiffusionGenerationRepository.interruptGeneration() }
        coVerify(exactly = 0) { stubStableDiffusionCppGenerationRepository.interruptGeneration() }
        coVerify(exactly = 0) { stubCoreMlGenerationRepository.interruptGeneration() }
    }
}
