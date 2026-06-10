package com.shifthackz.aisdv1.presentation.screen.onboarding

/**
 * Coordinates `OnBoardingPage` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
enum class OnBoardingPage {
    Universal,
    Providers,
    Form,
    LocalDiffusion,
    LookAndFeel,
}

/**
 * Executes the `visibleOnBoardingPages` step in the SDAI presentation layer.
 *
 * @param showLocalDiffusionPage show local diffusion page value consumed by the API.
 * @return Result produced by `visibleOnBoardingPages`.
 * @author Dmitriy Moroz
 */
fun visibleOnBoardingPages(
    showLocalDiffusionPage: Boolean,
): List<OnBoardingPage> = OnBoardingPage.entries.filter { page ->
    showLocalDiffusionPage || page != OnBoardingPage.LocalDiffusion
}
