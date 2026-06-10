package com.shifthackz.aisdv1.core.extensions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.common.extensions.EmptyLambda

/**
 * Renders the `gesturesDisabled` UI for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Composable
fun Modifier.gesturesDisabled() = clickable(
    indication = null,
    interactionSource = remember { MutableInteractionSource() },
    onClick = EmptyLambda,
)

/**
 * Renders the `clearFocusOnTap` UI for the SDAI presentation layer.
 *
 * @return Result produced by `clearFocusOnTap`.
 * @author Dmitriy Moroz
 */
@Composable
fun Modifier.clearFocusOnTap(): Modifier {
    if (!clearFocusOnTapEnabled) return this
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    return pointerInput(focusManager, keyboardController) {
        awaitEachGesture {
            val down = awaitFirstDown(
                requireUnconsumed = false,
                pass = PointerEventPass.Final,
            )
            if (down.isConsumed) return@awaitEachGesture

            val up = waitForUpOrCancellation(pass = PointerEventPass.Final)
            if (up != null && !up.isConsumed) {
                focusManager.clearFocus(force = true)
                keyboardController?.hide()
                dismissPlatformKeyboard()
            }
        }
    }
}

/**
 * Renders the `measureTextWidth` UI for the SDAI presentation layer.
 *
 * @param text text value consumed by the API.
 * @param style style value consumed by the API.
 * @return Result produced by `measureTextWidth`.
 * @author Dmitriy Moroz
 */
@Composable
fun measureTextWidth(text: String, style: TextStyle = LocalTextStyle.current): Dp {
    val textMeasurer = rememberTextMeasurer()
    val widthInPixels = textMeasurer.measure(text, style).size.width
    return with(LocalDensity.current) { widthInPixels.toDp() }
}

/**
 * Renders the `fadedEdge` UI for the SDAI presentation layer.
 *
 * @param color color value consumed by the API.
 * @param gradientOffset gradient offset value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
fun Modifier.fadedEdge(
    color: Color = MaterialTheme.colorScheme.background,
    gradientOffset: Dp = 0.dp,
) = graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(
            brush = Brush.verticalGradient(
                startY = gradientOffset.toPx(),
                colors = listOf(
                    color,
                    Color.Transparent,
                ),
            ),
            blendMode = BlendMode.DstIn,
        )
    }
