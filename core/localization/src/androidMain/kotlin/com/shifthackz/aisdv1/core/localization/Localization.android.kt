package com.shifthackz.aisdv1.core.localization

import java.util.Locale

/**
 * Executes the `countryFlagDrawableResId` step in the SDAI localization layer.
 *
 * @param code code value consumed by the API.
 * @author Dmitriy Moroz
 */
internal actual fun countryFlagDrawableResId(code: String): Int? = when (code) {
    "en" -> R.drawable.gb
    "uk" -> R.drawable.ua
    "tr" -> R.drawable.tr
    "ru" -> R.drawable.ru
    "zh" -> R.drawable.cn
    else -> null
}

/**
 * Executes the `platformLanguageCode` step in the SDAI localization layer.
 *
 * @author Dmitriy Moroz
 */
internal actual fun platformLanguageCode(): String? = Locale.getDefault().language
