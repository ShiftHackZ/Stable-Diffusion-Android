package com.shifthackz.aisdv1.presentation.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Path

@Immutable
data class InPaintModel(
    val paths: List<Pair<Path, Int>> = emptyList(),
    val maskBlur: Int = 8,
    val maskMode: MaskMode = MaskMode.InPaintMasked,
    val maskContent: MaskContent = MaskContent.Fill,
    val inPaintArea: Area = Area.OnlyMasked,
    val onlyMaskedPaddingPx: Int = 0,
) {
    enum class MaskMode {
        InPaintMasked,
        InPaintNotMasked;
    }

    enum class MaskContent {
        Fill,
        Original,
        LatentNoise,
        LatentNothing;
    }

    enum class Area {
        WholePicture,
        OnlyMasked;
    }
}
