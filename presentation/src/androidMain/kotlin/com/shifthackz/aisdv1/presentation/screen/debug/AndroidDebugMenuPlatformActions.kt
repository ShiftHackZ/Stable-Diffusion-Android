package com.shifthackz.aisdv1.presentation.screen.debug

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.log.FileLoggingTree
import com.shifthackz.aisdv1.domain.feature.work.BackgroundTaskManager

class AndroidDebugMenuPlatformActions(
    private val fileProviderDescriptor: FileProviderDescriptor,
    private val backgroundTaskManager: BackgroundTaskManager,
) : DebugMenuPlatformActions {

    override val showWorkManagerSection = true
    override val showLocalDiffusionSection = true

    override suspend fun clearLogs(): Result<Unit> = runCatching {
        FileLoggingTree.clearLog(fileProviderDescriptor)
    }

    override fun retryLastTextToImageTask(): Result<Unit> =
        backgroundTaskManager.retryLastTextToImageTask()

    override fun retryLastImageToImageTask(): Result<Unit> =
        backgroundTaskManager.retryLastImageToImageTask()

    override fun cancelAllWork(): Result<Unit> =
        backgroundTaskManager.cancelAll()
}
