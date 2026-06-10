package com.shifthackz.aisdv1.domain.usecase.sdmodel

import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.ServerConfigurationRepository

/**
 * Implements `SelectStableDiffusionModelUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class SelectStableDiffusionModelUseCaseImpl(
    /**
     * Exposes the `serverConfigurationRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val serverConfigurationRepository: ServerConfigurationRepository,
    /**
     * Exposes the `preferenceManager` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
) : SelectStableDiffusionModelUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param modelName model name value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend operator fun invoke(modelName: String) {
        val config = serverConfigurationRepository.getConfiguration()
        preferenceManager.sdModel = modelName
        serverConfigurationRepository.updateConfiguration(config.copy(sdModelCheckpoint = modelName))
        serverConfigurationRepository.fetchConfiguration()
    }
}
