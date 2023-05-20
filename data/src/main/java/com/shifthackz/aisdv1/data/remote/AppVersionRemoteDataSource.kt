package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildVersion
import com.shifthackz.aisdv1.domain.datasource.AppVersionDataSource
import com.shifthackz.aisdv1.network.api.sdai.AppUpdateRestApi
import io.reactivex.rxjava3.core.Single

internal class AppVersionRemoteDataSource(
    private val buildInfoProvider: BuildInfoProvider,
    private val api: AppUpdateRestApi,
) : AppVersionDataSource.Remote {

    override fun get(): Single<BuildVersion> = api
        .fetchAppVersion()
        .map { response ->
            val version = when (buildInfoProvider.buildType) {
                com.shifthackz.aisdv1.core.common.appbuild.BuildType.FOSS -> response.fDroid
                com.shifthackz.aisdv1.core.common.appbuild.BuildType.GOOGLE_PLAY -> response.googlePlay
            }
            BuildVersion(version)
        }
}
