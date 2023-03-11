package com.shifthackz.aisdv1.core.common.appbuild

interface BuildInfoProvider {
    val buildNumber: Int
    val version: String
    val buildType: BuildType
}
