package com.shifthackz.aisdv1.presentation.modal.tag

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.common.math.roundTo
import com.shifthackz.aisdv1.presentation.model.ExtraType
import com.shifthackz.android.core.mvi.MviState

@Immutable
data class EditTagState(
    val prompt: String = "",
    val negativePrompt: String = "",
    val originalTag: String = "",
    val currentTag: String = "",
    val extraType: ExtraType? = null,
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

private fun String?.getExtraDoubleValue(): Double? = this
    ?.replace("<", "")
    ?.replace(">", "")
    ?.split(":")
    ?.lastOrNull()
    ?.toDoubleOrNull()

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
