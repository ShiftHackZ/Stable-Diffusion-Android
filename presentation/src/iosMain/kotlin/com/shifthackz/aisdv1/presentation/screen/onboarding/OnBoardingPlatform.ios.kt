package com.shifthackz.aisdv1.presentation.screen.onboarding

import androidx.compose.runtime.Composable

/**
 * Executes the `isLocalDiffusionOnBoardingAvailable` step in the SDAI presentation layer.
 *
 * @return Result produced by `isLocalDiffusionOnBoardingAvailable`.
 * @author Dmitriy Moroz
 */
actual fun isLocalDiffusionOnBoardingAvailable(): Boolean = false

/**
 * Renders the `OnBoardingBackHandler` UI for the SDAI presentation layer.
 *
 * @param enabled enabled value consumed by the API.
 * @param onBack callback invoked by the component.
 * @author Dmitriy Moroz
 */
@Composable
actual fun OnBoardingBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) = Unit
