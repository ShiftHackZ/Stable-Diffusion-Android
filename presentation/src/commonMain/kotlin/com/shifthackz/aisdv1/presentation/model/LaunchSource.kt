package com.shifthackz.aisdv1.presentation.model

enum class LaunchSource {
    SPLASH,
    SETTINGS;

    companion object {
        fun fromKey(key: Int) = entries.firstOrNull { it.ordinal == key } ?: SPLASH
    }
}
