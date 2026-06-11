package com.shifthackz.aisdv1.network.di

import platform.Foundation.NSProcessInfo

internal actual fun stableDiffusionAppApiUrlOverride(): String? =
    NSProcessInfo.processInfo.environment[KEY_STABLE_DIFFUSION_APP_API_URL] as? String

private const val KEY_STABLE_DIFFUSION_APP_API_URL = "SDAI_APP_API_URL"
