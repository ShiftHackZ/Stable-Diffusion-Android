package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.data.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.data.mocks.mockImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.FalAiAcceleration
import com.shifthackz.aisdv1.domain.entity.FalAiImageSize
import com.shifthackz.aisdv1.domain.entity.FalAiModel
import org.junit.Assert
import org.junit.Test

class KtorFalAiGenerationMappersTest {

    @Test
    fun `given text payload, expected fal ai request contract`() {
        val payload = mockTextToImagePayload.copy(
            samplingSteps = 32,
            cfgScale = 0.7f,
            seed = " 5598 ",
            nsfw = false,
        )

        val request = payload.mapToFalAiRequest()

        Assert.assertEquals(payload.prompt, request.prompt)
        Assert.assertEquals(FalAiImageSize.default.key, request.imageSize)
        Assert.assertEquals(32, request.numInferenceSteps)
        Assert.assertEquals(1f, request.guidanceScale)
        Assert.assertEquals(5598L, request.seed)
        Assert.assertFalse(request.syncMode)
        Assert.assertEquals(1, request.numImages)
        Assert.assertTrue(request.enableSafetyChecker)
        Assert.assertEquals("png", request.outputFormat)
        Assert.assertEquals(FalAiAcceleration.NONE.key, request.acceleration)
    }

    @Test
    fun `given text payload with invalid seed and nsfw, expected safe defaults`() {
        val payload = mockTextToImagePayload.copy(
            samplingSteps = 0,
            cfgScale = 32f,
            seed = "-1",
            nsfw = true,
        )

        val request = payload.mapToFalAiRequest()

        Assert.assertEquals(1, request.numInferenceSteps)
        Assert.assertEquals(20f, request.guidanceScale)
        Assert.assertNull(request.seed)
        Assert.assertFalse(request.enableSafetyChecker)
    }

    @Test
    fun `given image payload, expected fal ai image request contract`() {
        val payload = mockImageToImagePayload.copy(
            base64Image = "AQID",
            denoisingStrength = 7f,
            samplingSteps = 60,
            cfgScale = 32f,
            falAiModel = FalAiModel.FLUX_LORA_IMAGE_TO_IMAGE,
            falAiAcceleration = FalAiAcceleration.HIGH,
            falAiSyncMode = true,
            batchCount = 6,
            nsfw = true,
        )

        val request = payload.mapToFalAiRequest()

        Assert.assertEquals("data:image/png;base64,AQID", request.imageUrl)
        Assert.assertEquals(payload.prompt, request.prompt)
        Assert.assertEquals(1f, request.strength)
        Assert.assertEquals(FalAiImageSize.default.key, request.imageSize)
        Assert.assertEquals(50, request.numInferenceSteps)
        Assert.assertEquals(32f, request.guidanceScale)
        Assert.assertEquals(5598L, request.seed)
        Assert.assertTrue(request.syncMode)
        Assert.assertEquals(4, request.numImages)
        Assert.assertFalse(request.enableSafetyChecker)
        Assert.assertEquals(FalAiAcceleration.NONE.key, request.acceleration)
    }

    @Test
    fun `given response image, expected domain result`() {
        val actual = mockTextToImagePayload.mapFalAiTextToImageResult(
            base64 = "base64",
            responseSeed = 1504L,
            createdAtMillis = 5598L,
        )

        Assert.assertEquals("base64", actual.image)
        Assert.assertEquals(5598L, actual.createdAt)
        Assert.assertEquals("1504", actual.seed)
        Assert.assertEquals(mockTextToImagePayload.prompt, actual.prompt)
        Assert.assertEquals(mockTextToImagePayload.negativePrompt, actual.negativePrompt)
    }

    @Test
    fun `given image response image, expected domain result`() {
        val actual = mockImageToImagePayload.mapFalAiImageToImageResult(
            base64 = "base64",
            responseSeed = 1504L,
            createdAtMillis = 5598L,
        )

        Assert.assertEquals(AiGenerationResult.Type.IMAGE_TO_IMAGE, actual.type)
        Assert.assertEquals("base64", actual.image)
        Assert.assertEquals(mockImageToImagePayload.base64Image, actual.inputImage)
        Assert.assertEquals(5598L, actual.createdAt)
        Assert.assertEquals("1504", actual.seed)
    }
}
