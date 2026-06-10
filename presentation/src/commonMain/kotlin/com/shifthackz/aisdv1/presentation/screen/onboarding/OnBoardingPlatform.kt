package com.shifthackz.aisdv1.presentation.screen.onboarding

import androidx.compose.runtime.Composable

expect fun isLocalDiffusionOnBoardingAvailable(): Boolean

@Composable
expect fun OnBoardingBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
)
