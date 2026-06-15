package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import java.io.File

/**
 * Coordinates `AndroidDownloadableModelFileStore` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class AndroidDownloadableModelFileStore(
    /**
     * Exposes the `fileProviderDescriptor` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val fileProviderDescriptor: FileProviderDescriptor,
) : DownloadableModelFileStore {

    /**
     * Executes the `isDownloaded` step in the SDAI data layer.
     *
     * @param model model value consumed by the API.
     * @return Result produced by `isDownloaded`.
     * @author Dmitriy Moroz
     */
    override fun isDownloaded(model: LocalAiModel): Boolean = try {
        when (model.id) {
            LocalAiModel.CustomOnnx.id,
            LocalAiModel.CustomMediaPipe.id,
            LocalAiModel.CustomSdxl.id,
            LocalAiModel.CustomCoreMl.id,
            LocalAiModel.CustomBonsai.id,
            -> true

            else -> when (model.type) {
                LocalAiModel.Type.ONNX -> {
                    val files = getLocalModelFiles(model.id).filter { it.isDirectory }
                    files.size == 4
                }

                LocalAiModel.Type.MediaPipe -> {
                    val files = getLocalModelFiles(model.id)
                    files.isNotEmpty()
                }

                LocalAiModel.Type.Sdxl -> {
                    val files = getLocalModelFiles(model.id)
                    files.isNotEmpty()
                }

                LocalAiModel.Type.CoreMl -> {
                    val files = getLocalModelFiles(model.id)
                    files.isNotEmpty()
                }

                LocalAiModel.Type.Bonsai -> {
                    val files = getLocalModelFiles(model.id)
                    files.isNotEmpty()
                }
            }
        }
    } catch (_: Exception) {
        false
    }

    /**
     * Loads SDAI data through `resolvePath`.
     *
     * @param model model value consumed by the API.
     * @return Result produced by `resolvePath`.
     * @author Dmitriy Moroz
     */
    override fun resolvePath(model: LocalAiModel): String {
        val directory = getLocalModelDirectory(model.id)
        if (model.type != LocalAiModel.Type.Sdxl) return directory.path

        return resolveSingleFilePath(directory.path)
    }

    /**
     * Loads SDAI data through `resolveSingleFilePath`.
     *
     * @param path raw file or directory path used by the operation.
     * @return Result produced by `resolveSingleFilePath`.
     * @author Dmitriy Moroz
     */
    override fun resolveSingleFilePath(path: String): String {
        val file = File(path)
        if (file.isFile) return file.path

        return file
            .listFiles()
            ?.filter { file -> file.isFile && file.extension.lowercase() in singleFileModelExtensions }
            ?.maxByOrNull(File::length)
            ?.path
            ?: file.path
    }

    /**
     * Performs the SDAI side effect handled by `delete`.
     *
     * @param id identifier of the target entity.
     * @author Dmitriy Moroz
     */
    override fun delete(id: String) {
        getLocalModelDirectory(id).deleteRecursively()
    }

    /**
     * Loads SDAI data through `getLocalModelDirectory`.
     *
     * @param id identifier of the target entity.
     * @return Result produced by `getLocalModelDirectory`.
     * @author Dmitriy Moroz
     */
    private fun getLocalModelDirectory(id: String): File {
        return File("${fileProviderDescriptor.localModelDirPath}/$id")
    }

    /**
     * Loads SDAI data through `getLocalModelFiles`.
     *
     * @param id identifier of the target entity.
     * @return Result produced by `getLocalModelFiles`.
     * @author Dmitriy Moroz
     */
    private fun getLocalModelFiles(id: String): List<File> {
        val localModelDir = getLocalModelDirectory(id)
        if (!localModelDir.exists()) return emptyList()
        return localModelDir.listFiles()?.toList() ?: emptyList()
    }

    private companion object {
        val singleFileModelExtensions = setOf("ckpt", "gguf", "safetensors")
    }
}
