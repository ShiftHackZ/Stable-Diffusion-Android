package com.shifthackz.aisdv1.presentation.screen.onboarding

enum class OnBoardingPage {
    Universal,
    Providers,
    Form,
    LocalDiffusion,
    LookAndFeel,
}

fun visibleOnBoardingPages(
    showLocalDiffusionPage: Boolean,
): List<OnBoardingPage> = OnBoardingPage.entries.filter { page ->
    showLocalDiffusionPage || page != OnBoardingPage.LocalDiffusion
}
