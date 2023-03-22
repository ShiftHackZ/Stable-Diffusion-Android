package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.core.common.appbuild.BuildVersion
import io.reactivex.rxjava3.core.Single

sealed interface AppVersionDataSource {

    interface Local : AppVersionDataSource {
        fun get(): Single<BuildVersion>
    }

    interface Remote : AppVersionDataSource {
        fun get(): Single<BuildVersion>
    }
}
