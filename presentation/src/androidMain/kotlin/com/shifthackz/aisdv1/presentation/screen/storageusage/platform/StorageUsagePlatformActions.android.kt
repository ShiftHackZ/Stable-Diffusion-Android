package com.shifthackz.aisdv1.presentation.screen.storageusage.platform

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.File

/**
 * Creates the Android storage bridge backed by app-private cache and files directories.
 *
 * @author Dmitriy Moroz
 */
@Composable
actual fun rememberStorageUsagePlatformActions(): StorageUsagePlatformActions {
    val context = LocalContext.current
    return remember(context) {
        AndroidStorageUsagePlatformActions(context)
    }
}

/**
 * Android implementation that measures and deletes app-private storage used by the usage screen.
 *
 * @param context Android context whose app-private cache and files directories are inspected.
 *
 * @author Dmitriy Moroz
 */
private class AndroidStorageUsagePlatformActions(
    private val context: Context,
) : StorageUsagePlatformActions {

    override suspend fun getAppCacheBytes(): Long = context.cacheDir.directorySize()

    override suspend fun clearAppCache() {
        context.cacheDir.listFiles()?.forEach { file ->
            runCatching { file.deleteRecursively() }
        }
    }

    override suspend fun getAllDownloadedModelsBytes(): Long =
        File(context.filesDir, LOCAL_MODEL_DIRECTORY).directorySize()

    override suspend fun clearAllDownloadedModels() {
        File(context.filesDir, LOCAL_MODEL_DIRECTORY).listFiles()?.forEach { file ->
            runCatching { file.deleteRecursively() }
        }
    }

    override suspend fun getDownloadedModelsBytes(modelIds: List<String>): Long {
        val modelDir = File(context.filesDir, LOCAL_MODEL_DIRECTORY)
        return modelIds
            .distinct()
            .sumOf { id -> File(modelDir, id).directorySize() }
    }
}

/**
 * Recursively calculates byte size for files and directories, returning zero for missing paths.
 *
 * @receiver File or directory inside app-private storage.
 *
 * @author Dmitriy Moroz
 */
private fun File.directorySize(): Long {
    if (!exists()) return 0L
    if (isFile) return length()
    return listFiles()
        ?.sumOf { child -> child.directorySize() }
        ?: 0L
}

private const val LOCAL_MODEL_DIRECTORY = "model"
