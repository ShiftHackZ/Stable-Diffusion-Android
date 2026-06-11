package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.ForgeModule
import com.shifthackz.aisdv1.network.model.ForgeModuleRaw
import org.junit.Assert.assertEquals
import org.junit.Test

class KtorForgeModulesMappersTest {

    @Test
    fun `given raw forge module, expected domain model value`() {
        val actual = ForgeModuleRaw(
            modelName = "sdxl_vae",
            filename = "/models/VAE/sdxl_vae.safetensors",
        ).mapKtorRawToCheckpointDomain()

        assertEquals(
            ForgeModule(
                name = "sdxl_vae",
                path = "/models/VAE/sdxl_vae.safetensors",
            ),
            actual,
        )
    }

    @Test
    fun `given raw forge module with null values, expected empty strings`() {
        val actual = ForgeModuleRaw().mapKtorRawToCheckpointDomain()

        assertEquals(ForgeModule(name = "", path = ""), actual)
    }
}
