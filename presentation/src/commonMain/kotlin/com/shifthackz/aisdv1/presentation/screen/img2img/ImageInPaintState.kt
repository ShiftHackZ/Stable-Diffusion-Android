package com.shifthackz.aisdv1.presentation.screen.img2img

import androidx.compose.runtime.Immutable

/**
 * Carries `ImageInPaintState` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class ImageInPaintState(
    /**
     * Exposes the `strokes` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val strokes: List<InPaintStroke> = emptyList(),
    /**
     * Exposes the `brushSize` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val brushSize: Int = DEFAULT_IN_PAINT_BRUSH_SIZE,
    /**
     * Exposes the `maskBlur` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val maskBlur: Int = DEFAULT_IN_PAINT_MASK_BLUR,
    /**
     * Exposes the `maskMode` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val maskMode: MaskMode = MaskMode.InPaintMasked,
    /**
     * Exposes the `maskContent` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val maskContent: MaskContent = MaskContent.Original,
    /**
     * Exposes the `area` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val area: Area = Area.WholePicture,
    /**
     * Exposes the `onlyMaskedPaddingPx` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
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

/**
 * Carries `InPaintStroke` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class InPaintStroke(
    /**
     * Exposes the `points` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val points: List<InPaintPoint>,
    /**
     * Exposes the `brushSize` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val brushSize: Int,
    /**
     * Exposes the `canvasWidth` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val canvasWidth: Int,
    /**
     * Exposes the `canvasHeight` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val canvasHeight: Int,
)

/**
 * Carries `InPaintPoint` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class InPaintPoint(
    /**
     * Exposes the `x` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val x: Float,
    /**
     * Exposes the `y` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val y: Float,
)

/**
 * Exposes the `IN_PAINT_BRUSH_SIZE_MIN` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val IN_PAINT_BRUSH_SIZE_MIN = 1
/**
 * Exposes the `IN_PAINT_BRUSH_SIZE_MAX` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val IN_PAINT_BRUSH_SIZE_MAX = 60
/**
 * Exposes the `IN_PAINT_MASK_BLUR_MIN` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val IN_PAINT_MASK_BLUR_MIN = 1
/**
 * Exposes the `IN_PAINT_MASK_BLUR_MAX` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val IN_PAINT_MASK_BLUR_MAX = 64
/**
 * Exposes the `IN_PAINT_MASK_PADDING_MIN` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val IN_PAINT_MASK_PADDING_MIN = 0
/**
 * Exposes the `IN_PAINT_MASK_PADDING_MAX` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val IN_PAINT_MASK_PADDING_MAX = 256
/**
 * Exposes the `DEFAULT_IN_PAINT_BRUSH_SIZE` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val DEFAULT_IN_PAINT_BRUSH_SIZE = 16
/**
 * Exposes the `DEFAULT_IN_PAINT_MASK_BLUR` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val DEFAULT_IN_PAINT_MASK_BLUR = 4
/**
 * Exposes the `DEFAULT_IN_PAINT_MASK_PADDING` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val DEFAULT_IN_PAINT_MASK_PADDING = 32
