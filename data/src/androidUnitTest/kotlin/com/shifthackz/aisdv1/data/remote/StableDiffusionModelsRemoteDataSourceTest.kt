package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111MetadataApi
import com.shifthackz.aisdv1.network.auth.BasicHttpAuthorization
import com.shifthackz.aisdv1.network.model.KtorStableDiffusionModelRaw
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StableDiffusionModelsRemoteDataSourceTest {

    private val stubBaseUrl = "http://192.168.0.1:7860"
    private val stubCredentials = AuthorizationCredentials.HttpBasic("user", "password")
    private val stubAuthorization = BasicHttpAuthorization("user", "password")
    private val stubException = RuntimeException("Internal server error.")
    private val stubApi = mockk<Automatic1111MetadataApi>()

    private val remoteDataSource = KtorStableDiffusionModelsRemoteDataSource(
        api = stubApi,
    )

    @Test
    fun `given attempt to fetch models, api returns success response, expected valid models list value`() = runTest {
        coEvery {
            stubApi.fetchModels(stubBaseUrl, stubAuthorization)
        } returns mockStableDiffusionModelRaw

        val actual = remoteDataSource.fetchSdModels(stubBaseUrl, stubCredentials)

        assertTrue(actual is List<StableDiffusionModel>)
        assertEquals(mockStableDiffusionModelRaw.size, actual.size)
    }

    @Test
    fun `given attempt to fetch models, api returns empty response, expected empty models value`() = runTest {
        coEvery {
            stubApi.fetchModels(stubBaseUrl, stubAuthorization)
        } returns emptyList()

        assertEquals(emptyList<Any>(), remoteDataSource.fetchSdModels(stubBaseUrl, stubCredentials))
    }

    @Test
    fun `given attempt to fetch models, api returns error response, expected error value`() = runTest {
        coEvery {
            stubApi.fetchModels(stubBaseUrl, stubAuthorization)
        } throws stubException

        val actual = runCatching {
            remoteDataSource.fetchSdModels(stubBaseUrl, stubCredentials)
        }.exceptionOrNull()

        assertTrue(actual === stubException)
    }

    private companion object {
        val mockStableDiffusionModelRaw = listOf(
            KtorStableDiffusionModelRaw(
                title = "title",
                modelName = "model",
                hash = "hash",
                sha256 = "sha256",
                filename = "model.safetensors",
                config = "config",
            ),
        )
    }
}
