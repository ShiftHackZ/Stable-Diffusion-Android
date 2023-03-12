package com.shifthackz.aisdv1.domain.appbuild

import com.shifthackz.aisdv1.domain.entity.AppVersion

interface BuildInfoProvider {
    val isDebug: Boolean
    val buildNumber: Int
    val version: AppVersion
    val buildType: BuildType
}
