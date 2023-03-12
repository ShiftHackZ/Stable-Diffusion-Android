package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.domain.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.domain.appbuild.BuildType
import com.shifthackz.aisdv1.domain.datasource.AppVersionDataSource
import com.shifthackz.aisdv1.domain.entity.AppVersion
import com.shifthackz.aisdv1.network.api.StableDiffusionAppUpdateRestApi
import io.reactivex.rxjava3.core.Single

internal class AppVersionRemoteDataSource(
    private val buildInfoProvider: BuildInfoProvider,
    private val api: StableDiffusionAppUpdateRestApi,
) : AppVersionDataSource.Remote {

    override fun get(): Single<AppVersion> = api
        .fetchAppVersion()
        .map { response ->
            val version = when (buildInfoProvider.buildType) {
                BuildType.FOSS -> response.fDroid
                BuildType.GOOGLE_PLAY -> response.googlePlay
            }
            AppVersion(version)
        }
}
