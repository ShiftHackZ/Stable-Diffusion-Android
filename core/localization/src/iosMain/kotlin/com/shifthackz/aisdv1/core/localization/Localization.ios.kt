package com.shifthackz.aisdv1.core.localization

import platform.Foundation.NSUserDefaults

internal actual fun countryFlagDrawableResId(code: String): Int? = null

internal actual fun platformLanguageCode(): String? =
    NSUserDefaults.standardUserDefaults
        .stringArrayForKey("AppleLanguages")
        ?.firstOrNull() as? String
