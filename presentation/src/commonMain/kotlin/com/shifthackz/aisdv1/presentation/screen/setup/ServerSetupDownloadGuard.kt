package com.shifthackz.aisdv1.presentation.screen.setup

interface ServerSetupDownloadGuard {
    suspend fun <T> withDownload(block: suspend () -> T): T
}

object NoOpServerSetupDownloadGuard : ServerSetupDownloadGuard {
    override suspend fun <T> withDownload(block: suspend () -> T): T = block()
}
