package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.network.model.StableDiffusionScriptInfoRaw
import com.shifthackz.aisdv1.network.model.StableDiffusionExtensionRaw
import org.junit.Assert.assertTrue
import org.junit.Test

class KtorStableDiffusionScriptsMappersTest {

    @Test
    fun `given script-info with ADetailer always-on, expected domain contains ADetailer`() {
        val result = listOf(
            StableDiffusionScriptInfoRaw(
                name = "ADetailer",
                isAlwaysOn = true,
                isImg2Img = false,
            ),
        ).mapToDomain()

        assertTrue(result.contains("ADetailer"))
    }

    @Test
    fun `given enabled adetailer extension, expected domain contains ADetailer`() {
        val result = listOf(
            StableDiffusionExtensionRaw(
                name = "adetailer",
                enabled = true,
            ),
        ).mapExtensionsToDomain()

        assertTrue(result.contains("ADetailer"))
    }
}
