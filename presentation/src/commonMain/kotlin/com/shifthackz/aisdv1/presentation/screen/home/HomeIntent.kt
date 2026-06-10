package com.shifthackz.aisdv1.presentation.screen.home

import com.shifthackz.aisdv1.core.mvi.MviIntent

sealed interface HomeIntent : MviIntent {
    data object ConfigureProvider : HomeIntent
    data object StartTextToImage : HomeIntent
    data object StartImageToImage : HomeIntent
    data object OpenGallery : HomeIntent
    data object OpenSettings : HomeIntent
    data object OpenHistory : HomeIntent
    data object RefreshConfiguration : HomeIntent
}
