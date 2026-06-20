package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class AndroidDownloadableModelFileStoreTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val fileStore by lazy {
        AndroidDownloadableModelFileStore(
            fileProviderDescriptor = TestFileProviderDescriptor(
                localModelDirPath = temporaryFolder.root.path,
            ),
        )
    }

    @Test
    fun `given sdxl model directory contains checkpoint file, resolve path returns checkpoint path`() {
        val model = model(id = "segmind-ssd-1b-a1111", type = LocalAiModel.Type.Sdxl)
        val directory = File(temporaryFolder.root, model.id).apply(File::mkdirs)
        val checkpoint = File(directory, "SSD-1B-A1111.safetensors").apply {
            writeText("checkpoint")
        }

        val actual = fileStore.resolvePath(model)

        assertEquals(checkpoint.path, actual)
    }

    @Test
    fun `given sdxl model directory does not contain checkpoint file, resolve path returns directory path`() {
        val model = model(id = "diffusers-model", type = LocalAiModel.Type.Sdxl)
        val directory = File(temporaryFolder.root, model.id).apply(File::mkdirs)
        File(directory, "unet").mkdirs()

        val actual = fileStore.resolvePath(model)

        assertEquals(directory.path, actual)
    }

    @Test
    fun `given custom directory contains multiple checkpoint files, resolve single file path returns largest checkpoint path`() {
        val directory = temporaryFolder.newFolder("custom-sdxl")
        File(directory, "tiny.gguf").writeText("1")
        val expected = File(directory, "model.safetensors").apply {
            writeText("checkpoint")
        }

        val actual = fileStore.resolveSingleFilePath(directory.path)

        assertEquals(expected.path, actual)
    }

    @Test
    fun `given non sdxl model directory contains checkpoint file, resolve path returns directory path`() {
        val model = model(id = "onnx-model", type = LocalAiModel.Type.ONNX)
        val directory = File(temporaryFolder.root, model.id).apply(File::mkdirs)
        File(directory, "model.safetensors").writeText("checkpoint")

        val actual = fileStore.resolvePath(model)

        assertEquals(directory.path, actual)
    }

    @Test
    fun `given bonsai model directory contains complete layout, is downloaded returns true`() {
        val model = model(id = "bonsai-model", type = LocalAiModel.Type.Bonsai)
        File(temporaryFolder.root, model.id)
            .apply(File::mkdirs)
            .createBonsaiLayout()

        val actual = fileStore.isDownloaded(model)

        assertTrue(actual)
    }

    @Test
    fun `given bonsai model directory contains invalid residue, is downloaded returns false`() {
        val model = model(id = "bonsai-model", type = LocalAiModel.Type.Bonsai)
        val directory = File(temporaryFolder.root, model.id).apply(File::mkdirs)
        File(directory, "download-error.html").writeText("pve001 unavailable")

        val actual = fileStore.isDownloaded(model)

        assertFalse(actual)
    }
}

private fun File.createBonsaiLayout() {
    File(this, "transformer-packed-mflux").apply(File::mkdirs)
        .resolve("quantization_config.json")
        .writeText("{}")
    File(this, "text_encoder-mlx-4bit").mkdirs()
    File(this, "tokenizer").mkdirs()
    File(this, "vae").mkdirs()
    File(this, "scheduler").mkdirs()
}

private fun model(
    id: String,
    type: LocalAiModel.Type,
) = LocalAiModel(
    id = id,
    type = type,
    name = id,
    size = "",
    sources = emptyList(),
)

private data class TestFileProviderDescriptor(
    override val localModelDirPath: String,
) : FileProviderDescriptor {
    override val providerPath: String = ""
    override val imagesCacheDirPath: String = ""
    override val logsCacheDirPath: String = ""
    override val workCacheDirPath: String = ""
}
