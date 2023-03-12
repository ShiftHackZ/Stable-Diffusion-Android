package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.AppVersion
import io.reactivex.rxjava3.core.Single

interface AppVersionRepository {
    fun getActualVersion(): Single<AppVersion>
    fun getLocalVersion(): Single<AppVersion>
}
