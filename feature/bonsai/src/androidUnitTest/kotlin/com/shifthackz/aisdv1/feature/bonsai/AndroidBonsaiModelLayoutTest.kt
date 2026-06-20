package com.shifthackz.aisdv1.feature.bonsai

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class AndroidBonsaiModelLayoutTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `given direct Bonsai root, resolve returns direct layout`() {
        val root = temporaryFolder.newFolder("bonsai")
        root.createBonsaiLayout()

        val actual = AndroidBonsaiModelLayout.resolve(root.path)

        assertEquals(root.path, actual.rootPath)
        assertEquals(File(root, "transformer-packed-mflux").path, actual.packedTransformerPath)
        assertEquals(File(root, "text_encoder-mlx-4bit").path, actual.textEncoderPath)
        assertEquals(File(root, "tokenizer").path, actual.tokenizerPath)
        assertEquals(File(root, "vae").path, actual.vaePath)
        assertEquals(File(root, "scheduler").path, actual.schedulerPath)
    }

    @Test
    fun `given both text encoder directories, resolve prefers mlx text encoder`() {
        val root = temporaryFolder.newFolder("bonsai")
        root.createBonsaiLayout(includeLegacyTextEncoder = true)

        val actual = AndroidBonsaiModelLayout.resolve(root.path)

        assertEquals(File(root, "text_encoder-mlx-4bit").path, actual.textEncoderPath)
    }

    @Test
    fun `given Resources Bonsai root, resolve returns resources layout`() {
        val modelDirectory = temporaryFolder.newFolder("bonsai")
        val resources = File(modelDirectory, "Resources").apply(File::mkdirs)
        resources.createBonsaiLayout(includeMlxTextEncoder = false, includeLegacyTextEncoder = true)

        val actual = AndroidBonsaiModelLayout.resolve(modelDirectory.path)

        assertEquals(resources.path, actual.rootPath)
        assertEquals(File(resources, "text_encoder").path, actual.textEncoderPath)
    }

    @Test
    fun `given nested Resources Bonsai root, resolve finds nested layout`() {
        val modelDirectory = temporaryFolder.newFolder("bonsai")
        val resources = File(modelDirectory, "wrapper/level/Resources").apply(File::mkdirs)
        resources.createBonsaiLayout()

        val actual = AndroidBonsaiModelLayout.resolve(modelDirectory.path)

        assertEquals(resources.path, actual.rootPath)
    }

    @Test
    fun `given model zip, resolve extracts archive and returns extracted layout`() {
        val modelDirectory = temporaryFolder.newFolder("bonsai")
        val archive = File(modelDirectory, "model.zip")
        archive.writeZip(
            "ArchiveRoot/Resources/transformer-packed-mflux/quantization_config.json" to "{}",
            "ArchiveRoot/Resources/text_encoder-mlx-4bit/model.safetensors" to "text",
            "ArchiveRoot/Resources/tokenizer/tokenizer.json" to "{}",
            "ArchiveRoot/Resources/vae/model.safetensors" to "vae",
            "ArchiveRoot/Resources/scheduler/scheduler_config.json" to "{}",
        )

        val actual = AndroidBonsaiModelLayout.resolve(modelDirectory.path)

        val expectedRoot = File(modelDirectory, "extracted/ArchiveRoot/Resources")
        assertEquals(expectedRoot.path, actual.rootPath)
        assertTrue(File(expectedRoot, "tokenizer/tokenizer.json").isFile)
    }

    @Test
    fun `given invalid model directory, resolve reports missing resources`() {
        val modelDirectory = temporaryFolder.newFolder("bonsai")

        val actual = runCatching {
            AndroidBonsaiModelLayout.resolve(modelDirectory.path)
        }.exceptionOrNull()

        assertEquals(
            "Bonsai model resources not found at ${modelDirectory.path}.",
            actual?.message,
        )
    }

    @Test
    fun `given unsafe model zip entry, resolve rejects archive`() {
        val modelDirectory = temporaryFolder.newFolder("bonsai")
        File(modelDirectory, "model.zip").writeZip("../escape.txt" to "escape")

        val actual = runCatching {
            AndroidBonsaiModelLayout.resolve(modelDirectory.path)
        }.exceptionOrNull()

        assertEquals(
            "Invalid Bonsai model archive entry: ../escape.txt.",
            actual?.message,
        )
    }
}

private fun File.createBonsaiLayout(
    includeMlxTextEncoder: Boolean = true,
    includeLegacyTextEncoder: Boolean = false,
) {
    File(this, "transformer-packed-mflux").apply(File::mkdirs)
    File(this, "transformer-packed-mflux/quantization_config.json").writeText("{}")
    if (includeMlxTextEncoder) File(this, "text_encoder-mlx-4bit").mkdirs()
    if (includeLegacyTextEncoder) File(this, "text_encoder").mkdirs()
    File(this, "tokenizer").mkdirs()
    File(this, "vae").mkdirs()
    File(this, "scheduler").mkdirs()
}

private fun File.writeZip(vararg entries: Pair<String, String>) {
    outputStream().use { output ->
        ZipOutputStream(output).use { zip ->
            entries.forEach { (name, content) ->
                zip.putNextEntry(ZipEntry(name))
                zip.write(content.toByteArray())
                zip.closeEntry()
            }
        }
    }
}
