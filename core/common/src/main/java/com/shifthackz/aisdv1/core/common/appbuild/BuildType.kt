package com.shifthackz.aisdv1.core.common.appbuild

enum class BuildType {
    FOSS,
    PLAY;

    companion object {
        fun fromBuildConfig(input: String) = when (input) {
            "FOSS" -> FOSS
            else -> PLAY
        }
    }
}
