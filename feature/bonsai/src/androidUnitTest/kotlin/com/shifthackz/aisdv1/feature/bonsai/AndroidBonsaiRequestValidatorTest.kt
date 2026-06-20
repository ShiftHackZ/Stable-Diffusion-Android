package com.shifthackz.aisdv1.feature.bonsai

import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class AndroidBonsaiRequestValidatorTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `given valid request, validate succeeds`() {
        val modelDirectory = temporaryFolder.newFolder("bonsai")

        val actual = runCatching {
            AndroidBonsaiRequestValidator.validate(
                payload = makePayload(),
                modelPath = modelDirectory.path,
            )
        }.exceptionOrNull()

        assertEquals(null, actual)
    }

    @Test
    fun `given blank prompt, validate reports required prompt`() {
        val modelDirectory = temporaryFolder.newFolder("bonsai")

        val actual = runCatching {
            AndroidBonsaiRequestValidator.validate(
                payload = makePayload(prompt = " \n "),
                modelPath = modelDirectory.path,
            )
        }.exceptionOrNull()

        assertEquals("Prompt is required.", actual?.message)
    }

    @Test
    fun `given invalid size, validate reports size requirement`() {
        val modelDirectory = temporaryFolder.newFolder("bonsai")

        val actual = runCatching {
            AndroidBonsaiRequestValidator.validate(
                payload = makePayload(width = 130),
                modelPath = modelDirectory.path,
            )
        }.exceptionOrNull()

        assertEquals(
            "Bonsai image size must be positive and divisible by 32.",
            actual?.message,
        )
    }

    @Test
    fun `given missing model path, validate reports missing resources`() {
        val missingPath = temporaryFolder.root.resolve("missing-bonsai").path

        val actual = runCatching {
            AndroidBonsaiRequestValidator.validate(
                payload = makePayload(),
                modelPath = missingPath,
            )
        }.exceptionOrNull()

        assertEquals(
            "Bonsai model resources were not found at $missingPath.",
            actual?.message,
        )
    }
}

private fun makePayload(
    prompt: String = "a bonsai tree",
    width: Int = 128,
    height: Int = 128,
): TextToImagePayload = TextToImagePayload(
    prompt = prompt,
    negativePrompt = "",
    samplingSteps = 4,
    cfgScale = 1f,
    width = width,
    height = height,
    restoreFaces = false,
    seed = "1",
    subSeed = "",
    subSeedStrength = 0f,
    sampler = "",
    nsfw = false,
    batchCount = 1,
    style = null,
    quality = null,
    openAiModel = null,
    stabilityAiClipGuidance = null,
    stabilityAiStylePreset = null,
)
