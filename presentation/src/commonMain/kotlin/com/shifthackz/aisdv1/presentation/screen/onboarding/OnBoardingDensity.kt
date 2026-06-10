package com.shifthackz.aisdv1.presentation.screen.onboarding

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.Density

val onBoardingDensity = Density(2f, 1f)
val onBoardingPhoneWidthFraction = 0.76f
val onBoardingPhoneAspectRatio = 9.5f / 16f
val onBoardingPageAnimation = tween<Float>(1200, easing = FastOutSlowInEasing)
