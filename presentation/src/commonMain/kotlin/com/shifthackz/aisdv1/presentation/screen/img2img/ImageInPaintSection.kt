@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.img2img

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.presentation.theme.sliderColors
import com.shifthackz.aisdv1.presentation.theme.global.ApplySystemBarTheme
import com.shifthackz.aisdv1.presentation.widget.scrollbar.verticalScrollbar
import kotlin.math.abs
import kotlin.math.roundToInt


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
 * @param onStrokeDrawn callback invoked by the component.
 * @author Dmitriy Moroz
 */
@Composable
internal fun ImageInPaintCanvas(
    modifier: Modifier = Modifier,
    image: ImageBitmap,
    state: ImageInPaintState,
    drawEnabled: Boolean = true,
    onStrokeDrawn: (InPaintStroke) -> Unit = {},
) {
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var activeStroke by remember { mutableStateOf<InPaintStroke?>(null) }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(MaterialTheme.colorScheme.surface),
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            bitmap = image,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
        )
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { canvasSize = it }
                .then(
                    if (drawEnabled) {
                        Modifier.pointerInput(state.brushSize, canvasSize) {
                            fun Offset.coerceToCanvas(): InPaintPoint {
                                val maxX = (canvasSize.width - 1).coerceAtLeast(0).toFloat()
                                val maxY = (canvasSize.height - 1).coerceAtLeast(0).toFloat()
                                return copy(
                                    x = x.coerceIn(0f, maxX),
                                    y = y.coerceIn(0f, maxY),
                                ).toInPaintPoint()
                            }
                            detectDragGestures(
                                onDragStart = { offset ->
                                    if (canvasSize.width > 0 && canvasSize.height > 0) {
                                        activeStroke = InPaintStroke(
                                            points = listOf(offset.coerceToCanvas()),
                                            brushSize = state.brushSize,
                                            canvasWidth = canvasSize.width,
                                            canvasHeight = canvasSize.height,
                                        )
                                    }
                                },
                                onDrag = { change, _ ->
                                    activeStroke = activeStroke?.let { stroke ->
                                        stroke.copy(points = stroke.points + change.position.coerceToCanvas())
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
                    } else {
                        Modifier
                    },
                ),
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
                    drawStroke(stroke, Color.White)
                }
                activeStroke?.let { stroke ->
                    drawStroke(stroke, Color.White.copy(alpha = 0.7f))
                }
            }
        }
    }
}
