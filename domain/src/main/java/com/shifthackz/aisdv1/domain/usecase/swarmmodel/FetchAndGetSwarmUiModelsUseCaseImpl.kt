package com.shifthackz.aisdv1.domain.usecase.swarmmodel

import com.shifthackz.aisdv1.domain.entity.SwarmUiModel
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.SwarmUiModelsRepository
import io.reactivex.rxjava3.core.Single

internal class FetchAndGetSwarmUiModelsUseCaseImpl(
    private val preferenceManager: PreferenceManager,
    private val repository: SwarmUiModelsRepository,
) : FetchAndGetSwarmUiModelsUseCase {

    override fun invoke(): Single<List<Pair<SwarmUiModel, Boolean>>> = repository
        .fetchAndGetModels()
        .map { models ->
            if (!models.map(SwarmUiModel::name).contains(preferenceManager.swarmModel)) {
                preferenceManager.swarmModel = models.firstOrNull()?.name ?: ""
            }
            models.map { model ->
                model to (preferenceManager.swarmModel == model.name)
            }
        }
}
