package com.shifthackz.aisdv1.core.common.appbuild

interface BuildInfoProvider {
    val isDebug: Boolean
    val buildNumber: Int
    val version: BuildVersion
    val type: BuildType

    companion object {
        val stub = object : BuildInfoProvider {
            override val isDebug: Boolean = true
            override val buildNumber: Int = 0
            override val version: BuildVersion = BuildVersion()
            override val type: BuildType = BuildType.FOSS
        }
    }
}
