package com.shifthackz.aisdv1.presentation.screen.debug

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.log.FileLoggingTree
import com.shifthackz.aisdv1.domain.feature.work.BackgroundTaskManager

/**
 * Coordinates `AndroidDebugMenuPlatformActions` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
class AndroidDebugMenuPlatformActions(
    /**
     * Exposes the `fileProviderDescriptor` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val fileProviderDescriptor: FileProviderDescriptor,
    /**
     * Exposes the `backgroundTaskManager` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val backgroundTaskManager: BackgroundTaskManager,
) : DebugMenuPlatformActions {

    /**
     * Exposes the `showWorkManagerSection` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val showWorkManagerSection = true
    /**
     * Exposes the `showLocalDiffusionSection` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val showLocalDiffusionSection = true

    /**
     * Performs the SDAI side effect handled by `clearLogs`.
     *
     * @return Result produced by `clearLogs`.
     * @author Dmitriy Moroz
     */
    override suspend fun clearLogs(): Result<Unit> = runCatching {
        FileLoggingTree.clearLog(fileProviderDescriptor)
    }

    /**
     * Executes the `retryLastTextToImageTask` step in the SDAI presentation layer.
     *
     * @return Result produced by `retryLastTextToImageTask`.
     * @author Dmitriy Moroz
     */
    override fun retryLastTextToImageTask(): Result<Unit> =
        backgroundTaskManager.retryLastTextToImageTask()

    /**
     * Executes the `retryLastImageToImageTask` step in the SDAI presentation layer.
     *
     * @return Result produced by `retryLastImageToImageTask`.
     * @author Dmitriy Moroz
     */
    override fun retryLastImageToImageTask(): Result<Unit> =
        backgroundTaskManager.retryLastImageToImageTask()

    /**
     * Executes the `cancelAllWork` step in the SDAI presentation layer.
     *
     * @return Result produced by `cancelAllWork`.
     * @author Dmitriy Moroz
     */
    override fun cancelAllWork(): Result<Unit> =
        backgroundTaskManager.cancelAll()
}
