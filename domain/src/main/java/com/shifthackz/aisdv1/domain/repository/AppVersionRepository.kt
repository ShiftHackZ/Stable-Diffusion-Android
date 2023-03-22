package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.core.common.appbuild.BuildVersion
import io.reactivex.rxjava3.core.Single

interface AppVersionRepository {
    fun getActualVersion(): Single<BuildVersion>
    fun getLocalVersion(): Single<BuildVersion>
}
