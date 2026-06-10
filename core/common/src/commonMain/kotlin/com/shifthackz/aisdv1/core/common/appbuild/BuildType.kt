package com.shifthackz.aisdv1.core.common.appbuild

enum class BuildType {
    FULL,
    FOSS,
    PLAY;

    companion object {
        fun fromBuildConfig(input: String) = when (input) {
            "FULL" -> FULL
            "FOSS" -> FOSS
            else -> PLAY
        }
    }
}
