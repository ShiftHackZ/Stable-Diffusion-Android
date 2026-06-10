package com.shifthackz.aisdv1.core.model

import android.content.Context
import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

fun UiText.asString(resources: Resources): String = when (this) {
    is UiText.Static -> value
    is UiText.Resource -> resources.getString(resId, *args.nestedArgs(resources))
    is UiText.Concat -> buildString {
        texts
            .map { it.mapArg(resources) + separator }
            .forEach(::append)
    }
}

fun UiText.asString(context: Context): String = asString(context.resources)

fun Array<out Any>.nestedArgs(resources: Resources) =
    map { it.mapArg(resources) }.toTypedArray()

fun Any.mapArg(resources: Resources): String = when (this) {
    is UiText -> asString(resources)
    else -> toString()
}

@Composable
internal actual fun resolveUiTextResource(
    resId: Int,
    args: Array<out Any>,
): String = stringResource(resId, *args.nestedArgs(LocalContext.current.resources))
