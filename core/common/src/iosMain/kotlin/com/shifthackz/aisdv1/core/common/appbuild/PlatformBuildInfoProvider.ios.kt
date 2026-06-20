package com.shifthackz.aisdv1.core.common.appbuild

import com.shifthackz.aisdv1.core.common.platform.Platform
import platform.Foundation.NSBundle

/**
 * Reads iOS bundle metadata for the shared build info contract.
 *
 * The iOS target has no Android-style flavor split, so it reports the App Store
 * compatible flavor while still exposing [Platform.IOS] to common code.
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

    override val platform: Platform = Platform.IOS

    override fun toString(): String = displayString()
}
