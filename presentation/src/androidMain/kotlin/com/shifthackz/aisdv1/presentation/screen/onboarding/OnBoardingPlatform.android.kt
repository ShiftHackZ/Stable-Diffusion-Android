package com.shifthackz.aisdv1.presentation.screen.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

actual fun isLocalDiffusionOnBoardingAvailable(): Boolean = true

@Composable
actual fun OnBoardingBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    BackHandler(enabled = enabled, onBack = onBack)
}
