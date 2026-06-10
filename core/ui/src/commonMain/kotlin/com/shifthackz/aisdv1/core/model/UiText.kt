package com.shifthackz.aisdv1.core.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

/**
 * Coordinates `UiText` behavior in the SDAI core UI layer.
 *
 * @author Dmitriy Moroz
 */
sealed class UiText {

    /**
     * Carries `Static` data through the SDAI core UI layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    @Immutable
    data class Static(val value: String) : UiText()

    /**
     * Coordinates `Resource` behavior in the SDAI core UI layer.
     *
     * @author Dmitriy Moroz
     */
    @Immutable
    class Resource(
        /**
         * Exposes the `resId` value used by the SDAI core UI layer.
         *
         * @author Dmitriy Moroz
         */
        val resId: Int,
        vararg val args: Any,
    ) : UiText()

    /**
     * Coordinates `Concat` behavior in the SDAI core UI layer.
     *
     * @author Dmitriy Moroz
     */
    @Immutable
    class Concat(
        vararg val texts: Any,
        /**
         * Exposes the `separator` value used by the SDAI core UI layer.
         *
         * @author Dmitriy Moroz
         */
        val separator: String = "",
    ) : UiText()

    /**
     * Executes the `equals` step in the SDAI core UI layer.
     *
     * @param other other value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun equals(other: Any?): Boolean = when (other) {
        is UiText -> when (other) {
            is Concat -> this is Concat && this.texts.contentEquals(other.texts)
            is Resource -> this is Resource && this.resId == other.resId
            is Static -> this is Static && this.value == other.value
        }
        else -> false
    }

    /**
     * Executes the `hashCode` step in the SDAI core UI layer.
     *
     * @author Dmitriy Moroz
     */
    override fun hashCode(): Int = when (this) {
        is Concat -> texts.contentHashCode()
        is Resource -> resId
        is Static -> value.hashCode()
    }

    /**
     * Provides the `companion object` singleton used by the SDAI core UI layer.
     *
     * @author Dmitriy Moroz
     */
    companion object {
        /**
         * Exposes the `empty` value used by the SDAI core UI layer.
         *
         * @author Dmitriy Moroz
         */
        val empty: UiText = Static("")
    }
}

/**
 * Executes the `asUiText` step in the SDAI core UI layer.
 *
 * @author Dmitriy Moroz
 */
fun String.asUiText(): UiText.Static = UiText.Static(this)

/**
 * Executes the `asUiText` step in the SDAI core UI layer.
 *
 * @author Dmitriy Moroz
 */
fun Int.asUiText(): UiText.Resource = UiText.Resource(this)

/**
 * Renders the `asString` UI for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
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

/**
 * Renders the `mapArg` UI for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Composable
private fun Any.mapArg(): String = when (this) {
    is UiText -> asString()
    else -> toString()
}

/**
 * Renders the `resolveUiTextResource` UI for the SDAI presentation layer.
 *
 * @param resId res id value consumed by the API.
 * @param args args value consumed by the API.
 * @return Result produced by `resolveUiTextResource`.
 * @author Dmitriy Moroz
 */
@Composable
internal expect fun resolveUiTextResource(
    resId: Int,
    args: Array<out Any>,
): String
