@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.img2img

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.presentation.theme.sliderColors
import com.shifthackz.aisdv1.presentation.widget.scrollbar.verticalScrollbar
import kotlin.math.abs
import kotlin.math.roundToInt


/**
 * Renders the `BrushSizeSlider` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param size size value consumed by the API.
 * @param onValueChanged callback invoked by the component.
 * @author Dmitriy Moroz
 */
@Composable
internal fun BrushSizeSlider(
    modifier: Modifier = Modifier,
    size: Int,
    onValueChanged: (Int) -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(IN_PAINT_SLIDER_LEADING_WIDTH),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier.defaultMinSize(
                    minWidth = 30.dp,
                    minHeight = 30.dp,
                ),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(size.dp)
                        .background(Color.White)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape,
                        ),
                )
            }
        }

        Slider(
            modifier = Modifier.weight(1f),
            value = size.toFloat(),
            valueRange = IN_PAINT_BRUSH_SIZE_MIN.toFloat()..IN_PAINT_BRUSH_SIZE_MAX.toFloat(),
            steps = abs(IN_PAINT_BRUSH_SIZE_MIN - IN_PAINT_BRUSH_SIZE_MAX) - 1,
            colors = sliderColors.copy(
                inactiveTrackColor = MaterialTheme.colorScheme.background,
            ),
            onValueChange = { onValueChanged(it.roundToInt()) },
        )
    }
}

/**
 * Renders the `ImageInPaintParamsForm` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param model model value consumed by the API.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
internal fun ImageInPaintParamsForm(
    modifier: Modifier = Modifier,
    model: ImageInPaintState,
    processIntent: (ImageToImageIntent) -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxSize()
            .verticalScrollbar(scrollState)
            .verticalScroll(scrollState),
    ) {
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = Localization.string("hint_mask_blur", model.maskBlur),
        )
        Slider(
            value = model.maskBlur.toFloat(),
            valueRange = IN_PAINT_MASK_BLUR_MIN.toFloat()..IN_PAINT_MASK_BLUR_MAX.toFloat(),
            steps = abs(IN_PAINT_MASK_BLUR_MIN - IN_PAINT_MASK_BLUR_MAX) - 1,
            colors = sliderColors,
            onValueChange = {
                processIntent(ImageToImageIntent.UpdateInPaintMaskBlur(it.roundToInt()))
            },
        )

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = Localization.string("hint_mask_mode"),
        )
        ImageInPaintState.MaskMode.entries.forEach { maskMode ->
            SelectableRow(
                selected = model.maskMode == maskMode,
                text = when (maskMode) {
                    ImageInPaintState.MaskMode.InPaintMasked -> Localization.string("in_paint_mode_masked")
                    ImageInPaintState.MaskMode.InPaintNotMasked -> Localization.string("in_paint_mode_not_masked")
                },
                onClick = { processIntent(ImageToImageIntent.UpdateInPaintMaskMode(maskMode)) },
            )
        }

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = Localization.string("hint_mask_content"),
        )
        ImageInPaintState.MaskContent.entries.forEach { maskContent ->
            SelectableRow(
                selected = model.maskContent == maskContent,
                text = when (maskContent) {
                    ImageInPaintState.MaskContent.Fill -> Localization.string("in_paint_mask_content_fill")
                    ImageInPaintState.MaskContent.Original -> Localization.string("in_paint_mask_content_original")
                    ImageInPaintState.MaskContent.LatentNoise -> Localization.string("in_paint_mask_content_latent_noise")
                    ImageInPaintState.MaskContent.LatentNothing -> Localization.string("in_paint_mask_content_latent_nothing")
                },
                onClick = { processIntent(ImageToImageIntent.UpdateInPaintMaskContent(maskContent)) },
            )
        }

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = Localization.string("hint_in_paint_area"),
        )
        ImageInPaintState.Area.entries.forEach { area ->
            SelectableRow(
                selected = model.area == area,
                text = when (area) {
                    ImageInPaintState.Area.WholePicture -> Localization.string("in_paint_area_whole")
                    ImageInPaintState.Area.OnlyMasked -> Localization.string("in_paint_area_only_masked")
                },
                onClick = { processIntent(ImageToImageIntent.UpdateInPaintArea(area)) },
            )
        }

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = Localization.string("hint_only_masked_padding", model.onlyMaskedPaddingPx),
        )
        Slider(
            value = model.onlyMaskedPaddingPx.toFloat(),
            valueRange = IN_PAINT_MASK_PADDING_MIN.toFloat()..IN_PAINT_MASK_PADDING_MAX.toFloat(),
            steps = abs(IN_PAINT_MASK_PADDING_MIN - IN_PAINT_MASK_PADDING_MAX) - 1,
            colors = sliderColors,
            onValueChange = {
                processIntent(ImageToImageIntent.UpdateInPaintOnlyMaskedPadding(it.roundToInt()))
            },
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

internal val IN_PAINT_SLIDER_LEADING_WIDTH = 48.dp

/**
 * Renders the `SelectableRow` UI for the SDAI presentation layer.
 *
 * @param selected selected value consumed by the API.
 * @param text text value consumed by the API.
 * @param onClick callback invoked when the user activates the control.
 * @author Dmitriy Moroz
 */
@Composable
internal fun SelectableRow(
    selected: Boolean,
    text: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
        )
        Text(text = text)
    }
}

/**
 * Converts SDAI data with `toInPaintPoint`.
 *
 * @author Dmitriy Moroz
 */
internal fun Offset.toInPaintPoint(): InPaintPoint = InPaintPoint(x = x, y = y)

/**
 * Converts SDAI data with `toPath`.
 *
 * @param targetWidth target width value consumed by the API.
 * @param targetHeight target height value consumed by the API.
 * @return Result produced by `toPath`.
 * @author Dmitriy Moroz
 */
internal fun InPaintStroke.toPath(
    targetWidth: Float,
    targetHeight: Float,
): Path {
    val path = Path()
    val first = points.firstOrNull() ?: return path
    val scaleX = targetWidth / canvasWidth.coerceAtLeast(1)
    val scaleY = targetHeight / canvasHeight.coerceAtLeast(1)
    var previousX = first.x * scaleX
    var previousY = first.y * scaleY
    path.moveTo(previousX, previousY)
    points.drop(1).forEach { point ->
        val x = point.x * scaleX
        val y = point.y * scaleY
        path.quadraticTo(
            previousX,
            previousY,
            (previousX + x) / 2f,
            (previousY + y) / 2f,
        )
        previousX = x
        previousY = y
    }
    path.lineTo(previousX, previousY)
    return path
}
