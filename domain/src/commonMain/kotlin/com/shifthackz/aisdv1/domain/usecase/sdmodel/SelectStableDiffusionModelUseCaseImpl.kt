package com.shifthackz.aisdv1.domain.usecase.sdmodel

import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.ServerConfigurationRepository

internal class SelectStableDiffusionModelUseCaseImpl(
    private val serverConfigurationRepository: ServerConfigurationRepository,
    private val preferenceManager: PreferenceManager,
) : SelectStableDiffusionModelUseCase {

    override suspend operator fun invoke(modelName: String) {
        val config = serverConfigurationRepository.getConfiguration()
        preferenceManager.sdModel = modelName
        serverConfigurationRepository.updateConfiguration(config.copy(sdModelCheckpoint = modelName))
        serverConfigurationRepository.fetchConfiguration()
    }
}
