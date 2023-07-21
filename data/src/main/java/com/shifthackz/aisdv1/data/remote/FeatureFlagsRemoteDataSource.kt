package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapToDomain
import com.shifthackz.aisdv1.domain.datasource.FeatureFlagsDataSource
import com.shifthackz.aisdv1.domain.entity.FeatureFlags
import com.shifthackz.aisdv1.network.api.sdai.FeatureFlagsRestApi
import com.shifthackz.aisdv1.network.response.FeatureFlagsResponse
import io.reactivex.rxjava3.core.Single

internal class FeatureFlagsRemoteDataSource(
    private val api: FeatureFlagsRestApi,
) : FeatureFlagsDataSource.Remote {

    override fun fetch(): Single<FeatureFlags> = api
        .fetchConfig()
        .map(FeatureFlagsResponse::mapToDomain)
}
