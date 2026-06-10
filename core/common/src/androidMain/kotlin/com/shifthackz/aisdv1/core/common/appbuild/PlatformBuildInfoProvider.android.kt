package com.shifthackz.aisdv1.core.common.appbuild

/**
 * Creates the SDAI value produced by `createPlatformBuildInfoProvider`.
 *
 * @return Result produced by `createPlatformBuildInfoProvider`.
 * @author Dmitriy Moroz
 */
actual fun createPlatformBuildInfoProvider(): BuildInfoProvider = BuildInfoProvider.stub
