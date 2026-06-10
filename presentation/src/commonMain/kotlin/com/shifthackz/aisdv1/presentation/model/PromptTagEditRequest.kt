package com.shifthackz.aisdv1.presentation.model

import androidx.compose.runtime.Immutable

@Immutable
data class PromptTagEditRequest(
    val prompt: String,
    val negativePrompt: String,
    val tag: String,
    val isNegative: Boolean,
)
