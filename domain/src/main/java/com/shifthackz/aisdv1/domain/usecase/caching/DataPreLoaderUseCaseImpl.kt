package com.shifthackz.aisdv1.domain.usecase.caching

import com.shifthackz.aisdv1.domain.repository.EmbeddingsRepository
import com.shifthackz.aisdv1.domain.repository.LorasRepository
import com.shifthackz.aisdv1.domain.repository.ServerConfigurationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionHyperNetworksRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionModelsRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionSamplersRepository

internal class DataPreLoaderUseCaseImpl(
    private val serverConfigurationRepository: ServerConfigurationRepository,
    private val sdModelsRepository: StableDiffusionModelsRepository,
    private val sdSamplersRepository: StableDiffusionSamplersRepository,
    private val sdLorasRepository: LorasRepository,
    private val sdHyperNetworksRepository: StableDiffusionHyperNetworksRepository,
    private val sdEmbeddingsRepository: EmbeddingsRepository,
) : DataPreLoaderUseCase {

    override operator fun invoke() = serverConfigurationRepository
        .fetchConfiguration()
        .andThen(sdModelsRepository.fetchModels())
        .andThen(sdSamplersRepository.fetchSamplers())
        .andThen(sdLorasRepository.fetchLoras())
        .andThen(sdHyperNetworksRepository.fetchHyperNetworks())
        .andThen(sdEmbeddingsRepository.fetchEmbeddings())
}
