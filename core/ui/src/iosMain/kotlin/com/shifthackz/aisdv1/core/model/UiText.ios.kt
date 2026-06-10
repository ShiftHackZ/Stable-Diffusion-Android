package com.shifthackz.aisdv1.core.model

import androidx.compose.runtime.Composable

/**
 * Renders the `resolveUiTextResource` UI for the SDAI presentation layer.
 *
 * @param resId res id value consumed by the API.
 * @param args args value consumed by the API.
 * @return Result produced by `resolveUiTextResource`.
 * @author Dmitriy Moroz
 */
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
