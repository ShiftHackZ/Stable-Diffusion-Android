package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.data.mocks.mockImageToImagePayload
import com.shifthackz.aisdv1.data.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.domain.entity.ArliAiSampler
import org.junit.Assert
import org.junit.Test

class KtorArliAiGenerationMappersTest {

    @Test
    fun `given text payload, expected arli ai request contract`() {
        val payload = mockTextToImagePayload.copy(
            samplingSteps = 80,
            seed = " 5598 ",
            sampler = "",
            batchCount = 3,
        )

        val request = payload.mapToArliAiRequest(MODEL)

        Assert.assertEquals(MODEL, request.sdModelCheckpoint)
        Assert.assertEquals(payload.prompt, request.prompt)
        Assert.assertEquals(payload.negativePrompt, request.negativePrompt)
        Assert.assertEquals(40, request.steps)
        Assert.assertEquals(ArliAiSampler.default.key, request.samplerName)
        Assert.assertEquals(5598L, request.seed)
        Assert.assertEquals(3, request.batchSize)
        Assert.assertEquals(payload.restoreFaces, request.restoreFaces)
    }

    @Test
    fun `given image payload, expected arli ai image request contract`() {
        val payload = mockImageToImagePayload.copy(
            base64Image = "AQID",
            base64MaskImage = "BAUG",
            samplingSteps = 0,
            seed = "-1",
            sampler = "Euler a",
            batchCount = 2,
            maskBlur = 8,
            inPaintingFill = 1,
            inPaintFullRes = true,
            inPaintFullResPadding = 32,
            inPaintingMaskInvert = 1,
        )

        val request = payload.mapToArliAiRequest(MODEL)

        Assert.assertEquals(MODEL, request.sdModelCheckpoint)
        Assert.assertEquals(listOf("AQID"), request.initImages)
        Assert.assertEquals("BAUG", request.mask)
        Assert.assertEquals(1, request.steps)
        Assert.assertEquals("Euler a", request.samplerName)
        Assert.assertEquals(-1L, request.seed)
        Assert.assertEquals(2, request.batchSize)
        Assert.assertEquals(8, request.maskBlur)
        Assert.assertEquals(1, request.inPaintingFill)
        Assert.assertTrue(request.inPaintFullRes == true)
        Assert.assertEquals(32, request.inPaintFullResPadding)
        Assert.assertEquals(1, request.inPaintingMaskInvert)
    }

    private companion object {
        const val MODEL = "Illustrious-XL-v2.0"
    }
}
