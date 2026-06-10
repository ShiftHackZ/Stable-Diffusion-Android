package com.shifthackz.aisdv1.domain.usecase.caching

import com.shifthackz.aisdv1.domain.repository.EmbeddingsRepository
import com.shifthackz.aisdv1.domain.repository.LorasRepository
import com.shifthackz.aisdv1.domain.repository.ServerConfigurationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionHyperNetworksRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionModelsRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionSamplersRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class DataPreLoaderUseCaseImplTest {

    private val stubServerConfigurationRepository = mockk<ServerConfigurationRepository>()
    private val stubStableDiffusionModelsRepository = mockk<StableDiffusionModelsRepository>()
    private val stubStableDiffusionSamplersRepository = mockk<StableDiffusionSamplersRepository>()
    private val stubLorasRepository = mockk<LorasRepository>()
    private val stubStableDiffusionHyperNetworksRepository = mockk<StableDiffusionHyperNetworksRepository>()
    private val stubEmbeddingsRepository = mockk<EmbeddingsRepository>()

    private val useCase = DataPreLoaderUseCaseImpl(
        serverConfigurationRepository = stubServerConfigurationRepository,
        sdModelsRepository = stubStableDiffusionModelsRepository,
        sdSamplersRepository = stubStableDiffusionSamplersRepository,
        sdLorasRepository = stubLorasRepository,
        sdHyperNetworksRepository = stubStableDiffusionHyperNetworksRepository,
        sdEmbeddingsRepository = stubEmbeddingsRepository,
    )

    @Test
    fun `given all data fetched successfully, expected complete value`() = runTest {
        stubSuccess()

        useCase()
    }

    @Test
    fun `given configuration fetch failed, expected error value`() = runTest {
        val stubException = Throwable("Can not fetch configuration.")
        stubSuccess()

        coEvery {
            stubServerConfigurationRepository.fetchConfiguration()
        } throws stubException

        assertUseCaseFails(stubException)
    }

    @Test
    fun `given models fetch failed, expected error value`() = runTest {
        val stubException = Throwable("Can not fetch models.")
        stubSuccess()

        coEvery {
            stubStableDiffusionModelsRepository.fetchModels()
        } throws stubException

        assertUseCaseFails(stubException)
    }

    @Test
    fun `given samplers fetch failed, expected error value`() = runTest {
        val stubException = Throwable("Can not fetch samplers.")
        stubSuccess()

        coEvery {
            stubStableDiffusionSamplersRepository.fetchSamplers()
        } throws stubException

        assertUseCaseFails(stubException)
    }

    @Test
    fun `given loras fetch failed, expected error value`() = runTest {
        val stubException = Throwable("Can not fetch loras.")
        stubSuccess()

        coEvery {
            stubLorasRepository.fetchLoras()
        } throws stubException

        assertUseCaseFails(stubException)
    }

    @Test
    fun `given hypernetworks fetch failed, expected error value`() = runTest {
        val stubException = Throwable("Can not fetch hypernetworks.")
        stubSuccess()

        coEvery {
            stubStableDiffusionHyperNetworksRepository.fetchHyperNetworks()
        } throws stubException

        assertUseCaseFails(stubException)
    }

    @Test
    fun `given embeddings fetch failed, expected error value`() = runTest {
        val stubException = Throwable("Can not fetch embeddings.")
        stubSuccess()

        coEvery {
            stubEmbeddingsRepository.fetchEmbeddings()
        } throws stubException

        assertUseCaseFails(stubException)
    }

    private fun stubSuccess() {
        coEvery {
            stubServerConfigurationRepository.fetchConfiguration()
        } returns Unit

        coEvery {
            stubStableDiffusionModelsRepository.fetchModels()
        } returns Unit

        coEvery {
            stubStableDiffusionSamplersRepository.fetchSamplers()
        } returns Unit

        coEvery {
            stubLorasRepository.fetchLoras()
        } returns Unit

        coEvery {
            stubStableDiffusionHyperNetworksRepository.fetchHyperNetworks()
        } returns Unit

        coEvery {
            stubEmbeddingsRepository.fetchEmbeddings()
        } returns Unit
    }

    private suspend fun assertUseCaseFails(expected: Throwable) {
        val actual = runCatching { useCase() }.exceptionOrNull()
        assertTrue(actual === expected || actual?.cause === expected)
    }
}
