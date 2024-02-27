package com.shifthackz.aisdv1.domain.usecase.caching

import com.shifthackz.aisdv1.domain.repository.ServerConfigurationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionEmbeddingsRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionHyperNetworksRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionLorasRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionModelsRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionSamplersRepository
import io.reactivex.rxjava3.core.Completable

internal class DataPreLoaderUseCaseImpl(
    private val serverConfigurationRepository: ServerConfigurationRepository,
    private val sdModelsRepository: StableDiffusionModelsRepository,
    private val sdSamplersRepository: StableDiffusionSamplersRepository,
    private val sdLorasRepository: StableDiffusionLorasRepository,
    private val sdHyperNetworksRepository: StableDiffusionHyperNetworksRepository,
    private val sdEmbeddingsRepository: StableDiffusionEmbeddingsRepository,
) : DataPreLoaderUseCase {

    override operator fun invoke() = serverConfigurationRepository
        .fetchConfiguration()
        .andThen(sdModelsRepository.fetchModels())
        .andThen(sdSamplersRepository.fetchSamplers())
        .andThen(sdLorasRepository.fetchLoras())
        .andThen(sdHyperNetworksRepository.fetchHyperNetworks())
        .andThen(sdEmbeddingsRepository.fetchEmbeddings())
}
