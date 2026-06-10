package com.shifthackz.aisdv1.core.localization

import platform.Foundation.NSUserDefaults

/**
 * Executes the `countryFlagDrawableResId` step in the SDAI localization layer.
 *
 * @param code code value consumed by the API.
 * @return Result produced by `countryFlagDrawableResId`.
 * @author Dmitriy Moroz
 */
internal actual fun countryFlagDrawableResId(code: String): Int? = null

/**
 * Executes the `platformLanguageCode` step in the SDAI localization layer.
 *
 * @return Result produced by `platformLanguageCode`.
 * @author Dmitriy Moroz
 */
internal actual fun platformLanguageCode(): String? =
    NSUserDefaults.standardUserDefaults
        .stringArrayForKey("AppleLanguages")
        ?.firstOrNull() as? String
