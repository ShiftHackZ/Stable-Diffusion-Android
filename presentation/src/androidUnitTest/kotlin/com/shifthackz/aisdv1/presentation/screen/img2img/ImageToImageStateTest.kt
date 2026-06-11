package com.shifthackz.aisdv1.presentation.screen.img2img

import com.shifthackz.aisdv1.domain.entity.ADetailerConfig
import com.shifthackz.aisdv1.domain.entity.Scheduler
import com.shifthackz.aisdv1.domain.entity.ServerSource
import org.junit.Assert
import org.junit.Test

class ImageToImageStateTest {

    @Test
    fun `given A1111 state with ADetailer, expected payload contains config`() {
        val aDetailer = ADetailerConfig(
            enabled = true,
            model = "person_yolov8n-seg.pt",
            confidence = 0.65f,
            denoisingStrength = 0.5f,
        )

        val payload = ImageToImageState(
            mode = ServerSource.AUTOMATIC1111,
            imageBase64 = "base64",
            aDetailer = aDetailer,
            aDetailerAvailable = true,
            selectedScheduler = Scheduler.SIMPLE,
        ).mapToPayload()

        Assert.assertEquals(aDetailer, payload.aDetailer)
        Assert.assertEquals(Scheduler.SIMPLE, payload.scheduler)
    }

    @Test
    fun `given non A1111 state with ADetailer, expected payload disables config`() {
        val payload = ImageToImageState(
            mode = ServerSource.SWARM_UI,
            imageBase64 = "base64",
            aDetailer = ADetailerConfig(enabled = true),
        ).mapToPayload()

        Assert.assertEquals(ADetailerConfig.DISABLED, payload.aDetailer)
        Assert.assertEquals(Scheduler.AUTOMATIC, payload.scheduler)
    }

    @Test
    fun `given A1111 state with unavailable ADetailer, expected payload disables config`() {
        val payload = ImageToImageState(
            mode = ServerSource.AUTOMATIC1111,
            imageBase64 = "base64",
            aDetailer = ADetailerConfig(enabled = true),
            aDetailerAvailable = false,
        ).mapToPayload()

        Assert.assertEquals(ADetailerConfig.DISABLED, payload.aDetailer)
    }
}
