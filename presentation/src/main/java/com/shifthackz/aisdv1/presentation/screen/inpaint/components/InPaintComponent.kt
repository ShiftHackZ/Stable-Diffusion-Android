package com.shifthackz.aisdv1.presentation.screen.inpaint.components

import android.graphics.Bitmap
import android.graphics.Picture
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.presentation.model.InPaintModel
import com.shifthackz.aisdv1.presentation.model.MotionEvent
import com.smarttoolfactory.gesture.pointerMotionEvents
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun InPaintComponent(
    modifier: Modifier = Modifier,
    drawMode: Boolean = false,
    inPaint: InPaintModel = InPaintModel(),
    bitmap: Bitmap? = null,
    capWidth: Int = 16,
    onPathDrawn: (Path) -> Unit = {},
    onPathBitmapDrawn: (Bitmap?) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
    ) {
        bitmap?.takeIf { it.width != 0 && it.height != 0 }?.asImageBitmap()?.let {
            Image(
                modifier = Modifier.fillMaxSize(),
                bitmap = it,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
            )
        }
        inPaint.bitmap?.takeIf { it.width != 0 && it.height != 0 }?.asImageBitmap()?.let {
            Image(
                modifier = Modifier
                    .fillMaxSize(),
                bitmap = it,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
            )
        }
        var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
        var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
        var previousPosition by remember { mutableStateOf(Offset.Unspecified) }
        var currentPath by remember { mutableStateOf(Path()) }
        val picture = remember { Picture() }
        LaunchedEffect(inPaint.paths.size) {
            if (picture.width != 0 && picture.height != 0) {
                val pathBmp = createBitmapFromPicture(picture)
                val resized = bitmap?.let {
                    Bitmap.createScaledBitmap(pathBmp, it.width, it.height, false)
                }
                onPathBitmapDrawn(resized ?: pathBmp)
            } else {
                onPathBitmapDrawn(null)
            }
        }
        Canvas(
            modifier = Modifier
                .alpha(0.7f)
                .fillMaxSize()
                .drawWithCache {
                    val width = this.size.width.toInt()
                    val height = this.size.height.toInt()
                    onDrawWithContent {
                        val pictureCanvas = androidx.compose.ui.graphics.Canvas(
                                picture.beginRecording(width, height)
                        )
                        draw(this, this.layoutDirection, pictureCanvas, this.size) {
                            this@onDrawWithContent.drawContent()
                        }
                        picture.endRecording()
                        drawIntoCanvas { canvas -> canvas.nativeCanvas.drawPicture(picture) }
                    }
                }
                .let {
                    if (drawMode) it.pointerMotionEvents(
                        onDown = { pointerInputChange: PointerInputChange ->
                            currentPosition = pointerInputChange.position
                            motionEvent = MotionEvent.Down
                            pointerInputChange.consume()
                        },
                        onMove = { pointerInputChange: PointerInputChange ->
                            currentPosition = pointerInputChange.position
                            motionEvent = MotionEvent.Move
                            pointerInputChange.consume()
                        },
                        onUp = { pointerInputChange: PointerInputChange ->
                            motionEvent = MotionEvent.Up
                            pointerInputChange.consume()
                        },
                        delayAfterDownInMillis = 25L,
                    )
                    else it
                },
        ) {
            if (drawMode) {
                when (motionEvent) {
                    MotionEvent.Down -> {
                        currentPath.moveTo(currentPosition.x, currentPosition.y)
                        previousPosition = currentPosition
                    }

                    MotionEvent.Move -> {
                        currentPath.quadraticTo(
                            previousPosition.x,
                            previousPosition.y,
                            (previousPosition.x + currentPosition.x) / 2,
                            (previousPosition.y + currentPosition.y) / 2,
                        )
                        previousPosition = currentPosition
                    }

                    MotionEvent.Up -> {
                        currentPath.lineTo(currentPosition.x, currentPosition.y)
                        currentPosition = Offset.Unspecified
                        previousPosition = currentPosition
                        motionEvent = MotionEvent.Idle
                        scope.launch {
                            onPathDrawn(currentPath)
                            delay(100L)
                            currentPath = Path()
                        }
                    }

                    else -> Unit
                }
            }

            val draw: (Path, Int) -> Unit = { path, cap ->
                drawPath(
                    color = Color.White,
                    path = path,
                    style = Stroke(
                        width = cap.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                    )
                )
            }

            inPaint.paths.forEach { (p, c) -> draw(p, c) }
            draw(currentPath, capWidth)
        }
    }
}

private fun createBitmapFromPicture(picture: Picture): Bitmap {
    val bitmap = Bitmap.createBitmap(
        picture.width,
        picture.height,
        Bitmap.Config.ARGB_8888
    )
    val canvas = android.graphics.Canvas(bitmap)
    canvas.drawPicture(picture)
    return bitmap
}
