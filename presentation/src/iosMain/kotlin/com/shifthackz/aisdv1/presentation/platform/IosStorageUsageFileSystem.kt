package com.shifthackz.aisdv1.presentation.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileManager
import platform.Foundation.NSFileSize
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSNumber

/**
 * Measures and mutates app-private iOS storage used by Settings usage screens.
 *
 * The paths intentionally match the iOS data layer: downloaded local models live under
 * `Library/Application Support/model`, while transient app files live under `Library/Caches`.
 *
 * @param fileManager Native file manager used for path traversal and deletion.
 * @param homePath App sandbox home directory.
 *
 * @author Dmitriy Moroz
 */
@OptIn(ExperimentalForeignApi::class)
internal class IosStorageUsageFileSystem(
    private val fileManager: NSFileManager = NSFileManager.defaultManager,
    private val homePath: String = NSHomeDirectory(),
) {

    private val cacheDirPath: String = "$homePath/Library/Caches"
    private val localModelDirPath: String = "$homePath/Library/Application Support/model"

    /**
     * Returns the current size of the app cache directory in bytes.
     *
     * @author Dmitriy Moroz
     */
    fun getAppCacheBytes(): Long = cacheDirPath.pathSize()

    /**
     * Hides tiny iOS filesystem bookkeeping sizes from user-facing storage UI.
     *
     * Raw filesystem methods still return values such as the 64-byte directory size reported by
     * APFS, but usage screens should not present that as real cache or model data.
     *
     * @param bytes Raw filesystem byte count.
     * @return Zero for sub-threshold residue, otherwise the original positive byte count.
     *
     * @author Dmitriy Moroz
     */
    fun mapStorageBytesForUi(bytes: Long): Long {
        val safeBytes = bytes.coerceAtLeast(0L)
        return if (safeBytes < UI_VISIBLE_STORAGE_THRESHOLD_BYTES) 0L else safeBytes
    }

    /**
     * Deletes cache directory children without deleting the cache directory itself.
     *
     * @author Dmitriy Moroz
     */
    fun clearAppCache() {
        fileManager.contentsOfDirectoryAtPath(path = cacheDirPath, error = null)
            ?.filterIsInstance<String>()
            ?.forEach { name ->
                fileManager.removeItemAtPath(
                    path = cacheDirPath.child(name),
                    error = null,
                )
            }
    }

    /**
     * Returns the total size of every local model directory in bytes.
     *
     * @author Dmitriy Moroz
     */
    fun getAllDownloadedModelsBytes(): Long = localModelDirPath.pathSize()

    /**
     * Deletes every local model directory without deleting the model store directory itself.
     *
     * @author Dmitriy Moroz
     */
    fun clearAllDownloadedModels() {
        fileManager.contentsOfDirectoryAtPath(path = localModelDirPath, error = null)
            ?.filterIsInstance<String>()
            ?.forEach { name ->
                fileManager.removeItemAtPath(
                    path = localModelDirPath.child(name),
                    error = null,
                )
            }
    }

    /**
     * Returns the total size of downloaded local model directories in bytes.
     *
     * @param modelIds Local model identifiers whose app-private directories should be measured.
     *
     * @author Dmitriy Moroz
     */
    fun getDownloadedModelsBytes(modelIds: List<String>): Long =
        modelIds
            .distinct()
            .sumOf { id -> localModelDirPath.child(id).pathSize() }

    /**
     * Recursively measures an existing file or directory path.
     *
     * @receiver Absolute app-private file or directory path.
     *
     * @author Dmitriy Moroz
     */
    private fun String.pathSize(): Long {
        if (!fileManager.fileExistsAtPath(path = this)) return 0L

        val ownSize = fileSize()
        val childrenSize = fileManager.subpathsAtPath(path = this)
            ?.filterIsInstance<String>()
            ?.sumOf { child -> child(child).fileSize() }
            ?: 0L

        return ownSize + childrenSize
    }

    /**
     * Reads the filesystem-reported size for one path.
     *
     * @receiver Absolute file or directory path.
     *
     * @author Dmitriy Moroz
     */
    private fun String.fileSize(): Long =
        (fileManager.attributesOfItemAtPath(path = this, error = null)
            ?.get(NSFileSize) as? NSNumber)
            ?.longLongValue
            ?: 0L

    /**
     * Appends a single child component to an app-private path.
     *
     * @receiver Parent directory path.
     * @param name Child file or directory name.
     *
     * @author Dmitriy Moroz
     */
    private fun String.child(name: String): String = "$this/$name"

    /**
     * Smallest iOS storage amount worth showing to users.
     *
     * @author Dmitriy Moroz
     */
    private companion object {
        const val UI_VISIBLE_STORAGE_THRESHOLD_BYTES = 1024L
    }
}
