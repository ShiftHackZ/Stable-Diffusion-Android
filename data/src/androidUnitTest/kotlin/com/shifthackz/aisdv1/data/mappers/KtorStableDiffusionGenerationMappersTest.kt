package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.data.mocks.mockImageToImagePayload
import com.shifthackz.aisdv1.data.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.domain.entity.ADetailerConfig
import com.shifthackz.aisdv1.domain.entity.ForgeModule
import com.shifthackz.aisdv1.domain.entity.HiresConfig
import com.shifthackz.aisdv1.domain.entity.Scheduler
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.float
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert
import org.junit.Test

class KtorStableDiffusionGenerationMappersTest {

    @Test
    fun `given text payload with hires and adetailer, expected a1111 request contains extension fields`() {
        val payload = mockTextToImagePayload.copy(
            aDetailer = ADetailerConfig(
                enabled = true,
                model = "face_yolov8n.pt",
                prompt = "face prompt",
                negativePrompt = "face negative",
                confidence = 0.4f,
                maskBlur = 8,
                denoisingStrength = 0.45f,
                inpaintOnlyMasked = true,
                inpaintPadding = 48,
            ),
            hires = HiresConfig(
                enabled = true,
                upscaler = "Latent",
                scale = 1.5f,
                steps = 12,
                denoisingStrength = 0.35f,
                hrCfg = 4f,
                hrDistilledCfg = 2.5f,
            ),
            scheduler = Scheduler.KARRAS,
            forgeModules = listOf(
                ForgeModule(
                    name = "sdxl_vae",
                    path = "/models/VAE/sdxl_vae.safetensors",
                ),
            ),
        )

        val request = payload.mapToStableDiffusionRequest()
        val aDetailerArgs = request.alwaysOnScripts
            ?.get("ADetailer")
            ?.jsonObject
            ?.get("args")
            ?.jsonArray
        val aDetailerConfig = aDetailerArgs
            ?.get(2)
            ?.jsonObject

        Assert.assertEquals(true, request.enableHr)
        Assert.assertEquals("karras", request.scheduler)
        Assert.assertEquals("Latent", request.hrUpscaler)
        Assert.assertEquals(1.5f, request.hrScale)
        Assert.assertEquals(12, request.hrSecondPassSteps)
        Assert.assertEquals(4f, request.hrCfg)
        Assert.assertEquals(2.5f, request.hrDistilledCfg)
        Assert.assertEquals(0.35f, request.denoisingStrength)
        Assert.assertEquals(
            listOf("/models/VAE/sdxl_vae.safetensors"),
            request.overrideSettings?.forgeAdditionalModules,
        )
        Assert.assertEquals(true, aDetailerArgs?.get(0)?.jsonPrimitive?.boolean)
        Assert.assertEquals(false, aDetailerArgs?.get(1)?.jsonPrimitive?.boolean)
        Assert.assertEquals("face_yolov8n.pt", aDetailerConfig?.get("ad_model")?.jsonPrimitive?.content)
        Assert.assertEquals("face prompt", aDetailerConfig?.get("ad_prompt")?.jsonPrimitive?.content)
        Assert.assertEquals("face negative", aDetailerConfig?.get("ad_negative_prompt")?.jsonPrimitive?.content)
        Assert.assertEquals(0.4f, aDetailerConfig?.get("ad_confidence")?.jsonPrimitive?.float)
        Assert.assertEquals(8, aDetailerConfig?.get("ad_dilate_erode")?.jsonPrimitive?.int)
        Assert.assertEquals(0.45f, aDetailerConfig?.get("ad_denoising_strength")?.jsonPrimitive?.float)
        Assert.assertEquals(true, aDetailerConfig?.get("ad_inpaint_only_masked")?.jsonPrimitive?.boolean)
        Assert.assertEquals(48, aDetailerConfig?.get("ad_inpaint_only_masked_padding")?.jsonPrimitive?.int)
        Assert.assertEquals(true, aDetailerConfig?.get("is_api")?.jsonPrimitive?.boolean)
    }

    @Test
    fun `given image payload with adetailer, expected a1111 request contains always on script`() {
        val payload = mockImageToImagePayload.copy(
            aDetailer = ADetailerConfig(
                enabled = true,
                model = "hand_yolov8n.pt",
            ),
            scheduler = Scheduler.SIMPLE,
        )

        val request = payload.mapToStableDiffusionRequest()

        Assert.assertEquals("simple", request.scheduler)
        Assert.assertEquals(
            "hand_yolov8n.pt",
            request.alwaysOnScripts
                ?.get("ADetailer")
                ?.jsonObject
                ?.get("args")
                ?.jsonArray
                ?.get(2)
                ?.jsonObject
                ?.get("ad_model")
                ?.jsonPrimitive
                ?.content,
        )
    }

    @Test
    fun `given default payload, expected optional a1111 extension fields omitted`() {
        val request = mockTextToImagePayload.mapToStableDiffusionRequest()

        Assert.assertNull(request.alwaysOnScripts)
        Assert.assertNull(request.scheduler)
        Assert.assertNull(request.enableHr)
        Assert.assertNull(request.hrUpscaler)
        Assert.assertNull(request.hrScale)
        Assert.assertNull(request.hrSecondPassSteps)
        Assert.assertNull(request.hrCfg)
        Assert.assertNull(request.hrDistilledCfg)
        Assert.assertNull(request.denoisingStrength)
        Assert.assertNull(request.overrideSettings)
    }
}
