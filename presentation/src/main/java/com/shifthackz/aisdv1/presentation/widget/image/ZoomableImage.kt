package com.shifthackz.aisdv1.presentation.widget.image

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.shifthackz.aisdv1.presentation.R as PresentationR

sealed interface ZoomableImageSource {
    data class Bmp(val bitmap: Bitmap) : ZoomableImageSource
    data class Resource(@DrawableRes val resId: Int) : ZoomableImageSource
}

/**
 * Allows to implement image zoom pinch, rotate behavior gestures
 *
 * Source: https://stackoverflow.com/questions/66005066/android-jetpack-compose-how-to-zoom-a-image-in-a-box
 */
@Composable
fun ZoomableImage(
    modifier: Modifier = Modifier,
    source: ZoomableImageSource,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    minScale: Float = 1f,
    maxScale: Float = 6f,
) {
    val configuration = LocalConfiguration.current
    val width = configuration.screenWidthDp

    val scale = remember { mutableStateOf(calculateInitialScale(source, width)) }
//    val rotationState = remember { mutableStateOf(1f) }

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .clip(RectangleShape)
            .fillMaxSize()
            .background(backgroundColor)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale.value *= zoom
                    //rotationState.value += rotation
                    offsetX += pan.x * zoom
                    offsetY += pan.y * zoom
                }
            },
    ) {
        val imageModifier = Modifier
            .align(Alignment.Center)
            .graphicsLayer(
                scaleX = maxOf(minScale, minOf(maxScale, scale.value)),
                scaleY = maxOf(minScale, minOf(maxScale, scale.value)),
                //rotationZ = rotationState.value,
                translationX = offsetX,
                translationY = offsetY,
            )
        when (source) {
            is ZoomableImageSource.Bmp -> Image(
                modifier = imageModifier,
                contentDescription = null,
                bitmap = source.bitmap.asImageBitmap(),
            )
            is ZoomableImageSource.Resource -> Image(
                modifier = imageModifier,
                contentDescription = null,
                painter = painterResource(id = source.resId),
            )
        }
    }
}

private fun calculateInitialScale(
    source: ZoomableImageSource,
    width: Int,
): Float {
    if (source is ZoomableImageSource.Bmp) {
        val initialScale = (width.toFloat() / source.bitmap.width.toFloat())
        return initialScale + 1f
    }
    return 1f
}

@Preview
@Composable
private fun ZoomableImagePreview() {
    ZoomableImage(
        modifier = Modifier.fillMaxSize(),
        source = ZoomableImageSource.Resource(PresentationR.drawable.ic_gallery)
    )
}
