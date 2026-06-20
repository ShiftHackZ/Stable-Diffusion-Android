package com.shifthackz.aisdv1.feature.bonsai

import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile

/**
 * Resolves Android Bonsai model resources using the same layout rules as the iOS Swift runtime.
 *
 * @property rootPath directory containing the Bonsai resource folders.
 * @property packedTransformerPath directory containing packed mflux transformer weights.
 * @property textEncoderPath preferred text encoder directory.
 * @property tokenizerPath tokenizer directory.
 * @property vaePath VAE directory.
 * @property schedulerPath scheduler directory.
 * @author Dmitriy Moroz
 */
internal data class AndroidBonsaiModelLayout(
    val rootPath: String,
    val packedTransformerPath: String,
    val textEncoderPath: String,
    val tokenizerPath: String,
    val vaePath: String,
    val schedulerPath: String,
) {

    companion object {
        private const val TEXT_ENCODER_MLX_DIRECTORY = "text_encoder-mlx-4bit"
        private const val TEXT_ENCODER_LEGACY_DIRECTORY = "text_encoder"
        private const val TRANSFORMER_DIRECTORY = "transformer-packed-mflux"
        private const val TOKENIZER_DIRECTORY = "tokenizer"
        private const val VAE_DIRECTORY = "vae"
        private const val SCHEDULER_DIRECTORY = "scheduler"
        private const val MODEL_ARCHIVE = "model.zip"
        private const val EXTRACTED_DIRECTORY = "extracted"
        private const val RESOURCES_DIRECTORY = "Resources"
        private const val MAX_NESTED_SEARCH_DEPTH = 4

        /**
         * Resolves a Bonsai model path to its runtime layout, extracting `model.zip` when needed.
         *
         * @param modelPath selected model directory.
         * @return resolved Bonsai layout.
         * @throws IllegalStateException when required Bonsai resources cannot be found.
         * @author Dmitriy Moroz
         */
        fun resolve(modelPath: String): AndroidBonsaiModelLayout {
            val modelDirectory = File(modelPath)
            find(inDirectory = modelDirectory)?.let { layout -> return layout }

            val archive = File(modelDirectory, MODEL_ARCHIVE)
            if (!archive.isFile) {
                throw IllegalStateException("Bonsai model resources not found at $modelPath.")
            }

            val extracted = File(modelDirectory, EXTRACTED_DIRECTORY)
            if (find(inDirectory = extracted) == null) {
                if (extracted.exists()) extracted.deleteRecursively()
                extracted.mkdirs()
                archive.unzipSafely(destination = extracted)
            }

            return find(inDirectory = extracted)
                ?: throw IllegalStateException(
                    "Invalid Bonsai model layout: expected $TRANSFORMER_DIRECTORY, " +
                        "$TEXT_ENCODER_MLX_DIRECTORY or $TEXT_ENCODER_LEGACY_DIRECTORY, " +
                        "$TOKENIZER_DIRECTORY, $VAE_DIRECTORY, and $SCHEDULER_DIRECTORY directories.",
                )
        }

        private fun find(inDirectory: File): AndroidBonsaiModelLayout? {
            if (!inDirectory.exists()) return null

            directCandidates(inDirectory)
                .firstNotNullOfOrNull(::layout)
                ?.let { layout -> return layout }

            val rootDepth = inDirectory.toPath().nameCount
            return inDirectory
                .walkTopDown()
                .filter(File::isDirectory)
                .filter { candidate ->
                    candidate.toPath().nameCount - rootDepth <= MAX_NESTED_SEARCH_DEPTH
                }
                .firstNotNullOfOrNull(::layout)
        }

        private fun directCandidates(root: File): List<File> = listOf(
            root,
            File(root, RESOURCES_DIRECTORY),
            File(root, EXTRACTED_DIRECTORY),
            File(File(root, EXTRACTED_DIRECTORY), RESOURCES_DIRECTORY),
        )

        private fun layout(root: File): AndroidBonsaiModelLayout? {
            if (!isBonsaiRoot(root)) return null
            val textEncoder = firstDirectory(
                root = root,
                names = listOf(TEXT_ENCODER_MLX_DIRECTORY, TEXT_ENCODER_LEGACY_DIRECTORY),
            ) ?: return null

            return AndroidBonsaiModelLayout(
                rootPath = root.path,
                packedTransformerPath = File(root, TRANSFORMER_DIRECTORY).path,
                textEncoderPath = textEncoder.path,
                tokenizerPath = File(root, TOKENIZER_DIRECTORY).path,
                vaePath = File(root, VAE_DIRECTORY).path,
                schedulerPath = File(root, SCHEDULER_DIRECTORY).path,
            )
        }

        private fun isBonsaiRoot(root: File): Boolean {
            val quantizationConfig = File(
                File(root, TRANSFORMER_DIRECTORY),
                "quantization_config.json",
            )
            val requiredDirectories = listOf(
                File(root, TOKENIZER_DIRECTORY),
                File(root, VAE_DIRECTORY),
                File(root, SCHEDULER_DIRECTORY),
            )

            return quantizationConfig.isFile &&
                firstDirectory(
                    root = root,
                    names = listOf(TEXT_ENCODER_MLX_DIRECTORY, TEXT_ENCODER_LEGACY_DIRECTORY),
                ) != null &&
                requiredDirectories.all(File::isDirectory)
        }

        private fun firstDirectory(
            root: File,
            names: List<String>,
        ): File? = names
            .map { name -> File(root, name) }
            .firstOrNull(File::isDirectory)

        private fun File.unzipSafely(destination: File) {
            val destinationRoot = destination.canonicalFile
            ZipFile(this).use { zip ->
                zip.entries().asSequence().forEach { entry ->
                    val target = File(destinationRoot, entry.name).canonicalFile
                    if (!target.path.startsWith(destinationRoot.path + File.separator)) {
                        throw IllegalStateException("Invalid Bonsai model archive entry: ${entry.name}.")
                    }
                    if (entry.isDirectory) {
                        target.mkdirs()
                    } else {
                        target.parentFile?.mkdirs()
                        zip.getInputStream(entry).use { input ->
                            FileOutputStream(target).use { output ->
                                input.copyTo(output)
                            }
                        }
                    }
                }
            }
        }
    }
}
