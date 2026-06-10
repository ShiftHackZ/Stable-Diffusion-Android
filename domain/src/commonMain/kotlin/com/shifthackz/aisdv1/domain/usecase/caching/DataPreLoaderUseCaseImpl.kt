package com.shifthackz.aisdv1.domain.usecase.caching

import com.shifthackz.aisdv1.domain.repository.EmbeddingsRepository
import com.shifthackz.aisdv1.domain.repository.LorasRepository
import com.shifthackz.aisdv1.domain.repository.ServerConfigurationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionHyperNetworksRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionModelsRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionSamplersRepository

/**
 * Implements `DataPreLoaderUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class DataPreLoaderUseCaseImpl(
    /**
     * Exposes the `serverConfigurationRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val serverConfigurationRepository: ServerConfigurationRepository,
    /**
     * Exposes the `sdModelsRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val sdModelsRepository: StableDiffusionModelsRepository,
    /**
     * Exposes the `sdSamplersRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val sdSamplersRepository: StableDiffusionSamplersRepository,
    /**
     * Exposes the `sdLorasRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val sdLorasRepository: LorasRepository,
    /**
     * Exposes the `sdHyperNetworksRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val sdHyperNetworksRepository: StableDiffusionHyperNetworksRepository,
    /**
     * Exposes the `sdEmbeddingsRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val sdEmbeddingsRepository: EmbeddingsRepository,
) : DataPreLoaderUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override suspend operator fun invoke() {
        serverConfigurationRepository.fetchConfiguration()
        sdModelsRepository.fetchModels()
        sdSamplersRepository.fetchSamplers()
        sdLorasRepository.fetchLoras()
        sdHyperNetworksRepository.fetchHyperNetworks()
        sdEmbeddingsRepository.fetchEmbeddings()
    }
}
