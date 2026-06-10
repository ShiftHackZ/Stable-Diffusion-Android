package com.shifthackz.aisdv1.presentation.modal.tag

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.common.math.roundTo
import com.shifthackz.aisdv1.presentation.model.ExtraType
import com.shifthackz.aisdv1.core.mvi.MviState

/**
 * Carries `EditTagState` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class EditTagState(
    /**
     * Exposes the `prompt` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val prompt: String = "",
    /**
     * Exposes the `negativePrompt` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val negativePrompt: String = "",
    /**
     * Exposes the `originalTag` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val originalTag: String = "",
    /**
     * Exposes the `currentTag` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val currentTag: String = "",
    /**
     * Exposes the `extraType` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val extraType: ExtraType? = null,
    /**
     * Exposes the `isNegative` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val isNegative: Boolean = false,
) : MviState {

    val extraOriginalValue: Double?
        get() {
            if (extraType == null) return null
            return originalTag.getExtraDoubleValue()
        }

    val currentValue: Double?
        get() {
            if (extraType == null) return null
            return currentTag.getExtraDoubleValue()
        }
}

/**
 * Loads SDAI data through `getExtraDoubleValue`.
 *
 * @return Result produced by `getExtraDoubleValue`.
 * @author Dmitriy Moroz
 */
private fun String?.getExtraDoubleValue(): Double? = this
    ?.replace("<", "")
    ?.replace(">", "")
    ?.split(":")
    ?.lastOrNull()
    ?.toDoubleOrNull()

/**
 * Executes the `replaceExtraValue` step in the SDAI presentation layer.
 *
 * @param value value value consumed by the API.
 * @return Result produced by `replaceExtraValue`.
 * @author Dmitriy Moroz
 */
fun String.replaceExtraValue(value: Double): String {
    val parts = this.replace("<", "")
        .replace(">", "")
        .split(":")
    if (parts.size != 3) return this
    val result = buildString {
        append("<")
        append(parts.take(2).joinToString(":"))
        append(":${value.roundTo(2)}")
        append(">")
    }
    return result
        .replace(".0", "")
        .replace(",0", "")
}
