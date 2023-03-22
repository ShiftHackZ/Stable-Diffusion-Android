package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildVersion
import com.shifthackz.aisdv1.domain.datasource.AppVersionDataSource
import io.reactivex.rxjava3.core.Single

internal class AppVersionLocalDataSource(
    private val buildInfoProvider: BuildInfoProvider,
) : AppVersionDataSource.Local {

    override fun get(): Single<BuildVersion> = Single.fromCallable(buildInfoProvider::version)
}
