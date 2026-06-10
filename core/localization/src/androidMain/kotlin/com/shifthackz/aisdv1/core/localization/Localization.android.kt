package com.shifthackz.aisdv1.core.localization

import java.util.Locale

internal actual fun countryFlagDrawableResId(code: String): Int? = when (code) {
    "en" -> R.drawable.gb
    "uk" -> R.drawable.ua
    "tr" -> R.drawable.tr
    "ru" -> R.drawable.ru
    "zh" -> R.drawable.cn
    else -> null
}

internal actual fun platformLanguageCode(): String? = Locale.getDefault().language
