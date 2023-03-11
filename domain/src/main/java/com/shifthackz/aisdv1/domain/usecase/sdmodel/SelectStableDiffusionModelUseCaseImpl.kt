package com.shifthackz.aisdv1.domain.usecase.sdmodel

import com.shifthackz.aisdv1.domain.repository.ServerConfigurationRepository
import io.reactivex.rxjava3.core.Completable

internal class SelectStableDiffusionModelUseCaseImpl(
    private val serverConfigurationRepository: ServerConfigurationRepository,
) : SelectStableDiffusionModelUseCase {

    override operator fun invoke(modelName: String): Completable = serverConfigurationRepository
        .getConfiguration()
        .map { config -> config.copy(sdModelCheckpoint = modelName) }
        .flatMapCompletable(serverConfigurationRepository::updateConfiguration)
        .andThen(serverConfigurationRepository.fetchConfiguration())
}
