package com.shifthackz.aisdv1.domain.usecase.swarmmodel

import com.shifthackz.aisdv1.domain.entity.SwarmUiModel
import io.reactivex.rxjava3.core.Single

interface FetchAndGetSwarmUiModelsUseCase {
    operator fun invoke(): Single<List<SwarmUiModel>>
}
