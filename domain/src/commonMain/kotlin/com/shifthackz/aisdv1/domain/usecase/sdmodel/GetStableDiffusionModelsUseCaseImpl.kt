package com.shifthackz.aisdv1.domain.usecase.sdmodel

import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.domain.repository.ServerConfigurationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionModelsRepository

class GetStableDiffusionModelsUseCaseImpl(
    private val serverConfigurationRepository: ServerConfigurationRepository,
    private val sdModelsRepository: StableDiffusionModelsRepository,
) : GetStableDiffusionModelsUseCase {

    override suspend operator fun invoke(): List<Pair<StableDiffusionModel, Boolean>> {
        val config = serverConfigurationRepository.fetchAndGetConfiguration()
        val sdModels = sdModelsRepository.fetchAndGetModels()
        return sdModels.map { model ->
            model to (config.sdModelCheckpoint == model.title)
        }
    }
}
