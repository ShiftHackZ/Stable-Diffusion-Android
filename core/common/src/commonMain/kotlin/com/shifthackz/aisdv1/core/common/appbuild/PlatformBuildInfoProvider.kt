package com.shifthackz.aisdv1.core.common.appbuild

/**
 * Creates the platform-specific build metadata provider.
 *
 * Each target fills the shared [BuildInfoProvider] contract from its native
 * build system so common code can make flavor and platform decisions without
 * reaching into Android or iOS APIs.
 */
expect fun createPlatformBuildInfoProvider(): BuildInfoProvider
