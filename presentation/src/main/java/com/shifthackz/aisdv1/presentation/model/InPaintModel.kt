package com.shifthackz.aisdv1.presentation.model

import android.graphics.Bitmap
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Path

@Immutable
data class InPaintModel(
    val paths: List<Pair<Path, Int>> = emptyList(),
    val bitmap: Bitmap? = null,
    val base64: String = "",
    val maskBlur: Int = 4,
    val maskMode: MaskMode = MaskMode.InPaintMasked,
    val maskContent: MaskContent = MaskContent.Original,
    val inPaintArea: Area = Area.WholePicture,
    val onlyMaskedPaddingPx: Int = 32,
) {

    enum class MaskMode(val inverse: Int) {
        InPaintMasked(0),
        InPaintNotMasked(1);
    }

    enum class MaskContent(val fill: Int) {
        Fill(0),
        Original(1),
        LatentNoise(2),
        LatentNothing(3);
    }

    enum class Area(val fullRes: Boolean) {
        WholePicture(true),
        OnlyMasked(false);
    }
}
