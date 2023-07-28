package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.core.common.appbuild.BuildVersion
import com.shifthackz.aisdv1.domain.datasource.AppVersionDataSource
import com.shifthackz.aisdv1.network.api.sdai.AppUpdateRestApi
import com.shifthackz.aisdv1.network.response.AppVersionResponse
import io.reactivex.rxjava3.core.Single

internal class AppVersionRemoteDataSource(
    private val api: AppUpdateRestApi,
) : AppVersionDataSource.Remote {

    override fun get(): Single<BuildVersion> = api
        .fetchAppVersion()
        .map(AppVersionResponse::googlePlay)
        .map(::BuildVersion)
}
