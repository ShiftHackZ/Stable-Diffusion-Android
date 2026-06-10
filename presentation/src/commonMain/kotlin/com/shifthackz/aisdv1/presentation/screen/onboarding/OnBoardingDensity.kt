package com.shifthackz.aisdv1.presentation.screen.onboarding

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.Density

/**
 * Exposes the `onBoardingDensity` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
val onBoardingDensity = Density(2f, 1f)
/**
 * Exposes the `onBoardingPhoneWidthFraction` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
val onBoardingPhoneWidthFraction = 0.76f
/**
 * Exposes the `onBoardingPhoneAspectRatio` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
val onBoardingPhoneAspectRatio = 9.5f / 16f
/**
 * Exposes the `onBoardingPageAnimation` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
val onBoardingPageAnimation = tween<Float>(1200, easing = FastOutSlowInEasing)
