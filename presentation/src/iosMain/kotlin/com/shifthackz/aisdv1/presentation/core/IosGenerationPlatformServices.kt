package com.shifthackz.aisdv1.presentation.core

/**
 * Implements generation platform services for iOS.
 *
 * @author Dmitriy Moroz
 */
internal object IosGenerationPlatformServices : GenerationPlatformServices {

    /**
     * Exposes the `supportsBackgroundGeneration` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val supportsBackgroundGeneration: Boolean = true

    /**
     * Executes the `showGenerationSucceeded` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun showGenerationSucceeded() = Unit

    /**
     * Executes the `showGenerationFailed` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun showGenerationFailed() = Unit
}
