package com.shifthackz.aisdv1.domain.usecase.swarmmodel

import com.shifthackz.aisdv1.domain.entity.SwarmUiModel
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.SwarmUiModelsRepository

interface FetchAndGetSwarmUiModelsUseCase {

    suspend operator fun invoke(): List<SwarmUiModel>
}

internal class FetchAndGetSwarmUiModelsUseCaseImpl(
    private val preferenceManager: PreferenceManager,
    private val repository: SwarmUiModelsRepository,
) : FetchAndGetSwarmUiModelsUseCase {

    override suspend fun invoke(): List<SwarmUiModel> {
        val models = repository.fetchAndGetModels()
        if (!models.map(SwarmUiModel::name).contains(preferenceManager.swarmUiModel)) {
            preferenceManager.swarmUiModel = models.firstOrNull()?.name ?: ""
        }
        return models
    }
}
