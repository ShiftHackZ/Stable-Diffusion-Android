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
                    getLocalModelDirectory(model.id).hasBonsaiModelLayout()
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

private const val TEXT_ENCODER_MLX_DIRECTORY = "text_encoder-mlx-4bit"
private const val TEXT_ENCODER_LEGACY_DIRECTORY = "text_encoder"
private const val TRANSFORMER_DIRECTORY = "transformer-packed-mflux"
private const val TOKENIZER_DIRECTORY = "tokenizer"
private const val VAE_DIRECTORY = "vae"
private const val SCHEDULER_DIRECTORY = "scheduler"
private const val RESOURCES_DIRECTORY = "Resources"
private const val EXTRACTED_DIRECTORY = "extracted"
private const val MAX_NESTED_SEARCH_DEPTH = 4

private fun File.hasBonsaiModelLayout(): Boolean {
    if (!exists()) return false

    directBonsaiCandidates()
        .any(File::isBonsaiRoot)
        .let { found -> if (found) return true }

    val rootDepth = toPath().nameCount
    return walkTopDown()
        .filter(File::isDirectory)
        .filter { candidate ->
            candidate.toPath().nameCount - rootDepth <= MAX_NESTED_SEARCH_DEPTH
        }
        .any(File::isBonsaiRoot)
}

private fun File.directBonsaiCandidates(): List<File> = listOf(
    this,
    File(this, RESOURCES_DIRECTORY),
    File(this, EXTRACTED_DIRECTORY),
    File(File(this, EXTRACTED_DIRECTORY), RESOURCES_DIRECTORY),
)

private fun File.isBonsaiRoot(): Boolean {
    val quantizationConfig = File(
        File(this, TRANSFORMER_DIRECTORY),
        "quantization_config.json",
    )
    val requiredDirectories = listOf(
        File(this, TOKENIZER_DIRECTORY),
        File(this, VAE_DIRECTORY),
        File(this, SCHEDULER_DIRECTORY),
    )

    return quantizationConfig.isFile &&
        listOf(TEXT_ENCODER_MLX_DIRECTORY, TEXT_ENCODER_LEGACY_DIRECTORY)
            .map { name -> File(this, name) }
            .any(File::isDirectory) &&
        requiredDirectories.all(File::isDirectory)
}
