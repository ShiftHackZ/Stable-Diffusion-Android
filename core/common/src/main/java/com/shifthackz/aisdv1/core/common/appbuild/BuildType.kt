package com.shifthackz.aisdv1.core.common.appbuild

enum class BuildType {
    FOSS,
    GOOGLE_PLAY;

    companion object {
        fun parse(value: String) = values().firstOrNull { "$it" == value } ?: GOOGLE_PLAY
    }
}
