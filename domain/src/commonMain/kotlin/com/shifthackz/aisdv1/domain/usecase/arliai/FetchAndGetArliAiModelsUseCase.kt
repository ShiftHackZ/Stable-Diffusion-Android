package com.shifthackz.aisdv1.domain.usecase.arliai

import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.ArliAiModelsRepository

/**
 * Refreshes and returns ArliAI checkpoints for active ArliAI sessions.
 *
 * @author Dmitriy Moroz
 */
interface FetchAndGetArliAiModelsUseCase {

    /**
     * Loads ArliAI models when ArliAI is the selected source.
     *
     * @return cached ArliAI checkpoints, or an empty list for other server sources.
     *
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(): List<StableDiffusionModel>
}

/**
 * Keeps the selected ArliAI checkpoint valid after model-list refreshes.
 *
 * If the saved model no longer exists, the first available checkpoint becomes the selected model.
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
     * Loads ArliAI models and updates the persisted selected checkpoint if needed.
     *
     * @return cached ArliAI checkpoints, or an empty list for other server sources.
     *
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
 * Returns the best checkpoint identifier available in ArliAI model metadata.
 *
 * @author Dmitriy Moroz
 */
internal val StableDiffusionModel.checkpointName: String
    get() = title.ifBlank { modelName }.ifBlank { filename }
