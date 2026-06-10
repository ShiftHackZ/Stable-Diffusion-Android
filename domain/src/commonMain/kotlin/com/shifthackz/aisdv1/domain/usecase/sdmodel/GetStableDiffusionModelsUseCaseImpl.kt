package com.shifthackz.aisdv1.domain.usecase.sdmodel

import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.domain.repository.ServerConfigurationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionModelsRepository

/**
 * Implements `GetStableDiffusionModelsUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
class GetStableDiffusionModelsUseCaseImpl(
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
) : GetStableDiffusionModelsUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override suspend operator fun invoke(): List<Pair<StableDiffusionModel, Boolean>> {
        val config = serverConfigurationRepository.fetchAndGetConfiguration()
        val sdModels = sdModelsRepository.fetchAndGetModels()
        return sdModels.map { model ->
            model to (config.sdModelCheckpoint == model.title)
        }
    }
}
