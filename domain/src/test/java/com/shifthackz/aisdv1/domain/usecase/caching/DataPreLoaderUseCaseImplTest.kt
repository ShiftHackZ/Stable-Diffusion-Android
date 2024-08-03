package com.shifthackz.aisdv1.domain.usecase.caching

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.repository.ServerConfigurationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionEmbeddingsRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionHyperNetworksRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionLorasRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionModelsRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionSamplersRepository
import io.reactivex.rxjava3.core.Completable
import org.junit.Test

class DataPreLoaderUseCaseImplTest {

    private val stubServerConfigurationRepository = mock<ServerConfigurationRepository>()
    private val stubStableDiffusionModelsRepository = mock<StableDiffusionModelsRepository>()
    private val stubStableDiffusionSamplersRepository = mock<StableDiffusionSamplersRepository>()
    private val stubStableDiffusionLorasRepository = mock<StableDiffusionLorasRepository>()
    private val stubStableDiffusionHyperNetworksRepository = mock<StableDiffusionHyperNetworksRepository>()
    private val stubStableDiffusionEmbeddingsRepository = mock<StableDiffusionEmbeddingsRepository>()

    private val useCase = DataPreLoaderUseCaseImpl(
        serverConfigurationRepository = stubServerConfigurationRepository,
        sdModelsRepository = stubStableDiffusionModelsRepository,
        sdSamplersRepository = stubStableDiffusionSamplersRepository,
        sdLorasRepository = stubStableDiffusionLorasRepository,
        sdHyperNetworksRepository = stubStableDiffusionHyperNetworksRepository,
        sdEmbeddingsRepository = stubStableDiffusionEmbeddingsRepository,
    )

    @Test
    fun `given all data fetched successfully, expected complete value`() {
        whenever(stubServerConfigurationRepository.fetchConfiguration())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionModelsRepository.fetchModels())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionSamplersRepository.fetchSamplers())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionLorasRepository.fetchLoras())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionHyperNetworksRepository.fetchHyperNetworks())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionEmbeddingsRepository.fetchEmbeddings())
            .thenReturn(Completable.complete())

        useCase()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given configuration fetch failed, expected error value`() {
        val stubException = Throwable("Can not fetch configuration.")

        whenever(stubServerConfigurationRepository.fetchConfiguration())
            .thenReturn(Completable.error(stubException))

        whenever(stubStableDiffusionModelsRepository.fetchModels())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionSamplersRepository.fetchSamplers())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionLorasRepository.fetchLoras())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionHyperNetworksRepository.fetchHyperNetworks())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionEmbeddingsRepository.fetchEmbeddings())
            .thenReturn(Completable.complete())

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given models fetch failed, expected error value`() {
        val stubException = Throwable("Can not fetch models.")

        whenever(stubServerConfigurationRepository.fetchConfiguration())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionModelsRepository.fetchModels())
            .thenReturn(Completable.error(stubException))

        whenever(stubStableDiffusionSamplersRepository.fetchSamplers())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionLorasRepository.fetchLoras())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionHyperNetworksRepository.fetchHyperNetworks())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionEmbeddingsRepository.fetchEmbeddings())
            .thenReturn(Completable.complete())

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given samplers fetch failed, expected error value`() {
        val stubException = Throwable("Can not fetch samplers.")

        whenever(stubServerConfigurationRepository.fetchConfiguration())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionModelsRepository.fetchModels())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionSamplersRepository.fetchSamplers())
            .thenReturn(Completable.error(stubException))

        whenever(stubStableDiffusionLorasRepository.fetchLoras())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionHyperNetworksRepository.fetchHyperNetworks())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionEmbeddingsRepository.fetchEmbeddings())
            .thenReturn(Completable.complete())

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given loras fetch failed, expected error value`() {
        val stubException = Throwable("Can not fetch loras.")

        whenever(stubServerConfigurationRepository.fetchConfiguration())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionModelsRepository.fetchModels())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionSamplersRepository.fetchSamplers())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionLorasRepository.fetchLoras())
            .thenReturn(Completable.error(stubException))

        whenever(stubStableDiffusionHyperNetworksRepository.fetchHyperNetworks())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionEmbeddingsRepository.fetchEmbeddings())
            .thenReturn(Completable.complete())

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given hypernetworks fetch failed, expected error value`() {
        val stubException = Throwable("Can not fetch hypernetworks.")

        whenever(stubServerConfigurationRepository.fetchConfiguration())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionModelsRepository.fetchModels())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionSamplersRepository.fetchSamplers())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionLorasRepository.fetchLoras())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionHyperNetworksRepository.fetchHyperNetworks())
            .thenReturn(Completable.error(stubException))

        whenever(stubStableDiffusionEmbeddingsRepository.fetchEmbeddings())
            .thenReturn(Completable.complete())

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given embeddings fetch failed, expected error value`() {
        val stubException = Throwable("Can not fetch embeddings.")

        whenever(stubServerConfigurationRepository.fetchConfiguration())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionModelsRepository.fetchModels())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionSamplersRepository.fetchSamplers())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionLorasRepository.fetchLoras())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionHyperNetworksRepository.fetchHyperNetworks())
            .thenReturn(Completable.complete())

        whenever(stubStableDiffusionEmbeddingsRepository.fetchEmbeddings())
            .thenReturn(Completable.error(stubException))

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
