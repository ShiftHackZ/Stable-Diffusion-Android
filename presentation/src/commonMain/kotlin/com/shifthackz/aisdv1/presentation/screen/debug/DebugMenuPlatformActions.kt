package com.shifthackz.aisdv1.presentation.screen.debug

/**
 * Defines the `DebugMenuPlatformActions` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
interface DebugMenuPlatformActions {
    /**
     * Exposes the `showWorkManagerSection` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val showWorkManagerSection: Boolean
    /**
     * Exposes the `showLocalDiffusionSection` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val showLocalDiffusionSection: Boolean

    /**
     * Performs the SDAI side effect handled by `clearLogs`.
     *
     * @return Result produced by `clearLogs`.
     * @author Dmitriy Moroz
     */
    suspend fun clearLogs(): Result<Unit>
    /**
     * Executes the `retryLastTextToImageTask` step in the SDAI presentation layer.
     *
     * @return Result produced by `retryLastTextToImageTask`.
     * @author Dmitriy Moroz
     */
    fun retryLastTextToImageTask(): Result<Unit>
    /**
     * Executes the `retryLastImageToImageTask` step in the SDAI presentation layer.
     *
     * @return Result produced by `retryLastImageToImageTask`.
     * @author Dmitriy Moroz
     */
    fun retryLastImageToImageTask(): Result<Unit>
    /**
     * Executes the `cancelAllWork` step in the SDAI presentation layer.
     *
     * @return Result produced by `cancelAllWork`.
     * @author Dmitriy Moroz
     */
    fun cancelAllWork(): Result<Unit>
}

/**
 * Provides the `NoOpDebugMenuPlatformActions` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
object NoOpDebugMenuPlatformActions : DebugMenuPlatformActions {
    /**
     * Exposes the `showWorkManagerSection` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val showWorkManagerSection = false
    /**
     * Exposes the `showLocalDiffusionSection` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val showLocalDiffusionSection = false

    /**
     * Performs the SDAI side effect handled by `clearLogs`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun clearLogs(): Result<Unit> = Result.success(Unit)
    /**
     * Executes the `retryLastTextToImageTask` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun retryLastTextToImageTask(): Result<Unit> = Result.success(Unit)
    /**
     * Executes the `retryLastImageToImageTask` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun retryLastImageToImageTask(): Result<Unit> = Result.success(Unit)
    /**
     * Executes the `cancelAllWork` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun cancelAllWork(): Result<Unit> = Result.success(Unit)
}
