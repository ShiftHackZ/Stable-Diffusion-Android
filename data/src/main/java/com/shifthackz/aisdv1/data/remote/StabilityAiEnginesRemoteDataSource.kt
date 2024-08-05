package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapRawToCheckpointDomain
import com.shifthackz.aisdv1.domain.datasource.StabilityAiEnginesDataSource
import com.shifthackz.aisdv1.domain.entity.StabilityAiEngine
import com.shifthackz.aisdv1.network.api.stabilityai.StabilityAiApi
import com.shifthackz.aisdv1.network.model.StabilityAiEngineRaw
import io.reactivex.rxjava3.core.Single

internal class StabilityAiEnginesRemoteDataSource(
    private val api: StabilityAiApi,
) : StabilityAiEnginesDataSource.Remote {

    override fun fetch(): Single<List<StabilityAiEngine>> = api
        .fetchEngines()
        .map(List<StabilityAiEngineRaw>::mapRawToCheckpointDomain)
}
