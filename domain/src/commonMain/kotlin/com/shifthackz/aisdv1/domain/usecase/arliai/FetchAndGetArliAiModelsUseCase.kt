package com.shifthackz.aisdv1.domain.usecase.arliai

import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.ArliAiModelsRepository

/**
 * Defines the `FetchAndGetArliAiModelsUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface FetchAndGetArliAiModelsUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(): List<StableDiffusionModel>
}

/**
 * Implements `FetchAndGetArliAiModelsUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class FetchAndGetArliAiModelsUseCaseImpl(
    /**
     * Exposes the `preferenceManager` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
    /**
     * Exposes the `repository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val repository: ArliAiModelsRepository,
) : FetchAndGetArliAiModelsUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(): List<StableDiffusionModel> {
        if (preferenceManager.source != ServerSource.ARLI_AI) return emptyList()

        val models = repository.fetchAndGetModels()
        val modelNames = models.map(StableDiffusionModel::checkpointName)
        if (!modelNames.contains(preferenceManager.arliAiModel)) {
            preferenceManager.arliAiModel = modelNames.firstOrNull().orEmpty()
        }
        return models
    }
}

/**
 * Exposes the `StableDiffusionModel` value used by the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal val StableDiffusionModel.checkpointName: String
    get() = title.ifBlank { modelName }.ifBlank { filename }
