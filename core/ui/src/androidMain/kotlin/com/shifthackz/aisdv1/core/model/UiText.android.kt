package com.shifthackz.aisdv1.core.model

import android.content.Context
import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

/**
 * Executes the `asString` step in the SDAI core UI layer.
 *
 * @param resources resources value consumed by the API.
 * @author Dmitriy Moroz
 */
fun UiText.asString(resources: Resources): String = when (this) {
    is UiText.Static -> value
    is UiText.Resource -> resources.getString(resId, *args.nestedArgs(resources))
    is UiText.Concat -> buildString {
        texts
            .map { it.mapArg(resources) + separator }
            .forEach(::append)
    }
}

/**
 * Executes the `asString` step in the SDAI core UI layer.
 *
 * @param context Android context used by the operation.
 * @author Dmitriy Moroz
 */
fun UiText.asString(context: Context): String = asString(context.resources)

/**
 * Executes the `nestedArgs` step in the SDAI core UI layer.
 *
 * @param resources resources value consumed by the API.
 * @author Dmitriy Moroz
 */
fun Array<out Any>.nestedArgs(resources: Resources) =
    map { it.mapArg(resources) }.toTypedArray()

/**
 * Converts SDAI data with `mapArg`.
 *
 * @param resources resources value consumed by the API.
 * @author Dmitriy Moroz
 */
fun Any.mapArg(resources: Resources): String = when (this) {
    is UiText -> asString(resources)
    else -> toString()
}

/**
 * Renders the `resolveUiTextResource` UI for the SDAI presentation layer.
 *
 * @param resId res id value consumed by the API.
 * @param args args value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
internal actual fun resolveUiTextResource(
    resId: Int,
    args: Array<out Any>,
): String = stringResource(resId, *args.nestedArgs(LocalContext.current.resources))
