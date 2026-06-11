@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.img2img

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.localization.Localization


/**
 * Coordinates `ImageInPaintTab` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal enum class ImageInPaintTab(
    /**
     * Exposes the `label` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val label: String,
) {
    Draw(Localization.string("in_paint_tab_1")),
    Adjust(Localization.string("in_paint_tab_2")),
}

/**
 * Renders the `ImageInPaintSection` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param image image value consumed by the API.
 * @param state state rendered or processed by the component.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
internal fun ImageInPaintSection(
    modifier: Modifier = Modifier,
    image: ImageBitmap,
    state: ImageInPaintState,
    processIntent: (ImageToImageIntent) -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = Localization.string("in_paint_title"),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = Localization.string("in_paint_tab_1"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Icon(
                    imageVector = Icons.Default.Brush,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            ImageInPaintCanvas(
                modifier = Modifier.fillMaxWidth(),
                image = image,
                state = state,
                onStrokeDrawn = { stroke ->
                    processIntent(ImageToImageIntent.DrawInPaintStroke(stroke))
                },
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    enabled = state.strokes.isNotEmpty(),
                    onClick = { processIntent(ImageToImageIntent.UndoInPaintStroke) },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = null,
                    )
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = Localization.string("action_undo"),
                    )
                }
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    enabled = state.strokes.isNotEmpty(),
                    onClick = { processIntent(ImageToImageIntent.ClearInPaintMask) },
                ) {
                    Icon(
                        imageVector = Icons.Default.CleaningServices,
                        contentDescription = null,
                    )
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = Localization.string("action_clear"),
                    )
                }
            }

            BrushSizeSlider(
                size = state.brushSize,
                onValueChanged = { processIntent(ImageToImageIntent.UpdateInPaintBrushSize(it)) },
            )

            ImageInPaintParamsForm(
                model = state,
                processIntent = processIntent,
            )
        }
    }
}

/**
 * Renders the `ImageInPaintCanvas` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param image image value consumed by the API.
 * @param state state rendered or processed by the component.
 * @param drawEnabled draw enabled value consumed by the API.
 * @param maskAlpha mask alpha value consumed by the API.
 * @param onStrokeDrawn callback invoked by the component.
 * @author Dmitriy Moroz
 */
@Composable
internal fun ImageInPaintCanvas(
    modifier: Modifier = Modifier,
    image: ImageBitmap,
    state: ImageInPaintState,
    drawEnabled: Boolean = true,
    maskAlpha: Float = 1f,
    zoom: Float = 1f,
    pan: Offset = Offset.Zero,
    transformEnabled: Boolean = false,
    onZoomChange: (Float) -> Unit = {},
    onPanChange: (Offset) -> Unit = {},
    onStrokeDrawn: (InPaintStroke) -> Unit = {},
) {
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var activeStroke by remember { mutableStateOf<InPaintStroke?>(null) }
    val currentZoom by rememberUpdatedState(zoom)
    val currentPan by rememberUpdatedState(pan)

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(MaterialTheme.colorScheme.surface),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { canvasSize = it }
                .graphicsLayer {
                    scaleX = zoom
                    scaleY = zoom
                    translationX = pan.x
                    translationY = pan.y
                    transformOrigin = TransformOrigin.Center
                },
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                bitmap = image,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
            )
            Canvas(
                modifier = Modifier.fillMaxSize(),
            ) {
                val drawStroke: (InPaintStroke, Color) -> Unit = { stroke, color ->
                    drawPath(
                        color = color,
                        path = stroke.toPath(
                            targetWidth = size.width,
                            targetHeight = size.height,
                        ),
                        style = Stroke(
                            width = stroke.brushSize.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                        ),
                    )
                }
                clipRect {
                    state.strokes.forEach { stroke ->
                        drawStroke(stroke, Color.White.copy(alpha = maskAlpha))
                    }
                    activeStroke?.let { stroke ->
                        drawStroke(stroke, Color.White.copy(alpha = 0.7f))
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (drawEnabled) {
                        Modifier.pointerInput(state.brushSize, canvasSize) {
                            fun Offset.toCanvasPoint(): InPaintPoint {
                                val center = Offset(
                                    x = canvasSize.width / 2f,
                                    y = canvasSize.height / 2f,
                                )
                                val unscaled = Offset(
                                    x = (x - center.x - currentPan.x) / currentZoom + center.x,
                                    y = (y - center.y - currentPan.y) / currentZoom + center.y,
                                )
                                return unscaled.coerceToCanvas(canvasSize).toInPaintPoint()
                            }
                            detectDragGestures(
                                onDragStart = { offset ->
                                    if (canvasSize.width > 0 && canvasSize.height > 0) {
                                        activeStroke = InPaintStroke(
                                            points = listOf(offset.toCanvasPoint()),
                                            brushSize = state.brushSize,
                                            canvasWidth = canvasSize.width,
                                            canvasHeight = canvasSize.height,
                                        )
                                    }
                                },
                                onDrag = { change, _ ->
                                    activeStroke = activeStroke?.let { stroke ->
                                        stroke.copy(points = stroke.points + change.position.toCanvasPoint())
                                    }
                                },
                                onDragEnd = {
                                    activeStroke
                                        ?.takeIf { it.points.size > 1 }
                                        ?.let(onStrokeDrawn)
                                    activeStroke = null
                                },
                                onDragCancel = {
                                    activeStroke = null
                                },
                            )
                        }
                    } else if (transformEnabled) {
                        Modifier.pointerInput(canvasSize) {
                            detectTransformGestures { centroid, panChange, zoomChange, _ ->
                                val oldZoom = currentZoom
                                val newZoom = (oldZoom * zoomChange).coerceIn(
                                    MIN_IN_PAINT_ZOOM,
                                    MAX_IN_PAINT_ZOOM,
                                )
                                val actualZoomChange = newZoom / oldZoom
                                val canvasCenter = Offset(
                                    x = canvasSize.width / 2f,
                                    y = canvasSize.height / 2f,
                                )
                                val centroidFromCenter = centroid - canvasCenter
                                val newPan = if (newZoom <= MIN_IN_PAINT_ZOOM) {
                                    Offset.Zero
                                } else {
                                    currentPan * actualZoomChange +
                                        centroidFromCenter * (1f - actualZoomChange) +
                                        panChange
                                }
                                onZoomChange(newZoom)
                                onPanChange(newPan.coercePan(newZoom, canvasSize))
                            }
                        }
                    } else {
                        Modifier
                    },
                ),
        )
    }
}

private fun Offset.coerceToCanvas(canvasSize: IntSize): Offset {
    val maxX = (canvasSize.width - 1).coerceAtLeast(0).toFloat()
    val maxY = (canvasSize.height - 1).coerceAtLeast(0).toFloat()
    return copy(
        x = x.coerceIn(0f, maxX),
        y = y.coerceIn(0f, maxY),
    )
}

private fun Offset.coercePan(zoom: Float, canvasSize: IntSize): Offset {
    if (canvasSize.width <= 0 || canvasSize.height <= 0 || zoom <= MIN_IN_PAINT_ZOOM) return Offset.Zero
    val maxX = canvasSize.width * (zoom - MIN_IN_PAINT_ZOOM) / 2f
    val maxY = canvasSize.height * (zoom - MIN_IN_PAINT_ZOOM) / 2f
    return Offset(
        x = x.coerceIn(-maxX, maxX),
        y = y.coerceIn(-maxY, maxY),
    )
}

internal const val MIN_IN_PAINT_ZOOM = 1f
internal const val MAX_IN_PAINT_ZOOM = 5f
