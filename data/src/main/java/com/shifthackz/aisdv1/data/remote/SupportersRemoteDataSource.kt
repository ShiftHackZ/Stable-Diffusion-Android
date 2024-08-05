package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapRawToDomain
import com.shifthackz.aisdv1.domain.datasource.SupportersDataSource
import com.shifthackz.aisdv1.domain.entity.Supporter
import com.shifthackz.aisdv1.network.api.sdai.DonateApi
import com.shifthackz.aisdv1.network.model.SupporterRaw
import io.reactivex.rxjava3.core.Single

internal class SupportersRemoteDataSource(
    private val api: DonateApi,
) : SupportersDataSource.Remote {

    override fun fetch(): Single<List<Supporter>> = api
        .fetchSupporters()
        .map(List<SupporterRaw>::mapRawToDomain)
}
