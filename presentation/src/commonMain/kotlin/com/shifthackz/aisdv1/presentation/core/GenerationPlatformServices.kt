package com.shifthackz.aisdv1.presentation.core

interface GenerationPlatformServices {
    val supportsBackgroundGeneration: Boolean
    suspend fun acquireWakeLock()
    suspend fun releaseWakeLock()
    fun showGenerationSucceeded()
    fun showGenerationFailed()
}

object NoOpGenerationPlatformServices : GenerationPlatformServices {
    override val supportsBackgroundGeneration: Boolean = false
    override suspend fun acquireWakeLock() = Unit
    override suspend fun releaseWakeLock() = Unit
    override fun showGenerationSucceeded() = Unit
    override fun showGenerationFailed() = Unit
}
