package com.shifthackz.aisdv1.core.common.appbuild

fun BuildInfoProvider.displayString(): String = buildString {
    append("$version")
    if (isDebug) append("-dev")
    append(" ($buildNumber)")
    when (type) {
        BuildType.FULL -> append(" FULL")
        BuildType.FOSS -> append(" FOSS")
        BuildType.PLAY -> Unit
    }
}

