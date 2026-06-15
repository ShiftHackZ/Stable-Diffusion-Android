package com.shifthackz.aisdv1.presentation.screen.txt2img

import com.shifthackz.aisdv1.core.validation.ValidationResult
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.domain.entity.ADetailerConfig
import com.shifthackz.aisdv1.domain.entity.ForgeModule
import com.shifthackz.aisdv1.domain.entity.HiresConfig
import com.shifthackz.aisdv1.domain.entity.Scheduler
import com.shifthackz.aisdv1.domain.entity.SdxlBackend
import com.shifthackz.aisdv1.domain.entity.ServerSource
import org.junit.Assert
import org.junit.Test

class TextToImageStateTest {

    private val dimensionValidator = DimensionValidator { input ->
        when {
            input.isNullOrEmpty() -> ValidationResult(
                isValid = false,
                validationError = DimensionValidator.Error.Empty,
            )
            input.toIntOrNull() == null -> ValidationResult(
                isValid = false,
                validationError = DimensionValidator.Error.Unexpected,
            )
            input.toInt() < MIN_SIZE -> ValidationResult(
                isValid = false,
                validationError = DimensionValidator.Error.LessThanMinimum(MIN_SIZE),
            )
            input.toInt() > MAX_SIZE -> ValidationResult(
                isValid = false,
                validationError = DimensionValidator.Error.BiggerThanMaximum(MAX_SIZE),
            )
            else -> ValidationResult(isValid = true)
        }
    }

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

    @Test
    fun `given Bonsai state, expected payload preserves configurable params`() {
        val payload = TextToImageState(
            mode = ServerSource.LOCAL_APPLE_BONSAI,
            samplingSteps = 30,
            cfgScale = 7.5f,
            batchCount = 4,
            nsfw = true,
        ).mapToPayload()

        Assert.assertEquals(30, payload.samplingSteps)
        Assert.assertEquals(7.5f, payload.cfgScale)
        Assert.assertEquals(DEFAULT_SIZE, payload.width)
        Assert.assertEquals(DEFAULT_SIZE, payload.height)
        Assert.assertEquals(1, payload.batchCount)
        Assert.assertTrue(payload.nsfw)
    }

    @Test
    fun `given state switched to Bonsai, expected safe default size`() {
        val state = TextToImageState(
            mode = ServerSource.AUTOMATIC1111,
            width = "512",
            height = "512",
        ).withSource(
            source = ServerSource.LOCAL_APPLE_BONSAI,
            stableDiffusionSamplers = null,
            forgeModules = null,
            aDetailerAvailable = null,
            arliAiModels = null,
        )

        Assert.assertEquals(BONSAI_DEFAULT_SIZE.toString(), state.width)
        Assert.assertEquals(BONSAI_DEFAULT_SIZE.toString(), state.height)
    }

    @Test
    fun `given Bonsai state with non multiple width, expected validation error`() {
        val state = TextToImageState(
            mode = ServerSource.LOCAL_APPLE_BONSAI,
            width = "100",
            height = "96",
        ).validated(dimensionValidator)

        Assert.assertNotNull(state.widthValidationError)
        Assert.assertNull(state.heightValidationError)
    }

    @Test
    fun `given Bonsai state with large size, expected no Bonsai specific max validation error`() {
        val state = TextToImageState(
            mode = ServerSource.LOCAL_APPLE_BONSAI,
            width = "512",
            height = "256",
        ).validated(dimensionValidator)

        Assert.assertNull(state.widthValidationError)
        Assert.assertNull(state.heightValidationError)
    }

    @Test
    fun `given non Bonsai state with non multiple width, expected no multiple validation error`() {
        val state = TextToImageState(
            mode = ServerSource.AUTOMATIC1111,
            width = "100",
            height = "96",
        ).validated(dimensionValidator)

        Assert.assertNull(state.widthValidationError)
        Assert.assertNull(state.heightValidationError)
    }

    private companion object {
        const val MIN_SIZE = 64
        const val MAX_SIZE = 2048
    }
}
