package com.shifthackz.aisdv1.presentation.screen.setup.platform

/**
 * Wraps long-running local model downloads with platform-specific safeguards.
 *
 * Android keeps a wakelock while the block runs; common/iOS fallback executes the block directly.
 */
interface ServerSetupDownloadGuard {
    suspend fun <T> withDownload(block: suspend () -> T): T
}

/**
 * Fallback used on platforms where downloads do not need extra lifecycle handling.
 */
object NoOpServerSetupDownloadGuard : ServerSetupDownloadGuard {
    override suspend fun <T> withDownload(block: suspend () -> T): T = block()
}
