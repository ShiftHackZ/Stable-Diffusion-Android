package com.shifthackz.aisdv1.presentation.screen.onboarding

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.domain.entity.DarkThemeToken

/**
 * Carries `OnBoardingState` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class OnBoardingState(
    /**
     * Exposes the `darkThemeToken` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val darkThemeToken: DarkThemeToken = DarkThemeToken.FRAPPE,
    /**
     * Exposes the `appVersion` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val appVersion: String = "",
    /**
     * Exposes the `showLocalDiffusionPage` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val showLocalDiffusionPage: Boolean = false,
) : MviState
