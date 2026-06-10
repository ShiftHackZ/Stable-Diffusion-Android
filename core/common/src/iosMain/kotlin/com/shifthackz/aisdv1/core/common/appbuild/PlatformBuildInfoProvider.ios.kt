package com.shifthackz.aisdv1.core.common.appbuild

import platform.Foundation.NSBundle

/**
 * Creates the SDAI value produced by `createPlatformBuildInfoProvider`.
 *
 * @return Result produced by `createPlatformBuildInfoProvider`.
 * @author Dmitriy Moroz
 */
actual fun createPlatformBuildInfoProvider(): BuildInfoProvider = object : BuildInfoProvider {
    private val bundle = NSBundle.mainBundle

    override val isDebug: Boolean = false
    override val buildNumber: Int = bundle
        .objectForInfoDictionaryKey("CFBundleVersion")
        .toString()
        .toIntOrNull()
        ?: 0
    override val version: BuildVersion = BuildVersion(
        bundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String,
    )
    override val type: BuildType = BuildType.PLAY

    override fun toString(): String = displayString()
}

