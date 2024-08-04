package com.shifthackz.aisdv1.core.model

import android.content.Context
import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

sealed class UiText {

    @Immutable
    data class Static(val value: String) : UiText()

    @Immutable
    class Resource(
        @StringRes val resId: Int,
        vararg val args: Any,
    ) : UiText()

    @Immutable
    class Concat(
        vararg val texts: Any,
        val separator: String = "",
    ) : UiText()

    fun asString(resources: Resources): String = when (this) {
        is Static -> value
        is Resource -> resources.getString(resId, *args.nestedArgs(resources))
        is Concat -> buildString {
            texts.map { it.mapArg(resources) + separator }.forEach(::append)
        }
    }

    fun asString(context: Context): String = asString(context.resources)

    override fun equals(other: Any?): Boolean = when (other) {
        is UiText -> when (other) {
            is Concat -> this is Concat && this.texts.contentEquals(other.texts)
            is Resource -> this is Resource && this.resId == other.resId
            is Static -> this is Static && this.value == other.value
        }

        else -> false
    }

    override fun hashCode(): Int = javaClass.hashCode()

    companion object {
        val empty: UiText = Static("")
    }
}

fun String.asUiText(): UiText.Static = UiText.Static(this)

fun Int.asUiText(): UiText.Resource = UiText.Resource(this)

fun Array<out Any>.nestedArgs(resources: Resources): Array<String> =
    map { it.mapArg(resources) }.toTypedArray()

fun Any.mapArg(resources: Resources): String = when (this) {
    is UiText.Resource -> asString(resources)
    is UiText.Concat -> asString(resources)
    is UiText.Static -> value
    else -> this.toString()
}

@Composable
fun UiText.asString(): String = when (this) {
    is UiText.Static -> value
    is UiText.Resource -> {
        stringResource(resId, *args.nestedArgs(LocalContext.current.resources))
    }

    is UiText.Concat -> buildString {
        texts
            .map { it.mapArg(LocalContext.current.resources) + separator }
            .forEach(::append)
    }
}
