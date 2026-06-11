package com.shifthackz.aisdv1.presentation.screen.onboarding

import androidx.compose.runtime.Composable
import com.shifthackz.aisdv1.domain.entity.ServerSource

/**
 * Carries local generation onboarding values for the active platform.
 *
 * @author Dmitriy Moroz
 */
data class LocalDiffusionOnBoardingSpec(
    /**
     * Exposes the `serverSource` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val serverSource: ServerSource,
    /**
     * Exposes the `titleKey` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val titleKey: String,
    /**
     * Exposes the `progressTitleKey` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val progressTitleKey: String,
)

/**
 * Executes the `isLocalDiffusionOnBoardingAvailable` step in the SDAI presentation layer.
 *
 * @return Result produced by `isLocalDiffusionOnBoardingAvailable`.
 * @author Dmitriy Moroz
 */
expect fun isLocalDiffusionOnBoardingAvailable(): Boolean

/**
 * Executes the `localDiffusionOnBoardingSpec` step in the SDAI presentation layer.
 *
 * @return Result produced by `localDiffusionOnBoardingSpec`.
 * @author Dmitriy Moroz
 */
expect fun localDiffusionOnBoardingSpec(): LocalDiffusionOnBoardingSpec

/**
 * Renders the `OnBoardingBackHandler` UI for the SDAI presentation layer.
 *
 * @param enabled enabled value consumed by the API.
 * @param onBack callback invoked by the component.
 * @author Dmitriy Moroz
 */
@Composable
expect fun OnBoardingBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
)
