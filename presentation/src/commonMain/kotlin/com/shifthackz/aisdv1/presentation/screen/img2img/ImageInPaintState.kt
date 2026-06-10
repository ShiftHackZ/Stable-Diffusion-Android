package com.shifthackz.aisdv1.presentation.screen.img2img

import androidx.compose.runtime.Immutable

@Immutable
data class ImageInPaintState(
    val strokes: List<InPaintStroke> = emptyList(),
    val brushSize: Int = DEFAULT_IN_PAINT_BRUSH_SIZE,
    val maskBlur: Int = DEFAULT_IN_PAINT_MASK_BLUR,
    val maskMode: MaskMode = MaskMode.InPaintMasked,
    val maskContent: MaskContent = MaskContent.Original,
    val area: Area = Area.WholePicture,
    val onlyMaskedPaddingPx: Int = DEFAULT_IN_PAINT_MASK_PADDING,
) {
    val hasMask: Boolean
        get() = strokes.isNotEmpty()

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

@Immutable
data class InPaintStroke(
    val points: List<InPaintPoint>,
    val brushSize: Int,
    val canvasWidth: Int,
    val canvasHeight: Int,
)

@Immutable
data class InPaintPoint(
    val x: Float,
    val y: Float,
)

internal const val IN_PAINT_BRUSH_SIZE_MIN = 1
internal const val IN_PAINT_BRUSH_SIZE_MAX = 60
internal const val IN_PAINT_MASK_BLUR_MIN = 1
internal const val IN_PAINT_MASK_BLUR_MAX = 64
internal const val IN_PAINT_MASK_PADDING_MIN = 0
internal const val IN_PAINT_MASK_PADDING_MAX = 256
internal const val DEFAULT_IN_PAINT_BRUSH_SIZE = 16
internal const val DEFAULT_IN_PAINT_MASK_BLUR = 4
internal const val DEFAULT_IN_PAINT_MASK_PADDING = 32
