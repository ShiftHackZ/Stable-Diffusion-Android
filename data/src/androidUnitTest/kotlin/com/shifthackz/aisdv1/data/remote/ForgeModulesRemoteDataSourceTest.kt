package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.domain.entity.ForgeModule
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111MetadataApi
import com.shifthackz.aisdv1.network.auth.BasicHttpAuthorization
import com.shifthackz.aisdv1.network.model.ForgeModuleRaw
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ForgeModulesRemoteDataSourceTest {

    private val stubBaseUrl = "http://192.168.0.1:7860"
    private val stubCredentials = AuthorizationCredentials.HttpBasic("user", "password")
    private val stubAuthorization = BasicHttpAuthorization("user", "password")
    private val stubException = RuntimeException("Internal server error.")
    private val stubApi = mockk<Automatic1111MetadataApi>()

    private val remoteDataSource = KtorForgeModulesRemoteDataSource(
        api = stubApi,
    )

    @Test
    fun `given attempt to fetch forge modules, api returns success response, expected valid modules list value`() = runTest {
        coEvery {
            stubApi.fetchForgeModules(stubBaseUrl, stubAuthorization)
        } returns mockForgeModulesRaw

        val actual = remoteDataSource.fetchModules(stubBaseUrl, stubCredentials)

        assertEquals(mockForgeModules, actual)
    }

    @Test
    fun `given attempt to fetch forge modules, api returns empty response, expected empty modules value`() = runTest {
        coEvery {
            stubApi.fetchForgeModules(stubBaseUrl, stubAuthorization)
        } returns emptyList()

        assertEquals(emptyList<Any>(), remoteDataSource.fetchModules(stubBaseUrl, stubCredentials))
    }

    @Test
    fun `given attempt to fetch forge modules, api returns error response, expected error value`() = runTest {
        coEvery {
            stubApi.fetchForgeModules(stubBaseUrl, stubAuthorization)
        } throws stubException

        val actual = runCatching {
            remoteDataSource.fetchModules(stubBaseUrl, stubCredentials)
        }.exceptionOrNull()

        assertTrue(actual === stubException)
    }

    private companion object {
        val mockForgeModulesRaw = listOf(
            ForgeModuleRaw(
                modelName = "sdxl_vae",
                filename = "/models/VAE/sdxl_vae.safetensors",
            ),
        )
        val mockForgeModules = listOf(
            ForgeModule(
                name = "sdxl_vae",
                path = "/models/VAE/sdxl_vae.safetensors",
            ),
        )
    }
}
