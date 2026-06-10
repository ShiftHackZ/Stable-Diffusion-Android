package com.shifthackz.aisdv1.presentation.screen.home

import com.shifthackz.aisdv1.core.mvi.MviIntent

/**
 * Defines the `HomeIntent` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface HomeIntent : MviIntent {
    /**
     * Provides the `ConfigureProvider` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object ConfigureProvider : HomeIntent
    /**
     * Provides the `StartTextToImage` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object StartTextToImage : HomeIntent
    /**
     * Provides the `StartImageToImage` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object StartImageToImage : HomeIntent
    /**
     * Provides the `OpenGallery` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object OpenGallery : HomeIntent
    /**
     * Provides the `OpenSettings` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object OpenSettings : HomeIntent
    /**
     * Provides the `OpenHistory` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object OpenHistory : HomeIntent
    /**
     * Provides the `RefreshConfiguration` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object RefreshConfiguration : HomeIntent
}
