package com.shifthackz.aisdv1.presentation.model

import androidx.compose.runtime.Immutable

/**
 * Carries `PromptTagEditRequest` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class PromptTagEditRequest(
    /**
     * Exposes the `prompt` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val prompt: String,
    /**
     * Exposes the `negativePrompt` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val negativePrompt: String,
    /**
     * Exposes the `tag` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val tag: String,
    /**
     * Exposes the `isNegative` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val isNegative: Boolean,
)
