package com.shifthackz.aisdv1.core.extensions

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.debugInspectorInfo

fun Modifier.shake(
    enabled: Boolean,
    animationDurationMillis: Int = 167,
    animationStartOffset: Int = 0,
) = this.composed(
    factory = {
        val infiniteTransition = rememberInfiniteTransition(label = "shake")
        val scaleInfinite by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = .99f,
            animationSpec = infiniteRepeatable(
                animation = tween(animationDurationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
                initialStartOffset = StartOffset(animationStartOffset),
            ),
            label = "shake",
        )
        val rotation by infiniteTransition.animateFloat(
            initialValue = -1.5f,
            targetValue = 1.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(animationDurationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
                initialStartOffset = StartOffset(animationStartOffset),
            ),
            label = "shake",
        )

        Modifier.graphicsLayer {
            scaleX = if (enabled) scaleInfinite else 1f
            scaleY = if (enabled) scaleInfinite else 1f
            rotationZ = if (enabled) rotation else 0f
        }
    },
    inspectorInfo = debugInspectorInfo {
        name = "shake"
        properties["enabled"] = enabled
    },
)
