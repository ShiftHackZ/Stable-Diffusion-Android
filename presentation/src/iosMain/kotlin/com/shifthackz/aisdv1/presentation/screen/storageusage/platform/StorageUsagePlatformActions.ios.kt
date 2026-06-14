package com.shifthackz.aisdv1.presentation.screen.storageusage.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.shifthackz.aisdv1.presentation.platform.IosStorageUsageFileSystem

/**
 * Creates the iOS storage bridge backed by app-private Caches and Application Support directories.
 *
 * @author Dmitriy Moroz
 */
@Composable
actual fun rememberStorageUsagePlatformActions(): StorageUsagePlatformActions = remember {
    IosStorageUsagePlatformActions(IosStorageUsageFileSystem())
}

/**
 * iOS implementation that measures and deletes app-private storage used by the usage screen.
 *
 * @param fileSystem Shared iOS storage helper that knows the app cache and local model paths.
 *
 * @author Dmitriy Moroz
 */
private class IosStorageUsagePlatformActions(
    private val fileSystem: IosStorageUsageFileSystem,
) : StorageUsagePlatformActions {

    override fun mapStorageBytesForUi(bytes: Long): Long =
        fileSystem.mapStorageBytesForUi(bytes)

    override suspend fun getAppCacheBytes(): Long = fileSystem.getAppCacheBytes()

    override suspend fun clearAppCache() {
        fileSystem.clearAppCache()
    }

    override suspend fun getAllDownloadedModelsBytes(): Long =
        fileSystem.getAllDownloadedModelsBytes()

    override suspend fun clearAllDownloadedModels() {
        fileSystem.clearAllDownloadedModels()
    }

    override suspend fun getDownloadedModelsBytes(modelIds: List<String>): Long =
        fileSystem.getDownloadedModelsBytes(modelIds)
}
