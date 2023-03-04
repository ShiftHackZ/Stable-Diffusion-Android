package com.shifthackz.aisdv1.domain.interactor

import com.shifthackz.aisdv1.domain.entity.StableDiffusionModelDomain
import com.shifthackz.aisdv1.domain.repository.ServerConfigurationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionModelsRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class StableDiffusionModelSelectionInteractorImpl(
    private val serverConfigurationRepository: ServerConfigurationRepository,
    private val sdModelsRepository: StableDiffusionModelsRepository,
) : StableDiffusionModelSelectionInteractor {

    override fun getData(): Single<List<Pair<StableDiffusionModelDomain, Boolean>>> =
        serverConfigurationRepository
            .fetchAndGetConfiguration()
            .flatMap { config ->
                sdModelsRepository
                    .fetchAndGetModels()
                    .map { sdModels -> config to sdModels }
            }
            .map { (config, sdModels) ->
                val cc = sdModels.map { model ->
                    model to (config.sdModelCheckpoint == model.title)
                }
                println("DBG0 - $cc")
                cc
            }

    override fun selectModelByName(modelName: String): Completable = serverConfigurationRepository
        .getConfiguration()
        .map { config -> config.copy(sdModelCheckpoint = modelName) }
        .flatMapCompletable(serverConfigurationRepository::updateConfiguration)
        .andThen(serverConfigurationRepository.fetchConfiguration())
}
