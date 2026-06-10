package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import java.io.File

internal class AndroidDownloadableModelFileStore(
    private val fileProviderDescriptor: FileProviderDescriptor,
) : DownloadableModelFileStore {

    override fun isDownloaded(model: LocalAiModel): Boolean = try {
        when (model.id) {
            LocalAiModel.CustomOnnx.id,
            LocalAiModel.CustomMediaPipe.id -> true

            else -> when (model.type) {
                LocalAiModel.Type.ONNX -> {
                    val files = getLocalModelFiles(model.id).filter { it.isDirectory }
                    files.size == 4
                }

                LocalAiModel.Type.MediaPipe -> {
                    val files = getLocalModelFiles(model.id)
                    files.isNotEmpty()
                }
            }
        }
    } catch (_: Exception) {
        false
    }

    override fun delete(id: String) {
        getLocalModelDirectory(id).deleteRecursively()
    }

    private fun getLocalModelDirectory(id: String): File {
        return File("${fileProviderDescriptor.localModelDirPath}/$id")
    }

    private fun getLocalModelFiles(id: String): List<File> {
        val localModelDir = getLocalModelDirectory(id)
        if (!localModelDir.exists()) return emptyList()
        return localModelDir.listFiles()?.toList() ?: emptyList()
    }
}
