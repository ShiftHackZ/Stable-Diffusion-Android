package com.shifthackz.aisdv1.domain.usecase.sdmodel

import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.domain.repository.ServerConfigurationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionModelsRepository
import io.reactivex.rxjava3.core.Single

class GetStableDiffusionModelsUseCaseImpl(
    private val serverConfigurationRepository: ServerConfigurationRepository,
    private val sdModelsRepository: StableDiffusionModelsRepository,
) : GetStableDiffusionModelsUseCase {

    override operator fun invoke(): Single<List<Pair<StableDiffusionModel, Boolean>>> =
        serverConfigurationRepository
            .fetchAndGetConfiguration()
            .flatMap { config ->
                sdModelsRepository
                    .fetchAndGetModels()
                    .map { sdModels -> config to sdModels }
            }
            .map { (config, sdModels) ->
                sdModels.map { model ->
                    model to (config.sdModelCheckpoint == model.title)
                }
            }
}
