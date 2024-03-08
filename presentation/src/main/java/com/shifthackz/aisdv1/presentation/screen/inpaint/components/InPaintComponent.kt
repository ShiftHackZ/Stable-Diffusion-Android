package com.shifthackz.aisdv1.presentation.screen.inpaint.components

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputChange
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
) {
    val scope = rememberCoroutineScope()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
    ) {
        bitmap?.asImageBitmap()?.let {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                bitmap = it,
                contentDescription = null,
            )
        }
        var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
        var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
        var previousPosition by remember { mutableStateOf(Offset.Unspecified) }
        var currentPath by remember { mutableStateOf(Path()) }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clipToBounds()
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
                        currentPath.quadraticBezierTo(
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
