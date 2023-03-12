package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AppVersion
import io.reactivex.rxjava3.core.Single

sealed interface AppVersionDataSource {

    interface Local : AppVersionDataSource {
        fun get(): Single<AppVersion>
    }

    interface Remote : AppVersionDataSource {
        fun get(): Single<AppVersion>
    }
}
