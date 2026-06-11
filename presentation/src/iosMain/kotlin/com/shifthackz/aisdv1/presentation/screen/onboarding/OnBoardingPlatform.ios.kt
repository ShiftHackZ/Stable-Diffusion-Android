package com.shifthackz.aisdv1.presentation.screen.onboarding

import androidx.compose.runtime.Composable
import com.shifthackz.aisdv1.domain.entity.ServerSource

/**
 * Executes the `isLocalDiffusionOnBoardingAvailable` step in the SDAI presentation layer.
 *
 * @return Result produced by `isLocalDiffusionOnBoardingAvailable`.
 * @author Dmitriy Moroz
 */
actual fun isLocalDiffusionOnBoardingAvailable(): Boolean = true

/**
 * Executes the `localDiffusionOnBoardingSpec` step in the SDAI presentation layer.
 *
 * @return Result produced by `localDiffusionOnBoardingSpec`.
 * @author Dmitriy Moroz
 */
actual fun localDiffusionOnBoardingSpec() = LocalDiffusionOnBoardingSpec(
    serverSource = ServerSource.LOCAL_APPLE_CORE_ML,
    titleKey = "on_boarding_page_core_ml_title",
    progressTitleKey = "communicating_core_ml_title",
)

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
