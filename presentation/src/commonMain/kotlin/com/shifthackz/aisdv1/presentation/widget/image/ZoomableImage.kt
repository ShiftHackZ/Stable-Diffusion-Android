package com.shifthackz.aisdv1.presentation.widget.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize

sealed interface ZoomableImageSource {
    data class Bitmap(val image: ImageBitmap) : ZoomableImageSource
}

@Composable
fun ZoomableImage(
    modifier: Modifier = Modifier,
    source: ZoomableImageSource,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    minScale: Float = 1f,
    maxScale: Float = 6f,
    hideImage: Boolean = false,
    hideBlurRadius: Float = 69f,
    fitToWidth: Boolean = false,
) {
    val scale = remember(source) { mutableFloatStateOf(1f) }
    var offsetX by remember(source) { mutableFloatStateOf(0f) }
    var offsetY by remember(source) { mutableFloatStateOf(0f) }
    var containerSize by remember(source) { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
            .clip(RectangleShape)
            .fillMaxSize()
            .onSizeChanged { size ->
                containerSize = size
                val imageSize = source.sizeForContainer(size, fitToWidth)
                offsetX = offsetX.coerceInBounds(
                    containerSize = size.width.toFloat(),
                    contentSize = imageSize.width,
                    scale = scale.floatValue,
                )
                offsetY = offsetY.coerceInBounds(
                    containerSize = size.height.toFloat(),
                    contentSize = imageSize.height,
                    scale = scale.floatValue,
                )
            }
            .background(backgroundColor)
            .pointerInput(source) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale.floatValue = (scale.floatValue * zoom).coerceIn(minScale, maxScale)
                    val imageSize = source.sizeForContainer(containerSize, fitToWidth)
                    offsetX = (offsetX + pan.x * zoom).coerceInBounds(
                        containerSize = containerSize.width.toFloat(),
                        contentSize = imageSize.width,
                        scale = scale.floatValue,
                    )
                    offsetY = (offsetY + pan.y * zoom).coerceInBounds(
                        containerSize = containerSize.height.toFloat(),
                        contentSize = imageSize.height,
                        scale = scale.floatValue,
                    )
                }
            },
    ) {
        val imageModifier = Modifier
            .align(Alignment.Center)
            .then(
                if (fitToWidth) {
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(source.aspectRatio)
                } else {
                    Modifier
                }
            )
            .graphicsLayer(
                scaleX = scale.floatValue,
                scaleY = scale.floatValue,
                translationX = offsetX,
                translationY = offsetY,
            )
            .then(
                if (!hideImage) Modifier
                else Modifier.graphicsLayer {
                    renderEffect = BlurEffect(
                        radiusX = hideBlurRadius,
                        radiusY = hideBlurRadius,
                    )
                }
            )

        when (source) {
            is ZoomableImageSource.Bitmap -> Image(
                modifier = imageModifier,
                contentDescription = null,
                bitmap = source.image,
                contentScale = ContentScale.Fit,
            )
        }
    }
}

private val ZoomableImageSource.aspectRatio: Float
    get() = bitmap.width.toFloat() / bitmap.height.coerceAtLeast(1)

private val ZoomableImageSource.bitmap: ImageBitmap
    get() = (this as ZoomableImageSource.Bitmap).image

private data class ImageContentSize(
    val width: Float,
    val height: Float,
)

private fun ZoomableImageSource.sizeForContainer(
    containerSize: IntSize,
    fitToWidth: Boolean,
): ImageContentSize {
    val image = bitmap
    val sourceWidth = image.width.toFloat()
    val sourceHeight = image.height.toFloat().coerceAtLeast(1f)
    if (!fitToWidth || containerSize.width <= 0) {
        return ImageContentSize(sourceWidth, sourceHeight)
    }
    val width = containerSize.width.toFloat()
    return ImageContentSize(
        width = width,
        height = width / aspectRatio,
    )
}

private fun Float.coerceInBounds(
    containerSize: Float,
    contentSize: Float,
    scale: Float,
): Float {
    val overflow = (contentSize * scale - containerSize) / 2f
    if (overflow <= 0f) return 0f
    return coerceIn(-overflow, overflow)
}
