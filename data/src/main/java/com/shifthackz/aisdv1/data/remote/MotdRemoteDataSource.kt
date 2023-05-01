package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.toDomain
import com.shifthackz.aisdv1.domain.datasource.MotdDataSource
import com.shifthackz.aisdv1.network.api.sdai.MotdRestApi
import com.shifthackz.aisdv1.network.response.MotdResponse

class MotdRemoteDataSource(
    private val api: MotdRestApi,
) : MotdDataSource.Remote {

    override fun fetch() = api
        .fetchMotd()
        .map(MotdResponse::toDomain)
}
