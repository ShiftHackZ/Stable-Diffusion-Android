package com.shifthackz.aisdv1.presentation.screen.onboarding

import androidx.compose.runtime.Composable

actual fun isLocalDiffusionOnBoardingAvailable(): Boolean = false

@Composable
actual fun OnBoardingBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) = Unit
