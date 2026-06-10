package com.shifthackz.aisdv1.core.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

sealed class UiText {

    @Immutable
    data class Static(val value: String) : UiText()

    @Immutable
    class Resource(
        val resId: Int,
        vararg val args: Any,
    ) : UiText()

    @Immutable
    class Concat(
        vararg val texts: Any,
        val separator: String = "",
    ) : UiText()

    override fun equals(other: Any?): Boolean = when (other) {
        is UiText -> when (other) {
            is Concat -> this is Concat && this.texts.contentEquals(other.texts)
            is Resource -> this is Resource && this.resId == other.resId
            is Static -> this is Static && this.value == other.value
        }
        else -> false
    }

    override fun hashCode(): Int = when (this) {
        is Concat -> texts.contentHashCode()
        is Resource -> resId
        is Static -> value.hashCode()
    }

    companion object {
        val empty: UiText = Static("")
    }
}

fun String.asUiText(): UiText.Static = UiText.Static(this)

fun Int.asUiText(): UiText.Resource = UiText.Resource(this)

@Composable
fun UiText.asString(): String = when (this) {
    is UiText.Static -> value
    is UiText.Resource -> resolveUiTextResource(resId, args)
    is UiText.Concat -> buildString {
        texts
            .map { it.mapArg() + separator }
            .forEach(::append)
    }
}

@Composable
private fun Any.mapArg(): String = when (this) {
    is UiText -> asString()
    else -> toString()
}

@Composable
internal expect fun resolveUiTextResource(
    resId: Int,
    args: Array<out Any>,
): String
