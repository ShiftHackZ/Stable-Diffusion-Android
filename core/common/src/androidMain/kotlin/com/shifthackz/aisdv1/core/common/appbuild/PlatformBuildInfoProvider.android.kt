package com.shifthackz.aisdv1.core.common.appbuild

/**
 * Supplies Android build metadata from the generated app-level provider.
 *
 * Core modules use a stub here so Android app variants can override the binding
 * with flavor-aware metadata during dependency injection.
 */
actual fun createPlatformBuildInfoProvider(): BuildInfoProvider = BuildInfoProvider.stub
