package com.shifthackz.aisdv1.core.localization

object Localization {

    val entries = listOf(
        "en" to "English",
        "uk" to "Українська",
        "tr" to "Türkçe",
        "ru" to "Русский",
        "zh" to "中文简体",
    )

    fun getCountryFlagDrawableResId(code: String): Int? = when (code) {
        "en" -> R.drawable.gb
        "uk" -> R.drawable.ua
        "tr" -> R.drawable.tr
        "ru" -> R.drawable.ru
        "zh" -> R.drawable.cn
        else -> null
    }
}
