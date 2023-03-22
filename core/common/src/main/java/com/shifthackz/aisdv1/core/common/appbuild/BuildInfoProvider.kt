package com.shifthackz.aisdv1.core.common.appbuild

interface BuildInfoProvider {
    val isDebug: Boolean
    val buildNumber: Int
    val version: BuildVersion
    val buildType: BuildType
}
