package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111MetadataApi
import com.shifthackz.aisdv1.network.auth.BasicHttpAuthorization
import com.shifthackz.aisdv1.network.model.StableDiffusionSamplerRaw
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StableDiffusionSamplersRemoteDataSourceTest {

    private val stubBaseUrl = "http://192.168.0.1:7860"
    private val stubCredentials = AuthorizationCredentials.HttpBasic("user", "password")
    private val stubAuthorization = BasicHttpAuthorization("user", "password")
    private val stubException = RuntimeException("Internal server error.")
    private val stubApi = mockk<Automatic1111MetadataApi>()

    private val remoteDataSource = KtorStableDiffusionSamplersRemoteDataSource(
        api = stubApi,
    )

    @Test
    fun `given attempt to fetch samplers, api returns success response, expected valid samplers list value`() = runTest {
        coEvery {
            stubApi.fetchSamplers(stubBaseUrl, stubAuthorization)
        } returns mockStableDiffusionSamplerRaw

        val actual = remoteDataSource.fetchSamplers(stubBaseUrl, stubCredentials)

        assertTrue(actual is List<StableDiffusionSampler>)
        assertEquals(mockStableDiffusionSamplerRaw.size, actual.size)
    }

    @Test
    fun `given attempt to fetch samplers, api returns empty response, expected empty samplers value`() = runTest {
        coEvery {
            stubApi.fetchSamplers(stubBaseUrl, stubAuthorization)
        } returns emptyList()

        assertEquals(emptyList<Any>(), remoteDataSource.fetchSamplers(stubBaseUrl, stubCredentials))
    }

    @Test
    fun `given attempt to fetch samplers, api returns error response, expected error value`() = runTest {
        coEvery {
            stubApi.fetchSamplers(stubBaseUrl, stubAuthorization)
        } throws stubException

        val actual = runCatching {
            remoteDataSource.fetchSamplers(stubBaseUrl, stubCredentials)
        }.exceptionOrNull()

        assertTrue(actual === stubException)
    }

    private companion object {
        val mockStableDiffusionSamplerRaw = listOf(
            StableDiffusionSamplerRaw(
                name = "Euler",
                aliases = listOf("euler"),
                options = mapOf("scheduler" to "normal"),
            ),
        )
    }
}
