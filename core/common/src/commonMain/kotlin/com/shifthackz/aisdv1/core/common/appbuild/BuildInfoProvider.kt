package com.shifthackz.aisdv1.core.common.appbuild

import com.shifthackz.aisdv1.core.common.platform.Platform

/**
 * Runtime build metadata shared by domain and presentation code.
 *
 * Provider filtering relies on both the distribution flavor and the current
 * platform, so this abstraction is intentionally available outside platform UI
 * modules.
 */
interface BuildInfoProvider {

    val isDebug: Boolean

    val buildNumber: Int

    val version: BuildVersion

    val type: BuildType

    val platform: Platform

    companion object {
        val stub = object : BuildInfoProvider {
            override val isDebug: Boolean = true
            override val buildNumber: Int = 0
            override val version: BuildVersion = BuildVersion()
            override val type: BuildType = BuildType.FOSS
            override val platform: Platform = Platform.ANDROID

            override fun toString(): String = displayString()
        }
    }
}
