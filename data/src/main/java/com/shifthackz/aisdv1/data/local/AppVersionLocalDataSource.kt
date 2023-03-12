package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.domain.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.domain.datasource.AppVersionDataSource
import com.shifthackz.aisdv1.domain.entity.AppVersion
import io.reactivex.rxjava3.core.Single

internal class AppVersionLocalDataSource(
    private val buildInfoProvider: BuildInfoProvider,
) : AppVersionDataSource.Local {

    override fun get(): Single<AppVersion> = Single.fromCallable(buildInfoProvider::version)
}
