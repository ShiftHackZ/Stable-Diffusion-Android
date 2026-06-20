package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.data.hasZipArchiveSignature
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileManager
import platform.Foundation.NSFileSize
import platform.Foundation.NSNumber

/**
 * Coordinates `IosDownloadableModelFileStore` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
@OptIn(ExperimentalForeignApi::class)
internal class IosDownloadableModelFileStore(
    /**
     * Exposes the `fileProviderDescriptor` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val fileProviderDescriptor: FileProviderDescriptor,
) : DownloadableModelFileStore {

    override fun isDownloaded(model: LocalAiModel): Boolean = try {
        when (model.id) {
            LocalAiModel.CustomOnnx.id,
            LocalAiModel.CustomMediaPipe.id,
            LocalAiModel.CustomSdxl.id,
            LocalAiModel.CustomCoreMl.id,
            LocalAiModel.CustomBonsai.id -> true

            else -> model.hasDownloadedArchive(
                archivePath = "${fileProviderDescriptor.localModelDirPath}/${model.id}/$MODEL_ARCHIVE_NAME",
            )
        }
    } catch (_: Exception) {
        false
    }

    override fun resolvePath(model: LocalAiModel): String =
        "${fileProviderDescriptor.localModelDirPath}/${model.id}"

    override fun resolveSingleFilePath(path: String): String = path

    override fun delete(id: String) {
        NSFileManager.defaultManager.removeItemAtPath(
            path = "${fileProviderDescriptor.localModelDirPath}/$id",
            error = null,
        )
    }

    private companion object {
        const val MODEL_ARCHIVE_NAME = "model.zip"
        const val MIN_COMPLETE_DOWNLOAD_RATIO = 0.9
    }

    private fun LocalAiModel.hasDownloadedArchive(archivePath: String): Boolean {
        if (!NSFileManager.defaultManager.fileExistsAtPath(path = archivePath)) return false
        if (!archivePath.hasZipArchiveSignature()) return false

        val archiveSize = archivePath.fileSize() ?: return true
        val expectedSize = size.expectedSizeBytes() ?: return archiveSize > 0L
        return archiveSize >= (expectedSize * MIN_COMPLETE_DOWNLOAD_RATIO).toLong()
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun String.fileSize(): Long? =
    (NSFileManager.defaultManager.attributesOfItemAtPath(path = this, error = null)
        ?.get(NSFileSize) as? NSNumber)
        ?.longLongValue

private fun String.expectedSizeBytes(): Long? {
    val parts = trim().split(Regex("\\s+"))
    if (parts.size != 2) return null

    val value = parts[0].toDoubleOrNull() ?: return null
    val multiplier = when (parts[1].uppercase()) {
        "KB" -> 1_000L
        "MB" -> 1_000_000L
        "GB" -> 1_000_000_000L
        "KIB" -> 1_024L
        "MIB" -> 1_048_576L
        "GIB" -> 1_073_741_824L
        else -> return null
    }
    return (value * multiplier).toLong()
}
