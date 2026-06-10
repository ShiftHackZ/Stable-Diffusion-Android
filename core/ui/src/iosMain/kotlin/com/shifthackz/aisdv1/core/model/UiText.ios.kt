package com.shifthackz.aisdv1.core.model

import androidx.compose.runtime.Composable

@Composable
internal actual fun resolveUiTextResource(
    resId: Int,
    args: Array<out Any>,
): String = buildString {
    append(resId)
    if (args.isNotEmpty()) {
        append(": ")
        append(args.joinToString())
    }
}
