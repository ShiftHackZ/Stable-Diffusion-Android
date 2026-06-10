package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapRawToDomain
import com.shifthackz.aisdv1.domain.datasource.SupportersRemoteDataSource
import com.shifthackz.aisdv1.domain.entity.Supporter
import com.shifthackz.aisdv1.network.api.sdai.SdaiAppApi

/**
 * Coordinates `KtorSupportersRemoteDataSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
class KtorSupportersRemoteDataSource(
    /**
     * Exposes the `api` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val api: SdaiAppApi,
) : SupportersRemoteDataSource {

    /**
     * Loads SDAI data through `fetch`.
     *
     * @return Result produced by `fetch`.
     * @author Dmitriy Moroz
     */
    override suspend fun fetch(): List<Supporter> = api
        .fetchSupporters()
        .mapRawToDomain()
}
