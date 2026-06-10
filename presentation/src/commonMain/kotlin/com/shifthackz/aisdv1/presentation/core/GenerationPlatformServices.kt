package com.shifthackz.aisdv1.presentation.core

/**
 * Defines the `GenerationPlatformServices` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
interface GenerationPlatformServices {
    /**
     * Exposes the `supportsBackgroundGeneration` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val supportsBackgroundGeneration: Boolean
    /**
     * Performs the SDAI side effect handled by `acquireWakeLock`.
     *
     * @author Dmitriy Moroz
     */
    suspend fun acquireWakeLock()
    /**
     * Performs the SDAI side effect handled by `releaseWakeLock`.
     *
     * @author Dmitriy Moroz
     */
    suspend fun releaseWakeLock()
    /**
     * Executes the `showGenerationSucceeded` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun showGenerationSucceeded()
    /**
     * Executes the `showGenerationFailed` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun showGenerationFailed()
}

/**
 * Provides the `NoOpGenerationPlatformServices` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
object NoOpGenerationPlatformServices : GenerationPlatformServices {
    /**
     * Exposes the `supportsBackgroundGeneration` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val supportsBackgroundGeneration: Boolean = false
    /**
     * Performs the SDAI side effect handled by `acquireWakeLock`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun acquireWakeLock() = Unit
    /**
     * Performs the SDAI side effect handled by `releaseWakeLock`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun releaseWakeLock() = Unit
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
