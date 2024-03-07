package com.shifthackz.aisdv1.presentation.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Path

@Immutable
data class InPaintModel(
    val paths: List<Pair<Path, Int>> = emptyList(),
)
