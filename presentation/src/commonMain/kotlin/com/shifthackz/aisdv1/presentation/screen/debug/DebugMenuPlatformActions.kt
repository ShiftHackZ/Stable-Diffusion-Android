package com.shifthackz.aisdv1.presentation.screen.debug

interface DebugMenuPlatformActions {
    val showWorkManagerSection: Boolean
    val showLocalDiffusionSection: Boolean

    suspend fun clearLogs(): Result<Unit>
    fun retryLastTextToImageTask(): Result<Unit>
    fun retryLastImageToImageTask(): Result<Unit>
    fun cancelAllWork(): Result<Unit>
}

object NoOpDebugMenuPlatformActions : DebugMenuPlatformActions {
    override val showWorkManagerSection = false
    override val showLocalDiffusionSection = false

    override suspend fun clearLogs(): Result<Unit> = Result.success(Unit)
    override fun retryLastTextToImageTask(): Result<Unit> = Result.success(Unit)
    override fun retryLastImageToImageTask(): Result<Unit> = Result.success(Unit)
    override fun cancelAllWork(): Result<Unit> = Result.success(Unit)
}
