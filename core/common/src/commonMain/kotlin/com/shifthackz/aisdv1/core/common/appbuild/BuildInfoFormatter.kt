package com.shifthackz.aisdv1.core.common.appbuild

/**
 * Executes the `displayString` step in the SDAI core common layer.
 *
 * @return Result produced by `displayString`.
 * @author Dmitriy Moroz
 */
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

