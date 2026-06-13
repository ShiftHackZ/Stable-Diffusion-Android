package com.shifthackz.aisdv1.presentation.screen.txt2img

import com.shifthackz.aisdv1.domain.entity.ADetailerConfig
import com.shifthackz.aisdv1.domain.entity.ForgeModule
import com.shifthackz.aisdv1.domain.entity.HiresConfig
import com.shifthackz.aisdv1.domain.entity.Scheduler
import com.shifthackz.aisdv1.domain.entity.SdxlBackend
import com.shifthackz.aisdv1.domain.entity.ServerSource
import org.junit.Assert
import org.junit.Test

class TextToImageStateTest {

    @Test
    fun `given A1111 state with hires and ADetailer, expected payload contains configs`() {
        val hires = HiresConfig(
            enabled = true,
            upscaler = "Latent",
            scale = 1.5f,
            steps = 12,
            denoisingStrength = 0.35f,
        )
        val aDetailer = ADetailerConfig(
            enabled = true,
            model = "hand_yolov8n.pt",
            confidence = 0.7f,
            denoisingStrength = 0.45f,
        )
        val forgeModules = listOf(
            ForgeModule(
                name = "sdxl_vae",
                path = "/models/VAE/sdxl_vae.safetensors",
            ),
        )

        val payload = TextToImageState(
            mode = ServerSource.AUTOMATIC1111,
            hires = hires,
            aDetailer = aDetailer,
            aDetailerAvailable = true,
            selectedScheduler = Scheduler.KARRAS,
            selectedForgeModules = forgeModules,
        ).mapToPayload()

        Assert.assertEquals(hires, payload.hires)
        Assert.assertEquals(aDetailer, payload.aDetailer)
        Assert.assertEquals(Scheduler.KARRAS, payload.scheduler)
        Assert.assertEquals(forgeModules, payload.forgeModules)
    }

    @Test
    fun `given non A1111 state with hires and ADetailer, expected payload disables configs`() {
        val payload = TextToImageState(
            mode = ServerSource.SWARM_UI,
            hires = HiresConfig(enabled = true),
            aDetailer = ADetailerConfig(enabled = true),
            selectedForgeModules = listOf(ForgeModule(name = "module", path = "/module")),
        ).mapToPayload()

        Assert.assertEquals(HiresConfig.DISABLED, payload.hires)
        Assert.assertEquals(ADetailerConfig.DISABLED, payload.aDetailer)
        Assert.assertEquals(Scheduler.AUTOMATIC, payload.scheduler)
        Assert.assertEquals(emptyList<ForgeModule>(), payload.forgeModules)
    }

    @Test
    fun `given A1111 state with unavailable ADetailer, expected payload disables ADetailer`() {
        val payload = TextToImageState(
            mode = ServerSource.AUTOMATIC1111,
            aDetailer = ADetailerConfig(enabled = true),
            aDetailerAvailable = false,
        ).mapToPayload()

        Assert.assertEquals(ADetailerConfig.DISABLED, payload.aDetailer)
    }

    @Test
    fun `given SDXL state with Vulkan backend, expected payload preserves backend`() {
        val payload = TextToImageState(
            mode = ServerSource.LOCAL_STABLE_DIFFUSION_CPP,
            sdxlBackend = SdxlBackend.VULKAN,
        ).mapToPayload()

        Assert.assertEquals(SdxlBackend.VULKAN, payload.sdxlBackend)
    }

    @Test
    fun `given non SDXL state with Vulkan backend, expected payload uses Auto backend`() {
        val payload = TextToImageState(
            mode = ServerSource.AUTOMATIC1111,
            sdxlBackend = SdxlBackend.VULKAN,
        ).mapToPayload()

        Assert.assertEquals(SdxlBackend.AUTO, payload.sdxlBackend)
    }
}
